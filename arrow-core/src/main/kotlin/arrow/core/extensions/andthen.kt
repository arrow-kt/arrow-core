package arrow.core.extensions

import arrow.Kind
import arrow.Kind2
import arrow.core.Either
import arrow.core.AndThen
import arrow.core.AndThenOf
import arrow.core.AndThenPartialOf
import arrow.core.ForAndThen
import arrow.core.fix
import arrow.core.invoke
import arrow.extension
import arrow.typeclasses.Apply
import arrow.typeclasses.Functor
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup
import arrow.typeclasses.Applicative
import arrow.typeclasses.Monad
import arrow.typeclasses.Category
import arrow.typeclasses.Profunctor
import arrow.typeclasses.Contravariant

@extension
interface AndThenSemigroup<A, B> : Semigroup<AndThen<A, B>> {
  fun SB(): Semigroup<B>

  override fun AndThen<A, B>.combine(b: AndThen<A, B>): AndThen<A, B> = SB().run {
    AndThen { a: A -> invoke(a).combine(b.invoke(a)) }
  }
}

@extension
interface AndThenMonoid<A, B> : Monoid<AndThen<A, B>>, AndThenSemigroup<A, B> {

  fun MB(): Monoid<B>

  override fun SB(): Semigroup<B> = MB()

  override fun empty(): AndThen<A, B> =
    AndThen { MB().empty() }
}

@extension
interface AndThenFunctor : Functor<ForAndThen> {
  override fun <A, B> AndThenPartialOf<A>.map(f: (A) -> B): AndThenPartialOf<B> =
    fix().map(f).unnest()
}

@extension
interface AndThenApply : Apply<ForAndThen>, AndThenFunctor {
  override fun <A, B> AndThenPartialOf<A>.ap(ff: AndThenPartialOf<(A) -> B>): AndThenPartialOf<B> =
    fix().ap(ff).unnest()

  override fun <A, B> AndThenPartialOf<A>.map(f: (A) -> B): AndThenPartialOf<B> =
    fix().map(f).unnest()
}

@extension
interface AndThenApplicative : Applicative<ForAndThen>, AndThenFunctor {
  override fun <A> just(a: A): AndThenPartialOf<A> =
    AndThen.just<Nothing, A>(a).unnest()

  override fun <A, B> AndThenPartialOf<A>.ap(ff: AndThenPartialOf<(A) -> B>): AndThenPartialOf<B> =
    fix().ap(ff).unnest()

  override fun <A, B> AndThenPartialOf<A>.map(f: (A) -> B): AndThenPartialOf<B> =
    fix().map(f).unnest()
}

@extension
interface AndThenMonad : Monad<ForAndThen>, AndThenApplicative {
  override fun <A, B> AndThenPartialOf<A>.flatMap(f: (A) -> AndThenPartialOf<B>): AndThenPartialOf<B> =
    fix().flatMap(f).unnest()

  override fun <A, B> tailRecM(a: A, f: (A) -> AndThenPartialOf<Either<A, B>>): AndThenPartialOf<B> =
    AndThen.tailRecM(a, f).unnest()

  override fun <A, B> AndThenPartialOf<A>.map(f: (A) -> B): AndThenPartialOf<B> =
    fix().map(f).unnest()

  override fun <A, B> AndThenPartialOf<A>.ap(ff: AndThenPartialOf<(A) -> B>): AndThenPartialOf<B> =
    fix().ap(ff).unnest()
}

@extension
interface AndThenCategory : Category<ForAndThen> {
  override fun <A> id(): AndThen<A, A> =
    AndThen.id()

  override fun <A, B, C> AndThenOf<B, C>.compose(arr: Kind2<ForAndThen, A, B>): AndThen<A, C> =
    fix().compose(arr::invoke)
}

@extension
interface AndThenContravariant : Contravariant<ForAndThen> {

  override fun <A, B> AndThenPartialOf<A>.contramap(f: (B) -> A): AndThenPartialOf<B> =
    nest<A>().fix().contramap(f).unnest()
}

@extension
interface AndThenProfunctor : Profunctor<ForAndThen> {
  override fun <A, B, C, D> AndThenOf<A, B>.dimap(fl: (C) -> A, fr: (B) -> D): AndThen<C, D> =
    fix().andThen(fr).compose(fl)
}
