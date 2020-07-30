package arrow.continuations.generic

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.loop
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.startCoroutineUninterceptedOrReturn
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * Implements delimited continuations with with single shot mode.
 *
 * This is basically only there to show how the run loop works
 */
class SingleShotDelimContScope<R>(val f: suspend DelimitedScope<R>.() -> R): RunnableDelimitedScope<R> {

  private val resultVar = atomic<R?>(null)
  private val nextShift = atomic<(suspend () -> R)?>(null)
  // TODO This can be append only, but needs fast reversed access
  private val shiftFnContinuations = mutableListOf<Continuation<R>>()

  data class SingleShotCont<A, R>(
    private val continuation: Continuation<A>,
    private val shiftFnContinuations: MutableList<Continuation<R>>
  ) : DelimitedContinuation<A, R> {
    override suspend fun invoke(a: A): R = suspendCoroutine { resumeShift ->
      shiftFnContinuations.add(resumeShift)
      continuation.resume(a)
    }
  }

  override suspend fun <A> shift(func: suspend (DelimitedContinuation<A, R>) -> R): A =
    suspendCoroutine { continueMain ->
      val delCont = SingleShotCont(continueMain, shiftFnContinuations)
      assert(nextShift.compareAndSet(null, suspend { func(delCont) }))
    }

  override fun invoke(): R {
    f.startCoroutineUninterceptedOrReturn(this, Continuation(EmptyCoroutineContext) { result ->
      resultVar.value = result.getOrThrow()
    }).let {
      if (it == COROUTINE_SUSPENDED) {
        resultVar.loop { mRes ->
          if (mRes == null) {
            val nextShiftFn = nextShift.getAndSet(null)
              ?: throw IllegalStateException("No further work to do but also no result!")
            nextShiftFn.startCoroutineUninterceptedOrReturn(Continuation(EmptyCoroutineContext) { result ->
              resultVar.value = result.getOrThrow()
            }).let {
              if (it != COROUTINE_SUSPENDED) resultVar.value = it as R
            }
          } else return@let
        }
      }
      else return@invoke it as R
    }
    assert(resultVar.value != null)
    for (c in shiftFnContinuations.asReversed()) c.resume(resultVar.value!!)
    return resultVar.value!!
  }

  companion object {
    fun <R> reset(f: suspend DelimitedScope<R>.() -> R): R = SingleShotDelimContScope(f).invoke()
  }
}
