package arrow.core.semigroup

import arrow.core.extensions.eq
import arrow.core.extensions.hash
import arrow.core.extensions.order
import arrow.core.extensions.semigroup.max.applicative.applicative
import arrow.core.extensions.semigroup.max.eq.eq
import arrow.core.extensions.semigroup.max.eqK.eqK
import arrow.core.extensions.semigroup.max.functor.functor
import arrow.core.extensions.semigroup.max.hash.hash
import arrow.core.extensions.semigroup.max.monad.monad
import arrow.core.extensions.semigroup.max.semigroup.semigroup
import arrow.core.extensions.semigroup.max.show.show
import arrow.core.extensions.semigroup.max.traverse.traverse
import arrow.core.extensions.show
import arrow.core.test.UnitSpec
import arrow.core.test.generators.genK
import arrow.core.test.generators.max
import arrow.core.test.laws.EqKLaws
import arrow.core.test.laws.EqLaws
import arrow.core.test.laws.HashLaws
import arrow.core.test.laws.MonadLaws
import arrow.core.test.laws.SemigroupLaws
import arrow.core.test.laws.ShowLaws
import arrow.core.test.laws.TraverseLaws
import arrow.core.test.laws.equalUnderTheLaw
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import kotlin.math.max

class MaxTest : UnitSpec() {
  init {
    testLaws(
      EqLaws.laws(Max.eq(Int.eq()), Gen.int().max()),
      EqKLaws.laws(Max.eqK(), Max.genK()),
      ShowLaws.laws(Max.show(Int.show()), Max.eq(Int.eq()), Gen.int().max()),
      HashLaws.laws(Max.hash(Int.hash()), Gen.int().max(), Max.eq(Int.eq())),
      MonadLaws.laws(Max.monad(), Max.functor(), Max.applicative(), Max.monad(), Max.genK(), Max.eqK()),
      TraverseLaws.laws(Max.traverse(), Max.applicative(), Max.genK(), Max.eqK()),
      SemigroupLaws.laws(Max.semigroup(Int.order()), Gen.int().max(), Max.eq(Int.eq()))
    )

    "Max chooses the larger element on combine" {
      forAll { i1: Int, i2: Int ->
        max(i1, i2)
          .equalUnderTheLaw(
            Max.semigroup(Int.order()).run { Max(i1) + Max(i2) }.getMax,
            Int.eq()
          )
      }
    }
  }
}
