package arrow.core.extensions.semigroup

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.semigroup.Dual
import arrow.core.semigroup.ForDual
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
import arrow.typeclasses.Order
import arrow.typeclasses.Semigroup
import arrow.typeclasses.Show
import arrow.typeclasses.Traverse

@extension
interface DualEq<A> : Eq<Dual<A>> {
  fun EQ(): Eq<A>
  override fun Dual<A>.eqv(b: Dual<A>): Boolean = EQ().run { getDual.eqv(b.getDual) }
}

@extension
interface DualEqK : EqK<ForDual> {
  override fun <A> Kind<ForDual, A>.eqK(other: Kind<ForDual, A>, EQ: Eq<A>): Boolean =
    EQ.run { fix().getDual.eqv(other.fix().getDual) }
}

@extension
interface DualOrder<A> : Order<Dual<A>> {
  fun ORD(): Order<A>
  override fun Dual<A>.compare(b: Dual<A>): Int = ORD().run { getDual.compare(b.getDual) }
}

@extension
interface DualShow<A> : Show<Dual<A>> {
  fun SA(): Show<A>
  override fun Dual<A>.show(): String = SA().run { "Dual(${getDual.show()})" }
}

@extension
interface DualHash<A> : Hash<Dual<A>> {
  fun HA(): Hash<A>
  override fun Dual<A>.eqv(b: Dual<A>): Boolean = HA().run { getDual.eqv(b.getDual) }
  override fun Dual<A>.hash(): Int = HA().run { getDual.hash() }
}

@extension
interface DualSemigroup<A> : Semigroup<Dual<A>> {
  fun SA(): Semigroup<A>
  override fun Dual<A>.combine(b: Dual<A>): Dual<A> = Dual(SA().run { b.getDual.combine(getDual) })
}

@extension
interface DualMonoid<A> : Monoid<Dual<A>>, DualSemigroup<A> {
  fun MA(): Monoid<A>
  override fun SA(): Semigroup<A> = MA()
  override fun empty(): Dual<A> = Dual(MA().empty())
}

@extension
interface DualFunctor : Functor<ForDual> {
  override fun <A, B> Kind<ForDual, A>.map(f: (A) -> B): Kind<ForDual, B> = Dual(f(fix().getDual))
}

@extension
interface DualApplicative : Applicative<ForDual>, DualFunctor {
  override fun <A> just(a: A): Kind<ForDual, A> = Dual(a)
  override fun <A, B> Kind<ForDual, A>.ap(ff: Kind<ForDual, (A) -> B>): Kind<ForDual, B> =
    Dual(ff.fix().getDual.invoke(fix().getDual))
  override fun <A, B> Kind<ForDual, A>.map(f: (A) -> B): Kind<ForDual, B> = Dual(f(fix().getDual))
}

@extension
interface DualMonad : Monad<ForDual>, DualApplicative {
  override fun <A, B> Kind<ForDual, A>.flatMap(f: (A) -> Kind<ForDual, B>): Kind<ForDual, B> = f(fix().getDual)

  private tailrec fun <A, B> loop(a: A, f: (A) -> Kind<ForDual, Either<A, B>>): Dual<B> =
    when (val fa = f(a).fix().getDual) {
      is Either.Left -> loop(fa.a, f)
      is Either.Right -> Dual(fa.b)
    }

  override fun <A, B> tailRecM(a: A, f: (A) -> Kind<ForDual, Either<A, B>>): Kind<ForDual, B> = loop(a, f)

  override fun <A, B> Kind<ForDual, A>.map(f: (A) -> B): Kind<ForDual, B> = Dual(f(fix().getDual))
  override fun <A, B> Kind<ForDual, A>.ap(ff: Kind<ForDual, (A) -> B>): Kind<ForDual, B> =
    Dual(ff.fix().getDual.invoke(fix().getDual))
}

@extension
interface DualFoldable : Foldable<ForDual> {
  override fun <A, B> Kind<ForDual, A>.foldLeft(b: B, f: (B, A) -> B): B = f(b, fix().getDual)
  override fun <A, B> Kind<ForDual, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    Eval.defer { f(fix().getDual, lb) }
}

@extension
interface DualTraverse : Traverse<ForDual>, DualFoldable {
  override fun <G, A, B> Kind<ForDual, A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, Kind<ForDual, B>> =
    AP.run { f(fix().getDual).map(::Dual) }
}
