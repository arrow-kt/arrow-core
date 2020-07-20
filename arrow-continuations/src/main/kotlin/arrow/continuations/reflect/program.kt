package arrow.continuations.reflect

import arrow.core.ForListK
import arrow.core.ListK
import arrow.core.extensions.listk.monad.monad
import arrow.core.k

fun main() {
  val result = list {
    val a: Int = listOf(1, 2, 3).k()()
    val b: String = listOf("a", "b", "c").k()()
    "$a$b"
  }
  println(result)
}

fun <A> list(program: suspend Reflect<List<*>>.() -> A): List<A> =
  listOf(reify<ForListK>()(ListK.monad()) {
    program(it as Reflect<List<*>>)
  })
