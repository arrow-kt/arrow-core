package arrow.continuations.generic

import kotlinx.atomicfu.atomic
import kotlin.coroutines.Continuation
import kotlin.coroutines.coroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.startCoroutineUninterceptedOrReturn
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.resume

/**
 * Implements delimited continuations with with no multi shot support (apart from shiftCPS which trivially supports it).
 *
 * For a version that simulates multishot (albeit with drawbacks) see [MultiShotDelimContScope].
 * For a version that allows nesting [reset] and calling parent scopes inside inner scopes see [NestedDelimContScope].
 *
 * The basic concept here is appending callbacks and polling for a result.
 * Every shift is evaluated until it either finishes (short-circuit) or suspends (called continuation). When it suspends its
 *  continuation is appended to a list waiting to be invoked with the final result of the block.
 * When running a function we jump back and forth between the main function and every function inside shift via their continuations.
 */
class DelimContScope<R>(val f: suspend DelimitedScope<R>.() -> R) : DelimitedScope<R> {

  /**
   * Variable for the next shift block to (partially) run.
   */
  private val promise: ResettablePromise<R> = ResettablePromise()

  /**
   * Variable used for polling the result after suspension happened.
   */
  private val resultVar = atomic<Any?>(EMPTY_VALUE)

  /**
   * "Callbacks"/partially evaluated shift blocks which now wait for the final result
   */
  // TODO This can be append only, but needs fast reversed access
  private val shiftFnContinuations = mutableListOf<Continuation<R>>()

  /**
   * Small wrapper that handles invoking the correct continuations and appending continuations from shift blocks
   */
  data class SingleShotCont<A, R>(
    private val continuation: Continuation<A>,
    private val shiftFnContinuations: MutableList<Continuation<R>>
  ) : DelimitedContinuation<A, R> {
    override suspend fun invoke(a: A): R = suspendCoroutineUninterceptedOrReturn { resumeShift ->
      shiftFnContinuations.add(resumeShift)
      continuation.resume(a)
      COROUTINE_SUSPENDED
    }
  }

  /**
   * Wrapper that handles invoking manually cps transformed continuations
   */
  data class CPSCont<A, R>(
    private val runFunc: suspend DelimitedScope<R>.(A) -> R
  ) : DelimitedContinuation<A, R> {
    override suspend fun invoke(a: A): R = DelimContScope<R> { runFunc(a) }.invoke()
  }

  /**
   * Captures the continuation and set [f] with the continuation to be executed next by the runloop.
   */
  override suspend fun <A> shift(f: suspend DelimitedScope<R>.(DelimitedContinuation<A, R>) -> R): A =
    suspendCoroutineUninterceptedOrReturn { continueMain ->
      val delCont = SingleShotCont(continueMain, shiftFnContinuations)
      assert(promise.setShift { this.f(delCont) })
      COROUTINE_SUSPENDED
    }

  /**
   * Same as [shift] except we never resume execution because we only continue in [c].
   */
  override suspend fun <A, B> shiftCPS(f: suspend (DelimitedContinuation<A, B>) -> R, c: suspend DelimitedScope<B>.(A) -> B): Nothing =
    suspendCoroutineUninterceptedOrReturn {
      assert(promise.setShift { f(CPSCont(c)) })
      COROUTINE_SUSPENDED
    }

  /**
   * Unsafe if [f] calls [shift] on this scope! Use [NestedDelimContScope] instead if this is a problem.
   */
  override suspend fun <A> reset(f: suspend DelimitedScope<A>.() -> A): A =
    DelimContScope(f).invoke()

  @Suppress("UNCHECKED_CAST")
  suspend fun invoke(): R {
    val result = f.startCoroutineUninterceptedOrReturn(this, Continuation(coroutineContext) { result ->
      promise.setResult(result.getOrThrow())
    })
    // We suspended, we might receive a result or encounter shiftFn
    if (result == COROUTINE_SUSPENDED) {
      while (true) { // Loop as long as we encounter `shift` functions, stop looping when we find an `R`
        val nextShiftOrResult = promise.await()
        when (val shiftOrNull = nextShiftOrResult as? (suspend () -> R)) {
          null -> { // `null` means reset returned with a suspended result
            resultVar.value = nextShiftOrResult as R
            break // Break out of the infinite loop if we have a result
          }
          else -> {
            val r = shiftOrNull.startCoroutineUninterceptedOrReturn(Continuation(coroutineContext) { result ->
              // Store intermediate result from shift
              resultVar.value = result.getOrThrow()
            })
            // If we suspended here we can just continue to loop because we should now have a new function to run
            // If we did not suspend we short-circuited and are thus done with looping
            if (r != COROUTINE_SUSPENDED) {
              resultVar.value = r as R
              break // Break out of the infinite loop if we have a result
            }
          }
        }
      }
    } else return result as R

    assert(resultVar.value !== EMPTY_VALUE)
    // We need to finish the partially evaluated shift blocks by passing them our result.
    // This will update the result via the continuations that now finish up
    for (c in shiftFnContinuations.asReversed()) c.resume(resultVar.value as R)
    // Return the final result
    return resultVar.value as R
  }

  companion object {
    suspend fun <R> reset(f: suspend DelimitedScope<R>.() -> R): R = DelimContScope(f).invoke()

    @Suppress("ClassName")
    private object EMPTY_VALUE
  }
}

// Uncancellable, let's assume for now computation blocks are uncancellable
private class ResettablePromise<A> {

  /**
   * Can have following states:
   * - EMPTY
   * - Continuation that is listening to this value.
   *    => This is decoupled in a marker, and a mutable var since
   * - Shift function
   * - Result value from reset
   */
  private val state = atomic<Any?>(EMPTY_VALUE)
  private var cont: Continuation<Any?>? = null

  fun setResult(a: A): Boolean =
    when (val value = state.value) {
      is EMPTY_VALUE -> state.compareAndSet(EMPTY_VALUE, a)
      is CONT ->
        state.compareAndSet(value, EMPTY_VALUE)
          .also {
            val cont2 = requireNotNull(this.cont)
            this.cont = null
            cont2.resume(a)
          }
      else -> throw IllegalStateException("Value already set")
    }

  fun setShift(shiftFn: suspend () -> A): Boolean =
    when (val value = state.value) {
      is EMPTY_VALUE -> state.compareAndSet(EMPTY_VALUE, shiftFn)
      is CONT ->
        state.compareAndSet(value, EMPTY_VALUE)
          .also {
            val cont2 = requireNotNull(this.cont)
            this.cont = null
            cont2.resume(shiftFn)
          }
      else -> throw IllegalStateException("Value already set")
    }

  /**
   * Awaits either a value `R` or a shift function `suspend () -> R`
   */
  suspend fun await(): Any? =
    suspendCoroutineUninterceptedOrReturn { cont: Continuation<Any?> ->
      when (val value = state.value) {
        is EMPTY_VALUE -> {
          if (state.compareAndSet(EMPTY_VALUE, CONT)) {
            this.cont = cont
          } else throw IllegalArgumentException("Something went wrong while setting result.")
          COROUTINE_SUSPENDED
        }
        is CONT -> throw IllegalStateException("Cannot be awaited twice")
        else -> {
          if (state.compareAndSet(value, EMPTY_VALUE)) value
          else throw IllegalArgumentException("Something went wrong while setting result.")
        }
      }
    }

  companion object {
    @Suppress("ClassName")
    private object EMPTY_VALUE
    private object CONT
  }
}
