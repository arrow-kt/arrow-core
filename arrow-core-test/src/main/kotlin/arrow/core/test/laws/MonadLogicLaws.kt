package arrow.core.test.laws

import arrow.Kind
import arrow.core.Option
import arrow.core.Tuple2
import arrow.core.extensions.eq
import arrow.core.extensions.option.eq.eq
import arrow.core.extensions.tuple2.eq.eq
import arrow.core.test.generators.GenK
import arrow.core.test.generators.functionAToB
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.MonadLogic
import arrow.typeclasses.reflect
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.forAll
import io.kotest.matchers.shouldBe
import io.kotest.property.arbitrary.string

object MonadLogicLaws {

  fun <F> laws(
    ML: MonadLogic<F>,
    GK: GenK<F>,
    EQK: EqK<F>,
    iterations: Int = 1000
  ): List<Law> =
    MonadPlusLaws.laws(ML, GK, EQK) +
      monadLogicLaws(ML, GK, EQK, iterations)

  private fun <F> monadLogicLaws(
    ML: MonadLogic<F>,
    GK: GenK<F>,
    EQK: EqK<F>,
    iterations: Int
  ): List<Law> {

    val EQ = EQK.liftEq(Option.eq(Tuple2.eq(EQK.liftEq(Int.eq()), Int.eq())))
    val genFB = GK.genK(Arb.string())
    val genFunAToFB = Arb.functionAToB<Int, Kind<F, String>>(genFB)
    val genFA = GK.genK(Arb.int())
    val eqS = EQK.liftEq(String.eq())

    return listOf(
      Law("MonadLogic Laws: msplit mzero is empty") {
        ML.msplitZeroIsNone(EQ)
      },
      Law("MonadLogic Laws: msplit gives access to the first result") {
        ML.msplitGivesAccessToFirstResult(Arb.int(), genFA, EQ)
      },
      Law("MonadLogic Laws: ifte picks first on success") {
        ML.iftePicksFirstOnSuccess(iterations, Arb.int(), genFunAToFB, genFB, eqS)
      },
      Law("MonadLogic Laws: ifte picks second on failure") {
        ML.iftePicksSecondOnFailure(iterations, Arb.int(), genFunAToFB, genFB, eqS)
      },
      Law("MonadLogic Laws: ifte returns remaining computation") {
        ML.ifteReturnsRemainingComputation(iterations, Arb.int(), genFA, genFunAToFB, genFB, eqS)
      },
      Law("MonadLogic Laws: reflect is inverse of msplit") {
        ML.msplitReflect(iterations, genFA, EQK.liftEq(Int.eq()))
      },
      Law("MonadLogic Laws: once of just is just") {
        ML.onceJust<F, Int>(Arb.int(), EQK.liftEq(Int.eq()))
      }
    )
  }

  fun <F, A> MonadLogic<F>.msplitZeroIsNone(EQ: Eq<Kind<F, Option<Tuple2<Kind<F, A>, A>>>>) =
    zeroM<A>().splitM().equalUnderTheLaw(just(Option.empty()), EQ) shouldBe true

  private suspend fun <F, A> MonadLogic<F>.onceJust(
    genA: Arb<A>,
    EQ: Eq<Kind<F, A>>
  ) = forAll(genA) { a ->
    just(a).once().equalUnderTheLaw(just(a), EQ)
  }

  private suspend fun <F, A> MonadLogic<F>.msplitGivesAccessToFirstResult(
    genA: Arb<A>,
    genFA: Arb<Kind<F, A>>,
    EQ: Eq<Kind<F, Option<Tuple2<Kind<F, A>, A>>>>
  ) =
    forAll(genA, genFA) { a, fa ->
      val ls = just(a).plusM(fa).splitM()
      val rs = just(Option.just(Tuple2(fa, a)))

      ls.equalUnderTheLaw(rs, EQ)
    }

  private suspend fun <F, A, B> MonadLogic<F>.iftePicksFirstOnSuccess(
    iterations: Int,
    genA: Arb<A>,
    genFunAToFB: Arb<(A) -> Kind<F, B>>,
    genFB: Arb<Kind<F, B>>,
    EQ: Eq<Kind<F, B>>
  ) =
    forAll(iterations, genA, genFunAToFB, genFB) { a, funA, fb ->
      val ls = just(a).ifThen(fb, funA)
      val rs = funA(a)
      ls.equalUnderTheLaw(rs, EQ)
    }

  private suspend fun <F, A, B> MonadLogic<F>.iftePicksSecondOnFailure(
    iterations: Int,
    genA: Arb<A>,
    genFunAToFB: Arb<(A) -> Kind<F, B>>,
    genFB: Arb<Kind<F, B>>,
    EQ: Eq<Kind<F, B>>
  ) =
    forAll(iterations, genA, genFunAToFB, genFB) { a, funA, fb ->
      zeroM<A>().ifThen(fb, funA).equalUnderTheLaw(fb, EQ)
    }

  private suspend fun <F, A, B> MonadLogic<F>.ifteReturnsRemainingComputation(
    iterations: Int,
    genA: Arb<A>,
    genFA: Arb<Kind<F, A>>,
    genFunAToFB: Arb<(A) -> Kind<F, B>>,
    genFB: Arb<Kind<F, B>>,
    EQ: Eq<Kind<F, B>>
  ) =
    forAll(iterations, genA, genFA, genFunAToFB, genFB) { a, fa, funA, fb ->
      val ls = just(a).plusM(fa).ifThen(fb, funA)
      val rs = funA(a).plusM(fa.flatMap { funA(it) })

      ls.equalUnderTheLaw(rs, EQ)
    }

  private suspend fun <F, A> MonadLogic<F>.msplitReflect(iterations: Int, genFA: Arb<Kind<F, A>>, EQ: Eq<Kind<F, A>>) =
    forAll(iterations, genFA) { fa ->
      fa.splitM().flatMap {
        it.reflect(this@msplitReflect)
      }.equalUnderTheLaw(fa, EQ)
    }
}
