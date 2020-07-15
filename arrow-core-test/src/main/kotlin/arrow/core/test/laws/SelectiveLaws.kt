package arrow.core.test.laws

import arrow.Kind
import arrow.core.Right
import arrow.core.Tuple2
import arrow.core.extensions.eq
import arrow.core.identity
import arrow.core.toT
import arrow.core.test.generators.GenK
import arrow.core.test.generators.applicative
import arrow.core.test.generators.either
import arrow.core.test.generators.functionAToB
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Functor
import arrow.typeclasses.Selective
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.forAll

object SelectiveLaws {

  fun <F> laws(A: Selective<F>, GENK: GenK<F>, EQK: EqK<F>): List<Law> =
    laws(A, A, GENK, EQK)

  fun <F> laws(A: Selective<F>, FF: Functor<F>, GENK: GenK<F>, EQK: EqK<F>): List<Law> {
    val EQ = EQK.liftEq(Int.eq())

    return ApplicativeLaws.laws(A, FF, GENK, EQK) + listOf(
      Law("Selective Laws: identity") { A.identityLaw(EQ) },
      Law("Selective Laws: distributivity") { A.distributivity(GENK, EQ) },
      Law("Selective Laws: associativity") { A.associativity(GENK, EQ) },
      Law("Selective Laws: branch") { A.branch(GENK, EQ) },
      Law("Selective Laws: ifS") { A.ifSLaw(GENK, EQ) }
    )
  }

  fun <F> Selective<F>.identityLaw(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Arb.either(Arb.int(), Arb.int())) { either ->
      either.fold(
        { l -> just(either).select(just(::identity)).equalUnderTheLaw(just(l), EQ) },
        { r -> just(either).select(just(::identity)).equalUnderTheLaw(just(r), EQ) }
      )
    }

  fun <F> Selective<F>.distributivity(GK: GenK<F>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Arb.either(Arb.int(), Arb.int()).applicative(this),
      GK.genK(Arb.functionAToB<Int, Int>(Arb.int())),
      GK.genK(Arb.functionAToB<Int, Int>(Arb.int()))) { fe, f, g ->
      fe.select(f.apTap(g)).equalUnderTheLaw(fe.select(f).apTap(fe.select(g)), EQ)
    }

  fun <F> Selective<F>.associativity(GK: GenK<F>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(
      GK.genK(Arb.either(Arb.int(), Arb.int())),
      GK.genK(Arb.either(Arb.int(), Arb.functionAToB<Int, Int>(Arb.int()))),
      GK.genK(Arb.functionAToB<Int, (Int) -> Int>(Arb.functionAToB(Arb.int())))
    ) { x, y, z ->
      x.select(y.select(z)).equalUnderTheLaw(
        x.map { it.map(::Right) }
          .select(y.map { e -> { a: Int -> e.bimap({ a toT it }, { it(a) }) } })
          .select(z.map { f -> { t: Tuple2<Int, Int> -> f(t.a)(t.b) } }), EQ)
    }

  fun <F> Selective<F>.branch(GK: GenK<F>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(GK.genK(Arb.functionAToB<Double, Int>(Arb.int())),
      GK.genK(Arb.functionAToB<Float, Int>(Arb.int())),
      Arb.either(Arb.double(), Arb.float())) { fl, fr, either ->
      either.fold(
        { l -> just(either).branch(fl, fr).equalUnderTheLaw(fl.map { ff -> ff(l) }, EQ) },
        { r -> just(either).branch(fl, fr).equalUnderTheLaw(fr.map { ff -> ff(r) }, EQ) }
      )
    }

  fun <F> Selective<F>.ifSLaw(GK: GenK<F>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Arb.bool(), GK.genK(Arb.int()), GK.genK(Arb.int())) { bool, l, r ->
      if (bool) just(bool).ifS(l, r).equalUnderTheLaw(l, EQ)
      else just(bool).ifS(l, r).equalUnderTheLaw(r, EQ)
    }
}
