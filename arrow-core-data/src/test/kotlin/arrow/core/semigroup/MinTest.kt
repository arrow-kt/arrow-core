package arrow.core.semigroup

import arrow.core.extensions.eq
import arrow.core.extensions.hash
import arrow.core.extensions.order
import arrow.core.extensions.semigroup.min.applicative.applicative
import arrow.core.extensions.semigroup.min.eq.eq
import arrow.core.extensions.semigroup.min.eqK.eqK
import arrow.core.extensions.semigroup.min.functor.functor
import arrow.core.extensions.semigroup.min.hash.hash
import arrow.core.extensions.semigroup.min.monad.monad
import arrow.core.extensions.semigroup.min.semigroup.semigroup
import arrow.core.extensions.semigroup.min.show.show
import arrow.core.extensions.semigroup.min.traverse.traverse
import arrow.core.extensions.show
import arrow.core.test.UnitSpec
import arrow.core.test.generators.genK
import arrow.core.test.generators.min
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
import kotlin.math.min

class MinTest : UnitSpec() {
  init {
    testLaws(
      EqLaws.laws(Min.eq(Int.eq()), Gen.int().min()),
      EqKLaws.laws(Min.eqK(), Min.genK()),
      ShowLaws.laws(Min.show(Int.show()), Min.eq(Int.eq()), Gen.int().min()),
      HashLaws.laws(Min.hash(Int.hash()), Gen.int().min(), Min.eq(Int.eq())),
      MonadLaws.laws(Min.monad(), Min.functor(), Min.applicative(), Min.monad(), Min.genK(), Min.eqK()),
      TraverseLaws.laws(Min.traverse(), Min.applicative(), Min.genK(), Min.eqK()),
      SemigroupLaws.laws(Min.semigroup(Int.order()), Gen.int().min(), Min.eq(Int.eq()))
    )

    "Min chooses the smaller element on combine" {
      forAll { i1: Int, i2: Int ->
        min(i1, i2)
          .equalUnderTheLaw(
            Min.semigroup(Int.order()).run { Min(i1) + Min(i2) }.getMin,
            Int.eq()
          )
      }
    }
  }
}
