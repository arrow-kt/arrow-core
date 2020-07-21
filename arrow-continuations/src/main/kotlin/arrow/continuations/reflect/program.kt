package arrow.continuations.reflect

import arrow.core.ForListK
import arrow.core.ListK
import arrow.core.extensions.listk.monad.monad
import arrow.core.fix
import arrow.core.k

fun main() {
//  val result = list {
//    val a: Int = listOf(1, 2, 3).k()()
//    val b: String = listOf("a", "b", "c").k()()
//    "$a$b"
//  }
//  println(result)
  list {
    val a: Int = listOf(1, 2, 3).k()()
    val b: Int = listOf(1, 3, 4).k()()
    println("I came here $a$b")
    a + b
  }.let(::println)
}

inline fun list(crossinline program: suspend Reflect<ForListK, Int>.() -> Int): List<Int> {
  val a = reify<ForListK, Int>(ListK.monad()) {
    println("Starting with: $it")
    program(it)
  }

  return a.fix()
}
