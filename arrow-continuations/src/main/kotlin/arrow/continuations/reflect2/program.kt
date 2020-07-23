package arrow.continuations.reflect2

import arrow.core.ForListK
import arrow.core.ListK
import arrow.core.extensions.listk.monad.monad
import arrow.core.fix
import arrow.core.k

fun main() {
  list {
    val a: Int = listOf(1, 2, 3).invoke()
//        val b: Int = listOf(1, 3, 4).invoke()
    println("I came here $a")
    a
  }.let(::println)
}

inline fun list(crossinline program: suspend Reflect/*<Int>*/.() -> Int): List<Int> =
  reify {
    println("Starting with: $it")
    listOf(program(it))
  }
