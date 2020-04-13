@file:Suppress("UnusedImports")

package arrow.core.extensions

import arrow.Kind
import arrow.Kind2
import arrow.core.Either
import arrow.core.EitherOf
import arrow.core.EitherPartialOf
import arrow.core.Eval
import arrow.core.ForEither
import arrow.core.Left
import arrow.core.Right
import arrow.core.extensions.either.eq.eq
import arrow.core.extensions.either.monad.monad
import arrow.core.fix
import arrow.core.left
import arrow.core.right
import arrow.extension
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
import arrow.typeclasses.Semigroup
import arrow.typeclasses.SemigroupK
import arrow.typeclasses.Show
import arrow.typeclasses.Traverse
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

@extension
interface EitherSemigroup<L, R> : Semigroup<Either<L, R>> {

  fun SGL(): Semigroup<L>
  fun SGR(): Semigroup<R>

  override fun Either<L, R>.combine(b: Either<L, R>): Either<L, R> =
    fix().combine(SGL(), SGR(), b)
}

@extension
interface EitherMonoid<L, R> : Monoid<Either<L, R>>, EitherSemigroup<L, R> {
  override fun SGL(): Monoid<L>
  override fun SGR(): Monoid<R>

  override fun empty(): Either<L, R> =
    Right(SGR().empty())
}

@extension
interface EitherFunctor : Functor<ForEither> {
  override fun <A, B> EitherPartialOf<A>.map(f: (A) -> B): EitherPartialOf<B> =
    fix().map(f).unnest()
}

@extension
interface EitherBifunctor : Bifunctor<ForEither> {
  override fun <A, B, C, D> EitherOf<A, B>.bimap(fl: (A) -> C, fr: (B) -> D): Either<C, D> =
    fix().bimap(fl, fr)
}

@extension
interface EitherApply : Apply<ForEither>, EitherFunctor {

  override fun <A, B> EitherPartialOf<A>.apEval(ff: Eval<EitherPartialOf<(A) -> B>>): Eval<EitherPartialOf<B>> =
    fix().fold(
      { l -> Eval.now(l.left().unnest()) },
      { r -> ff.map { it.fix().map { f -> f(r) }.unnest<B>() } }
    )

  override fun <A, B> EitherPartialOf<A>.ap(ff: EitherPartialOf<(A) -> B>): EitherPartialOf<B> =
    fix().eitherAp(ff).unnest()
}

@extension
interface EitherApplicative : Applicative<ForEither>, EitherApply {

  override fun <A> just(a: A): EitherPartialOf<A> =
    Right(a).unnest()

  override fun <A, B> EitherPartialOf<A>.map(f: (A) -> B): EitherPartialOf<B> =
    fix().map(f).unnest()
}

@extension
interface EitherMonad : Monad<ForEither>, EitherApplicative {

  override fun <A, B> EitherPartialOf<A>.map(f: (A) -> B): EitherPartialOf<B> =
    fix().map(f).unnest()

  override fun <A, B> EitherPartialOf<A>.ap(ff: EitherPartialOf<(A) -> B>): EitherPartialOf<B> =
    fix().eitherAp(ff).unnest()

  override fun <A, B> EitherPartialOf<A>.flatMap(f: (A) -> EitherPartialOf<B>): EitherPartialOf<B> =
    fix().eitherFlatMap { f(it).fix() }.unnest()

  override fun <A, B> tailRecM(a: A, f: (A) -> EitherPartialOf<Either<A, B>>): EitherPartialOf<B> =
    Either.tailRecM(a, f).unnest()

  override val fx: MonadFx<ForEither>
    get() = EitherMonadFx
}

internal object EitherMonadFx : MonadFx<ForEither> {
  override val M: Monad<ForEither> = Either.monad()
  override fun <A> monad(c: suspend MonadSyntax<ForEither>.() -> A): EitherPartialOf<A> =
    super.monad(c).fix().unnest()
}

@extension
interface EitherApplicativeError<L> : ApplicativeError<ForEither, L>, EitherApplicative {

