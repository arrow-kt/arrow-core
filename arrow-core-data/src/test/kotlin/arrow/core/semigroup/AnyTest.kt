package arrow.core.semigroup

import arrow.core.extensions.semigroup.any.eq.eq
import arrow.core.extensions.semigroup.any.hash.hash
import arrow.core.extensions.semigroup.any.monoid.monoid
import arrow.core.extensions.semigroup.any.show.show
import arrow.core.test.UnitSpec
import arrow.core.test.generators.any
import arrow.core.test.laws.EqLaws
import arrow.core.test.laws.HashLaws
import arrow.core.test.laws.MonoidLaws
import arrow.core.test.laws.ShowLaws
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

class AnyTest : UnitSpec() {
  init {
    testLaws(
      EqLaws.laws(Any.eq(), Gen.any()),
      ShowLaws.laws(Any.show(), Any.eq(), Gen.any()),
      HashLaws.laws(Any.hash(), Gen.any(), Any.eq()),
      MonoidLaws.laws(Any.monoid(), Gen.any(), Any.eq())
    )

    // This alone is enough because the monoid laws cover Any other useful cases
    "Any combines elements using ||" {
      forAll { b1: Boolean, b2: Boolean ->
        (b1 || b2) == Any.monoid().run { Any(b1) + Any(b2) }.getAny
      }
    }
  }
}
