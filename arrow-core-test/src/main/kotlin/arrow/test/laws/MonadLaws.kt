package arrow.test.laws

import arrow.Kind
import arrow.core.Eval
import arrow.core.Left
import arrow.core.Right
import arrow.core.Tuple2
import arrow.core.extensions.eq
import arrow.core.extensions.tuple2.eq.eq
import arrow.core.identity
import arrow.mtl.Kleisli
import arrow.test.generators.GenK
import arrow.test.generators.applicative
import arrow.test.generators.either
import arrow.test.generators.functionAToB
import arrow.test.generators.functionToA
import arrow.typeclasses.Apply
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import arrow.typeclasses.Selective
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object MonadLaws {

  fun <F> laws(M: Monad<F>, GENK: GenK<F>, EQK: EqK<F>): List<Law> {
    val EQ = EQK.liftEq(Int.eq())
    val G = GENK.genK(Gen.int())

    return SelectiveLaws.laws(M, GENK, EQK) +
      listOf(
        Law("Monad Laws: left identity") { M.leftIdentity(G, EQ) },
        Law("Monad Laws: right identity") { M.rightIdentity(G, EQ) },
        Law("Monad Laws: kleisli left identity") { M.kleisliLeftIdentity(G, EQ) },
        Law("Monad Laws: kleisli right identity") { M.kleisliRightIdentity(G, EQ) },
        Law("Monad Laws: monad comprehensions") { M.monadComprehensions(EQ) },
        Law("Monad Laws: stack safe") { M.stackSafety(5000, EQ) }
      )
  }

  fun <F> laws(
    M: Monad<F>,
    FF: Functor<F>,
    AP: Apply<F>,
    SL: Selective<F>,
    GENK: GenK<F>,
    EQK: EqK<F>
  ): List<Law> {
    val EQ = EQK.liftEq(Int.eq())
    val EQTuple2: Eq<Kind<F, Tuple2<Int, Int>>> = EQK.liftEq(Tuple2.eq(Int.eq(), Int.eq()))
    val G = GENK.genK(Gen.int())

    return laws(M, GENK, EQK) + listOf(
      Law("Monad Laws: monad map should be consistent with functor map") { M.derivedMapConsistent(G, FF, EQ) },
      Law("Monad Laws: monad ap should be consistent with applicative ap") { M.derivedApConsistent(GENK, AP, EQ) },
      Law("Monad Laws: monad apTap should be consistent with applicative apTap") { M.derivedApTapConsistent(GENK, AP, EQ) },
      Law("Monad Laws: monad followedBy should be consistent with applicative followedBy") { M.derivedFollowedByConsistent(GENK, AP, EQ) },
      Law("Monad Laws: monad selective should be consistent with selective selective") { M.derivedSelectiveConsistent(GENK, SL, EQ) },
      Law("Monad Laws: monad flatten should be consistent") { M.derivedFlattenConsistent(GENK, EQ) },
      Law("Monad Laws: monad followedByEval should be consistent") { M.derivedFollowedByEvalConsistent(GENK, EQ) },
      Law("Monad Laws: monad productL should be consistent") { M.derivedProductLConsistent(GENK, EQ) },
      Law("Monad Laws: monad productLEval should be consistent") { M.derivedProductLEvalConsistent(GENK, EQ) },
      Law("Monad Laws: monad product should be consistent with applicative product") { M.derivedProductConsistent(GENK, AP, EQTuple2) },
      Law("Monad Laws: monad mproduct should be consistent") { M.derivedMProductConsistent(GENK, EQTuple2) },
      Law("Monad Laws: monad ifM should be consistent") { M.derivedIfMConsistent(GENK, EQ) }
    )
  }

  fun <F> Monad<F>.leftIdentity(G: Gen<Kind<F, Int>>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.functionAToB<Int, Kind<F, Int>>(G), Gen.int()) { f: (Int) -> Kind<F, Int>, a: Int ->
      just(a).flatMap(f).equalUnderTheLaw(f(a), EQ)
    }

  fun <F> Monad<F>.rightIdentity(G: Gen<Kind<F, Int>>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(G) { fa: Kind<F, Int> ->
      fa.flatMap { just(it) }.equalUnderTheLaw(fa, EQ)
    }

  fun <F> Monad<F>.kleisliLeftIdentity(G: Gen<Kind<F, Int>>, EQ: Eq<Kind<F, Int>>) {
    val M = this
    forAll(Gen.functionAToB<Int, Kind<F, Int>>(G), Gen.int()) { f: (Int) -> Kind<F, Int>, a: Int ->
      (Kleisli { n: Int -> just(n) }.andThen(M, Kleisli(f)).run(a).equalUnderTheLaw(f(a), EQ))
    }
  }

  fun <F> Monad<F>.kleisliRightIdentity(G: Gen<Kind<F, Int>>, EQ: Eq<Kind<F, Int>>) {
    val M = this
    forAll(Gen.functionAToB<Int, Kind<F, Int>>(G), Gen.int()) { f: (Int) -> Kind<F, Int>, a: Int ->
      (Kleisli(f).andThen(M, Kleisli { n: Int -> just(n) }).run(a).equalUnderTheLaw(f(a), EQ))
    }
  }

  fun <F> Monad<F>.stackSafety(iter: Int = 5000, EQ: Eq<Kind<F, Int>>) {
    val res = tailRecM(0) { i -> just(if (i < iter) Left(i + 1) else Right(i)) }
    res.equalUnderTheLaw(just(iter), EQ)
  }

  fun <F> Monad<F>.monadComprehensions(EQ: Eq<Kind<F, Int>>): Unit =
    forAll(Gen.int()) { num: Int ->
      fx.monad {
        val a = !just(num)
        val b = !just(a + 1)
        val c = !just(b + 1)
        c
      }.equalUnderTheLaw(just(num + 2), EQ)
    }

  fun <F> Monad<F>.derivedSelectiveConsistent(GK: GenK<F>, SL: Selective<F>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(GK.genK(Gen.either(Gen.int(), Gen.int())), GK.genK(Gen.functionAToB<Int, Int>(Gen.int()))) { x, f ->
      SL.run { x.select(f) }.equalUnderTheLaw(x.select(f), EQ)
    }

  fun <F> Monad<F>.derivedMapConsistent(G: Gen<Kind<F, Int>>, FF: Functor<F>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(G, Gen.functionAToB<Int, Int>(Gen.int())) { fa, f ->
      FF.run { fa.map(f) }.equalUnderTheLaw(fa.map(f), EQ)
    }

  fun <F> Monad<F>.derivedApConsistent(GK: GenK<F>, AP: Apply<F>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(GK.genK(Gen.int()), GK.genK(Gen.functionAToB<Int, Int>(Gen.int()))) { fa, ff ->
      AP.run { fa.ap(ff) }.equalUnderTheLaw(fa.ap(ff), EQ)
    }

  fun <F> Monad<F>.derivedFollowedByConsistent(GK: GenK<F>, AP: Apply<F>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(GK.genK(Gen.int()), GK.genK(Gen.int())) { fa, fb ->
      AP.run { fa.followedBy(fb) }.equalUnderTheLaw(fa.followedBy(fb), EQ)
    }

  fun <F> Monad<F>.derivedApTapConsistent(GK: GenK<F>, AP: Apply<F>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(GK.genK(Gen.int()), GK.genK(Gen.int())) { fa, fb ->
      AP.run { fa.apTap(fb) }.equalUnderTheLaw(fa.apTap(fb), EQ)
    }

  fun <F> Monad<F>.derivedFlattenConsistent(GK: GenK<F>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(GK.genK(GK.genK(Gen.int()))) { fa: Kind<F, Kind<F, Int>> ->
      fa.flatten().equalUnderTheLaw(fa.flatMap(::identity), EQ)
    }

  fun <F> Monad<F>.derivedFollowedByEvalConsistent(GK: GenK<F>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(GK.genK(Gen.int()), GK.genK(Gen.int())) { fa, fb ->
      val fbEval: Eval<Kind<F, Int>> = Eval.just(fb)
      fa.followedByEval(fbEval).equalUnderTheLaw(fa.flatMap { fbEval.value() }, EQ)
    }

  fun <F> Monad<F>.derivedProductLConsistent(GK: GenK<F>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(GK.genK(Gen.int()), GK.genK(Gen.int())) { fa, fb ->
      fa.productL(fb).equalUnderTheLaw(fa.flatMap { a -> fb.map { a } }, EQ)
    }

  fun <F> Monad<F>.derivedProductLEvalConsistent(GK: GenK<F>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(GK.genK(Gen.int()), GK.genK(Gen.int())) { fa, fb ->
      val fbEval: Eval<Kind<F, Int>> = Eval.just(fb)
      fa.productLEval(fbEval).equalUnderTheLaw(fa.flatMap { a -> fbEval.value().map { a } }, EQ)
    }

  fun <F> Monad<F>.derivedProductConsistent(GK: GenK<F>, AP: Apply<F>, EQ: Eq<Kind<F, Tuple2<Int, Int>>>): Unit =
    forAll(GK.genK(Gen.int()), GK.genK(Gen.int())) { fa, fb ->
      AP.run { fa.product(fb) }.equalUnderTheLaw(fa.product(fb), EQ)
    }

  fun <F> Monad<F>.derivedMProductConsistent(GK: GenK<F>, EQ: Eq<Kind<F, Tuple2<Int, Int>>>): Unit =
    forAll(GK.genK(Gen.int()), Gen.functionAToB<Int, Kind<F, Int>>(Gen.int().applicative(this))) { fa, fb: (Int) -> Kind<F, Int> ->
      fa.mproduct(fb).equalUnderTheLaw(fa.flatMap { a -> fb(a).map { Tuple2(a, it) } }, EQ)
    }

  fun <F> Monad<F>.derivedIfMConsistent(GK: GenK<F>, EQ: Eq<Kind<F, Int>>): Unit =
    forAll(GK.genK(Gen.bool()), Gen.functionToA(GK.genK(Gen.int())), Gen.functionToA(GK.genK(Gen.int()))) { fa, fTrue, fFalse ->
      fa.ifM(fTrue, fFalse).equalUnderTheLaw(fa.flatMap { if (it) fTrue() else fFalse() }, EQ)
    }
}
