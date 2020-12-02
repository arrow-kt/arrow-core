package arrow.continuations.generic

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.loop
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.startCoroutineUninterceptedOrReturn
import kotlin.coroutines.resumeWithException

interface SuspendingComputation<R> {
  suspend fun <A> shift(a: R): A
}

internal const val UNDECIDED = 0
internal const val SUSPENDED = 1

class ShortCircuit internal constructor(internal val token: Token, val raiseValue: Any?) : ControlThrowable()

/** Represents a unique identifier using object equality. */
internal class Token {
  override fun toString(): String = "Token(${Integer.toHexString(hashCode())})"
}

@Suppress("UNCHECKED_CAST")
internal class SuspendMonadContinuation<R>(private val parent: Continuation<R>) : Continuation<R>, SuspendingComputation<R> {

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
            _decision.compareAndSet(UNDECIDED, r) -> return
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

  fun startCoroutineUninterceptedOrReturn(f: suspend SuspendMonadContinuation<R>.() -> R): Any? =
    try {
      f.startCoroutineUninterceptedOrReturn(this, this)?.let {
        if (it == COROUTINE_SUSPENDED) getResult()
        else it
      }
    } catch (e: Throwable) {
      if (e is ShortCircuit && e.token === token) e.raiseValue as R
      else throw e
    }
}
