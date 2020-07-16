package arrow.core.test.laws

import arrow.Kind
import arrow.core.extensions.eq
import arrow.core.test.generators.GenK
import arrow.core.test.generators.applicative
import arrow.core.test.generators.applicativeError
import arrow.core.test.generators.fatalThrowable
import arrow.core.test.generators.functionAToB
import arrow.core.test.generators.throwable
import arrow.typeclasses.Apply
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Functor
import arrow.typeclasses.MonadError
import arrow.typeclasses.Selective
import io.kotest.assertions.fail
import io.kotest.assertions.throwables.shouldThrowAny
import io.kotest.property.Arb
import io.kotest.property.arbitrary.bool
import io.kotest.property.arbitrary.int
import io.kotest.property.forAll

object MonadErrorLaws {

  private fun <F> monadErrorLaws(M: MonadError<F, Throwable>, EQK: EqK<F>): List<Law> {
    val EQ = EQK.liftEq(Int.eq())

    return listOf(
      Law("Monad Error Laws: left zero") { M.monadErrorLeftZero(EQ) },
      Law("Monad Error Laws: ensure consistency") { M.monadErrorEnsureConsistency(EQ) },
      Law("Monad Error Laws: NonFatal is caught") { M.monadErrorCatchesNonFatalThrowables(EQ) },
      Law("Monad Error Laws: Fatal errors are thrown") { M.monadErrorThrowsFatalThrowables(EQ) },
      Law("Monad Error Laws: redeemWith is derived from flatMap & HandleErrorWith") { M.monadErrorDerivesRedeemWith(EQ) },
      Law("Monad Error Laws: redeemWith pure is flatMap") { M.monadErrorRedeemWithPureIsFlatMap(EQ) }
    )
  }

  fun <F> laws(M: MonadError<F, Throwable>, GENK: GenK<F>, EQK: EqK<F>): List<Law> =
    MonadLaws.laws(M, GENK, EQK) +
      ApplicativeErrorLaws.laws(M, GENK, EQK) +
      monadErrorLaws(M, EQK)

  fun <F> laws(
    M: MonadError<F, Throwable>,
    FF: Functor<F>,
    AP: Apply<F>,
    SL: Selective<F>,
    GENK: GenK<F>,
    EQK: EqK<F>
  ): List<Law> =
    MonadLaws.laws(M, FF, AP, SL, GENK, EQK) +
      ApplicativeErrorLaws.laws(M, GENK, EQK) +
      monadErrorLaws(M, EQK)

  private suspend fun <F> MonadError<F, Throwable>.monadErrorLeftZero(EQ: Eq<Kind<F, Int>>) =
    forAll(Arb.functionAToB<Int, Kind<F, Int>>(Arb.int().applicativeError(this)), Arb.throwable()) { f: (Int) -> Kind<F, Int>, e: Throwable ->
      raiseError<Int>(e).flatMap(f).equalUnderTheLaw(raiseError(e), EQ)
    }

  private suspend fun <F> MonadError<F, Throwable>.monadErrorEnsureConsistency(EQ: Eq<Kind<F, Int>>) =
    forAll(Arb.int().applicativeError(this), Arb.throwable(), Arb.functionAToB<Int, Boolean>(Arb.bool())) { fa: Kind<F, Int>, e: Throwable, p: (Int) -> Boolean ->
      fa.ensure({ e }, p).equalUnderTheLaw(fa.flatMap { a -> if (p(a)) just(a) else raiseError(e) }, EQ)
    }

  private suspend fun <F> MonadError<F, Throwable>.monadErrorCatchesNonFatalThrowables(EQ: Eq<Kind<F, Int>>) {
    forAll(Arb.throwable()) { nonFatal: Throwable ->
      catch { throw nonFatal }.equalUnderTheLaw(raiseError(nonFatal), EQ)
    }
  }

  private suspend fun <F> MonadError<F, Throwable>.monadErrorThrowsFatalThrowables(EQ: Eq<Kind<F, Int>>) {
    forAll(Arb.fatalThrowable()) { fatal: Throwable ->
      shouldThrowAny {
        fun <A> itShouldNotComeThisFar(): Kind<F, A> {
          fail("MonadError should rethrow the fatal Throwable: '$fatal'.")
        }

        catch { throw fatal }.equalUnderTheLaw(itShouldNotComeThisFar(), EQ)
      } == fatal
    }
  }

  private suspend fun <F> MonadError<F, Throwable>.monadErrorDerivesRedeemWith(EQ: Eq<Kind<F, Int>>) =
    forAll(Arb.int().applicativeError(this),
      Arb.functionAToB<Throwable, Kind<F, Int>>(Arb.int().applicativeError(this)),
      Arb.functionAToB<Int, Kind<F, Int>>(Arb.int().applicative(this))) { fa, fe, fb ->
      fa.redeemWith(fe, fb).equalUnderTheLaw(fa.flatMap(fb).handleErrorWith(fe), EQ)
    }

  private suspend fun <F> MonadError<F, Throwable>.monadErrorRedeemWithPureIsFlatMap(EQ: Eq<Kind<F, Int>>) =
    forAll(Arb.int().applicative(this),
      Arb.functionAToB<Throwable, Kind<F, Int>>(Arb.int().applicativeError(this)),
      Arb.functionAToB<Int, Kind<F, Int>>(Arb.int().applicative(this))) { fa, fe, fb ->
      fa.redeemWith(fe, fb).equalUnderTheLaw(fa.flatMap(fb), EQ)
    }
}
