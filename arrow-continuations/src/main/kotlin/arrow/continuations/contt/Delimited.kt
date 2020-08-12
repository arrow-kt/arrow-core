package arrow.continuations.conts

import arrow.core.ShortCircuit // TODO
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.loop
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.startCoroutineUninterceptedOrReturn
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.resumeWithException

fun <A> reset(prompt: DelimitedScope<A>): A =
  ContinuationState.reset(prompt) {
    val result = prompt.body(prompt)
    result as A
  }

interface DelimitedContinuation<A, B> : Continuation<A> {
  fun invokeWith(value: Result<B>): A
  fun isDone(): Boolean
  fun run(): Unit
  suspend fun yield(): Unit
}

interface DelimitedScope<A> : DelimitedContinuation<A, Any?> {
  fun ShortCircuit.recover(): A = throw this
  suspend fun <B> shift(block: suspend (DelimitedScope<B>) -> A): B =
    ContinuationState.shift(this, block)

  fun startCoroutineUninterceptedOrReturn(): Any?
  fun startCoroutineUninterceptedOrReturn(body: suspend DelimitedScope<A>.() -> A): Any?

  val body: suspend DelimitedScope<A>.() -> A
}

open class DelimitedScopeImpl<A>(open val prompt: Continuation<A>, override val body: suspend DelimitedScope<A>.() -> A) : DelimitedScope<A> {

  /**
   * State is either
   *  0 - UNDECIDED
   *  1 - SUSPENDED
   *  Any? (3) `resumeWith` always stores it upon UNDECIDED, and `getResult` can atomically get it.
   */
  private val _decision = atomic<Any?>(UNDECIDED)

  override val context: CoroutineContext = EmptyCoroutineContext

  override fun resumeWith(result: Result<A>) {
    _decision.loop { decision ->
        when (decision) {
          UNDECIDED -> {
            val r: A? = when {
              result.isFailure -> {
                val e = result.exceptionOrNull()
                if (e is ShortCircuit) e.recover() else null
              }
              result.isSuccess -> result.getOrNull()
              else -> TODO("Impossible bug")
            }

            when {
              r == null -> {
                prompt.resumeWithException(result.exceptionOrNull()!!)
                return
              }
              _decision.compareAndSet(UNDECIDED, r) -> return
              else -> Unit // loop again
            }
          }
          else -> { // If not `UNDECIDED` then we need to pass result to `parent`
            val res: Result<A> = result.fold({ Result.success(it) }, { t ->
              if (t is ShortCircuit) Result.success(t.recover())
              else Result.failure(t)
            })
            prompt.resumeWith(res)
            return
          }
        }
    }
  }

  override fun invokeWith(value: Result<Any?>): A =
    value.getOrThrow() as A

  override fun isDone(): Boolean =
    _decision.loop { decision ->
      return when (decision) {
        UNDECIDED -> false
        SUSPENDED -> false
        COROUTINE_SUSPENDED -> false
        else -> true
      }
    }

  override fun startCoroutineUninterceptedOrReturn(body: suspend DelimitedScope<A>.() -> A): Any? =
    try {
      body.startCoroutineUninterceptedOrReturn(this, this)?.let {
        if (it == COROUTINE_SUSPENDED) getResult()
        else it
      }
    } catch (e: Throwable) {
      if (e is ShortCircuit) e.recover()
      else throw e
    }

  override fun startCoroutineUninterceptedOrReturn(): Any? =
    startCoroutineUninterceptedOrReturn(body)

  override suspend fun yield() {
    suspendCoroutineUninterceptedOrReturn<Unit> {
      if (isDone()) getResult()
      else COROUTINE_SUSPENDED
    }
  }

  override fun run(): Unit {
    val result = try {
      body.startCoroutineUninterceptedOrReturn(this, this)?.let {
        if (it == COROUTINE_SUSPENDED) getResult()
        else it
      }
    } catch (e: Throwable) {
      if (e is ShortCircuit) e.recover()
      else throw e
    }
    _decision.getAndSet(result)
  }

  @PublishedApi // return the result
  internal fun getResult(): Any? =
    _decision.loop { decision ->
      when (decision) {
        UNDECIDED -> if (this._decision.compareAndSet(UNDECIDED, SUSPENDED)) return COROUTINE_SUSPENDED
        else -> return decision
      }
    }

  companion object {
    internal const val UNDECIDED = 0
    internal const val SUSPENDED = 1
  }
}

class ListComputation<A>(
  continuation: Continuation<List<A>>,
  f: suspend ListComputation<*>.() -> A
) : DelimitedScopeImpl<List<A>>(continuation, {
  val result = f(this as ListComputation<List<A>>)
  listOf(result)
}) {

  var result: List<A> = emptyList()

  suspend operator fun <B> List<B>.invoke(): B =
    shift { cont ->
      result = flatMap {
        cont.invokeWith(Result.success(it))
        reset(this@ListComputation)
      }
      result
    }

}

suspend fun <A> list(f: suspend ListComputation<*>.() -> A): List<A> =
  suspendCoroutineUninterceptedOrReturn {
    reset(ListComputation(it, f))
  }


suspend fun main() {
  val result = list {
    val a = listOf(1, 2, 3)()
    val b = listOf(1f, 2f, 3f)()
    a + b
  }
  println(result)
}
