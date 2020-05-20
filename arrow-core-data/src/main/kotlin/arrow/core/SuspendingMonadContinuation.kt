package arrow.core

import arrow.Kind
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.loop
import java.lang.RuntimeException
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.resumeWithException

internal const val UNDECIDED = 0
internal const val SUSPENDED = 1

@Suppress("UNCHECKED_CAST")
abstract class SuspendMonadContinuation<F, A>(private val parent: Continuation<Kind<F, A>>) : Continuation<Kind<F, A>> {

  class ShortCircuit(val e: Any?) : Throwable(message = null, cause = null) {
    override fun fillInStackTrace(): Throwable = this
  }

  abstract operator fun <A> Kind<F, A>.not(): A

  abstract fun ShortCircuit.recover(): Kind<F, A>

  /**
   * State is either
   *  0 - UNDECIDED
   *  1 - SUSPENDED
   *  Any? (3) `resumeWith` always stores it upon UNDECIDED, and `getResult` can atomically get it.
   */
  private val _decision = atomic<Any>(UNDECIDED)

  override val context: CoroutineContext = EmptyCoroutineContext

  override fun resumeWith(result: Result<Kind<F, A>>) {
    _decision.loop { decision ->
      when (decision) {
        UNDECIDED -> {
          val r: Kind<F, A>? = when {
            result.isFailure -> {
              val e = result.exceptionOrNull()
              if (e is ShortCircuit) e.recover() else null
            }
            result.isSuccess -> result.getOrNull()
            else -> throw RuntimeException("IMPOSSIBLE")
          }

          println("r: $r, exception: ${result.exceptionOrNull()}")

          when {
            r == null -> {
              parent.resumeWithException(result.exceptionOrNull()!!)
              return
            }
            _decision.compareAndSet(UNDECIDED, r) -> return
            else -> Unit // loop again
          }
        }
        else -> { // If not `UNDECIDED` then wwe need to pass result to `parent`
          val res: Result<Kind<F, A>> = result.fold({ Result.success(it) }, { t ->
            if (t is ShortCircuit) Result.success(t.recover())
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
}
