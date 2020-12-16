@file:Suppress("UnusedImports")

package arrow.core.extensions

import arrow.Kind
import arrow.Kind2
import arrow.core.Either
import arrow.core.EitherOf
import arrow.core.EitherPartialOf
import arrow.core.Eval
import arrow.core.ForEither
import arrow.core.GT
import arrow.core.LT
import arrow.core.Left
import arrow.core.Ordering
import arrow.core.Right
import arrow.core.extensions.either.eq.eq
import arrow.core.extensions.either.monad.monad
import arrow.core.fix
import arrow.core.left
import arrow.core.right
import arrow.typeclasses.Align
import arrow.typeclasses.Applicative
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.Apply
import arrow.typeclasses.Bicrosswalk
import arrow.typeclasses.Bifoldable
import arrow.typeclasses.Bifunctor
import arrow.typeclasses.Bitraverse
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.EqK2
import arrow.typeclasses.Foldable
import arrow.typeclasses.Functor
import arrow.typeclasses.Hash
import arrow.typeclasses.Monad
import arrow.typeclasses.MonadError
import arrow.typeclasses.MonadFx
import arrow.typeclasses.MonadSyntax
import arrow.typeclasses.Monoid
import arrow.typeclasses.Order
import arrow.typeclasses.Semigroup
import arrow.typeclasses.SemigroupK
import arrow.typeclasses.Show
import arrow.typeclasses.Traverse
import arrow.typeclasses.hashWithSalt
import arrow.core.ap as eitherAp
import arrow.core.combineK as eitherCombineK
import arrow.core.extensions.traverse as eitherTraverse
import arrow.core.flatMap as eitherFlatMap
import arrow.core.handleErrorWith as eitherHandleErrorWith

fun <L, R> Either<L, R>.combine(SGL: Semigroup<L>, SGR: Semigroup<R>, b: Either<L, R>): Either<L, R> {
  val a = this

  return when (a) {
    is Either.Left -> when (b) {
      is Either.Left -> Either.Left(SGL.run { a.a.combine(b.a) })
      is Either.Right -> a
    }
    is Either.Right -> when (b) {
      is Either.Left -> b
      is Either.Right -> Either.right(SGR.run { a.b.combine(b.b) })
    }
  }
}

interface EitherSemigroup<L, R> : Semigroup<Either<L, R>> {

  fun SGL(): Semigroup<L>
  fun SGR(): Semigroup<R>

  override fun Either<L, R>.combine(b: Either<L, R>): Either<L, R> = fix().combine(SGL(), SGR(), b)
}

interface EitherMonoid<L, R> : Monoid<Either<L, R>>, EitherSemigroup<L, R> {
  fun MOL(): Monoid<L>
  fun MOR(): Monoid<R>

  override fun SGL(): Semigroup<L> = MOL()
  override fun SGR(): Semigroup<R> = MOR()

  override fun empty(): Either<L, R> = Right(MOR().empty())
}

interface EitherFunctor<L> : Functor<EitherPartialOf<L>> {
  override fun <A, B> EitherOf<L, A>.map(f: (A) -> B): Either<L, B> = fix().map(f)
}

interface EitherBifunctor : Bifunctor<ForEither> {
  override fun <A, B, C, D> EitherOf<A, B>.bimap(fl: (A) -> C, fr: (B) -> D): Either<C, D> =
    fix().bimap(fl, fr)
}

interface EitherApply<L> : Apply<EitherPartialOf<L>>, EitherFunctor<L> {

  override fun <A, B> EitherOf<L, A>.map(f: (A) -> B): Either<L, B> = fix().map(f)

  override fun <A, B> Kind<EitherPartialOf<L>, A>.apEval(ff: Eval<Kind<EitherPartialOf<L>, (A) -> B>>): Eval<Kind<EitherPartialOf<L>, B>> =
    fix().fold({ l -> Eval.now(l.left()) }, { r -> ff.map { it.fix().map { f -> f(r) } } })

