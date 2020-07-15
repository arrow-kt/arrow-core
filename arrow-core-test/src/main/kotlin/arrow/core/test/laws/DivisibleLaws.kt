package arrow.core.test.laws

import arrow.Kind
import arrow.core.extensions.eq
import arrow.core.test.generators.GenK
import arrow.typeclasses.Divisible
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotlintest.properties.forAll

object DivisibleLaws {

  fun <F> laws(
    DF: Divisible<F>,
    GENK: GenK<F>,
    EQK: EqK<F>
  ): List<Law> {
    val G = GENK.genK(Arb.int())
    val EQ = EQK.liftEq(Int.eq())
    return DivideLaws.laws(DF, GENK, EQK) + listOf(
        Law("Divisible laws: Left identity") { DF.leftIdentity(G, EQ) },
        Law("Divisible laws: Right identity") { DF.rightIdentity(G, EQ) }
      )
  }

  fun <F> Divisible<F>.leftIdentity(
    G: Arb<Kind<F, Int>>,
    EQ: Eq<Kind<F, Int>>
  ): Unit =
    forAll(G) { fa ->
      divide<Int, Int, Int>(fa, conquer()) { DivideLaws.delta(it) }.equalUnderTheLaw(fa, EQ)
    }

  fun <F> Divisible<F>.rightIdentity(
    G: Arb<Kind<F, Int>>,
    EQ: Eq<Kind<F, Int>>
  ): Unit =
    forAll(G) { fa ->
      divide<Int, Int, Int>(conquer(), fa) { DivideLaws.delta(it) }.equalUnderTheLaw(fa, EQ)
    }
}
