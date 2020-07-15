package arrow.core.test.laws

import arrow.Kind
import arrow.core.Tuple2
import arrow.core.extensions.eq
import arrow.core.extensions.tuple2.eq.eq
import arrow.core.test.generators.GenK
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Semigroupal
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.forAll

object SemigroupalLaws {

  fun <F> laws(
    SGAL: Semigroupal<F>,
    GENK: GenK<F>,
    bijection: (Kind<F, Tuple2<Tuple2<Int, Int>, Int>>) -> (Kind<F, Tuple2<Int, Tuple2<Int, Int>>>),
    EQK: EqK<F>
  ): List<Law> = listOf(Law("Semigroupal: Bijective associativity") {
    val EQ = EQK.liftEq(Tuple2.eq(Int.eq(), Tuple2.eq(Int.eq(), Int.eq())))
    SGAL.semigroupalAssociative(GENK.genK(Arb.int()), bijection, EQ)
  })

  private suspend fun <F> Semigroupal<F>.semigroupalAssociative(
    GEN: Arb<Kind<F, Int>>,
    bijection: (Kind<F, Tuple2<Tuple2<Int, Int>, Int>>) -> (Kind<F, Tuple2<Int, Tuple2<Int, Int>>>),
    EQ: Eq<Kind<F, Tuple2<Int, Tuple2<Int, Int>>>>
  ) = forAll(GEN, GEN, GEN) { a, b, c ->
    a.product(b.product(c)).equalUnderTheLaw(bijection(a.product(b).product(c)), EQ)
  }
}
