package arrow.continuations.generic

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.loop
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.coroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.startCoroutineUninterceptedOrReturn
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

internal const val UNDECIDED = 0
internal const val SUSPENDED = 1

@Suppress("UNCHECKED_CAST")
internal open class SuspendMonadContinuation<R>(
  private val parent: Continuation<R>,
  val f: suspend DelimitedScope<R>.() -> R
) : Continuation<R>, DelimitedScope<R> {

  /**
   * State is either
   *  0 - UNDECIDED
   *  1 - SUSPENDED
   *  Any? (3) `resumeWith` always stores it upon UNDECIDED, and `getResult` can atomically get it.
   */
  private val _decision = atomic<Any>(UNDECIDED)
  private val token: Token = Token()

  override val context: CoroutineContext = EmptyCoroutineContext

  override fun resumeWith(result: Result<R>) {
    _decision.loop { decision ->
      when (decision) {
        UNDECIDED -> {
          val r: R? = when {
            result.isFailure -> {
              val e = result.exceptionOrNull()
              if (e is ShortCircuit && e.token === token) e.raiseValue as R else null
            }
            result.isSuccess -> result.getOrNull()
            else -> throw RuntimeException("Internal Arrow Exception")
          }

          when {
            r == null -> {
              parent.resumeWithException(result.exceptionOrNull()!!)
              return
            }
            _decision.compareAndSet(UNDECIDED, r) -> {
              return
            }
            else -> Unit // loop again
          }
        }
        else -> { // If not `UNDECIDED` then we need to pass result to `parent`
          val res: Result<R> = result.fold({ Result.success(it) }, { t ->
            if (t is ShortCircuit && t.token === token) Result.success(t.raiseValue as R)
            else Result.failure(t)
          })
          parent.resumeWith(res)
          return
        }
      }
    }
  }

  @PublishedApi // return the result
  internal fun getResult(): Any? =
    _decision.loop { decision ->
      when (decision) {
        UNDECIDED -> if (this._decision.compareAndSet(UNDECIDED, SUSPENDED)) return COROUTINE_SUSPENDED
        else -> return decision
      }
    }

  override suspend fun <A> shift(a: R): A = throw ShortCircuit(token, a)

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

  private val shiftFnContinuations = mutableListOf<Continuation<R>>()

  /**
   * Captures the continuation and set [f] with the continuation to be executed next by the runloop.
   */
  override suspend fun <A> shift(f: suspend DelimitedScope<R>.(DelimitedContinuation<A, R>) -> R): A =
    suspendCoroutineUninterceptedOrReturn { continueMain ->
      val delCont = SingleShotCont(continueMain, shiftFnContinuations)
      val decisionValue = _decision.value
      //assert(_decision.compareAndSet(UNDECIDED, suspend { this.f(delCont) }))
      assert(setShift { this.f(delCont) })
      COROUTINE_SUSPENDED
    }

  fun setShift(shiftFn: suspend () -> R): Boolean =
    _decision.loop { value ->
      return when (value) {
        UNDECIDED -> _decision.compareAndSet(UNDECIDED, shiftFn)
        SUSPENDED ->
          _decision.compareAndSet(value, SUSPENDED)
//            .also {
//              val cont2 = requireNotNull(this.cont)
//              this.cont = null
//              cont2.resume(shiftFn)
//            }
        else -> throw IllegalStateException("Value already set")
      }
    }

  private fun startCoroutineUninterceptedOrReturn(): Any? =
    try {
      f.startCoroutineUninterceptedOrReturn(this, this)?.let {
        if (it == COROUTINE_SUSPENDED) getResult()
        else it
      }
    } catch (e: Throwable) {
      if (e is ShortCircuit && e.token === token) e.raiseValue as R
      else throw e
    }

  @Suppress("UNCHECKED_CAST")
  operator fun invoke(): R {
    val result = startCoroutineUninterceptedOrReturn()
    // We suspended, we might receive a result or encounter shiftFn
    if (result == COROUTINE_SUSPENDED) {
      while (true) { // Loop as long as we encounter `shift` functions, stop looping when we find an `R`
        val nextShiftOrResult = getResult() //promise.await()
        when (val shiftOrNull = nextShiftOrResult as? (suspend () -> R)) {
          null -> { // `null` means reset returned with a suspended result
            resume(nextShiftOrResult as R)//promise.setResult(nextShiftOrResult as R)
            break // Break out of the infinite loop if we have a result
          }
          else -> {
            val r = shiftOrNull.startCoroutineUninterceptedOrReturn(this)
            // If we suspended here we can just continue to loop because we should now have a new function to run
            // If we did not suspend we short-circuited and are thus done with looping
            if (r != COROUTINE_SUSPENDED) {
              resume(nextShiftOrResult as R) //promise.setResult(r as R)
              break // Break out of the infinite loop if we have a result
            }
          }
        }
      }
    } else return result as R

    // We need to finish the partially evaluated shift blocks by passing them our result.
    // This will update the result via the continuations that now finish up
    val r = getResult() as R
    for (c in shiftFnContinuations.asReversed()) c.resume(r)
    // Return the final result
    return r
  }
}
