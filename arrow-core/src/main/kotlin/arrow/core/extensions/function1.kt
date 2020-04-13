package arrow.core.extensions

import arrow.Kind
import arrow.core.extensions.function1.monad.monad
import arrow.core.Either
import arrow.core.Function1
import arrow.core.ForFunction1
import arrow.core.Function1Of
import arrow.core.Function1PartialOf
import arrow.core.Tuple2
import arrow.core.compose
import arrow.extension
import arrow.core.fix
import arrow.core.invoke
import arrow.core.k
import arrow.typeclasses.Applicative
import arrow.typeclasses.Apply
import arrow.typeclasses.Category
import arrow.typeclasses.Contravariant
import arrow.typeclasses.Decidable
import arrow.typeclasses.Divide
import arrow.typeclasses.Divisible
import arrow.typeclasses.Functor
import arrow.typeclasses.Monad
import arrow.typeclasses.MonadSyntax
import arrow.typeclasses.Monoid
import arrow.typeclasses.Profunctor
import arrow.typeclasses.Semigroup

@extension
interface Function1Semigroup<A, B> : Semigroup<Function1<A, B>> {
  fun SB(): Semigroup<B>

  override fun Function1<A, B>.combine(b: Function1<A, B>): Function1<A, B> = { a: A -> SB().run { invoke(a).combine(b(a)) } }.k()
}

@extension
interface Function1Monoid<A, B> : Monoid<Function1<A, B>>, Function1Semigroup<A, B> {
  fun MB(): Monoid<B>

  override fun SB() = MB()

  override fun empty(): Function1<A, B> = Function1 { MB().run { empty() } }
}

@extension
interface Function1Functor : Functor<ForFunction1> {
  override fun <A, B> Function1PartialOf<A>.map(f: (A) -> B): Function1PartialOf<B> =
    fix().map(f).unnest()
}

@extension
interface Function1Contravariant : Contravariant<ForFunction1> {
  override fun <A, B> Function1PartialOf<A>.contramap(f: (B) -> A): Function1PartialOf<B> =
    nest<A>().fix().contramap(f).unnest()

//  fun <A, B, C> Function1Of<A, B>.contramapC(f: (C) -> A): Function1Of<C, B> =
//    unnest<B>().contramap(f)
}

@extension
interface Function1Divide<O> : Divide<ForFunction1>, Function1Contravariant {
  fun MO(): Monoid<O>

  override fun <A, B, Z> divide(fa: Function1PartialOf<A>, fb: Function1PartialOf<B>, f: (Z) -> Tuple2<A, B>): Kind<ForFunction1, Z> =
    Function1<Z, O> {
      val (a, b) = f(it)

      MO().run {
        fa.unnest<O>().invoke(a) +
          fb.unnest<O>().invoke(b)
      }
    }.unnest()

//  fun <A, B, Z> divideC(fa: Function1Of<A, O>, fb: Function1Of<A, O>, f: (Z) -> Tuple2<A, B>): Function1Of<Z, O> =
//    divide(fa.unnest(), fb.unnest(), f)
}

@extension
interface Function1Divisible<O> : Divisible<ForFunction1>, Function1Divide<O> {
  override fun MO(): Monoid<O>

  override fun <A> conquer(): Function1PartialOf<A> =
    Function1<A, O> {
      MO().empty()
    }.unnest()

  fun <A> conquerC(): Function1Of<A, O> =
    conquer()
}

@extension
interface Function1Decidable<O> : Decidable<ForFunction1>, Function1Divisible<O> {
  override fun MO(): Monoid<O>

  override fun <A, B, Z> choose(fa: Function1PartialOf<A>, fb: Function1PartialOf<B>, f: (Z) -> Either<A, B>): Function1PartialOf<Z> =
    Function1<Z, O> {
      f(it).fold({
        fa.unnest<O>().invoke(it)
      }, {
        fb.unnest<O>().invoke(it)
      })
    }.unnest()

  fun <A, B, Z> chooseC(fa: Function1Of<A, O>, fb: Function1Of<B, O>, f: (Z) -> Either<A, B>): Function1Of<Z, O> =
    choose(fa.unnest(), fb.unnest(), f).unnest()
}

@extension
interface Function1Profunctor : Profunctor<ForFunction1> {
  override fun <A, B, C, D> Function1Of<A, B>.dimap(fl: (C) -> A, fr: (B) -> D): Function1<C, D> =
    (fr compose fix().f compose fl).k()
}

@extension
interface Function1Apply : Apply<ForFunction1>, Function1Functor {

  override fun <A, B> Function1PartialOf<A>.map(f: (A) -> B): Function1PartialOf<B> =
    fix().map(f).unnest()

  override fun <A, B> Function1PartialOf<A>.ap(ff: Function1PartialOf<(A) -> B>): Function1PartialOf<B> =
    fix().ap(ff).unnest()
}

@extension
interface Function1Applicative : Applicative<ForFunction1>, Function1Functor {

  override fun <A> just(a: A): Function1PartialOf<A> =
    Function1.just<Nothing, A>(a).unnest()

  override fun <A, B> Function1PartialOf<A>.map(f: (A) -> B): Function1PartialOf<B> =
    fix().map(f).unnest()

  override fun <A, B> Function1PartialOf<A>.ap(ff: Function1PartialOf<(A) -> B>): Function1PartialOf<B> =
    fix().ap(ff).unnest()
}

@extension
interface Function1Monad : Monad<ForFunction1>, Function1Applicative {

  override fun <A, B> Function1PartialOf<A>.map(f: (A) -> B): Function1PartialOf<B> =
    fix().map(f).unnest()

  override fun <A, B> Function1PartialOf<A>.ap(ff: Function1PartialOf<(A) -> B>): Function1PartialOf<B> =
    fix().ap(ff).unnest()

  override fun <A, B> Function1PartialOf<A>.flatMap(f: (A) -> Function1PartialOf<B>): Function1PartialOf<B> =
    fix().flatMap(f).unnest()

  override fun <A, B> tailRecM(a: A, f: (A) -> Function1PartialOf<Either<A, B>>): Function1PartialOf<B> =
    Function1.tailRecM(a, f).unnest()
}

fun <A, B> Function1.Companion.fx(c: suspend MonadSyntax<ForFunction1>.() -> B): Function1<A, B> =
  Function1.monad().fx.monad(c).fix()

@extension
interface Function1Category : Category<ForFunction1> {
  override fun <A> id(): Function1<A, A> = Function1.id()

  override fun <A, B, C> Function1Of<B, C>.compose(arr: Function1Of<A, B>): Function1Of<A, C> = fix().compose(arr.fix())
}
