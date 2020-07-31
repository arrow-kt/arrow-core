package arrow.core.extensions

import arrow.Kind
import arrow.Kind2
import arrow.core.Either
import arrow.core.Eval
import arrow.core.ForIor
import arrow.core.Ior
import arrow.core.Ior.Both
import arrow.core.Ior.Left
import arrow.core.Ior.Right
import arrow.core.IorOf
import arrow.core.IorPartialOf
import arrow.core.ap
import arrow.core.extensions.ior.eq.eq
import arrow.core.extensions.ior.monad.monad
import arrow.core.fix
import arrow.core.flatMap
import arrow.core.leftIor
import arrow.core.rightIor
import arrow.extension
import arrow.typeclasses.Align
import arrow.typeclasses.Applicative
import arrow.typeclasses.Apply
import arrow.typeclasses.BiSemigroup
import arrow.typeclasses.Bicrosswalk
import arrow.typeclasses.Bifoldable
import arrow.typeclasses.Bifunctor
import arrow.typeclasses.Bitraverse
import arrow.typeclasses.Crosswalk
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.EqK2
import arrow.typeclasses.Foldable
import arrow.typeclasses.Functor
import arrow.typeclasses.Hash
import arrow.typeclasses.Monad
import arrow.typeclasses.MonadSyntax
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup
import arrow.typeclasses.Show
import arrow.typeclasses.Traverse
import arrow.typeclasses.plus
import arrow.undocumented

fun <L, R> Ior<L, R>.combine(SGL: Semigroup<L>, SGR: Semigroup<R>, b: Ior<L, R>): Ior<L, R> =
  (SGL + SGR).combine(this, b)

private fun <L, R> BiSemigroup<L, R>.combine(a: Ior<L, R>, b: Ior<L, R>): Ior<L, R> =
  when (a) {
    is Left -> when (b) {
      is Left -> Left(a.value + b.value)
      is Right -> a//Both(a.value, b.value)
      is Both -> Both(a.value + b.leftValue, b.rightValue)
    }
    is Right -> when (b) {
      is Left -> b//Both(b.value, a.value)
      is Right -> Right(a.value + b.value)
      is Both -> Both(b.leftValue, a.value + b.rightValue)
    }
    is Both -> when (b) {
      is Left -> Both(a.leftValue + b.value, a.rightValue)
      is Right -> Both(a.leftValue, a.rightValue + b.value)
      is Both -> Both(a.leftValue + b.leftValue, a.rightValue + b.rightValue)
    }
  }

@extension
interface IorSemigroup<L, R> : Semigroup<Ior<L, R>> {

  fun SGL(): Semigroup<L>
  fun SGR(): Semigroup<R>

  override fun Ior<L, R>.combine(b: Ior<L, R>): Ior<L, R> = fix().combine(SGL(), SGR(), b)

}

@extension
interface IorMonoid<L, R> : Monoid<Ior<L, R>>, IorSemigroup<L, R> {
  fun MOL(): Monoid<L>
  fun MOR(): Monoid<R>

  override fun SGL(): Semigroup<L> = MOL()
  override fun SGR(): Semigroup<R> = MOR()

  override fun empty(): Ior<L, R> = Both(MOL().empty(), MOR().empty())
}

@extension
@undocumented
interface IorFunctor<L> : Functor<IorPartialOf<L>> {
  override fun <A, B> Kind<IorPartialOf<L>, A>.map(f: (A) -> B): Ior<L, B> = fix().map(f)
}

@extension
interface IorBifunctor : Bifunctor<ForIor> {
  override fun <A, B, C, D> Kind2<ForIor, A, B>.bimap(fl: (A) -> C, fr: (B) -> D): Kind2<ForIor, C, D> =
    fix().bimap(fl, fr)
}

@extension
interface IorApply<L> : Apply<IorPartialOf<L>>, IorFunctor<L> {

  fun SL(): Semigroup<L>

  override fun <A, B> Kind<IorPartialOf<L>, A>.map(f: (A) -> B): Ior<L, B> = fix().map(f)

  override fun <A, B> Kind<IorPartialOf<L>, A>.ap(ff: Kind<IorPartialOf<L>, (A) -> B>): Ior<L, B> =
    fix().ap(SL(), ff)

