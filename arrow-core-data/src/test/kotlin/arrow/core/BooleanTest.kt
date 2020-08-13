package arrow.core

import arrow.core.extensions.AndMonoid
import arrow.core.extensions.eq
import arrow.core.extensions.hash
import arrow.core.extensions.order
import arrow.core.extensions.show
import arrow.core.test.UnitSpec
import arrow.core.test.laws.EqLaws
import arrow.core.test.laws.HashLaws
import arrow.core.test.laws.MonoidLaws
import arrow.core.test.laws.OrderLaws
import arrow.core.test.laws.ShowLaws
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bool

class BooleanTest : UnitSpec() {
  init {
    testLaws(
      MonoidLaws.laws(AndMonoid, Arb.bool(), Boolean.eq()),
      EqLaws.laws(Boolean.eq(), Arb.bool()),
      ShowLaws.laws(Boolean.show(), Boolean.eq(), Arb.bool()),
      HashLaws.laws(Boolean.hash(), Arb.bool(), Boolean.eq()),
      OrderLaws.laws(Boolean.order(), Arb.bool())
    )
  }
}