  override fun <A, B> EitherOf<L, A>.ap(ff: EitherOf<L, (A) -> B>): Either<L, B> =
    fix().eitherAp(ff)
}

interface EitherApplicative<L> : Applicative<EitherPartialOf<L>>, EitherApply<L> {

  override fun <A> just(a: A): Either<L, A> = Right(a)

  override fun <A, B> EitherOf<L, A>.map(f: (A) -> B): Either<L, B> = fix().map(f)
}

interface EitherMonad<L> : Monad<EitherPartialOf<L>>, EitherApplicative<L> {

  override fun <A, B> EitherOf<L, A>.map(f: (A) -> B): Either<L, B> = fix().map(f)

  override fun <A, B> EitherOf<L, A>.ap(ff: EitherOf<L, (A) -> B>): Either<L, B> =
    fix().eitherAp(ff)

  override fun <A, B> EitherOf<L, A>.flatMap(f: (A) -> EitherOf<L, B>): Either<L, B> =
    fix().eitherFlatMap { f(it).fix() }

  override fun <A, B> tailRecM(a: A, f: (A) -> EitherOf<L, Either<A, B>>): Either<L, B> =
    Either.tailRecM(a, f)

  @Suppress("UNCHECKED_CAST")
  override val fx: MonadFx<EitherPartialOf<L>>
    get() = EitherMonadFx as MonadFx<EitherPartialOf<L>>
}

internal object EitherMonadFx : MonadFx<EitherPartialOf<Any?>> {
  override val M: Monad<EitherPartialOf<Any?>> = Either.monad()
  override fun <A> monad(c: suspend MonadSyntax<EitherPartialOf<Any?>>.() -> A): Either<Any?, A> =
    super.monad(c).fix()
}

interface EitherApplicativeError<L> : ApplicativeError<EitherPartialOf<L>, L>, EitherApplicative<L> {

  override fun <A> raiseError(e: L): Either<L, A> = Left(e)

  override fun <A> EitherOf<L, A>.handleErrorWith(f: (L) -> EitherOf<L, A>): Either<L, A> =
    fix().eitherHandleErrorWith(f)
}

interface EitherMonadError<L> : MonadError<EitherPartialOf<L>, L>, EitherApplicativeError<L>, EitherMonad<L>

interface EitherFoldable<L> : Foldable<EitherPartialOf<L>> {

  override fun <A, B> EitherOf<L, A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> EitherOf<L, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().foldRight(lb, f)
}

interface EitherBifoldable : Bifoldable<ForEither> {
  override fun <A, B, C> EitherOf<A, B>.bifoldLeft(c: C, f: (C, A) -> C, g: (C, B) -> C): C = fix().bifoldLeft(c, f, g)

  override fun <A, B, C> EitherOf<A, B>.bifoldRight(c: Eval<C>, f: (A, Eval<C>) -> Eval<C>, g: (B, Eval<C>) -> Eval<C>): Eval<C> =
    fix().bifoldRight(c, f, g)
}

fun <G, A, B, C> EitherOf<A, B>.traverse(GA: Applicative<G>, f: (B) -> Kind<G, C>): Kind<G, Either<A, C>> =
  fix().fold({ GA.just(Either.Left(it)) }, { GA.run { f(it).map { Either.Right(it) } } })

interface EitherTraverse<L> : Traverse<EitherPartialOf<L>>, EitherFoldable<L> {

  override fun <G, A, B> EitherOf<L, A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, EitherOf<L, B>> =
    fix().eitherTraverse(AP, f)
}

interface EitherBitraverse : Bitraverse<ForEither>, EitherBifoldable {
  override fun <G, A, B, C, D> EitherOf<A, B>.bitraverse(AP: Applicative<G>, f: (A) -> Kind<G, C>, g: (B) -> Kind<G, D>): Kind<G, EitherOf<C, D>> =
    fix().let { AP.run { it.fold({ f(it).map { Either.Left(it) } }, { g(it).map { Either.Right(it) } }) } }
}

