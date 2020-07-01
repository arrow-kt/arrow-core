package arrow.continuations

import arrow.core.ArrowCoreInternalException
import arrow.core.Either
import arrow.core.ShortCircuit
import arrow.core.left
import arrow.core.right
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.loop
import me.eugeniomarletti.kotlin.metadata.shadow.utils.addToStdlib.cast
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.startCoroutineUninterceptedOrReturn
import kotlin.coroutines.resumeWithException
import kotlin.experimental.ExperimentalTypeInference

interface Computation<out A>

internal const val UNDECIDED = 0
internal const val SUSPENDED = 1

sealed class ContState<F, A>(val parent: Continuation<*>) : Continuation<F>, Computation<F> {

  @Suppress("UNCHECKED_CAST")
  abstract fun ShortCircuit.recover(): F

  override fun resumeWith(result: Result<F>) {
    _decision.loop { decision ->
      when (decision) {
        UNDECIDED -> {
          val r: Any? = when {
            result.isFailure -> {
              val e = result.exceptionOrNull()
              if (e is ShortCircuit) e.recover() else null
            }
            result.isSuccess -> result.getOrNull()
            else -> throw ArrowCoreInternalException
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
          val res: Result<F> = result.fold({ Result.success(it) }, { t ->
            if (t is ShortCircuit) Result.success(t.recover())
            else Result.failure(t)
          })
          parent.resumeWith(res.cast())
          return
        }
      }
    }
  }

  internal fun getResult(): Any? =
    _decision.loop { decision ->
      when (decision) {
        UNDECIDED -> if (this._decision.compareAndSet(UNDECIDED, SUSPENDED)) return COROUTINE_SUSPENDED
        else -> return decision
      }
    }

  private val _decision = atomic<Any>(UNDECIDED)

  override val context: CoroutineContext = EmptyCoroutineContext
}

@UseExperimental(ExperimentalTypeInference::class)
@Suppress("UNCHECKED_CAST")
sealed class Cont<F, A>(parent: Continuation<*>) : ContState<F, A>(parent) {

  abstract suspend fun <A> A.just(): F

  abstract class Strict<F, A>(parent: Continuation<*>) : Cont<F, A>(parent) {
    @UseExperimental(ExperimentalTypeInference::class)
    @BuilderInference
    @Suppress("UNCHECKED_CAST")
    open fun <C: Computation<F>> strict(computation: suspend C.() -> A): Any? =
      start { computation(this as C).just() }
  }
  abstract class Suspend<F, A>(parent: Continuation<*>) : Cont<F, A>(parent)
  abstract class NonDeterministic<F, A>(parent: Continuation<*>) : Cont<F, A>(parent)
  abstract class Interleaved(parent: Continuation<*>, val ctxs: Iterable<Cont<*, *>>) : Cont<Any?, Any?>(parent)

  @UseExperimental(ExperimentalTypeInference::class)
  @BuilderInference
  fun start(@BuilderInference f: suspend () -> F): Any? =
    try {
      f.startCoroutineUninterceptedOrReturn(this)?.let {
        if (it === COROUTINE_SUSPENDED) getResult()
        else it
      }
    } catch (e: Throwable) {
      if (e is ShortCircuit) e.recover()
      else throw e
    }
}

