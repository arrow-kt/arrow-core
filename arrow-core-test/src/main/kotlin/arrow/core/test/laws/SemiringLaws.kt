package arrow.core.test.laws

import arrow.typeclasses.Eq
import arrow.typeclasses.Semiring
import io.kotest.property.Arb
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe

object SemiringLaws {

  fun <F> laws(SG: Semiring<F>, GEN: Arb<F>, EQ: Eq<F>): List<Law> =
      listOf(
        Law("Semiring: Additive commutativity") { SG.semiringAdditiveCommutativity(GEN, EQ) },
        Law("Semiring: Additive left identity") { SG.semiringAdditiveLeftIdentity(GEN, EQ) },
        Law("Semiring: Additive right identity") { SG.semiringAdditiveRightIdentity(GEN, EQ) },
        Law("Semiring: Additive associativity") { SG.semiringAdditiveAssociativity(GEN, EQ) },
        Law("Semiring: Multiplicative commutativity") { SG.semiringMultiplicativeCommutativity(GEN, EQ) },
        Law("Semiring: Multiplicative left identity") { SG.semiringMultiplicativeLeftIdentity(GEN, EQ) },
        Law("Semiring: Multiplicative right identity") { SG.semiringMultiplicativeRightIdentity(GEN, EQ) },
        Law("Semiring: Multiplicative associativity") { SG.semiringMultiplicativeAssociativity(GEN, EQ) },
        Law("Semiring: Right distributivity") { SG.semiringRightDistributivity(GEN, EQ) },
        Law("Semiring: Left distributivity") { SG.semiringLeftDistributivity(GEN, EQ) },
        Law("Semiring: Multiplicative left absorption") { SG.semiringMultiplicativeLeftAbsorption(GEN, EQ) },
        Law("Semiring: Multiplicative right absorption") { SG.semiringMultiplicativeRightAbsorption(GEN, EQ) },
        Law("Semiring: times is derived") { SG.timesIsDerived(GEN, EQ) },
        Law("Semiring: plus is derived") { SG.plusIsDerived(GEN, EQ) },
        Law("Semiring: maybeCombineAddition is derived") { SG.maybeCombineAdditionIsDerived(GEN, EQ) },
        Law("Semiring: maybeCombineAddition left null") { SG.maybeCombineAdditionLeftNull(GEN, EQ) },
        Law("Semiring: maybeCombineAddition right null") { SG.maybeCombineAdditionRightNull(GEN, EQ) },
        Law("Semiring: maybeCombineAddition both null") { SG.maybeCombineAdditionBothNull(EQ) },
        Law("Semiring: maybeCombineMultiplicate is derived") { SG.maybeCombineMultiplicateIsDerived(GEN, EQ) },
        Law("Semiring: maybeCombineMultiplicate left null") { SG.maybeCombineMultiplicateLeftNull(GEN, EQ) },
        Law("Semiring: maybeCombineMultiplicate right null") { SG.maybeCombineMultiplicateRightNull(GEN, EQ) },
        Law("Semiring: maybeCombineMultiplicate both null") { SG.maybeCombineMultiplicateBothNull(EQ) }
      )

  // a + b = b + a
  fun <F> Semiring<F>.semiringAdditiveCommutativity(GEN: Arb<F>, EQ: Eq<F>) =
    forAll(GEN, GEN) { a, b ->
      a.combine(b).equalUnderTheLaw(b.combine(a), EQ)
    }

  // 0 + a = a
  fun <F> Semiring<F>.semiringAdditiveLeftIdentity(GEN: Arb<F>, EQ: Eq<F>) =
    forAll(GEN) { A ->
      (zero().combine(A)).equalUnderTheLaw(A, EQ)
    }

  // a + 0 = a
  fun <F> Semiring<F>.semiringAdditiveRightIdentity(GEN: Arb<F>, EQ: Eq<F>) =
    forAll(GEN) { A ->
      A.combine(zero()).equalUnderTheLaw(A, EQ)
    }

  // a + (b + c) = (a + b) + c
  fun <F> Semiring<F>.semiringAdditiveAssociativity(GEN: Arb<F>, EQ: Eq<F>) =
    forAll(GEN, GEN, GEN) { A, B, C ->
      A.combine(B.combine(C)).equalUnderTheLaw((A.combine(B)).combine(C), EQ)
    }

  // a · b = b · a
  fun <F> Semiring<F>.semiringMultiplicativeCommutativity(GEN: Arb<F>, EQ: Eq<F>) =
    forAll(GEN, GEN) { a, b ->
      a.combineMultiplicate(b).equalUnderTheLaw(b.combineMultiplicate(a), EQ)
    }

  // 1 · a = a
  fun <F> Semiring<F>.semiringMultiplicativeLeftIdentity(GEN: Arb<F>, EQ: Eq<F>) =
    forAll(GEN) { A ->
      (one().combineMultiplicate(A)).equalUnderTheLaw(A, EQ)
    }

