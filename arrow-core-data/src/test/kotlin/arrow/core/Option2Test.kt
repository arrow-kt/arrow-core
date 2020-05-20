package arrow.core

import arrow.core.extensions.eq
import arrow.core.extensions.ior.applicative.applicative
import arrow.core.extensions.ior.eqK.eqK
import arrow.core.extensions.ior.functor.functor
import arrow.core.extensions.ior.monad.monad
import arrow.core.extensions.monoid
import arrow.core.extensions.option.eqK.eqK
import arrow.core.extensions.option2.applicative.applicative
import arrow.core.extensions.option2.bifunctor.bifunctor
import arrow.core.extensions.option2.eqK.eqK
import arrow.core.extensions.option2.eqK2.eqK2
import arrow.core.extensions.option2.functor.functor
import arrow.core.extensions.option2.monad.monad
import arrow.core.extensions.semigroup
import arrow.core.test.UnitSpec
import arrow.core.test.generators.genK
import arrow.core.test.generators.genK2
import arrow.core.test.laws.BifunctorLaws
import arrow.core.test.laws.EqK2Laws
import arrow.core.test.laws.EqKLaws
import arrow.core.test.laws.MonadLaws
import io.kotlintest.properties.Gen

class Option2Test : UnitSpec() {

  init {

    testLaws(
      EqK2Laws.laws(Option2.eqK2(), Option2.genK2()),
      BifunctorLaws.laws(Option2.bifunctor(), Option2.genK2(), Option2.eqK2()),
      MonadLaws.laws(
        Option2.monad(Int.semigroup(), Int.monoid()),
        Option2.functor(),
        Option2.applicative(Int.monoid()),
        Option2.monad(Int.semigroup(), Int.monoid()),
        Option2.genK(Gen.int()),
        Option2.eqK(Int.eq())
      )
    )
  }
}
