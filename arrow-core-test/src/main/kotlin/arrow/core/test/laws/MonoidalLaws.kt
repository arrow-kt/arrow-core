package arrow.core.test.laws

import arrow.Kind
import arrow.core.Tuple2
import arrow.core.extensions.eq
import arrow.core.extensions.tuple2.eq.eq
import arrow.core.test.generators.GenK
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Monoidal
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.forAll

object MonoidalLaws {

  fun <F> laws(
    MDAL: Monoidal<F>,
    GENK: GenK<F>,
    EQK: EqK<F>,
    BIJECTION: (Kind<F, Tuple2<Tuple2<Int, Int>, Int>>) -> (Kind<F, Tuple2<Int, Tuple2<Int, Int>>>)
  ): List<Law> {
    val GEN = GENK.genK(Arb.int())
    val EQ = EQK.liftEq(Tuple2.eq(Int.eq(), Int.eq()))

    return SemigroupalLaws.laws(MDAL,
      GENK,
      BIJECTION,
      EQK
    ) + listOf(
      Law("Monoidal Laws: Left identity") { MDAL.monoidalLeftIdentity(GEN, EQ) },
      Law("Monoidal Laws: Right identity") { MDAL.monoidalRightIdentity(GEN, EQ) }
    )
  }

  private suspend fun <F> Monoidal<F>.monoidalLeftIdentity(G: Arb<Kind<F, Int>>, EQ: Eq<Kind<F, Tuple2<Int, Int>>>) =
    forAll(G) { fa: Kind<F, Int> ->
      identity<Int>().product(fa).equalUnderTheLaw(identity(), EQ)
    }

  private suspend fun <F> Monoidal<F>.monoidalRightIdentity(G: Arb<Kind<F, Int>>, EQ: Eq<Kind<F, Tuple2<Int, Int>>>) =
    forAll(G) { fa: Kind<F, Int> ->
      fa.product(identity<Int>()).equalUnderTheLaw(identity(), EQ)
    }
}
