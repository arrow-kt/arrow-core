package arrow.core.test.laws

import arrow.Kind
import arrow.core.extensions.eq
import arrow.core.test.generators.GenK
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.SemigroupK
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotlintest.properties.forAll

object SemigroupKLaws {

  fun <F> laws(SGK: SemigroupK<F>, GENK: GenK<F>, EQK: EqK<F>): List<Law> =
    listOf(Law("SemigroupK: associativity") { SGK.semigroupKAssociative(GENK.genK(Arb.int()), EQK.liftEq(Int.eq())) })

  fun <F> SemigroupK<F>.semigroupKAssociative(GEN: Arb<Kind<F, Int>>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(GEN, GEN, GEN) { a, b, c ->
      a.combineK(b).combineK(c).equalUnderTheLaw(a.combineK(b.combineK(c)), EQ)
    }
}
