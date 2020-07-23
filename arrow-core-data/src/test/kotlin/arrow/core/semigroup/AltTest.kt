package arrow.core.semigroup

import arrow.Kind
import arrow.core.ForOption
import arrow.core.Option
import arrow.core.extensions.eq
import arrow.core.extensions.hash
import arrow.core.extensions.option.alternative.alternative
import arrow.core.extensions.option.alternative.orElse
import arrow.core.extensions.option.applicative.applicative
import arrow.core.extensions.option.eq.eq
import arrow.core.extensions.option.eqK.eqK
import arrow.core.extensions.option.hash.hash
import arrow.core.extensions.option.monadPlus.monadPlus
import arrow.core.extensions.option.show.show
import arrow.core.extensions.option.traverse.traverse
import arrow.core.extensions.semigroup.alt.alternative.orElse
import arrow.core.extensions.semigroup.alt.applicative.applicative
import arrow.core.extensions.semigroup.alt.eq.eq
import arrow.core.extensions.semigroup.alt.eqK.eqK
import arrow.core.extensions.semigroup.alt.hash.hash
import arrow.core.extensions.semigroup.alt.monadPlus.monadPlus
import arrow.core.extensions.semigroup.alt.monoid.monoid
import arrow.core.extensions.semigroup.alt.show.show
import arrow.core.extensions.semigroup.alt.traverse.traverse
import arrow.core.extensions.show
import arrow.core.fix
import arrow.core.test.UnitSpec
import arrow.core.test.generators.alt
import arrow.core.test.generators.genK
import arrow.core.test.generators.option
import arrow.core.test.laws.EqKLaws
import arrow.core.test.laws.EqLaws
import arrow.core.test.laws.HashLaws
import arrow.core.test.laws.MonadPlusLaws
import arrow.core.test.laws.MonoidLaws
import arrow.core.test.laws.ShowLaws
import arrow.core.test.laws.TraverseLaws
import arrow.core.test.laws.equalUnderTheLaw
import arrow.typeclasses.Eq
import arrow.typeclasses.Hash
import arrow.typeclasses.Show
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

class AltTest : UnitSpec() {
  init {
    val optGen: Gen<Alt<ForOption, Int>> = Gen.option(Gen.int()).map { it as Kind<ForOption, Int> }.alt()
    val optEq = Alt.eq(Option.eq(Int.eq()) as Eq<Kind<ForOption, Int>>)
    testLaws(
      EqLaws.laws(optEq, optGen),
      EqKLaws.laws(Alt.eqK(Option.eqK()), Option.genK().alt()),
      ShowLaws.laws(Alt.show(Option.show(Int.show()) as Show<Kind<ForOption, Int>>), optEq, optGen),
      HashLaws.laws(Alt.hash(Option.hash(Int.hash()) as Hash<Kind<ForOption, Int>>), optGen, optEq),
      MonadPlusLaws.laws(Alt.monadPlus(Option.monadPlus()), Option.genK().alt(), Alt.eqK(Option.eqK())),
      TraverseLaws.laws(Alt.traverse(Option.traverse()), Alt.applicative(Option.applicative()), Option.genK().alt(), Alt.eqK(Option.eqK())),
      MonoidLaws.laws(Alt.monoid<ForOption, Int>(Option.alternative()), optGen, optEq)
    )

    "Alt combines elements using orElse" {
      forAll(optGen, optGen) { a1, a2 ->
        (Alt.monoid<ForOption, Int>(Option.alternative()).run { a1 + a2 }.getAlt.fix())
          .equalUnderTheLaw(a1.getAlt.orElse(a2.getAlt).fix(), Option.eq(Int.eq()))
      }
    }
  }
}
