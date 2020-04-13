package arrow.core.extensions

import arrow.Kind
import arrow.Kind2
import arrow.core.Either
import arrow.core.Eval
import arrow.core.ForIor
import arrow.core.Ior
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
import arrow.typeclasses.Semigroup
import arrow.typeclasses.Show
import arrow.typeclasses.Traverse
import arrow.undocumented

@extension
@undocumented
interface IorFunctor : Functor<ForIor> {
  override fun <A, B> IorPartialOf<A>.map(f: (A) -> B): IorPartialOf<B> =
    fix().map(f).unnest()
}

@extension
interface IorBifunctor : Bifunctor<ForIor> {
  override fun <A, B, C, D> IorOf<A, B>.bimap(fl: (A) -> C, fr: (B) -> D): IorOf<C, D> =
    fix().bimap(fl, fr)
}

@extension
interface IorApply<L> : Apply<ForIor>, IorFunctor {

  fun SL(): Semigroup<L>

  override fun <A, B> IorPartialOf<A>.map(f: (A) -> B): IorPartialOf<B> =
    fix().map(f).unnest()

  override fun <A, B> IorPartialOf<A>.ap(ff: IorPartialOf<(A) -> B>): IorPartialOf<B> =
    fix().ap(SL(), ff).unnest()

  override fun <A, B> IorPartialOf<A>.apEval(ff: Eval<IorPartialOf<(A) -> B>>): Eval<IorPartialOf<B>> =
    fix().fold({ l ->
      Eval.now(l.leftIor().unnest<B>())
    }, { r ->
      ff.map { it.fix().map { f -> f(r) }.unnest<B>() }
    }, { l, r ->
      ff.map { it.fix().fold({ ll ->
        SL().run { l + ll }.leftIor()
      }, { f ->
        Ior.Both(l, f(r))
      }, { ll, f ->
        Ior.Both(SL().run { l + ll }, f(r))
      }).unnest<B>() }
    })
}

@extension
interface IorApplicative<L> : Applicative<ForIor>, IorApply<L> {

  override fun SL(): Semigroup<L>

  override fun <A> just(a: A): IorPartialOf<A> =
    Ior.Right(a).unnest()

  override fun <A, B> IorPartialOf<A>.map(f: (A) -> B): IorPartialOf<B> =
    fix().map(f).unnest()

  override fun <A, B> IorPartialOf<A>.ap(ff: IorPartialOf<(A) -> B>): IorPartialOf<B> =
    fix().ap(SL(), ff).unnest()
}

@extension
interface IorMonad<L> : Monad<ForIor>, IorApplicative<L> {

  override fun SL(): Semigroup<L>

  override fun <A, B> IorPartialOf<A>.map(f: (A) -> B): IorPartialOf<B> =
    fix().map(f).unnest()

  override fun <A, B> IorPartialOf<A>.flatMap(f: (A) -> IorPartialOf<B>): IorPartialOf<B> =
    fix().flatMap(SL()) { f(it).fix() }.unnest()

  override fun <A, B> IorPartialOf<A>.ap(ff: IorPartialOf<(A) -> B>): IorPartialOf<B> =
    fix().ap(SL(), ff).unnest()

  override fun <A, B> tailRecM(a: A, f: (A) -> IorPartialOf<Either<A, B>>): IorPartialOf<B> =
    Ior.tailRecM(a, f, SL()).unnest()
}

@extension
interface IorFoldable : Foldable<ForIor> {

  override fun <B, C> IorPartialOf<B>.foldLeft(b: C, f: (C, B) -> C): C = fix().foldLeft(b, f)

  override fun <B, C> IorPartialOf<B>.foldRight(lb: Eval<C>, f: (B, Eval<C>) -> Eval<C>): Eval<C> =
    fix().foldRight(lb, f)
}

@extension
interface IorTraverse : Traverse<ForIor>, IorFoldable {

  override fun <G, B, C> IorPartialOf<B>.traverse(AP: Applicative<G>, f: (B) -> Kind<G, C>): Kind<G, IorPartialOf<C>> =
    fix().traverse(AP, f).unnest()
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
        it.fold(
          { f(it).map { Ior.Left(it) } },
          { g(it).map { Ior.Right(it) } },
          { a, b -> mapN(f(a), g(b)) { (a2, b2) -> Ior.Both(a2, b2) } }
        )
      }
    }
}

@extension
interface IorEq<L, R> : Eq<IorOf<L, R>> {

  fun EQL(): Eq<L>

  fun EQR(): Eq<R>

  override fun IorOf<L, R>.eqv(other: IorOf<L, R>): Boolean = when (val a = this.fix()) {
    is Ior.Left -> when (val b = other.fix()) {
      is Ior.Both -> false
      is Ior.Right -> false
      is Ior.Left -> EQL().run { a.value.eqv(b.value) }
    }
    is Ior.Both -> when (val b = other.fix()) {
      is Ior.Left -> false
      is Ior.Both -> EQL().run { a.leftValue.eqv(b.leftValue) } && EQR().run { b.rightValue.eqv(b.rightValue) }
      is Ior.Right -> false
    }
    is Ior.Right -> when (val b = other.fix()) {
      is Ior.Left -> false
      is Ior.Both -> false
      is Ior.Right -> EQR().run { a.value.eqv(b.value) }
    }
  }
}

@extension
interface IorEqK<A> : EqK<ForIor> {
  fun EQA(): Eq<A>

  override fun <B> IorPartialOf<B>.eqK(other: IorPartialOf<B>, EQ: Eq<B>): Boolean =
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
interface IorShow<L, R> : Show<IorOf<L, R>> {
  fun SL(): Show<L>
  fun SR(): Show<R>
  override fun IorOf<L, R>.show(): String =
    fix().show(SL(), SR())
}

@extension
interface IorHash<L, R> : Hash<IorOf<L, R>> {

  fun HL(): Hash<L>
  fun HR(): Hash<R>

  override fun IorOf<L, R>.hash(): Int = when (val a = this.fix()) {
    is Ior.Left -> HL().run { a.value.hash() }
    is Ior.Right -> HR().run { a.value.hash() }
    is Ior.Both -> 31 * HL().run { a.leftValue.hash() } + HR().run { a.rightValue.hash() }
  }
}

fun <L, R> Ior.Companion.fx(SL: Semigroup<L>, c: suspend MonadSyntax<ForIor>.() -> R): Ior<L, R> =
  Ior.monad(SL).fx.monad(c).fix()

@extension
interface IorCrosswalk<L> : Crosswalk<ForIor>, IorFunctor, IorFoldable {
  override fun <F, A, B> crosswalk(ALIGN: Align<F>, a: IorPartialOf<A>, fa: (A) -> Kind<F, B>): Kind<F, IorPartialOf<B>> =
    when (val ior = a.fix()) {
      is Ior.Left -> ALIGN.run { empty<IorPartialOf<B>>() }
      is Ior.Both -> ALIGN.run { fa(ior.rightValue).map { Ior.Both(ior.leftValue, it) } }
      is Ior.Right -> ALIGN.run { fa(ior.value).map { it.rightIor() } }
    }.unnest()
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
      is Ior.Left -> ALIGN.run {
        fa(e.value).map { it.leftIor() }
      }
      is Ior.Right -> ALIGN.run {
        fb(e.value).map { it.rightIor() }
      }
      is Ior.Both -> ALIGN.run {
        align(fa(e.leftValue), fb(e.rightValue))
      }
    }
}