  // a · 1 = a
  fun <F> Semiring<F>.semiringMultiplicativeRightIdentity(GEN: Arb<F>, EQ: Eq<F>) =
    forAll(GEN) { A ->
      A.combineMultiplicate(one()).equalUnderTheLaw(A, EQ)
    }

  // a · (b · c) = (a · b) · c
  fun <F> Semiring<F>.semiringMultiplicativeAssociativity(GEN: Arb<F>, EQ: Eq<F>) =
    forAll(GEN, GEN, GEN) { A, B, C ->
      A.combineMultiplicate(B.combineMultiplicate(C)).equalUnderTheLaw((B.combineMultiplicate(A)).combineMultiplicate(C), EQ)
    }

  // (a + b) · c = a · c + b · c
  fun <F> Semiring<F>.semiringRightDistributivity(GEN: Arb<F>, EQ: Eq<F>) =
    forAll(GEN, GEN, GEN) { A, B, C ->
      (A.combine(B)).combineMultiplicate(C).equalUnderTheLaw((A.combineMultiplicate(C)).combine(B.combineMultiplicate(C)), EQ)
    }

  // a · (b + c) = a · b + a · c
  fun <F> Semiring<F>.semiringLeftDistributivity(GEN: Arb<F>, EQ: Eq<F>) =
    forAll(GEN, GEN, GEN) { A, B, C ->
      A.combineMultiplicate(B.combine(C)).equalUnderTheLaw((A.combineMultiplicate(B)).combine(A.combineMultiplicate(C)), EQ)
    }

  // 0 · a = 0
  fun <F> Semiring<F>.semiringMultiplicativeLeftAbsorption(GEN: Arb<F>, EQ: Eq<F>) =
    forAll(GEN) { A ->
      (zero().combineMultiplicate(A)).equalUnderTheLaw(zero(), EQ)
    }

  // a · 0 = 0
  fun <F> Semiring<F>.semiringMultiplicativeRightAbsorption(GEN: Arb<F>, EQ: Eq<F>) =
    forAll(GEN) { A ->
      A.combineMultiplicate(zero()).equalUnderTheLaw(zero(), EQ)
    }

  fun <F> Semiring<F>.timesIsDerived(GEN: Arb<F>, EQ: Eq<F>): Unit =
    forAll(GEN, GEN) { A, B ->
      A.times(B).equalUnderTheLaw(A.combineMultiplicate(B), EQ)
    }

  fun <F> Semiring<F>.plusIsDerived(GEN: Arb<F>, EQ: Eq<F>): Unit =
    forAll(GEN, GEN) { A, B ->
      A.plus(B).equalUnderTheLaw(A.combine(B), EQ)
    }

  fun <F> Semiring<F>.maybeCombineAdditionIsDerived(GEN: Arb<F>, EQ: Eq<F>): Unit =
    forAll(GEN, GEN) { A, B ->
      A.maybeCombineAddition(B).equalUnderTheLaw(A.combine(B), EQ)
    }

  fun <F> Semiring<F>.maybeCombineAdditionLeftNull(GEN: Arb<F>, EQ: Eq<F>): Unit =
    forAll(GEN) { A ->
      null.maybeCombineAddition(A).equalUnderTheLaw(zero(), EQ)
    }

  fun <F> Semiring<F>.maybeCombineAdditionRightNull(GEN: Arb<F>, EQ: Eq<F>): Unit =
    forAll(GEN) { A ->
      A.maybeCombineAddition(null).equalUnderTheLaw(A, EQ)
    }

  fun <F> Semiring<F>.maybeCombineAdditionBothNull(EQ: Eq<F>): Unit =
    null.maybeCombineAddition(null).equalUnderTheLaw(zero(), EQ) shouldBe true

  fun <F> Semiring<F>.maybeCombineMultiplicateIsDerived(GEN: Arb<F>, EQ: Eq<F>): Unit =
    forAll(GEN, GEN) { A, B ->
      A.maybeCombineMultiplicate(B).equalUnderTheLaw(A.combineMultiplicate(B), EQ)
    }

  fun <F> Semiring<F>.maybeCombineMultiplicateLeftNull(GEN: Arb<F>, EQ: Eq<F>): Unit =
    forAll(GEN) { A ->
      null.maybeCombineMultiplicate(A).equalUnderTheLaw(one(), EQ)
    }

  fun <F> Semiring<F>.maybeCombineMultiplicateRightNull(GEN: Arb<F>, EQ: Eq<F>): Unit =
    forAll(GEN) { A ->
      A.maybeCombineMultiplicate(null).equalUnderTheLaw(A, EQ)
    }

  fun <F> Semiring<F>.maybeCombineMultiplicateBothNull(EQ: Eq<F>): Unit =
    null.maybeCombineMultiplicate(null).equalUnderTheLaw(one(), EQ) shouldBe true
}
