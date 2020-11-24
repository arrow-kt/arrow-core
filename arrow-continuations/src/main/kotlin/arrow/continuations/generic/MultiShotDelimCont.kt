package arrow.continuations.generic

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.loop
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.coroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.startCoroutineUninterceptedOrReturn
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * (Simulated) Multishot capable delimited control scope
 *
 * This has several drawbacks:
 * - f will rerun completely on multishot and only the results of [shift] are cached so any sideeffects outside of
 *   [shift] will rerun!
 * - This accumulates all results of [shift] (every argument passed when invoking the continuation) so on long running computations
 *   this may keep quite a bit of memory
 * - If the pure part before a multishot is expensive the multishot itself will have to rerun that, which makes it somewhat slow
 * - This is terribly hard to implement properly with nested scopes (which this one does not support)
 *
 * As per usual understanding of [DelimContScope] is required as I will only be commenting differences for now.
 */
open class MultiShotDelimContScope<R>(val f: suspend DelimitedScope<R>.() -> R) : DelimitedScope<R> {

//  private val resultVar = atomic<R?>(null)
//  private val nextShift = atomic<(suspend () -> R)?>(null)

  private val promise: ResettablePromise<R> = ResettablePromise()

  // TODO This can be append only and needs fast reversed access
  private val shiftFnContinuations = mutableListOf<Continuation<R>>()

  /**
   * Keep the arguments passed to [DelimitedContinuation.invoke] to be able to replay the scope if necessary
   */
  // TODO This can be append only and needs fast random access and slicing
  internal open val stack = mutableListOf<Any?>()

  /**
   * Our continuation now includes the function [f] to rerun on multishot, the current live (single-shot) continuation,
   *  the current stack and the offset from that stack when this is created which is used to know when to resume normal
   *  execution again on a replay.
   */
  class MultiShotCont<A, R>(
    liveContinuation: Continuation<A>,
    private val f: suspend DelimitedScope<R>.() -> R,
    private val stack: MutableList<Any?>,
    private val shiftFnContinuations: MutableList<Continuation<R>>
  ) : DelimitedContinuation<A, R> {
    // To make sure the continuation is only invoked once we put it in a nullable atomic and only access it through getAndSet
    private val liveContinuation = atomic<Continuation<A>?>(liveContinuation)
    private val stackOffset = stack.size

    override suspend fun invoke(a: A): R =
      when (val cont = liveContinuation.getAndSet(null)) {
        // On multishot we replay with a prefilled stack from start to the point at which this object was created
        //  (when the shift block this runs in was first called)
        null -> PrefilledDelimContScope((stack.subList(0, stackOffset).toList() + a).toMutableList(), f).invoke()
        // on the first pass we operate like a normal delimited scope but we also save the argument to the stack before resuming
        else -> suspendCoroutine { resumeShift ->
          shiftFnContinuations.add(resumeShift)
          stack.add(a)
          cont.resume(a)
        }
      }
  }

  data class CPSCont<A, R>(
    private val runFunc: suspend DelimitedScope<R>.(A) -> R
  ) : DelimitedContinuation<A, R> {
    override suspend fun invoke(a: A): R = DelimContScope<R> { runFunc(a) }.invoke()
  }

  override suspend fun <A> shift(func: suspend DelimitedScope<R>.(DelimitedContinuation<A, R>) -> R): A =
    suspendCoroutineUninterceptedOrReturn { continueMain ->
      val c = MultiShotCont(continueMain, f, stack, shiftFnContinuations)
      assert(promise.setShift { this.func(c) })
      COROUTINE_SUSPENDED
    }

  override suspend fun <A, B> shiftCPS(func: suspend (DelimitedContinuation<A, B>) -> R, c: suspend DelimitedScope<B>.(A) -> B): Nothing =
    suspendCoroutineUninterceptedOrReturn {
      assert(promise.setShift { func(CPSCont(c)) })
      COROUTINE_SUSPENDED
    }

  // This assumes RestrictSuspension or at least assumes the user to never reference the parent scope in f.
  override suspend fun <A> reset(f: suspend DelimitedScope<A>.() -> A): A =
    MultiShotDelimContScope(f).invoke()

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
            promise.setResult(nextShiftOrResult as R)
            break // Break out of the infinite loop if we have a result
          }
          else -> {
            val r = shiftOrNull.startCoroutineUninterceptedOrReturn(Continuation(coroutineContext) { result ->
              // Store intermediate result from shift
              promise.setResult(result.getOrThrow())
            })
            // If we suspended here we can just continue to loop because we should now have a new function to run
            // If we did not suspend we short-circuited and are thus done with looping
            if (r != COROUTINE_SUSPENDED) {
              promise.setResult(r as R)
              break // Break out of the infinite loop if we have a result
            }
          }
        }
      }
    } else return result as R

    // We need to finish the partially evaluated shift blocks by passing them our result.
    // This will update the result via the continuations that now finish up
    for (c in shiftFnContinuations.asReversed()) c.resume(promise.await() as R)
    // Return the final result
    return promise.await() as R
  }

  companion object {
    suspend fun <R> reset(f: suspend DelimitedScope<R>.() -> R): R =
      MultiShotDelimContScope(f).invoke()
  }
}

class PrefilledDelimContScope<R>(
  override val stack: MutableList<Any?>,
  f: suspend DelimitedScope<R>.() -> R
) : MultiShotDelimContScope<R>(f) {
  var depth = 0

  // Here we first check if we still have values in our local stack and if so we use those first
  //  if not we delegate to the normal delimited control implementation
  override suspend fun <A> shift(func: suspend DelimitedScope<R>.(DelimitedContinuation<A, R>) -> R): A =
    if (stack.size > depth) stack[depth++] as A
    else super.shift(func).also { depth++ }
}
