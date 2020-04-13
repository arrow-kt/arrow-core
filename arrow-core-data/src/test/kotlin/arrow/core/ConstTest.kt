package arrow.core

import arrow.core.extensions.const.applicative.applicative
import arrow.core.extensions.const.eq.eq
import arrow.core.extensions.const.eqK.eqK
import arrow.core.extensions.const.functor.functor
import arrow.core.extensions.const.show.show
import arrow.core.extensions.const.traverseFilter.traverseFilter
import arrow.core.extensions.eq
import arrow.core.extensions.monoid
import arrow.core.extensions.show
import arrow.core.test.UnitSpec
import arrow.core.test.generators.genConst
import arrow.core.test.generators.genK
import arrow.core.test.laws.ApplicativeLaws
import arrow.core.test.laws.EqLaws
import arrow.core.test.laws.ShowLaws
import arrow.core.test.laws.TraverseFilterLaws
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen

class ConstTest : UnitSpec() {

  init {
    Int.monoid().run {
      testLaws(
        TraverseFilterLaws.laws(Const.traverseFilter(),
          Const.applicative(this),
          Const.genK(Gen.int()),
          Const.eqK(Int.eq())),
        ApplicativeLaws.laws(Const.applicative(this), Const.functor(), Const.genK(Gen.int()), Const.eqK(Int.eq())),
        EqLaws.laws(Const.eq<Int, Int>(Eq.any()), Gen.genConst<Int, Int>(Gen.int())),
        ShowLaws.laws(Const.show(Int.show()), Const.eq<Int, Int>(Eq.any()), Gen.genConst<Int, Int>(Gen.int()))
      )
    }
  }
}
