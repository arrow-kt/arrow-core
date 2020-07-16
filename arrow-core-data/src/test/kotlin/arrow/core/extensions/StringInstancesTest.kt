package arrow.core.extensions

import arrow.core.test.UnitSpec
import arrow.core.test.laws.HashLaws
import arrow.core.test.laws.ShowLaws
import io.kotest.property.Arb
import io.kotest.property.arbitrary.string

class StringInstancesTest : UnitSpec() {
  init {
    testLaws(
      ShowLaws.laws(String.show(), String.eq(), Arb.string()),
      HashLaws.laws(String.hash(), Arb.string(), String.eq())
    )
  }
}
