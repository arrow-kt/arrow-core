package arrow.core.extensions.semigroup

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.semigroup.ForMax
import arrow.core.semigroup.Max
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
interface MaxEq<A> : Eq<Max<A>> {
  fun EQ(): Eq<A>
  override fun Max<A>.eqv(b: Max<A>): Boolean = EQ().run { getMax.eqv(b.getMax) }
}

@extension
interface MaxEqK : EqK<ForMax> {
  override fun <A> Kind<ForMax, A>.eqK(other: Kind<ForMax, A>, EQ: Eq<A>): Boolean =
    EQ.run { fix().getMax.eqv(other.fix().getMax) }
}

@extension
interface MaxOrder<A> : Order<Max<A>> {
  fun ORD(): Order<A>
  override fun Max<A>.compare(b: Max<A>): Int = ORD().run { getMax.compare(b.getMax) }
}

@extension
interface MaxShow<A> : Show<Max<A>> {
  fun SA(): Show<A>
  override fun Max<A>.show(): String = SA().run { "Max(${getMax.show()})" }
}

@extension
interface MaxHash<A> : Hash<Max<A>> {
  fun HA(): Hash<A>
  override fun Max<A>.eqv(b: Max<A>): Boolean = HA().run { getMax.eqv(b.getMax) }
  override fun Max<A>.hash(): Int = HA().run { getMax.hash() }
}

@extension
interface MaxSemigroup<A> : Semigroup<Max<A>> {
  fun ORD(): Order<A>
  override fun Max<A>.combine(b: Max<A>): Max<A> =
    when (val o = ORD().run { getMax.compare(b.getMax) }) {
      0 -> this
      else -> if (o < 0) b else this
    }
}

// TODO monoid when we have a way to declare bounds with typeclasses

@extension
interface MaxFunctor : Functor<ForMax> {
  override fun <A, B> Kind<ForMax, A>.map(f: (A) -> B): Kind<ForMax, B> = Max(f(fix().getMax))
}

@extension
interface MaxApplicative : Applicative<ForMax>, MaxFunctor {
  override fun <A> just(a: A): Kind<ForMax, A> = Max(a)
  override fun <A, B> Kind<ForMax, A>.ap(ff: Kind<ForMax, (A) -> B>): Kind<ForMax, B> =
    Max(ff.fix().getMax.invoke(fix().getMax))
  override fun <A, B> Kind<ForMax, A>.map(f: (A) -> B): Kind<ForMax, B> = Max(f(fix().getMax))
}

@extension
interface MaxMonad : Monad<ForMax>, MaxApplicative {
  override fun <A, B> Kind<ForMax, A>.flatMap(f: (A) -> Kind<ForMax, B>): Kind<ForMax, B> = f(fix().getMax)

  private tailrec fun <A, B> loop(a: A, f: (A) -> Kind<ForMax, Either<A, B>>): Max<B> =
    when (val fa = f(a).fix().getMax) {
      is Either.Left -> loop(fa.a, f)
      is Either.Right -> Max(fa.b)
    }

  override fun <A, B> tailRecM(a: A, f: (A) -> Kind<ForMax, Either<A, B>>): Kind<ForMax, B> = loop(a, f)

  override fun <A, B> Kind<ForMax, A>.map(f: (A) -> B): Kind<ForMax, B> = Max(f(fix().getMax))
  override fun <A, B> Kind<ForMax, A>.ap(ff: Kind<ForMax, (A) -> B>): Kind<ForMax, B> =
    Max(ff.fix().getMax.invoke(fix().getMax))
}

@extension
interface MaxFoldable : Foldable<ForMax> {
  override fun <A, B> Kind<ForMax, A>.foldLeft(b: B, f: (B, A) -> B): B = f(b, fix().getMax)
  override fun <A, B> Kind<ForMax, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    Eval.defer { f(fix().getMax, lb) }
}

@extension
interface MaxTraverse : Traverse<ForMax>, MaxFoldable {
  override fun <G, A, B> Kind<ForMax, A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, Kind<ForMax, B>> =
    AP.run { f(fix().getMax).map(::Max) }
}
