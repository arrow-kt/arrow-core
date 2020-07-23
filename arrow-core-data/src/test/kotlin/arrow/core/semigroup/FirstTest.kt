package arrow.core.semigroup

import arrow.core.extensions.eq
import arrow.core.extensions.hash
import arrow.core.extensions.semigroup.first.applicative.applicative
import arrow.core.extensions.semigroup.first.eq.eq
import arrow.core.extensions.semigroup.first.eqK.eqK
import arrow.core.extensions.semigroup.first.functor.functor
import arrow.core.extensions.semigroup.first.hash.hash
import arrow.core.extensions.semigroup.first.monad.monad
import arrow.core.extensions.semigroup.first.semigroup.semigroup
import arrow.core.extensions.semigroup.first.show.show
import arrow.core.extensions.semigroup.first.traverse.traverse
import arrow.core.extensions.show
import arrow.core.test.UnitSpec
import arrow.core.test.generators.first
import arrow.core.test.generators.genK
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

class FirstTest : UnitSpec() {
  init {
    testLaws(
      EqLaws.laws(First.eq(Int.eq()), Gen.int().first()),
      EqKLaws.laws(First.eqK(), First.genK()),
      ShowLaws.laws(First.show(Int.show()), First.eq(Int.eq()), Gen.int().first()),
      HashLaws.laws(First.hash(Int.hash()), Gen.int().first(), First.eq(Int.eq())),
      MonadLaws.laws(First.monad(), First.functor(), First.applicative(), First.monad(), First.genK(), First.eqK()),
      TraverseLaws.laws(First.traverse(), First.applicative(), First.genK(), First.eqK()),
      SemigroupLaws.laws(First.semigroup(), Gen.int().first(), First.eq(Int.eq()))
    )

    "First chooses the first (from left) argument on combine" {
      forAll(Gen.int().first(), Gen.int().first()) { f1, f2 ->
        (First.semigroup<Int>().run { f1 + f2 }).equalUnderTheLaw(f1, First.eq(Int.eq()))
      }
    }
  }
}
