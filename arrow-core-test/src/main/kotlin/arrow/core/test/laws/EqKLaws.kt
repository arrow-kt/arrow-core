package arrow.core.test.laws

import arrow.Kind
import arrow.core.extensions.eq
import arrow.core.test.generators.GenK
import arrow.typeclasses.EqK
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.forAll

object EqKLaws {

  fun <F> laws(
    EQK: EqK<F>,
    GENK: GenK<F>
  ): List<Law> =
    GENK.genK(Arb.int()).let { gen ->
      listOf(
        Law("EqK Laws: reflexivity") { EQK.eqkReflexivity(gen) },
        Law("EqK Laws: symmetry") { EQK.eqKSymmetry(gen) },
        Law("EqK Laws: transitivity") { EQK.eqKTransitivity(gen) }
      )
    }

  private suspend fun <F> EqK<F>.eqkReflexivity(G: Arb<Kind<F, Int>>) = forAll(G) { x: Kind<F, Int> ->
    x.eqK(x, Int.eq())
  }

  private suspend fun <F> EqK<F>.eqKSymmetry(G: Arb<Kind<F, Int>>) = forAll(G, G) { x: Kind<F, Int>, y: Kind<F, Int> ->
    x.eqK(y, Int.eq()) == y.eqK(x, Int.eq())
  }

  private suspend fun <F> EqK<F>.eqKTransitivity(G: Arb<Kind<F, Int>>) = forAll(G, G, G) { x: Kind<F, Int>, y: Kind<F, Int>, z: Kind<F, Int> ->
    !(x.eqK(y, Int.eq()) && y.eqK(z, Int.eq())) || x.eqK(z, Int.eq())
  }
}
