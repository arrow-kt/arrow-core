package arrow.core.semigroup

import arrow.Kind
import arrow.core.ForOption
import arrow.core.Option
import arrow.core.extensions.eq
import arrow.core.extensions.hash
import arrow.core.extensions.monoid
import arrow.core.extensions.option.applicative.applicative
import arrow.core.extensions.option.apply.map2
import arrow.core.extensions.option.eq.eq
import arrow.core.extensions.option.eqK.eqK
import arrow.core.extensions.option.hash.hash
import arrow.core.extensions.option.monadPlus.monadPlus
import arrow.core.extensions.option.show.show
import arrow.core.extensions.option.traverse.traverse
import arrow.core.extensions.semigroup.ap.applicative.applicative
import arrow.core.extensions.semigroup.ap.eq.eq
import arrow.core.extensions.semigroup.ap.eqK.eqK
import arrow.core.extensions.semigroup.ap.hash.hash
import arrow.core.extensions.semigroup.ap.monadPlus.monadPlus
import arrow.core.extensions.semigroup.ap.monoid.monoid
import arrow.core.extensions.semigroup.ap.show.show
import arrow.core.extensions.semigroup.ap.traverse.traverse
import arrow.core.extensions.semigroup.sum.eq.eq
import arrow.core.extensions.show
import arrow.core.fix
import arrow.core.test.UnitSpec
import arrow.core.test.generators.ap
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

class ApTest : UnitSpec() {
  init {
    val optGen: Gen<Ap<ForOption, String>> = Gen.option(Gen.string()).map { it as Kind<ForOption, String> }.ap()
    val optEq = Ap.eq(Option.eq(String.eq()) as Eq<Kind<ForOption, String>>)
    testLaws(
      EqLaws.laws(optEq, optGen),
      EqKLaws.laws(Ap.eqK(Option.eqK()), Option.genK().ap()),
      ShowLaws.laws(Ap.show(Option.show(String.show()) as Show<Kind<ForOption, String>>), optEq, optGen),
      HashLaws.laws(Ap.hash(Option.hash(String.hash()) as Hash<Kind<ForOption, String>>), optGen, optEq),
      MonadPlusLaws.laws(Ap.monadPlus(Option.monadPlus()), Option.genK().ap(), Ap.eqK(Option.eqK())),
      TraverseLaws.laws(Ap.traverse(Option.traverse()), Ap.applicative(Option.applicative()), Option.genK().ap(), Ap.eqK(Option.eqK())),
      MonoidLaws.laws(Ap.monoid(Option.applicative(), String.monoid()), optGen, optEq)
    )

    "Ap combines elements using map2 + combine" {
      forAll(optGen, optGen) { a1, a2 ->
        (Ap.monoid(Option.applicative(), String.monoid()).run { a1 + a2 }.getAp.fix())
          .equalUnderTheLaw(a1.getAp.map2(a2.getAp) { (a1, a2) -> a1 + a2 }.fix(), Option.eq(String.eq()))
      }
    }
  }
}
