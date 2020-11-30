package generic

import arrow.continuations.generic.DelimContScope
import arrow.continuations.generic.DelimitedScope
import arrow.core.Either
import arrow.core.Left
import arrow.fx.coroutines.milliseconds
import arrow.fx.coroutines.sleep
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class DelimContScopeTest : StringSpec({

  "Immediate return to reset" {
    DelimContScope.reset<Int> { 1 } shouldBe 1
  }

  "Immediate return to reset with suspension" {
    DelimContScope.reset<Int> {
      sleep(10.milliseconds)
      1
    } shouldBe 1
  }

  "short circuit with shift" {
    DelimContScope.reset<Either<String, Int>> {
      val no: Int = shift { Left("No thank you") }
      throw IllegalStateException("This should not be executed")
    } shouldBe Left("No thank you")
  }

  "short circuit with shift and suspension before shift" {
    DelimContScope.reset<Either<String, Int>> {
      sleep(10.milliseconds)
      val no: Int = shift { Left("No thank you") }
      throw IllegalStateException("This should not be executed")
    } shouldBe Left("No thank you")
  }

  "short circuit with shift and suspension in shift" {
    DelimContScope.reset<Either<String, Int>> {
      val no: Int = shift {
        sleep(10.milliseconds)
        Left("No thank you")
      }
      throw IllegalStateException("This should not be executed")
    } shouldBe Left("No thank you")
  }

  "yield a list (also verifies stacksafety)" {
    suspend fun <A> DelimitedScope<List<A>>.yield(a: A): Unit =
      shift { k -> listOf(a) + k(Unit) }

    DelimContScope.reset<List<Int>> {
      for (i in 0..10_000) yield(i)
      emptyList()
    } shouldBe (0..10_000).toList()
  }

})