  override fun <A> raiseError(e: L): EitherPartialOf<A> =
    Left(e).unnest()

  override fun <A> EitherPartialOf<A>.handleErrorWith(f: (L) -> EitherPartialOf<A>): Kind<ForEither, A> =
    fix().eitherHandleErrorWith(f).unnest()
}

@extension
interface EitherMonadError<L> : MonadError<ForEither, L>, EitherApplicativeError<L>, EitherMonad

@extension
interface EitherFoldable : Foldable<ForEither> {

  override fun <A, B> EitherPartialOf<A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> EitherPartialOf<A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().foldRight(lb, f)
}

@extension
interface EitherBifoldable : Bifoldable<ForEither> {
  override fun <A, B, C> EitherOf<A, B>.bifoldLeft(c: C, f: (C, A) -> C, g: (C, B) -> C): C = fix().bifoldLeft(c, f, g)

  override fun <A, B, C> EitherOf<A, B>.bifoldRight(c: Eval<C>, f: (A, Eval<C>) -> Eval<C>, g: (B, Eval<C>) -> Eval<C>): Eval<C> =
    fix().bifoldRight(c, f, g)
}

fun <G, A, B, C> EitherOf<A, B>.traverse(GA: Applicative<G>, f: (B) -> Kind<G, C>): Kind<G, Either<A, C>> =
  fix().fold({ GA.just(Either.Left(it)) }, { GA.run { f(it).map { Either.Right(it) } } })

@extension
interface EitherTraverse<L> : Traverse<ForEither>, EitherFoldable {

  override fun <G, A, B> EitherPartialOf<A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, EitherPartialOf<B>> =
    fix().eitherTraverse(AP, f).unnest()
}

@extension
interface EitherBitraverse : Bitraverse<ForEither>, EitherBifoldable {
  override fun <G, A, B, C, D> EitherOf<A, B>.bitraverse(AP: Applicative<G>, f: (A) -> Kind<G, C>, g: (B) -> Kind<G, D>): Kind<G, EitherOf<C, D>> =
    fix().let { AP.run { it.fold({ f(it).map { Either.Left(it) } }, { g(it).map { Either.Right(it) } }) } }
}

@extension
interface EitherSemigroupK : SemigroupK<ForEither> {

  override fun <A> EitherPartialOf<A>.combineK(y: EitherPartialOf<A>): EitherPartialOf<A> =
    fix().eitherCombineK(y).unnest()
}

@extension
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

@extension
interface EitherEqK<L> : EqK<ForEither> {
  fun EQL(): Eq<L>

  override fun <R> EitherPartialOf<R>.eqK(other: EitherPartialOf<R>, EQ: Eq<R>): Boolean =
    Either.eq(EQL(), EQ).run {
      this@eqK.fix().eqv(other.fix())
    }
}

@extension
interface EitherEqK2 : EqK2<ForEither> {
  override fun <A, B> Kind2<ForEither, A, B>.eqK(other: Kind2<ForEither, A, B>, EQA: Eq<A>, EQB: Eq<B>): Boolean =
    (this.fix() to other.fix()).let {
      Either.eq(EQA, EQB).run {
        it.first.eqv(it.second)
      }
    }
}

@extension
interface EitherShow<L, R> : Show<Either<L, R>> {
  fun SL(): Show<L>
  fun SR(): Show<R>
  override fun Either<L, R>.show(): String = show(SL(), SR())
}

@extension
interface EitherHash<L, R> : Hash<Either<L, R>>, EitherEq<L, R> {

  fun HL(): Hash<L>
  fun HR(): Hash<R>

  override fun EQL(): Eq<L> = HL()

  override fun EQR(): Eq<R> = HR()

  override fun Either<L, R>.hash(): Int = fold({
    HL().run { it.hash() }
  }, {
    HR().run { it.hash() }
  })
}

fun <L, R> Either.Companion.fx(c: suspend MonadSyntax<ForEither>.() -> R): Either<L, R> =
  Either.monad().fx.monad(c).fix()

@extension
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
