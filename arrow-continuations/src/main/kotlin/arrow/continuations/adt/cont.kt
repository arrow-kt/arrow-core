package arrow.continuations.adt

import java.util.*
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine
import kotlin.coroutines.suspendCoroutine

val frames: Stack<Cont<*, *>> = Stack()

sealed class Cont<A, B> {

  class Reset<A>(
    val f: suspend Reset<A>.() -> A
  ) : Cont<A, Any?>() {

    val shifts: Stack<Shift<*>> = Stack()

    inner class Shift<B>(
      val f: suspend Shift<B>.((B) -> B) -> A
    ) : Cont<B, Any?>() {
      init {
        shifts.push(this)
      }
      val parent: Reset<A> = this@Reset
    }

  }
}

suspend inline fun <reified A> reset(
  noinline f: suspend Cont.Reset<*>.() -> A
): A = Cont.Reset(f).yield()

suspend inline fun <reified A, reified B> Cont.Reset<*>.shift(
  noinline f: suspend Cont.Reset<A>.Shift<B>.((B) -> B) -> A
): B {
  this as Cont.Reset<A>
  return Shift(f).yield()
}

tailrec suspend fun <A> Cont<A, *>.yield(): A =
  when (this) {
    is Cont.Reset<A> -> {
      if (shifts.isNotEmpty()) shifts.pop().yield() as A
      else suspendCoroutine<A> { ca ->
        val body = suspend {
          if (frames.isNotEmpty()) frames.pop().yield() as A
          else null
        }
        var res: A? = null
        body.startCoroutine(object : Continuation<A?> {
          override val context: CoroutineContext = EmptyCoroutineContext
          override fun resumeWith(result: Result<A?>) {
            println("Resume shift: ${result}")
            res = result.getOrNull()
          }
        })
        res?.let { ca.resumeWith(Result.success(it)) }
      }
    }
    is Cont.Reset<*>.Shift<A> -> {
      frames as Stack<Cont<A, Any?>>
      this as Cont.Reset<A>.Shift<A>
      val body: suspend () -> A = suspend {
        f { a -> // each bound element when a shift is called within the body
          println("push $a")
          frames.push(Cont.Reset { a })
          a
        }
      }
      var res: A? = null
      body.startCoroutine(object : Continuation<A> {
        override val context: CoroutineContext = EmptyCoroutineContext
        override fun resumeWith(result: Result<A>) {
          println("Resume shift: ${result}")
          res = result.getOrNull()
        }
      })
      res!!
    }
  }

suspend inline operator fun <reified A> Cont.Reset<*>.times(fa: List<A>): A =
  shift<List<A>, A> { cb ->
    fa.flatMap {
      listOf(cb(it))
    }
  }


suspend fun main() {
  val result =
    reset {
      val a: Int = this * listOf(1, 2, 3)
      val b: String = this * listOf("a", "b", "c")
      listOf("$a$b")
    }
  println(result)
}




