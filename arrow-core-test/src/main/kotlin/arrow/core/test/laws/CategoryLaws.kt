package arrow.core.test.laws

import arrow.Kind2
import arrow.core.extensions.eq
import arrow.core.test.generators.GenK2
import arrow.typeclasses.Category
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK2
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.map
import io.kotest.property.forAll

object CategoryLaws {

  fun <F> laws(C: Category<F>, GENK: GenK2<F>, EQK: EqK2<F>): List<Law> =
    categoryLaws(
      C,
      GENK.genK(Arb.int(), Arb.int()),
      EQK.liftEq(Int.eq(), Int.eq())
    )

  fun <F> laws(C: Category<F>, f: (Int) -> Kind2<F, Int, Int>, EQ: Eq<Kind2<F, Int, Int>>): List<Law> =
    categoryLaws(
      C,
      Arb.int().map(f),
      EQ
    )

  private fun <F> categoryLaws(C: Category<F>, G: Arb<Kind2<F, Int, Int>>, EQ: Eq<Kind2<F, Int, Int>>): List<Law> =
    listOf(
      Law("Category Laws: right identity") { C.rightIdentity(G, EQ) },
      Law("Category Laws: left identity") { C.leftIdentity(G, EQ) },
      Law("Category Laws: associativity") { C.associativity(G, EQ) }
    )

  private suspend fun <F> Category<F>.rightIdentity(G: Arb<Kind2<F, Int, Int>>, EQ: Eq<Kind2<F, Int, Int>>) {
    forAll(G) { fa: Kind2<F, Int, Int> ->
      fa.compose(id()).equalUnderTheLaw(fa, EQ)
    }
  }

  private suspend fun <F> Category<F>.leftIdentity(G: Arb<Kind2<F, Int, Int>>, EQ: Eq<Kind2<F, Int, Int>>) {
    forAll(G) { fa: Kind2<F, Int, Int> ->
      id<Int>().compose(fa).equalUnderTheLaw(fa, EQ)
    }
  }

  private suspend fun <F> Category<F>.associativity(G: Arb<Kind2<F, Int, Int>>, EQ: Eq<Kind2<F, Int, Int>>) {
    forAll(G, G, G) { a, b, c ->
      a.compose(b).compose(c).equalUnderTheLaw(a.compose(b.compose(c)), EQ)
    }
  }
}
