package arrow.core.computations

import arrow.core.test.UnitSpec
import io.kotlintest.shouldBe

class NullableTest : UnitSpec() {

  init {
    "multiple types" {
      nullable {
          val number = "s".length.bind()
          val string = number.toString().bind()
          string
      } shouldBe "1"
    }
    "short circuit" {
      nullable<String> {
          val number: Int = "s".length.bind()
          number.takeIf { it > 1 }?.toString().bind()
          throw IllegalStateException("This should not be executed")
      } shouldBe null
    }
  }
}
