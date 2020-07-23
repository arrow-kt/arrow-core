package arrow.core.extensions.semigroup

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.semigroup.ForSum
import arrow.core.semigroup.Sum
import arrow.core.semigroup.fix
import arrow.extension
import arrow.typeclasses.Applicative
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Foldable
import arrow.typeclasses.Functor
import arrow.typeclasses.Hash
import arrow.typeclasses.Monad
import arrow.typeclasses.Monoid
import arrow.typeclasses.Num
import arrow.typeclasses.Order
import arrow.typeclasses.Semigroup
import arrow.typeclasses.Show
import arrow.typeclasses.Traverse

@extension
interface SumEq<A> : Eq<Sum<A>> {
  fun EQA(): Eq<A>
  override fun Sum<A>.eqv(b: Sum<A>): Boolean = EQA().run { getSum.eqv(b.getSum) }
}

@extension
interface SumEqK : EqK<ForSum> {
  override fun <A> Kind<ForSum, A>.eqK(other: Kind<ForSum, A>, EQ: Eq<A>): Boolean =
    EQ.run { fix().getSum.eqv(other.fix().getSum) }
}

@extension
interface SumOrder<A> : Order<Sum<A>> {
  fun ORD(): Order<A>
  override fun Sum<A>.compare(b: Sum<A>): Int = ORD().run { getSum.compare(b.getSum) }
}

@extension
interface SumShow<A> : Show<Sum<A>> {
  fun SA(): Show<A>
  override fun Sum<A>.show(): String = SA().run { "Sum(${getSum.show()})" }
}

@extension
interface SumHash<A> : Hash<Sum<A>>, SumEq<A> {
  fun HA(): Hash<A>
  override fun EQA(): Eq<A> = HA()
  override fun Sum<A>.hash(): Int = HA().run { getSum.hash() }
}

@extension
interface SumFunctor : Functor<ForSum> {
  override fun <A, B> Kind<ForSum, A>.map(f: (A) -> B): Kind<ForSum, B> = Sum(f(fix().getSum))
}

@extension
interface SumApplicative : Applicative<ForSum>, SumFunctor {
  override fun <A> just(a: A): Kind<ForSum, A> = Sum(a)
  override fun <A, B> Kind<ForSum, A>.ap(ff: Kind<ForSum, (A) -> B>): Kind<ForSum, B> =
    Sum(ff.fix().getSum.invoke(fix().getSum))
  override fun <A, B> Kind<ForSum, A>.map(f: (A) -> B): Kind<ForSum, B> = Sum(f(fix().getSum))
}

@extension
interface SumMonad : Monad<ForSum>, SumApplicative {
  override fun <A, B> Kind<ForSum, A>.flatMap(f: (A) -> Kind<ForSum, B>): Kind<ForSum, B> = f(fix().getSum)

  private tailrec fun <A, B> loop(a: A, f: (A) -> Kind<ForSum, Either<A, B>>): Kind<ForSum, B> =
    when (val fa = f(a).fix().getSum) {
      is Either.Left -> loop(fa.a, f)
      is Either.Right -> Sum(fa.b)
    }

  override fun <A, B> tailRecM(a: A, f: (A) -> Kind<ForSum, Either<A, B>>): Kind<ForSum, B> = loop(a, f)

  override fun <A, B> Kind<ForSum, A>.ap(ff: Kind<ForSum, (A) -> B>): Kind<ForSum, B> =
    Sum(ff.fix().getSum.invoke(fix().getSum))
  override fun <A, B> Kind<ForSum, A>.map(f: (A) -> B): Kind<ForSum, B> = Sum(f(fix().getSum))
}

@extension
interface SumFoldable : Foldable<ForSum> {
  override fun <A, B> Kind<ForSum, A>.foldLeft(b: B, f: (B, A) -> B): B = f(b, fix().getSum)
  override fun <A, B> Kind<ForSum, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    Eval.defer { f(fix().getSum, lb) }
}

@extension
interface SumTraverse : Traverse<ForSum>, SumFoldable {
  override fun <G, A, B> Kind<ForSum, A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, Kind<ForSum, B>> =
    AP.run { f(fix().getSum).map(::Sum) }
}

@extension
interface SumSemigroup<A> : Semigroup<Sum<A>> {
  fun NA(): Num<A>
  override fun Sum<A>.combine(b: Sum<A>): Sum<A> = Sum(NA().run { getSum + b.getSum })
}

@extension
interface SumMonoid<A> : Monoid<Sum<A>>, SumSemigroup<A> {
  override fun NA(): Num<A>
  override fun empty(): Sum<A> = Sum(NA().run { 0L.fromLong() })
}
