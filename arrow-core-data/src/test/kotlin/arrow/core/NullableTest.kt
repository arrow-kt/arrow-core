package arrow.core

import arrow.core.test.UnitSpec
import io.kotlintest.shouldBe

@Suppress("RedundantSuspendModifier")
class NullableTest : UnitSpec() {

  init {
    "map2 should return null if a is null" {
      map2<String, String, String>(null, "b") { a, b -> a + b } shouldBe null
    }

    "map2 should return null if b is null" {
      map2<String, String, String>("a", null) { a, b -> a + b } shouldBe null
    }

    "map2 should return null if both a and b are null" {
      map2<Int, Int, Int>(null, null) { a, b -> a + b } shouldBe null
    }

    "map2 should return R if both a and b are not null" {
      map2(1, 2) { a, b -> a + b } shouldBe 3
    }
  }
}
