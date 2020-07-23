package arrow.core.semigroup

import arrow.core.extensions.semigroup.all.eq.eq
import arrow.core.extensions.semigroup.all.hash.hash
import arrow.core.extensions.semigroup.all.monoid.monoid
import arrow.core.extensions.semigroup.all.show.show
import arrow.core.test.UnitSpec
import arrow.core.test.generators.all
import arrow.core.test.laws.EqLaws
import arrow.core.test.laws.HashLaws
import arrow.core.test.laws.MonoidLaws
import arrow.core.test.laws.ShowLaws
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

class AllTest : UnitSpec() {
  init {
    testLaws(
      EqLaws.laws(All.eq(), Gen.all()),
      ShowLaws.laws(All.show(), All.eq(), Gen.all()),
      HashLaws.laws(All.hash(), Gen.all(), All.eq()),
      MonoidLaws.laws(All.monoid(), Gen.all(), All.eq())
    )

    // This alone is enough because the monoid laws cover all other useful cases
    "All combines elements using &&" {
      forAll { b1: Boolean, b2: Boolean ->
        (b1 && b2) == All.monoid().run { All(b1) + All(b2) }.getAll
      }
    }
  }
}
