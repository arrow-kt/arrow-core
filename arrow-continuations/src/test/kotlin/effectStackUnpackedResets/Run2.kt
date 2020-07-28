package effectStackUnpackedResets

import arrow.continuations.effectStackUnpackedResets.Delimited
import arrow.continuations.effectStackUnpackedResets.reset
import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.core.flatMap
import arrow.core.test.UnitSpec

class Run2 : UnitSpec() {
  init {
    suspend fun <A> Delimited<List<A>>.yield(a: A): Unit = shift { k -> listOf(a) + k(Unit) + k(Unit) }
    "yield" {
      println("PROGRAM: yield")
      reset<List<Int>> {
        yield(1)
        yield(2)
        yield(5)
        emptyList()
      }.also { println("Result: $it") } // should be 1,2,5, 2,5, 5
    }
    "multi" {
      println("PROGRAM: multi")
      reset<Either<String, Int>> fst@{
        val ctx = this
        val i: Int = shift { it(5) }
        // println("HERE")
        val r = i * 2 + reset<Int> snd@{
          val k: Int = shift { it(3) + it(2) }
          // println("There")
          val j: Int = if (i == 5) ctx.shift { it(10).flatMap { i -> it(5).map { i + it } } }
          else shift { it(4) }
          val o: Int = shift { it(1) }
          // println("Those")
          j + k + o
        }
        val l: Int = shift { it(8) }
        Right(r + l)
      }.also { println("PROGRAM: Result $it") }
    }
  }
}
