package arrow.core

import arrow.core.extensions.AndMonoid
import arrow.core.extensions.eq
import arrow.core.test.UnitSpec
import arrow.core.test.laws.MonoidLaws
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bool

class BooleanTest : UnitSpec() {
  init {
    testLaws(
      MonoidLaws.laws(AndMonoid, Arb.bool(), Boolean.eq())
    )
  }
}
