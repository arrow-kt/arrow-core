package arrow.continuations.reflect

import arrow.core.ShortCircuit
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.loop
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.RestrictsSuspension
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.startCoroutineUninterceptedOrReturn
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.resume

interface Prompt<A, B> {
  suspend fun suspend(value: A): B
}

internal const val UNDECIDED = 0
internal const val SUSPENDED = 1

class Coroutine<A, B, C>(prog: suspend (Prompt<A, B>) -> C) {

  private val _decision = atomic<Any>(UNDECIDED)

  fun isDone() = continuation.isDone()

  fun value(): A {
    assert(!isDone()); return receive()
  }

  fun result(): C {
    assert(isDone()); return receive()
  }

  fun yield(v: B) {
    assert(!isDone())
    send(v)
    continuation.run()
  }

  private var channel: Any? = null
  private fun send(v: Any?) {
    channel = v
  }

  private fun <A> receive(): A {
    val v = channel
    return v as A
  }

  open inner class InnerPrompt : Prompt<A, B> /*ContinuationScope("cats-reflect")*/ {
    override suspend fun suspend(value: A): B {
      send(value)
      return suspendCoroutineUninterceptedOrReturn { cont ->
        println("Put value in channel: $value in $channel")
        val res = receive<B>()
        if (isDone()) cont.resumeWith(Result.success(res))
        COROUTINE_SUSPENDED
      }
    }
  }

  val prompt = InnerPrompt()

  @RestrictsSuspension
  inner class Continuation(
    val f: suspend () -> Unit
  ) : kotlin.coroutines.Continuation<C> {

    override val context: CoroutineContext = EmptyCoroutineContext

    var started = false

    fun run(): Unit {
      continuation.startCoroutineUninterceptedOrReturn {
        if (!started) {
          f()
          started = true
        }
        suspendCoroutineUninterceptedOrReturn {
          while (!isDone()) {
          }
          getResult()
        }
      }
    }

    override fun resumeWith(result: Result<C>) {
      _decision.loop { decision ->
        when (decision) {
          UNDECIDED -> {
            val r: C? = when {
              result.isFailure -> {
                val e = result.exceptionOrNull()
                if (e is ShortCircuit) throw e else null
              }
              result.isSuccess -> result.getOrNull()
              else -> TODO("Bug?")
            }

            when {
              r == null -> {
                println("resumeWith: result = $result")
                throw result.exceptionOrNull()!!
//                parent.resumeWithException(result.exceptionOrNull()!!)
//                return
              }
              _decision.compareAndSet(UNDECIDED, r) -> return
              else -> Unit // loop again
            }
          }
          else -> { // If not `UNDECIDED` then we need to pass result to `parent`
            val res: Result<C> = result.fold({ Result.success(it) }, { t ->
              if (t is ShortCircuit) throw t // Result.success(t.recover())
              else Result.failure(t)
            })
            send(res.getOrThrow())
            // parent.resumeWith(res)
            return
          }
        }
      }
    }

    @PublishedApi // return the result
    internal fun getResult(): Any? =
      _decision.loop { decision ->
        when (decision) {
          UNDECIDED -> if (_decision.compareAndSet(UNDECIDED, SUSPENDED)) return COROUTINE_SUSPENDED
          else -> return decision
        }
      }

    fun startCoroutineUninterceptedOrReturn(f: suspend () -> C): Any? =
      try {
        f.startCoroutineUninterceptedOrReturn(this)?.let {
          if (it == COROUTINE_SUSPENDED) getResult()
          else it
        }
      } catch (e: Throwable) {
        if (e is ShortCircuit) throw e // e.recover()
        else throw e
      }

    fun isDone(): Boolean =
      _decision.loop {
        return when (it) {
          UNDECIDED -> false
          SUSPENDED -> false
          else -> true
        }
      }
  }

  val continuation = Continuation { send(prog(prompt)) }

  init {
    continuation.run()
  }
}
