package arrow.core

import arrow.core.test.UnitSpec
import io.kotlintest.shouldBe

@Suppress("RedundantSuspendModifier")
class NullableTest : UnitSpec() {

  init {
    "fail" {
      true shouldBe false
//      val exception = Exception("My Exception")
//      val result: Validated<Throwable, String> = Invalid(exception)
//      result.fold(
//        { e -> e.message + " Checked" },
//        { fail("Some should not be called") }
//      ) shouldBe "My Exception Checked"
    }
  }
}
