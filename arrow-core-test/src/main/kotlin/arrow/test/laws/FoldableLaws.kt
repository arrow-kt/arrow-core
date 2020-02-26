package arrow.test.laws

import arrow.Kind
import arrow.core.Eval
import arrow.core.Eval.Companion.always
import arrow.core.Id
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.extensions.eq
import arrow.core.extensions.id.comonad.extract
import arrow.core.extensions.id.monad.monad
import arrow.core.extensions.list.eqK.eqK
import arrow.core.extensions.monoid
import arrow.core.extensions.option.eq.eq
import arrow.core.identity
import arrow.core.some
import arrow.test.concurrency.SideEffect
import arrow.test.generators.GenK
import arrow.test.generators.functionAAToA
import arrow.test.generators.functionABToB
import arrow.test.generators.functionAToB
import arrow.test.generators.functionBAToB
import arrow.test.generators.genEval
import arrow.test.generators.intPredicate
import arrow.test.generators.intSmall
import arrow.typeclasses.Applicative
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Foldable
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object FoldableLaws {

  fun <F> laws(FF: Foldable<F>, GENK: GenK<F>): List<Law> {
    val GEN: Gen<Kind<F, Int>> = GENK.genK(Gen.intSmall())
    val GENB = GENK.genK(Gen.bool())

    val EQ = Int.eq()
    val EQOptionInt = Option.eq(Int.eq())

    return listOf(
      Law("Foldable Laws: foldRight is lazy") { FF.`foldRight is lazy`(GEN, EQ) },
      Law("Foldable Laws: Left fold consistent with foldMap") { FF.leftFoldConsistentWithFoldMap(GEN, EQ) },
      Law("Foldable Laws: Right fold consistent with foldMap") { FF.rightFoldConsistentWithFoldMap(GEN, EQ) },
      Law("Foldable Laws: find matching predicate should return some(value) or none") { FF.`find matching predicate should return some(value) or none`(GEN, EQOptionInt) },
      Law("Foldable Laws: Exists is consistent with find") { FF.existsConsistentWithFind(GEN) },
      Law("Foldable Laws: Exists is lazy") { FF.existsIsLazy(GEN, EQ) },
      Law("Foldable Laws: ForAll is lazy") { FF.forAllIsLazy(GEN, EQ) },
      Law("Foldable Laws: ForAll consistent with exists") { FF.forallConsistentWithExists(GEN) },
      Law("Foldable Laws: ForAll returns true if isEmpty") { FF.forallReturnsTrueIfEmpty(GEN) },
      Law("Foldable Laws: FoldM for Id is equivalent to fold left") { FF.foldMIdIsFoldL(GEN, EQ) },
      Law("Foldable Laws: firstOrNone returns None if isEmpty") { FF.firstOrNoneReturnsNoneIfEmpty(GEN) },
      Law("Foldable Laws: firstOrNone returns None if predicate fails") { FF.firstOrNoneReturnsNoneIfPredicateFails(GEN) },
      Law("Foldable Laws: firstOrNone is consistent with find") { FF.`firstOrNone is consistent with find`(GEN, EQOptionInt) },
      Law("Foldable Laws: firstOrNone is consistent with find matching predicate") { FF.`firstOrNone is consistent with find predicate`(GEN, EQOptionInt) },
      Law("Foldable Laws: toList turn items into a list") { FF.`toList turn items into a list`(GEN, EQ) },
      Law("Foldable Laws: fold returns combination of all items") { FF.`fold should combine all items`(GEN, EQ) },
      Law("Foldable Laws: combineAll is an alias for fold") { FF.`combineAll consistent with fold (alias)`(GEN, EQ) },
      Law("Foldable Laws: reduceLeftToOption combines all items into an optional value") { FF.`reduceLeftToOption returns Option value`(GENB, EQOptionInt) },
      Law("Foldable Laws: reduceRightToOption combines all items into an optional value") { FF.`reduceRightToOption returns Option value`(GENB, EQOptionInt) },
      Law("Foldable Laws: reduceLeftOption consistent with reduceLeftToOption") { FF.`reduceLeftOption returns Option value`(GEN, EQOptionInt) },
      Law("Foldable Laws: reduceRightOption consistent with reduceRightToOption") { FF.`reduceRightOption returns Option value`(GEN, EQOptionInt) }
    )
  }

  fun <F> laws(FF: Foldable<F>, GA: Applicative<F>, GENK: GenK<F>, EQK: EqK<F>): List<Law> {
    val GEN: Gen<Kind<F, Int>> = GENK.genK(Gen.intSmall())

    val EQKInt: Eq<Kind<F, Int>> = EQK.liftEq(Int.eq())
    val EQKUnit = EQK.liftEq(Eq.any())

    return listOf(
      Law("Foldable Laws: orEmpty consistent with just empty") { FF.`orEmpty consistent with just empty`(GA, EQKInt) },
      Law("Foldable Laws: traverse_ consistent with foldRight") { FF.`traverse_ consistent with foldRight`(GA, GENK, GEN, EQKUnit) },
      Law("Foldable Laws: sequence_ consistent with traverse_") { FF.`sequence_ consistent with traverse_`(GA, GENK, EQKUnit) }
    )
  }

  fun <F> Foldable<F>.`foldRight is lazy`(G: Gen<Kind<F, Int>>, EQ: Eq<Int>) =
    forAll(G) { fa: Kind<F, Int> ->
      val sideEffect = SideEffect()
      fa.foldRight(Eval.now(0)) { _, _ ->
        sideEffect.increment()
        Eval.now(1)
      }.value()
      val expected = if (fa.isEmpty()) 0 else 1
      sideEffect.counter.equalUnderTheLaw(expected, EQ)
    }

  fun <F> Foldable<F>.leftFoldConsistentWithFoldMap(G: Gen<Kind<F, Int>>, EQ: Eq<Int>) =
    forAll(Gen.functionAToB<Int, Int>(Gen.intSmall()), G) { f: (Int) -> Int, fa: Kind<F, Int> ->
      with(Int.monoid()) {
        fa.foldMap(this, f).equalUnderTheLaw(fa.foldLeft(empty()) { acc, a -> acc.combine(f(a)) }, EQ)
      }
    }

  fun <F> Foldable<F>.rightFoldConsistentWithFoldMap(G: Gen<Kind<F, Int>>, EQ: Eq<Int>) =
    forAll(Gen.functionAToB<Int, Int>(Gen.intSmall()), G) { f: (Int) -> Int, fa: Kind<F, Int> ->
      with(Int.monoid()) {
        fa.foldMap(this, f).equalUnderTheLaw(fa.foldRight(Eval.later { empty() }) { a, lb: Eval<Int> -> lb.map { f(a).combine(it) } }.value(), EQ)
      }
    }

  fun <F> Foldable<F>.`find matching predicate should return some(value) or none`(G: Gen<Kind<F, Int>>, EQ: Eq<Option<Int>>) =
    forAll(Gen.intPredicate(), G) { f: (Int) -> Boolean, fa: Kind<F, Int> ->
      val expected = fa.foldRight(Eval.now<Option<Int>>(None)) { a, lb -> if (f(a)) Eval.now(a.some()) else lb }.value()
      fa.find { f(it) }.equalUnderTheLaw(expected, EQ)
    }

  fun <F> Foldable<F>.existsConsistentWithFind(G: Gen<Kind<F, Int>>) =
    forAll(Gen.intPredicate(), G) { f: (Int) -> Boolean, fa: Kind<F, Int> ->
      fa.exists(f).equalUnderTheLaw(fa.find(f).fold({ false }, { true }), Eq.any())
    }

  fun <F> Foldable<F>.existsIsLazy(G: Gen<Kind<F, Int>>, EQ: Eq<Int>) =
    forAll(G) { fa: Kind<F, Int> ->
      val sideEffect = SideEffect()
      fa.exists { _ ->
        sideEffect.increment()
        true
      }
      val expected = if (fa.isEmpty()) 0 else 1
      sideEffect.counter.equalUnderTheLaw(expected, EQ)
    }

  fun <F> Foldable<F>.forAllIsLazy(G: Gen<Kind<F, Int>>, EQ: Eq<Int>) =
    forAll(G) { fa: Kind<F, Int> ->
      val sideEffect = SideEffect()
      fa.all { _ ->
        sideEffect.increment()
        true
      }
      val expected = if (fa.isEmpty()) 0 else fa.size(Long.monoid())
      sideEffect.counter.equalUnderTheLaw(expected.toInt(), EQ)
    }

  fun <F> Foldable<F>.forallConsistentWithExists(G: Gen<Kind<F, Int>>) =
    forAll(Gen.intPredicate(), G) { f: (Int) -> Boolean, fa: Kind<F, Int> ->
      if (fa.all(f)) {
        // if f is true for all elements, then there cannot be an element for which
        // it does not hold.
        val negationExists = fa.exists { a -> !(f(a)) }
        // if f is true for all elements, then either there must be no elements
        // or there must exist an element for which it is true.
        !negationExists && (fa.isEmpty() || fa.exists(f))
      } else true
    }

  fun <F> Foldable<F>.forallReturnsTrueIfEmpty(G: Gen<Kind<F, Int>>) =
    forAll(Gen.intPredicate(), G) { f: (Int) -> Boolean, fa: Kind<F, Int> ->
      !fa.isEmpty() || fa.all(f)
    }

  fun <F> Foldable<F>.foldMIdIsFoldL(G: Gen<Kind<F, Int>>, EQ: Eq<Int>) =
    forAll(Gen.functionAToB<Int, Int>(Gen.intSmall()), G) { f: (Int) -> Int, fa: Kind<F, Int> ->
      with(Int.monoid()) {
        val foldL: Int = fa.foldLeft(empty()) { acc, a -> acc.combine(f(a)) }
        val foldM: Int = fa.foldM(Id.monad(), empty()) { acc, a -> Id(acc.combine(f(a))) }.extract()
        foldM.equalUnderTheLaw(foldL, EQ)
      }
    }

  fun <F> Foldable<F>.firstOrNoneReturnsNoneIfEmpty(G: Gen<Kind<F, Int>>) =
    forAll(G) { fa: Kind<F, Int> ->
      if (fa.isEmpty()) fa.firstOrNone().isEmpty()
      else fa.firstOrNone().isDefined()
    }

  fun <F> Foldable<F>.firstOrNoneReturnsNoneIfPredicateFails(G: Gen<Kind<F, Int>>) =
    forAll(G) { fa: Kind<F, Int> ->
      fa.firstOrNone { false }.isEmpty()
    }

  fun <F> Foldable<F>.`firstOrNone is consistent with find`(G: Gen<Kind<F, Int>>, EQ: Eq<Option<Int>>) =
    forAll(G) { fa: Kind<F, Int> ->
      fa.firstOrNone().equalUnderTheLaw(fa.find { true }, EQ)
    }

  fun <F> Foldable<F>.`firstOrNone is consistent with find predicate`(G: Gen<Kind<F, Int>>, EQ: Eq<Option<Int>>) =
    forAll(Gen.intPredicate(), G) { f: (Int) -> Boolean, fa: Kind<F, Int> ->
      fa.firstOrNone(f).equalUnderTheLaw(fa.find { f(it) }, EQ)
    }

  fun <F> Foldable<F>.`toList turn items into a list`(G: Gen<Kind<F, Int>>, EQ: Eq<Int>) =
    forAll(G) { fa: Kind<F, Int> ->
      val result = fa.toList()
      val expected = fa.foldRight(Eval.now(emptyList<Int>())) { v, acc -> acc.map { listOf(v) + it } }.value()
      result.eqK(expected, EQ)
    }

  fun <F> Foldable<F>.`fold should combine all items`(G: Gen<Kind<F, Int>>, EQ: Eq<Int>) =
    forAll(G) { fa: Kind<F, Int> ->
      with(Int.monoid()) {
        fa.fold(this).equalUnderTheLaw(fa.foldLeft(empty()) { acc, a -> acc.combine(a) }, EQ)
      }
    }

  fun <F> Foldable<F>.`combineAll consistent with fold (alias)`(G: Gen<Kind<F, Int>>, EQ: Eq<Int>) =
    forAll(G) { fa: Kind<F, Int> ->
      with(Int.monoid()) {
        fa.combineAll(this).equalUnderTheLaw(fa.fold(this), EQ)
      }
    }

  fun <F> Foldable<F>.`reduceLeftToOption returns Option value`(G: Gen<Kind<F, Boolean>>, EQ: Eq<Option<Int>>) =
    forAll(
      Gen.functionAToB<Boolean, Int>(Gen.intSmall()),
      Gen.functionBAToB<Boolean, Int>(Gen.intSmall()),
      G) { f: (Boolean) -> Int, g: (Int, Boolean) -> Int, fa: Kind<F, Boolean> ->

      val expected = fa.foldLeft(Option.empty<Int>()) { option, a ->
        when (option) {
          is Some<Int> -> Some(g(option.t, a))
          is None -> Some(f(a))
        }
      }
      fa.reduceLeftToOption(f, g).equalUnderTheLaw(expected, EQ)
    }

  fun <F> Foldable<F>.`reduceRightToOption returns Option value`(G: Gen<Kind<F, Boolean>>, EQ: Eq<Option<Int>>) =
    forAll(
      Gen.functionAToB<Boolean, Int>(Gen.intSmall()),
      Gen.functionABToB<Boolean, Eval<Int>>(Gen.genEval(Gen.intSmall())),
      G) { f: (Boolean) -> Int, g: (Boolean, Eval<Int>) -> Eval<Int>, fa: Kind<F, Boolean> ->

      val expected = fa.foldRight(Eval.Now<Option<Int>>(Option.empty())) { a: Boolean, lb: Eval<Option<Int>> ->
        lb.flatMap { option ->
          when (option) {
            is Some<Int> -> g(a, Eval.Now(option.t)).map { Some(it) }
            is None -> Eval.Later { Some(f(a)) }
          }
        }
      }
      fa.reduceRightToOption(f, g).value().equalUnderTheLaw(expected.value(), EQ)
    }

  fun <F> Foldable<F>.`reduceLeftOption returns Option value`(G: Gen<Kind<F, Int>>, EQ: Eq<Option<Int>>) =
    forAll(Gen.functionAAToA(Gen.intSmall()), G) { f: (Int, Int) -> Int, fa: Kind<F, Int> ->
      fa.reduceLeftOption(f).equalUnderTheLaw(fa.reduceLeftToOption({ a -> a }, f), EQ)
    }

  fun <F> Foldable<F>.`reduceRightOption returns Option value`(G: Gen<Kind<F, Int>>, EQ: Eq<Option<Int>>) =
    forAll(Gen.functionABToB<Int, Eval<Int>>(Gen.genEval(Gen.intSmall())), G) { f: (Int, Eval<Int>) -> Eval<Int>, fa ->
      fa.reduceRightOption(f).value().equalUnderTheLaw(fa.reduceRightToOption({ a -> a }, f).value(), EQ)
    }

  fun <F> Foldable<F>.`orEmpty consistent with just empty`(GA: Applicative<F>, EQ: Eq<Kind<F, Int>>) =
    GA.run {
      with(Int.monoid()) {
        orEmpty(this@run, this).equalUnderTheLaw(just(this.empty()), EQ)
      }
    }

  fun <F> Foldable<F>.`traverse_ consistent with foldRight`(GA: Applicative<F>, GENK: GenK<F>, G: Gen<Kind<F, Int>>, EQ: Eq<Kind<F, Unit>>) =
    forAll(Gen.functionAToB<Int, Kind<F, Int>>(GENK.genK(Gen.intSmall())), G) { f: (Int) -> Kind<F, Int>, fa: Kind<F, Int> ->
      GA.run {
        val expected = fa.foldRight(always { GA.just(Unit) }) { a, acc -> GA.run { f(a).apEval(acc.map { it.map { { _: Int -> Unit } } }) } }.value()
        fa.traverse_(this, f).equalUnderTheLaw(expected, EQ)
      }
    }

  fun <F> Foldable<F>.`sequence_ consistent with traverse_`(GA: Applicative<F>, GENK: GenK<F>, EQ: Eq<Kind<F, Unit>>) =
    forAll(GENK.genK(GENK.genK(Gen.intSmall()))) { fa: Kind<F, Kind<F, Int>> ->
      GA.run {
        fa.sequence_(this).equalUnderTheLaw(fa.traverse_(this, ::identity), EQ)
      }
    }
}
