package arrow.test.laws

import arrow.Kind
import arrow.core.Eval
import arrow.core.Id
import arrow.core.None
import arrow.core.Option
import arrow.core.extensions.eq
import arrow.core.extensions.id.comonad.extract
import arrow.core.extensions.id.monad.monad
import arrow.core.extensions.monoid
import arrow.core.extensions.option.eq.eq
import arrow.core.some
import arrow.test.concurrency.SideEffect
import arrow.test.generators.GenK
import arrow.test.generators.functionAToB
import arrow.test.generators.intPredicate
import arrow.test.generators.intSmall
import arrow.typeclasses.Eq
import arrow.typeclasses.Foldable
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

object FoldableLaws {

  fun <F> laws(FF: Foldable<F>, GENK: GenK<F>): List<Law> {
    val GEN = GENK.genK(Gen.intSmall())
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
      Law("Foldable Laws: firstOrNone returns None if isEmpty") { FF.firstOrNoneReturnsNoneIfEmpty(GEN) },
      Law("Foldable Laws: firstOrNone returns None if predicate fails") { FF.firstOrNoneReturnsNoneIfPredicateFails(GEN) },
      Law("Foldable Laws: FoldM for Id is equivalent to fold left") { FF.foldMIdIsFoldL(GEN, EQ) },
      Law("Foldable Laws: firstOrNone is consistent with find") { FF.`firstOrNone is consistent with find`(GEN, EQOptionInt) },
      Law("Foldable Laws: firstOrNone is consistent with find matching predicate") { FF.`firstOrNone is consistent with find predicate`(GEN, EQOptionInt) }
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

  fun <F> Foldable<F>.firstOrNoneReturnsNoneIfEmpty(G: Gen<Kind<F, Int>>) =
    forAll(G) { fa: Kind<F, Int> ->
      if (fa.isEmpty()) fa.firstOrNone().isEmpty()
      else fa.firstOrNone().isDefined()
    }

  fun <F> Foldable<F>.firstOrNoneReturnsNoneIfPredicateFails(G: Gen<Kind<F, Int>>) =
    forAll(G) { fa: Kind<F, Int> ->
      fa.firstOrNone { false }.isEmpty()
    }

  fun <F> Foldable<F>.foldMIdIsFoldL(G: Gen<Kind<F, Int>>, EQ: Eq<Int>) =
    forAll(Gen.functionAToB<Int, Int>(Gen.intSmall()), G) { f: (Int) -> Int, fa: Kind<F, Int> ->
      with(Int.monoid()) {
        val foldL: Int = fa.foldLeft(empty()) { acc, a -> acc.combine(f(a)) }
        val foldM: Int = fa.foldM(Id.monad(), empty()) { acc, a -> Id(acc.combine(f(a))) }.extract()
        foldM.equalUnderTheLaw(foldL, EQ)
      }
    }

  fun <F> Foldable<F>.`firstOrNone is consistent with find`(G: Gen<Kind<F, Int>>, EQ: Eq<Option<Int>>) =
    forAll(G) { fa: Kind<F, Int> ->
      fa.firstOrNone().equalUnderTheLaw(fa.find { true }, EQ)
    }

  fun <F> Foldable<F>.`firstOrNone is consistent with find predicate`(G: Gen<Kind<F, Int>>, EQ: Eq<Option<Int>>) =
    forAll(Gen.intPredicate(), G) { f: (Int) -> Boolean, fa: Kind<F, Int> ->
      fa.firstOrNone(f).equalUnderTheLaw(fa.find { f(it) }, EQ)
    }
}
