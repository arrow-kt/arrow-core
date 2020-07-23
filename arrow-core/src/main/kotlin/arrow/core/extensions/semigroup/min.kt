package arrow.core.extensions.semigroup

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.semigroup.ForMin
import arrow.core.semigroup.Min
import arrow.core.semigroup.fix
import arrow.extension
import arrow.typeclasses.Applicative
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Foldable
import arrow.typeclasses.Functor
import arrow.typeclasses.Hash
import arrow.typeclasses.Monad
import arrow.typeclasses.Order
import arrow.typeclasses.Semigroup
import arrow.typeclasses.Show
import arrow.typeclasses.Traverse

@extension
interface MinEq<A> : Eq<Min<A>> {
  fun EQ(): Eq<A>
  override fun Min<A>.eqv(b: Min<A>): Boolean = EQ().run { getMin.eqv(b.getMin) }
}

@extension
interface MinEqK : EqK<ForMin> {
  override fun <A> Kind<ForMin, A>.eqK(other: Kind<ForMin, A>, EQ: Eq<A>): Boolean =
    EQ.run { fix().getMin.eqv(other.fix().getMin) }
}

@extension
interface MinOrder<A> : Order<Min<A>> {
  fun ORD(): Order<A>
  override fun Min<A>.compare(b: Min<A>): Int = ORD().run { getMin.compare(b.getMin) }
}

@extension
interface MinShow<A> : Show<Min<A>> {
  fun SA(): Show<A>
  override fun Min<A>.show(): String = SA().run { "Min(${getMin.show()})" }
}

@extension
interface MinHash<A> : Hash<Min<A>> {
  fun HA(): Hash<A>
  override fun Min<A>.eqv(b: Min<A>): Boolean = HA().run { getMin.eqv(b.getMin) }
  override fun Min<A>.hash(): Int = HA().run { getMin.hash() }
}

@extension
interface MinSemigroup<A> : Semigroup<Min<A>> {
  fun ORD(): Order<A>
  override fun Min<A>.combine(b: Min<A>): Min<A> =
    when (val o = ORD().run { getMin.compare(b.getMin) }) {
      0 -> this
      else -> if (o < 0) this else b
    }
}

// TODO monoid when we have a way to declare bounds with typeclasses

@extension
interface MinFunctor : Functor<ForMin> {
  override fun <A, B> Kind<ForMin, A>.map(f: (A) -> B): Kind<ForMin, B> = Min(f(fix().getMin))
}

@extension
interface MinApplicative : Applicative<ForMin>, MinFunctor {
  override fun <A> just(a: A): Kind<ForMin, A> = Min(a)
  override fun <A, B> Kind<ForMin, A>.ap(ff: Kind<ForMin, (A) -> B>): Kind<ForMin, B> =
    Min(ff.fix().getMin.invoke(fix().getMin))
  override fun <A, B> Kind<ForMin, A>.map(f: (A) -> B): Kind<ForMin, B> = Min(f(fix().getMin))
}

@extension
interface MinMonad : Monad<ForMin>, MinApplicative {
  override fun <A, B> Kind<ForMin, A>.flatMap(f: (A) -> Kind<ForMin, B>): Kind<ForMin, B> = f(fix().getMin)

  private tailrec fun <A, B> loop(a: A, f: (A) -> Kind<ForMin, Either<A, B>>): Min<B> =
    when (val fa = f(a).fix().getMin) {
      is Either.Left -> loop(fa.a, f)
      is Either.Right -> Min(fa.b)
    }

  override fun <A, B> tailRecM(a: A, f: (A) -> Kind<ForMin, Either<A, B>>): Kind<ForMin, B> = loop(a, f)

  override fun <A, B> Kind<ForMin, A>.map(f: (A) -> B): Kind<ForMin, B> = Min(f(fix().getMin))
  override fun <A, B> Kind<ForMin, A>.ap(ff: Kind<ForMin, (A) -> B>): Kind<ForMin, B> =
    Min(ff.fix().getMin.invoke(fix().getMin))
}

@extension
interface MinFoldable : Foldable<ForMin> {
  override fun <A, B> Kind<ForMin, A>.foldLeft(b: B, f: (B, A) -> B): B = f(b, fix().getMin)
  override fun <A, B> Kind<ForMin, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    Eval.defer { f(fix().getMin, lb) }
}

@extension
interface MinTraverse : Traverse<ForMin>, MinFoldable {
  override fun <G, A, B> Kind<ForMin, A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, Kind<ForMin, B>> =
    AP.run { f(fix().getMin).map(::Min) }
}
