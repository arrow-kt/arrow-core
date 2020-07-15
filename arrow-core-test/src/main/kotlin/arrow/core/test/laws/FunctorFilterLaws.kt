package arrow.core.test.laws

import arrow.Kind
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.extensions.eq
import arrow.core.identity
import arrow.core.some
import arrow.core.test.generators.GenK
import arrow.core.test.generators.functionAToB
import arrow.core.test.generators.intSmall
import arrow.core.test.generators.option
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.FunctorFilter
import io.kotest.property.Arb
import io.kotlintest.properties.forAll

object FunctorFilterLaws {

  fun <F> laws(FFF: FunctorFilter<F>, GENK: GenK<F>, EQK: EqK<F>): List<Law> {
    val GEN = GENK.genK(Arb.int())
    val EQ = EQK.liftEq(Int.eq())

    return FunctorLaws.laws(FFF, GENK, EQK) + listOf(
      Law("Functor Filter: filterMap composition") { FFF.filterMapComposition(GEN, EQ) },
      Law("Functor Filter: filterMap map consistency") { FFF.filterMapMapConsistency(GEN, EQ) },
      Law("Functor Filter: flattenOption filterMap consistency") { FFF.flattenOptionConsistentWithfilterMap(GEN, EQ) },
      Law("Functor Filter: filter filterMap consistency") { FFF.filterConsistentWithfilterMap(GEN, EQ) },
      Law("Functor Filter: filterIsInstance filterMap consistency") { FFF.filterIsInstanceConsistentWithfilterMap(GEN, EQ) }
    )
  }

  fun <F> FunctorFilter<F>.filterMapComposition(G: Arb<Kind<F, Int>>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(
      G,
      Arb.functionAToB<Int, Option<Int>>(Arb.option(Arb.intSmall())),
      Arb.functionAToB<Int, Option<Int>>(Arb.option(Arb.intSmall()))
    ) { fa: Kind<F, Int>, f, g ->
      fa.filterMap(f).filterMap(g).equalUnderTheLaw(fa.filterMap { a -> f(a).flatMap(g) }, EQ)
    }

  fun <F> FunctorFilter<F>.filterMapMapConsistency(G: Arb<Kind<F, Int>>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(
      G,
      Arb.functionAToB<Int, Int>(Arb.int())
    ) { fa: Kind<F, Int>, f ->
      fa.filterMap { Some(f(it)) }.equalUnderTheLaw(fa.map(f), EQ)
    }

  fun <F> FunctorFilter<F>.flattenOptionConsistentWithfilterMap(G: Arb<Kind<F, Int>>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(
      G
    ) { fa: Kind<F, Int> ->
      fa.map { it.some() }.flattenOption().equalUnderTheLaw(fa.filterMap { Some(identity(it)) }, EQ)
    }

  fun <F> FunctorFilter<F>.filterConsistentWithfilterMap(G: Arb<Kind<F, Int>>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(
      G,
      Arb.functionAToB<Int, Boolean>(Arb.bool())
    ) { fa: Kind<F, Int>, f ->
      fa.filter(f).equalUnderTheLaw(fa.filterMap { if (f(it)) Some(it) else None }, EQ)
    }

  fun <F> FunctorFilter<F>.filterIsInstanceConsistentWithfilterMap(G: Arb<Kind<F, Int>>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(
      G
    ) { fa: Kind<F, Int> ->
      fa.filterIsInstance(Integer::class.java).map { it as Int }.equalUnderTheLaw(fa.filterMap { Some(it) }, EQ)
    }
}