  override fun <A, B> Kind<IorPartialOf<L>, A>.apEval(ff: Eval<Kind<IorPartialOf<L>, (A) -> B>>): Eval<Kind<IorPartialOf<L>, B>> =
    fix().fold({ l ->
      Eval.now(l.leftIor())
    }, { r ->
      ff.map { it.fix().map { f -> f(r) } }
    }, { l, r ->
      ff.map {
        it.fix().fold({ ll ->
          SL().run { l + ll }.leftIor()
        }, { f ->
          Both(l, f(r))
        }, { ll, f ->
          Both(SL().run { l + ll }, f(r))
        })
      }
    })
}

@extension
interface IorApplicative<L> : Applicative<IorPartialOf<L>>, IorApply<L> {

  override fun SL(): Semigroup<L>

  override fun <A> just(a: A): Ior<L, A> = Right(a)

  override fun <A, B> Kind<IorPartialOf<L>, A>.map(f: (A) -> B): Ior<L, B> = fix().map(f)

  override fun <A, B> Kind<IorPartialOf<L>, A>.ap(ff: Kind<IorPartialOf<L>, (A) -> B>): Ior<L, B> =
    fix().ap(SL(), ff)
}

@extension
interface IorMonad<L> : Monad<IorPartialOf<L>>, IorApplicative<L> {

  override fun SL(): Semigroup<L>

  override fun <A, B> Kind<IorPartialOf<L>, A>.map(f: (A) -> B): Ior<L, B> = fix().map(f)

  override fun <A, B> Kind<IorPartialOf<L>, A>.flatMap(f: (A) -> Kind<IorPartialOf<L>, B>): Ior<L, B> =
    fix().flatMap(SL()) { f(it).fix() }

  override fun <A, B> Kind<IorPartialOf<L>, A>.ap(ff: Kind<IorPartialOf<L>, (A) -> B>): Ior<L, B> =
    fix().ap(SL(), ff)

  override fun <A, B> tailRecM(a: A, f: (A) -> IorOf<L, Either<A, B>>): Ior<L, B> =
    Ior.tailRecM(a, f, SL())
}

@extension
interface IorFoldable<L> : Foldable<IorPartialOf<L>> {

  override fun <B, C> Kind<IorPartialOf<L>, B>.foldLeft(b: C, f: (C, B) -> C): C = fix().foldLeft(b, f)

  override fun <B, C> Kind<IorPartialOf<L>, B>.foldRight(lb: Eval<C>, f: (B, Eval<C>) -> Eval<C>): Eval<C> =
    fix().foldRight(lb, f)
}

@extension
interface IorTraverse<L> : Traverse<IorPartialOf<L>>, IorFoldable<L> {

  override fun <G, B, C> IorOf<L, B>.traverse(AP: Applicative<G>, f: (B) -> Kind<G, C>): Kind<G, Ior<L, C>> =
    fix().traverse(AP, f)
}

@extension
interface IorBifoldable : Bifoldable<ForIor> {
  override fun <A, B, C> IorOf<A, B>.bifoldLeft(c: C, f: (C, A) -> C, g: (C, B) -> C): C =
    fix().bifoldLeft(c, f, g)

  override fun <A, B, C> IorOf<A, B>.bifoldRight(c: Eval<C>, f: (A, Eval<C>) -> Eval<C>, g: (B, Eval<C>) -> Eval<C>): Eval<C> =
    fix().bifoldRight(c, f, g)
}

@extension
interface IorBitraverse : Bitraverse<ForIor>, IorBifoldable {
  override fun <G, A, B, C, D> IorOf<A, B>.bitraverse(AP: Applicative<G>, f: (A) -> Kind<G, C>, g: (B) -> Kind<G, D>): Kind<G, IorOf<C, D>> =
    fix().let {
      AP.run {
        it.fold({ f(it).map { Left(it) } }, { g(it).map { Right(it) } },
          { a, b -> mapN(f(a), g(b)) { Both(it.a, it.b) } })
      }
    }
}

@extension
interface IorEq<L, R> : Eq<Ior<L, R>> {