interface EitherSemigroupK<L> : SemigroupK<EitherPartialOf<L>> {

  override fun <A> EitherOf<L, A>.combineK(y: EitherOf<L, A>): Either<L, A> =
    fix().eitherCombineK(y)
}

interface EitherEq<in L, in R> : Eq<Either<L, R>> {

  fun EQL(): Eq<L>

  fun EQR(): Eq<R>

  override fun Either<L, R>.eqv(b: Either<L, R>): Boolean = when (this) {
    is Either.Left -> when (b) {
      is Either.Left -> EQL().run { a.eqv(b.a) }
      is Either.Right -> false
    }
    is Either.Right -> when (b) {
      is Either.Left -> false
      is Either.Right -> EQR().run { this@eqv.b.eqv(b.b) }
    }
  }
}

interface EitherEqK<L> : EqK<EitherPartialOf<L>> {
  fun EQL(): Eq<L>

  override fun <R> Kind<EitherPartialOf<L>, R>.eqK(other: Kind<EitherPartialOf<L>, R>, EQ: Eq<R>): Boolean =
    Either.eq(EQL(), EQ).run {
      this@eqK.fix().eqv(other.fix())
    }
}

interface EitherEqK2 : EqK2<ForEither> {
  override fun <A, B> Kind2<ForEither, A, B>.eqK(other: Kind2<ForEither, A, B>, EQA: Eq<A>, EQB: Eq<B>): Boolean =
    (this.fix() to other.fix()).let {
      Either.eq(EQA, EQB).run {
        it.first.eqv(it.second)
      }
    }
}

interface EitherShow<L, R> : Show<Either<L, R>> {
  fun SL(): Show<L>
  fun SR(): Show<R>
  override fun Either<L, R>.show(): String = show(SL(), SR())
}

interface EitherHash<L, R> : Hash<Either<L, R>> {

  fun HL(): Hash<L>
  fun HR(): Hash<R>

  override fun Either<L, R>.hash(): Int =
    fold(
      { HL().run { it.hashWithSalt(0) } },
      { HR().run { it.hashWithSalt(1) } }
    )

  override fun Either<L, R>.hashWithSalt(salt: Int): Int =
    fold(
      { l -> HL().run { l.hashWithSalt(salt.hashWithSalt(0)) } },
      { r -> HR().run { r.hashWithSalt(salt.hashWithSalt(1)) } }
    )
}

interface EitherOrder<L, R> : Order<Either<L, R>> {
  fun OL(): Order<L>
  fun OR(): Order<R>
  override fun Either<L, R>.compare(b: Either<L, R>): Ordering = fold({ l1 ->
    b.fold({ l2 -> OL().run { l1.compare(l2) } }, { LT })
  }, { r1 ->
    b.fold({ GT }, { r2 -> OR().run { r1.compare(r2) } })
  })
}

@Deprecated("Fx blocks are now named based on each datatype, please use `either { }` instead",
  replaceWith = ReplaceWith("either.eager<L, R>(c)"))
fun <L, R> Either.Companion.fx(c: suspend MonadSyntax<EitherPartialOf<L>>.() -> R): Either<L, R> =
  Either.monad<L>().fx.monad(c).fix()

interface EitherBicrosswalk : Bicrosswalk<ForEither>, EitherBifunctor, EitherBifoldable {
  override fun <F, A, B, C, D> bicrosswalk(
    ALIGN: Align<F>,
    tab: Kind2<ForEither, A, B>,
    fa: (A) -> Kind<F, C>,
    fb: (B) -> Kind<F, D>
  ): Kind<F, Kind2<ForEither, C, D>> =
    when (val e = tab.fix()) {
      is Either.Left -> ALIGN.run {
        fa(e.a).map { it.left() }
      }
      is Either.Right -> ALIGN.run {
        fb(e.b).map { it.right() }
      }
    }
}
