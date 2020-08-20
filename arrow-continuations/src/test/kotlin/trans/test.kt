package trans

import arrow.core.Eval
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import io.kotlintest.specs.StringSpec

class TransTest : StringSpec({
  "can trans" {
    val result: Eval<Option<Int>> = evalOption {
      val evalValue: Int = Eval.now(1)()
      val evalOptionValue: Int = Eval.now(None)()
      val optionValue: Int = Some(1)()
      val x = 1.just()
      val justed = x()
      evalValue + evalOptionValue + optionValue + justed
    }
    println(result)
  }
})