  fun EQL(): Eq<L>

  fun EQR(): Eq<R>

  override fun Ior<L, R>.eqv(b: Ior<L, R>): Boolean = when (this) {
    is Left -> when (b) {
      is Both -> false
      is Right -> false
      is Left -> EQL().run { value.eqv(b.value) }
    }
    is Both -> when (b) {
      is Left -> false
      is Both -> EQL().run { leftValue.eqv(b.leftValue) } && EQR().run { rightValue.eqv(b.rightValue) }
      is Right -> false
    }
    is Right -> when (b) {
      is Left -> false
      is Both -> false
      is Right -> EQR().run { value.eqv(b.value) }
    }
  }
}

@extension
interface IorEqK<A> : EqK<IorPartialOf<A>> {
  fun EQA(): Eq<A>

  override fun <B> Kind<IorPartialOf<A>, B>.eqK(other: Kind<IorPartialOf<A>, B>, EQ: Eq<B>): Boolean =
    Ior.eq(EQA(), EQ).run {
      this@eqK.fix().eqv(other.fix())
    }
}

@extension
interface IorEqK2 : EqK2<ForIor> {
  override fun <A, B> Kind2<ForIor, A, B>.eqK(other: Kind2<ForIor, A, B>, EQA: Eq<A>, EQB: Eq<B>): Boolean =
    (this.fix() to other.fix()).let {
      Ior.eq(EQA, EQB).run {
        it.first.eqv(it.second)
      }
    }
}

@extension
interface IorShow<L, R> : Show<Ior<L, R>> {
  fun SL(): Show<L>
  fun SR(): Show<R>
  override fun Ior<L, R>.show(): String = show(SL(), SR())
}

@extension
interface IorHash<L, R> : Hash<Ior<L, R>>, IorEq<L, R> {

  fun HL(): Hash<L>
  fun HR(): Hash<R>

  override fun EQL(): Eq<L> = HL()

  override fun EQR(): Eq<R> = HR()

  override fun Ior<L, R>.hash(): Int = when (this) {
    is Left -> HL().run { value.hash() }
    is Right -> HR().run { value.hash() }
    is Both -> 31 * HL().run { leftValue.hash() } + HR().run { rightValue.hash() }
  }
}

fun <L, R> Ior.Companion.fx(SL: Semigroup<L>, c: suspend MonadSyntax<IorPartialOf<L>>.() -> R): Ior<L, R> =
  Ior.monad(SL).fx.monad(c).fix()

@extension
interface IorCrosswalk<L> : Crosswalk<IorPartialOf<L>>, IorFunctor<L>, IorFoldable<L> {
  override fun <F, A, B> crosswalk(ALIGN: Align<F>, a: Kind<IorPartialOf<L>, A>, fa: (A) -> Kind<F, B>): Kind<F, Kind<IorPartialOf<L>, B>> {
    return when (val ior = a.fix()) {
      is Left -> ALIGN.run { empty<Kind<IorPartialOf<L>, B>>() }
      is Both -> ALIGN.run { fa(ior.rightValue).map { Both(ior.leftValue, it) } }
      is Right -> ALIGN.run { fa(ior.value).map { it.rightIor() } }
    }
  }
}

@extension
interface IorBicrosswalk : Bicrosswalk<ForIor>, IorBifunctor, IorBifoldable {
  override fun <F, A, B, C, D> bicrosswalk(
    ALIGN: Align<F>,
    tab: Kind2<ForIor, A, B>,
    fa: (A) -> Kind<F, C>,
    fb: (B) -> Kind<F, D>
  ): Kind<F, Kind2<ForIor, C, D>> =
    when (val e = tab.fix()) {
      is Left -> ALIGN.run {
        fa(e.value).map { it.leftIor() }
      }
      is Right -> ALIGN.run {
        fb(e.value).map { it.rightIor() }
      }
      is Both -> ALIGN.run {
        align(fa(e.leftValue), fb(e.rightValue))
      }
    }
}

operator fun <L, R> Ior<L, R>.component1(): L? = leftOrNull()
operator fun <L, R> Ior<L, R>.component2(): R? = orNull()
