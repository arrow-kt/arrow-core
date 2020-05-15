package arrow.core

import arrow.core.extensions.option2.bifunctor.bifunctor
import arrow.core.extensions.option2.eqK2.eqK2
import arrow.core.test.UnitSpec
import arrow.core.test.generators.genK2
import arrow.core.test.laws.BifunctorLaws
import arrow.core.test.laws.EqK2Laws

class Option2Test : UnitSpec() {

  init {

    testLaws(
      EqK2Laws.laws(Option2.eqK2(), Option2.genK2()),
      BifunctorLaws.laws(Option2.bifunctor(), Option2.genK2(), Option2.eqK2())
    )
  }
}
