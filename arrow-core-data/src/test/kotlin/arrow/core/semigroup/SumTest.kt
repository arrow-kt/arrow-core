package arrow.core.semigroup

import arrow.core.extensions.eq
import arrow.core.extensions.hash
import arrow.core.extensions.num
import arrow.core.extensions.semigroup.sum.applicative.applicative
import arrow.core.extensions.semigroup.sum.eq.eq
import arrow.core.extensions.semigroup.sum.eqK.eqK
import arrow.core.extensions.semigroup.sum.functor.functor
import arrow.core.extensions.semigroup.sum.hash.hash
import arrow.core.extensions.semigroup.sum.monad.monad
import arrow.core.extensions.semigroup.sum.monoid.monoid
import arrow.core.extensions.semigroup.sum.show.show
import arrow.core.extensions.semigroup.sum.traverse.traverse
import arrow.core.extensions.show
import arrow.core.test.UnitSpec
import arrow.core.test.generators.genK
import arrow.core.test.generators.sum
import arrow.core.test.laws.EqKLaws
import arrow.core.test.laws.EqLaws
import arrow.core.test.laws.HashLaws
import arrow.core.test.laws.MonadLaws
import arrow.core.test.laws.MonoidLaws
import arrow.core.test.laws.ShowLaws
import arrow.core.test.laws.TraverseLaws
import arrow.core.test.laws.equalUnderTheLaw
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

class SumTest : UnitSpec() {
  init {
    testLaws(
      EqLaws.laws(Sum.eq(Int.eq()), Gen.int().sum()),
      EqKLaws.laws(Sum.eqK(), Sum.genK()),
      ShowLaws.laws(Sum.show(Int.show()), Sum.eq(Int.eq()), Gen.int().sum()),
      HashLaws.laws(Sum.hash(Int.hash()), Gen.int().sum(), Sum.eq(Int.eq())),
      MonadLaws.laws(Sum.monad(), Sum.functor(), Sum.applicative(), Sum.monad(), Sum.genK(), Sum.eqK()),
      TraverseLaws.laws(Sum.traverse(), Sum.applicative(), Sum.genK(), Sum.eqK()),
      MonoidLaws.laws(Sum.monoid(Int.num()), Gen.int().sum(), Sum.eq(Int.eq()))
    )

    "Sum combines elements by adding them" {
      forAll { i1: Int, i2: Int ->
        (i1 + i2)
          .equalUnderTheLaw(
            Sum.monoid(Int.num()).run { Sum(i1) + Sum(i2) }.getSum,
            Int.eq()
          )
      }
    }
  }
}
