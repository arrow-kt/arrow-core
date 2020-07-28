package effectStackUnpackedResets

import arrow.continuations.effectStackUnpackedResets.reset
import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.core.flatMap
import arrow.core.test.UnitSpec

class Run2 : UnitSpec() {
  init {
    "multi" {
      println("PROGRAM: multi")
      reset<Either<String, Int>> fst@{
        val ctx = this
        val i: Int = shift { it(5) }
        // println("HERE")
        val r = i * 2 + reset<Int> snd@{
          val k: Int = shift { it(3) }
          // println("There")
          val j: Int = if (i == 5) ctx.shift { it(10).flatMap { i -> it(10).map { i + it } } }
          else shift { it(4) }
          val o: Int = shift { it(1) + it(2) }
          // println("Those")
          j + k + o
        }
        val l: Int = shift { it(8) }
        Right(r + l)
      }.also { println("PROGRAM: Result $it") }
    }
  }
}
