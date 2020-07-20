package arrow.continuations.reflect

import arrow.core.ListK
import arrow.core.extensions.listk.monad.monad
import arrow.core.k

fun main() {
//  val result = list {
//    val a: Int = listOf(1, 2, 3).k()()
//    val b: String = listOf("a", "b", "c").k()()
//    "$a$b"
//  }
//  println(result)
  list {
    val a: Int = listOf(1).k().invoke()
    println("I came here $a")
    "$a"
  }.let(::println)
}

fun <A> list(program: suspend Reflect<List<*>>.() -> A): List<A> {
  val a = reify(ListK.monad()) {
    println("Starting with: $it")
    program(it as Reflect<List<*>>)
  }

  return listOf(a)
}
