package arrow.core.semigroup

import arrow.core.extensions.eq
import arrow.core.extensions.hash
import arrow.core.extensions.monoid
import arrow.core.extensions.semigroup.dual.applicative.applicative
import arrow.core.extensions.semigroup.dual.eq.eq
import arrow.core.extensions.semigroup.dual.eqK.eqK
import arrow.core.extensions.semigroup.dual.functor.functor
import arrow.core.extensions.semigroup.dual.hash.hash
import arrow.core.extensions.semigroup.dual.monad.monad
import arrow.core.extensions.semigroup.dual.monoid.monoid
import arrow.core.extensions.semigroup.dual.show.show
import arrow.core.extensions.semigroup.dual.traverse.traverse
import arrow.core.extensions.show
import arrow.core.test.UnitSpec
import arrow.core.test.generators.dual
import arrow.core.test.generators.genK
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

class DualTest : UnitSpec() {
  init {
    testLaws(
      EqLaws.laws(Dual.eq(String.eq()), Gen.string().dual()),
      EqKLaws.laws(Dual.eqK(), Dual.genK()),
      ShowLaws.laws(Dual.show(String.show()), Dual.eq(String.eq()), Gen.string().dual()),
      HashLaws.laws(Dual.hash(String.hash()), Gen.string().dual(), Dual.eq(String.eq())),
      MonadLaws.laws(Dual.monad(), Dual.functor(), Dual.applicative(), Dual.monad(), Dual.genK(), Dual.eqK()),
      TraverseLaws.laws(Dual.traverse(), Dual.applicative(), Dual.genK(), Dual.eqK()),
      MonoidLaws.laws(Dual.monoid(String.monoid()), Gen.string().dual(), Dual.eq(String.eq()))
    )

    "Dual combines elements using the inner monoid instance but reverses argument order" {
      forAll { s1: String, s2: String ->
        (s2 + s1)
          .equalUnderTheLaw(
            Dual.monoid(String.monoid()).run { Dual(s1) + Dual(s2) }.getDual,
            String.eq()
          )
      }
    }
  }
}
