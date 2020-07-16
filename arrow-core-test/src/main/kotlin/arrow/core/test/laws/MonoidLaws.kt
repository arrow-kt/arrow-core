package arrow.core.test.laws

import arrow.typeclasses.Eq
import arrow.typeclasses.Monoid
import io.kotest.property.Arb
import io.kotest.property.forAll
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.list

object MonoidLaws {

  fun <F> laws(M: Monoid<F>, GEN: Arb<F>, EQ: Eq<F>): List<Law> =
    SemigroupLaws.laws(M, GEN, EQ) +
      listOf(
        Law("Monoid Laws: Left identity") { M.monoidLeftIdentity(GEN, EQ) },
        Law("Monoid Laws: Right identity") { M.monoidRightIdentity(GEN, EQ) },
        Law("Monoid Laws: combineAll should be derived") { M.combineAllIsDerived(GEN, EQ) },
        Law("Monoid Laws: combineAll of empty list is empty") { M.combineAllOfEmptyIsEmpty(EQ) }
      )

  private suspend fun <F> Monoid<F>.monoidLeftIdentity(GEN: Arb<F>, EQ: Eq<F>) =
    forAll(GEN) { a ->
      (empty().combine(a)).equalUnderTheLaw(a, EQ)
    }

  private suspend fun <F> Monoid<F>.monoidRightIdentity(GEN: Arb<F>, EQ: Eq<F>) =
    forAll(GEN) { a ->
      a.combine(empty()).equalUnderTheLaw(a, EQ)
    }

  private suspend fun <F> Monoid<F>.combineAllIsDerived(GEN: Arb<F>, EQ: Eq<F>) =
    forAll(5, Arb.list(GEN)) { list ->
      list.combineAll().equalUnderTheLaw(if (list.isEmpty()) empty() else list.reduce { acc, f -> acc.combine(f) }, EQ)
    }

  private suspend fun <F> Monoid<F>.combineAllOfEmptyIsEmpty(EQ: Eq<F>) =
    emptyList<F>().combineAll().equalUnderTheLaw(empty(), EQ) shouldBe true
}
