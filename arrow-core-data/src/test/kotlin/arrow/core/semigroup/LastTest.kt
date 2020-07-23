package arrow.core.semigroup

import arrow.core.extensions.eq
import arrow.core.extensions.hash
import arrow.core.extensions.semigroup.last.applicative.applicative
import arrow.core.extensions.semigroup.last.eq.eq
import arrow.core.extensions.semigroup.last.eqK.eqK
import arrow.core.extensions.semigroup.last.functor.functor
import arrow.core.extensions.semigroup.last.hash.hash
import arrow.core.extensions.semigroup.last.monad.monad
import arrow.core.extensions.semigroup.last.semigroup.semigroup
import arrow.core.extensions.semigroup.last.show.show
import arrow.core.extensions.semigroup.last.traverse.traverse
import arrow.core.extensions.show
import arrow.core.test.UnitSpec
import arrow.core.test.generators.genK
import arrow.core.test.generators.last
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

class LastTest : UnitSpec() {
  init {
    testLaws(
      EqLaws.laws(Last.eq(Int.eq()), Gen.int().last()),
      EqKLaws.laws(Last.eqK(), Last.genK()),
      ShowLaws.laws(Last.show(Int.show()), Last.eq(Int.eq()), Gen.int().last()),
      HashLaws.laws(Last.hash(Int.hash()), Gen.int().last(), Last.eq(Int.eq())),
      MonadLaws.laws(Last.monad(), Last.functor(), Last.applicative(), Last.monad(), Last.genK(), Last.eqK()),
      TraverseLaws.laws(Last.traverse(), Last.applicative(), Last.genK(), Last.eqK()),
      SemigroupLaws.laws(Last.semigroup(), Gen.int().last(), Last.eq(Int.eq()))
    )

    "Last chooses the second argument on combine" {
      forAll(Gen.int().last(), Gen.int().last()) { l1, l2 ->
        (Last.semigroup<Int>().run { l1 + l2 }).equalUnderTheLaw(l2, Last.eq(Int.eq()))
      }
    }
  }
}
