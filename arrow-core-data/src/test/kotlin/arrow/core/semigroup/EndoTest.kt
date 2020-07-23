package arrow.core.semigroup

import arrow.core.extensions.semigroup.endo.monoid.monoid
import arrow.core.semigroup.Endo
import arrow.core.test.UnitSpec
import arrow.core.test.generators.endo
import arrow.core.test.laws.MonoidLaws
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen

class EndoTest : UnitSpec() {
  val EQ: Eq<Endo<Int>> = Eq { a, b ->
    a.appEndo(1) == b.appEndo(1)
  }

  init {
    testLaws(
      MonoidLaws.laws(Endo.monoid(), Gen.endo(Gen.int()), EQ)
    )
  }
}
