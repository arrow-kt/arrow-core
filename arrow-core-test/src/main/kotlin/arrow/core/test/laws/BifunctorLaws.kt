package arrow.core.test.laws

import arrow.Kind2
import arrow.core.andThen
import arrow.core.extensions.eq
import arrow.core.test.generators.GenK2
import arrow.core.test.generators.functionAToB
import arrow.typeclasses.Bifunctor
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK2
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.forAll

object BifunctorLaws {

  fun <F> laws(BF: Bifunctor<F>, GENK: GenK2<F>, EQK: EqK2<F>): List<Law> {
    val G = GENK.genK(Arb.int(), Arb.int())
    val EQ = EQK.liftEq(Int.eq(), Int.eq())

    return listOf(
      Law("Bifunctor Laws: Identity") { BF.identity(G, EQ) },
      Law("Bifunctor Laws: Composition") { BF.composition(G, EQ) }
    )
  }

  private suspend fun <F> Bifunctor<F>.identity(G: Arb<Kind2<F, Int, Int>>, EQ: Eq<Kind2<F, Int, Int>>) {
    forAll(G) { fa: Kind2<F, Int, Int> ->
      fa.bimap({ it }, { it }).equalUnderTheLaw(fa, EQ)
    }
  }

  private suspend fun <F> Bifunctor<F>.composition(G: Arb<Kind2<F, Int, Int>>, EQ: Eq<Kind2<F, Int, Int>>) {
    forAll(
      G,
      Arb.functionAToB<Int, Int>(Arb.int()),
      Arb.functionAToB<Int, Int>(Arb.int()),
      Arb.functionAToB<Int, Int>(Arb.int()),
      Arb.functionAToB<Int, Int>(Arb.int())
    ) { fa: Kind2<F, Int, Int>, ff, g, x, y ->
      fa.bimap(ff, g).bimap(x, y).equalUnderTheLaw(fa.bimap(ff andThen x, g andThen y), EQ)
    }
  }
}
