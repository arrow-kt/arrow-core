package arrow.continuations.contxx

import arrow.core.ShortCircuit
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.loop
import java.util.*
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.RestrictsSuspension
import kotlin.coroutines.intrinsics.*

class DelimitedContinuation<A>(val prompt: Prompt<A>, val f: suspend () -> A) : Continuation<A> {

  override val context: CoroutineContext = EmptyCoroutineContext

  fun ShortCircuit.recover(): A = throw this

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
            else -> throw TODO("Impossible")
          }

          when {
            r == null -> {
              throw result.exceptionOrNull()!!
              //resumeWithException(result.exceptionOrNull()!!)
              return
            }
            _decision.compareAndSet(UNDECIDED, Completed(r)) -> return
            else -> Unit // loop again
          }
        }
        else -> { // If not `UNDECIDED` then we need to pass result to `parent`
          val res: Result<A> = result.fold({ Result.success(it) }, { t ->
            if (t is ShortCircuit) Result.success(t.recover())
            else Result.failure(t)
          })
          _decision.getAndSet(Completed(res.getOrThrow()))
          return
        }
      }
    }
  }

  private val _decision = atomic<DelimitedContinuationScope>(UNDECIDED)

  fun isDone(): Boolean =
    _decision.value is Completed<*>

  fun run(): Unit {
    f.startCoroutineUninterceptedOrReturn(this)
    _decision.loop { decision ->
      when (decision) {
        UNDECIDED -> if (this._decision.compareAndSet(UNDECIDED, prompt)) Unit //loop again
        else -> return@run
      }
    }
  }

  companion object {
    suspend fun <A> yield(p: Prompt<A>) {
      suspendCoroutineUninterceptedOrReturn<DelimitedContinuationScope> {
        it.resumeWith(Result.success(p))
        COROUTINE_SUSPENDED
      }
    }
  }
}

suspend fun <A> reset(prompt: Prompt<A>, f: suspend () -> A): A {
  val k: DelimitedContinuation<*> = DelimitedContinuation<Any?>(prompt) {
    DelimCC.result = f()
    DelimCC.result
  }
  return DelimCC.runCont(k)
}

suspend fun <A, R> shift(prompt: Prompt<R>, body: CPS<A, R>): A {
  DelimCC.body = body
  DelimitedContinuation.yield(prompt)
  return DelimCC.arg as A
}

// multiprompt delimited continuations in terms of the current API
// this implementation has Felleisen classification -F+
object DelimCC {
  internal var result: Any? = null
  internal var arg: Any? = null
  internal var body: CPS<*, *>? = null

  internal suspend fun <A> runCont(k: DelimitedContinuation<*>): A {
    k.run()
    val frames = Stack<DelimitedContinuation<*>>()
    while (!k.isDone()) {

      // IDEA:
      //   1) Push a separate (one-time) prompt on `shift`.
      //   2) On resume, capture the continuation on a heap allocated
      //      stack of `DelimitedContinuation`s
      //   3) Trampoline those continuations at the position of the original
      //      `reset`.
      //
      // This only works since continuations are one-shot. Otherwise the
      // captured frames would contain references to the continuation and
      // would be evaluated out of scope.
      val bodyPrompt: Prompt<Any?> = Prompt()
      val bodyCont: DelimitedContinuation<*> =
        DelimitedContinuation<Any?>(bodyPrompt) {
          result = (body as CPS<Any?, Any?>).invoke(Cont { value ->
            // yield and wait until the subcontinuation has been
            // evaluated.
            arg = value
            // yielding here returns control to the outer continuation
            DelimitedContinuation.yield(bodyPrompt)
            result
          })
          body = null
          body
        }
      bodyCont.run() // start it

      // continuation was called within body
      if (!bodyCont.isDone()) {
        frames.push(bodyCont)
        k.run()

        // continuation was discarded or escaped the dynamic scope of
        // bodyCont.
      } else {
        break
      }
    }
    while (!frames.isEmpty()) {
      val frame = frames.pop()
      if (!frame.isDone()) {
        frame.run()
      }
    }
    return result as A
  }
}

interface DelimitedContinuationScope
class Prompt<out A> : DelimitedContinuationScope
class Completed<out A>(val value: A) : DelimitedContinuationScope
object UNDECIDED : DelimitedContinuationScope

class Cont<A, R>(val f: suspend (A) -> R) {
  suspend operator fun invoke(p1: A): R = f(p1)
}

class CPS<A, R>(val f: suspend (Cont<A, R>) -> R) {
  suspend operator fun invoke(p1: Cont<A, R>): R = f(p1)
}

suspend fun test1() {
  val p1 = Prompt<Int>()
  val p2 = Prompt<Int>()
  val res1 = reset(p1) { 5 - shift(p1, CPS { k: Cont<Int, Int> -> k(2) * 7 }) }
  val res2 = reset(p1) {
    (1 + shift(p1, CPS { k: Cont<Int, Int> -> k(2) })
      + shift(p1, CPS { k: Cont<Int, Int> -> k(3) }))
  }
  val res3 = reset(p1) {
    (1 + shift(p1, CPS { k: Cont<Int, Int> -> k(2) })
      + reset(p2) {
      (2 + shift(p2, CPS { k: Cont<Int, Int> -> k(3) * 3 })
        + shift(p1, CPS { k: Cont<Int, Int> -> k(4) * 2 }))
    })
  }
  val res4 = reset(p1) {
    (1 + shift(p1, CPS { k: Cont<Int, Int> -> k(2) })
      + reset(p2) {
      (2 + shift(p2, CPS { k: Cont<Int, Int> -> k(3) * 3 })
        + shift(p1, CPS { k: Cont<Int, Int> -> 42 }))
    })
  }
  println(res1) // 21
  println(res2) // 6
  println(res3) // 60
  println(res4) // 42
}

suspend fun test2() {
  val p1 = Prompt<Int>()
  val res = reset(p1) {
    var n = 10000
    var r = 0
    while (n > 0) {
      r += shift(p1, CPS { k: Cont<Int, Int> -> k(1) })
      n--
    }
    r
  }
  println(res)
}

fun main() {
    suspend {
      DelimCC.run {
        test1()
        test2()
      }
    }.startCoroutineUninterceptedOrReturn(DelimitedContinuation(Prompt()){})
}
