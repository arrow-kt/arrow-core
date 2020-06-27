package arrow.continuations

import arrow.core.ArrowCoreInternalException
import arrow.core.ShortCircuit
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.loop
import kotlin.coroutines.jvm.internal.*
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.createCoroutine
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.startCoroutineUninterceptedOrReturn
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.startCoroutine
import kotlin.coroutines.suspendCoroutine
import kotlin.experimental.ExperimentalTypeInference


internal const val UNDECIDED = 0
internal const val SUSPENDED = 1

private object NoOpContinuation : Continuation<Any?> {
  override val context: CoroutineContext = EmptyCoroutineContext

  override fun resumeWith(result: Result<Any?>) {
    // Nothing
  }
}

@Suppress(
  "CANNOT_OVERRIDE_INVISIBLE_MEMBER",
  "INVISIBLE_MEMBER",
  "INVISIBLE_REFERENCE",
  "UNCHECKED_CAST",
  "EXPOSED_SUPER_CLASS"
)
abstract class DelimitedContinuation<A> : ContinuationImpl(NoOpContinuation, EmptyCoroutineContext) {

  abstract val parent: Continuation<Any?>

  fun ShortCircuit.recover(): A =
    throw this

  /**
   * State is either
   *  0 - UNDECIDED
   *  1 - SUSPENDED
   *  Any? (3) `resumeWith` always stores it upon UNDECIDED, and `getResult` can atomically get it.
   */
  private val _decision = atomic<Any>(UNDECIDED)

  override val context: CoroutineContext = EmptyCoroutineContext

  fun resumeWithX(result: Result<Any?>) {
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
          val res: Result<Any?> = result.fold({ Result.success(it) }, { t ->
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

  fun <C : DelimitedContinuation<A>> C.start(f: suspend C.() -> A): Any? =
    try {
      f.startCoroutineUninterceptedOrReturn(this, this)?.let {
        if (it == COROUTINE_SUSPENDED) getResult()
        else it
      }
    } catch (e: Throwable) {
      if (e is ShortCircuit) e.recover()
      else throw e
    }

  override fun invokeSuspend(result: Result<Any?>): Any? =
    resumeWithX(result).also {
      println("invokeSuspend: $this $result")
    }


  // Escalate visibility to manually release intercepted continuation
  override fun releaseIntercepted() {
    super.releaseIntercepted().also {
      println("releaseIntercepted $this")
    }
  }

  override val callerFrame: CoroutineStackFrame?
    get() = super.callerFrame.also {
      println("callerFrame $it")
    }

  override fun create(value: Any?, completion: Continuation<*>): Continuation<Unit> {
    return super.create(value, completion).also {
      println("create: $value completion: $completion , created: $it")
    }
  }

  override fun create(completion: Continuation<*>): Continuation<Unit> {
    return super.create(completion).also {
      println("create: $completion , created: $it")
    }
  }

  override fun getStackTraceElement(): StackTraceElement? {
    return super.getStackTraceElement().also {
      println("getStackTraceElement: $it")
    }
  }

}

class NonDeterministicContinuation<A> : DelimitedContinuation<A>() {
  override val parent: Continuation<Any?> = this
}

operator fun <A> List<A>.invoke(): A =
  iterator().next()

@UseExperimental(ExperimentalTypeInference::class)
@BuilderInference
suspend fun <A, B : List<A>> list(f: suspend DelimitedContinuation<A>.() -> A): B =
  suspendCoroutineUninterceptedOrReturn { c ->
    val ncont = NonDeterministicContinuation<A>()
    f.startCoroutine(ncont, ncont)
    when (val result = ncont.getResult()) {
      COROUTINE_SUSPENDED -> COROUTINE_SUSPENDED
      else -> c.resumeWith(result as Result<B>)
    }
  }

suspend fun main() {
  println("1. suspend main")
  val result : List<Int> =
    list {
      println("2. Before list bind")
      val a = listOf(1, 2, 3)()
      println("*. After list bind")
      val result = a + 1
      result
    }
  println(result)
}
