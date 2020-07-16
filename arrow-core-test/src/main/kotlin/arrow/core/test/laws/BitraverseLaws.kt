package arrow.core.test.laws

import arrow.Kind
import arrow.Kind2
import arrow.core.ForId
import arrow.core.Id
import arrow.core.extensions.eq
import arrow.core.extensions.id.applicative.applicative
import arrow.core.extensions.id.comonad.extract
import arrow.core.test.generators.GenK2
import arrow.core.test.generators.functionAToB
import arrow.core.test.generators.intSmall
import arrow.typeclasses.Bitraverse
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK2
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
import io.kotest.property.forAll

object BitraverseLaws {

  fun <F> laws(BT: Bitraverse<F>, GENK: GenK2<F>, EQK: EqK2<F>): List<Law> {

    val G = GENK.genK(Arb.int(), Arb.int())
    val EQ = EQK.liftEq(Int.eq(), Int.eq())

    return BifoldableLaws.laws(BT, GENK) + listOf(Law("Bitraverse Laws: Identity") { BT.identityBitraverse(BT, G, EQ) })
  }

  private suspend fun <F> Bitraverse<F>.identityBitraverse(BT: Bitraverse<F>, GEN: Arb<Kind2<F, Int, Int>>, EQ: Eq<Kind2<F, Int, Int>>) =
    Id.applicative().run {
      val idApp = this
      forAll(Arb.functionAToB<Int, Kind<ForId, Int>>(Arb.intSmall().map(::Id)),
        Arb.functionAToB<Int, Kind<ForId, Int>>(Arb.intSmall().map(::Id)), GEN) { f, g, fa ->
        fa.bitraverse(idApp, f, g).extract().equalUnderTheLaw(BT.run { fa.bimap({ f(it).extract() }, { g(it).extract() }) }, EQ)
      }
    }
}
