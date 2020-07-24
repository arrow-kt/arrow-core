package arrow.core.extensions.semigroup

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.semigroup.Last
import arrow.core.semigroup.ForLast
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
interface LastEq<A> : Eq<Last<A>> {
  fun EQ(): Eq<A>
  override fun Last<A>.eqv(b: Last<A>): Boolean = EQ().run { getLast.eqv(b.getLast) }
}

@extension
interface LastEqK : EqK<ForLast> {
  override fun <A> Kind<ForLast, A>.eqK(other: Kind<ForLast, A>, EQ: Eq<A>): Boolean =
    EQ.run { fix().getLast.eqv(other.fix().getLast) }
}

@extension
interface LastOrder<A> : Order<Last<A>> {
  fun ORD(): Order<A>
  override fun Last<A>.compare(b: Last<A>): Int = ORD().run { getLast.compare(b.getLast) }
}

@extension
interface LastShow<A> : Show<Last<A>> {
  fun SA(): Show<A>
  override fun Last<A>.show(): String = SA().run { "Last(${getLast.show()})" }
}

@extension
interface LastHash<A> : Hash<Last<A>> {
  fun HA(): Hash<A>
  override fun Last<A>.eqv(b: Last<A>): Boolean = HA().run { getLast.eqv(b.getLast) }
  override fun Last<A>.hash(): Int = HA().run { getLast.hash() }
}

@extension
interface LastSemigroup<A> : Semigroup<Last<A>> {
  override fun Last<A>.combine(b: Last<A>): Last<A> = b
}

@extension
interface LastFunctor : Functor<ForLast> {
  override fun <A, B> Kind<ForLast, A>.map(f: (A) -> B): Kind<ForLast, B> = Last(f(fix().getLast))
}

@extension
interface LastApplicative : Applicative<ForLast>, LastFunctor {
  override fun <A> just(a: A): Kind<ForLast, A> = Last(a)
  override fun <A, B> Kind<ForLast, A>.ap(ff: Kind<ForLast, (A) -> B>): Kind<ForLast, B> =
    Last((ff.fix().getLast(fix().getLast)))
  override fun <A, B> Kind<ForLast, A>.map(f: (A) -> B): Kind<ForLast, B> = Last(f(fix().getLast))
}

@extension
interface LastMonad : Monad<ForLast>, LastApplicative {
  override fun <A, B> Kind<ForLast, A>.flatMap(f: (A) -> Kind<ForLast, B>): Kind<ForLast, B> =
    f(fix().getLast)

  private tailrec fun <A, B> loop(a: A, f: (A) -> Kind<ForLast, Either<A, B>>): Last<B> =
    when (val fa = f(a).fix().getLast) {
      is Either.Left -> loop(fa.a, f)
      is Either.Right -> Last(fa.b)
    }

  override fun <A, B> tailRecM(a: A, f: (A) -> Kind<ForLast, Either<A, B>>): Kind<ForLast, B> = loop(a, f)

  override fun <A, B> Kind<ForLast, A>.ap(ff: Kind<ForLast, (A) -> B>): Kind<ForLast, B> =
    Last((ff.fix().getLast(fix().getLast)))
  override fun <A, B> Kind<ForLast, A>.map(f: (A) -> B): Kind<ForLast, B> = Last(f(fix().getLast))
}

@extension
interface LastFoldable : Foldable<ForLast> {
  override fun <A, B> Kind<ForLast, A>.foldLeft(b: B, f: (B, A) -> B): B =
    f(b, fix().getLast)
  override fun <A, B> Kind<ForLast, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    Eval.defer { f(fix().getLast, lb) }
}

@extension
interface LastTraverse : Traverse<ForLast>, LastFoldable {
  override fun <G, A, B> Kind<ForLast, A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, Kind<ForLast, B>> =
    AP.run { f(fix().getLast).map(::Last) }
}
