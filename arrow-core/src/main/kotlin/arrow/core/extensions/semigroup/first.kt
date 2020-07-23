package arrow.core.extensions.semigroup

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.semigroup.First
import arrow.core.semigroup.ForFirst
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
interface FirstEq<A> : Eq<First<A>> {
  fun EQ(): Eq<A>
  override fun First<A>.eqv(b: First<A>): Boolean = EQ().run { getFirst.eqv(b.getFirst) }
}

@extension
interface FirstEqK : EqK<ForFirst> {
  override fun <A> Kind<ForFirst, A>.eqK(other: Kind<ForFirst, A>, EQ: Eq<A>): Boolean =
    EQ.run { fix().getFirst.eqv(other.fix().getFirst) }
}

@extension
interface FirstOrder<A> : Order<First<A>> {
  fun ORD(): Order<A>
  override fun First<A>.compare(b: First<A>): Int = ORD().run { getFirst.compare(b.getFirst) }
}

@extension
interface FirstShow<A> : Show<First<A>> {
  fun SA(): Show<A>
  override fun First<A>.show(): String = SA().run { "First(${getFirst.show()})" }
}

@extension
interface FirstHash<A> : Hash<First<A>> {
  fun HA(): Hash<A>
  override fun First<A>.eqv(b: First<A>): Boolean = HA().run { getFirst.eqv(b.getFirst) }
  override fun First<A>.hash(): Int = HA().run { getFirst.hash() }
}

@extension
interface FirstSemigroup<A> : Semigroup<First<A>> {
  override fun First<A>.combine(b: First<A>): First<A> = this
}

@extension
interface FirstFunctor : Functor<ForFirst> {
  override fun <A, B> Kind<ForFirst, A>.map(f: (A) -> B): Kind<ForFirst, B> = First(f(fix().getFirst))
}

@extension
interface FirstApplicative : Applicative<ForFirst>, FirstFunctor {
  override fun <A> just(a: A): Kind<ForFirst, A> = First(a)
  override fun <A, B> Kind<ForFirst, A>.ap(ff: Kind<ForFirst, (A) -> B>): Kind<ForFirst, B> =
    First((ff.fix().getFirst(fix().getFirst)))
  override fun <A, B> Kind<ForFirst, A>.map(f: (A) -> B): Kind<ForFirst, B> = First(f(fix().getFirst))
}

@extension
interface FirstMonad : Monad<ForFirst>, FirstApplicative {
  override fun <A, B> Kind<ForFirst, A>.flatMap(f: (A) -> Kind<ForFirst, B>): Kind<ForFirst, B> =
    f(fix().getFirst)

  private tailrec fun <A, B> loop(a: A, f: (A) -> Kind<ForFirst, Either<A, B>>): First<B> =
    when (val fa = f(a).fix().getFirst) {
      is Either.Left -> loop(fa.a, f)
      is Either.Right -> First(fa.b)
    }

  override fun <A, B> tailRecM(a: A, f: (A) -> Kind<ForFirst, Either<A, B>>): Kind<ForFirst, B> = loop(a, f)

  override fun <A, B> Kind<ForFirst, A>.ap(ff: Kind<ForFirst, (A) -> B>): Kind<ForFirst, B> =
    First((ff.fix().getFirst(fix().getFirst)))
  override fun <A, B> Kind<ForFirst, A>.map(f: (A) -> B): Kind<ForFirst, B> = First(f(fix().getFirst))
}


@extension
interface FirstFoldable : Foldable<ForFirst> {
  override fun <A, B> Kind<ForFirst, A>.foldLeft(b: B, f: (B, A) -> B): B =
    f(b, fix().getFirst)
  override fun <A, B> Kind<ForFirst, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    Eval.defer { f(fix().getFirst, lb) }
}

@extension
interface FirstTraverse : Traverse<ForFirst>, FirstFoldable {
  override fun <G, A, B> Kind<ForFirst, A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, Kind<ForFirst, B>> =
    AP.run { f(fix().getFirst).map(::First) }
}
