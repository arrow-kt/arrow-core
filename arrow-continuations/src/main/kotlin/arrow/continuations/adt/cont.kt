package arrow.continuations.adt

import kotlin.coroutines.suspendCoroutine

typealias Scope<A> = Continuation.Scope<A>
typealias Shift<A, B> = Continuation.Scope<A>.Shift<B>
typealias Invoke<A, B> = Continuation<A, B>.Invoke
typealias ShortCircuit<A> = Continuation<A, *>.ShortCircuit
typealias Intercepted<A> = Continuation.Intercepted<A>
typealias KotlinContinuation<A> = kotlin.coroutines.Continuation<A>

sealed class Continuation<A, B> {
  data class Intercepted<A>(
    val parent: Continuation<*, *>,
    val continuation: KotlinContinuation<A>,
    val prompt: Continuation<*, *>
  ) : Continuation<A, Any?>()
  inner class ShortCircuit(val value: A) : Continuation<A, Any?>()
  inner class Invoke(val value: A) : Continuation<B, A>() {
    val parent: Continuation<A, B> = this@Continuation
  }
  abstract class Scope<A>: Continuation<A, Any?>() {
    abstract val result: A
    inner class Shift<B>(val block: suspend Scope<B>.(Continuation<B, A>) -> A) : Continuation<B, A>() {
      val scope: Scope<A> = this@Scope
    }
  }
}

suspend fun <A, B> Scope<A>.shift(block: suspend Scope<B>.(Continuation<B, A>) -> A): B =
  suspendCoroutine {
    Intercepted(this, it, Shift(block)).compile()
  }

suspend operator fun <A, B> Continuation<A, B>.invoke(value: A): B =
  suspendCoroutine {
    Intercepted(this, it, Invoke(value)).compile()
  }

fun <A, B> Continuation<A, B>.compile(): A =
  when (this) {
    is Shift -> {
      val block: suspend (Continuation.Scope<A>, Continuation<A, B>) -> B = block
      val scope: Continuation.Scope<B> = scope
      TODO()
    }
    is Invoke -> {
      val value: B = value
      val parent: Continuation<B, A> = parent
      TODO()
    }
    is Intercepted -> {
      val parent: Continuation<*, *> = parent
      val continuation: KotlinContinuation<A> = continuation
      val prompt: Continuation<*, *> = prompt
      TODO()
    }
    is Scope -> result
    is ShortCircuit<A> -> value
  }



object ListScope : Scope<List<*>>() {
  override val result: ArrayList<Any?> = arrayListOf()
  suspend operator fun <B> List<B>.invoke(): B =
    shift { cb ->
      this@invoke.flatMap {
        this@ListScope.result.addAll(cb(it))
        this@ListScope.result
      }
    }
}

inline fun <A> list(block: ListScope.() -> A): List<A> =
  listOf(block(ListScope))


suspend fun main() {
  val result = list {
    val a = listOf(1, 2, 3)()
    val b = listOf("a", "b", "c")()
    "$a$b "
  }
  println(result)
}

