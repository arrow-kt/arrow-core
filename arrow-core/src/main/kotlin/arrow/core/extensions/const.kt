package arrow.core.extensions

import arrow.Kind
import arrow.core.Const
import arrow.core.ConstOf
import arrow.core.ConstPartialOf
import arrow.core.Eval
import arrow.core.ForConst
import arrow.core.Option
import arrow.core.Tuple2
import arrow.core.extensions.const.eq.eq
import arrow.core.fix
import arrow.core.value
import arrow.extension
import arrow.typeclasses.Applicative
import arrow.typeclasses.Apply
import arrow.typeclasses.Contravariant
import arrow.typeclasses.Divide
import arrow.typeclasses.Divisible
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Foldable
import arrow.typeclasses.Functor
import arrow.typeclasses.Hash
import arrow.typeclasses.Invariant
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup
import arrow.typeclasses.Show
import arrow.typeclasses.Traverse
import arrow.typeclasses.TraverseFilter
import arrow.core.ap as constAp
import arrow.core.combine as combineAp

@extension
interface ConstInvariant : Invariant<ForConst> {
  override fun <T, U> ConstPartialOf<T>.imap(f: (T) -> U, g: (U) -> T): ConstPartialOf<U> =
    fix().retag<U>().unnest()
}

@extension
interface ConstContravariant : Contravariant<ForConst> {
  override fun <T, U> ConstPartialOf<T>.contramap(f: (U) -> T): ConstPartialOf<U> =
    fix().retag<U>().unnest()
}

@extension
interface ConstDivideInstance<O> : Divide<ForConst>, ConstContravariant {
  fun MO(): Monoid<O>

  override fun <A, B, Z> divide(fa: ConstPartialOf<A>, fb: ConstPartialOf<B>, f: (Z) -> Tuple2<A, B>): ConstPartialOf<Z> =
    Const<O, Z>(
      MO().run { fa.value() + fb.value() }
    ).unnest()
}

@extension
interface ConstDivisibleInstance<O> : Divisible<ForConst>, ConstDivideInstance<O> {
  override fun MO(): Monoid<O>

  override fun <A> conquer(): ConstPartialOf<A> =
    Const<O, A>(MO().empty()).unnest()
}

@extension
interface ConstFunctor : Functor<ForConst> {
  override fun <T, U> ConstPartialOf<T>.map(f: (T) -> U): ConstPartialOf<U> =
    fix().retag<U>().unnest()
}

@extension
interface ConstApply<A> : Apply<ForConst> {

  fun MA(): Monoid<A>

  override fun <T, U> ConstPartialOf<T>.map(f: (T) -> U): ConstPartialOf<U> =
    fix().retag<U>().unnest()

  override fun <T, U> ConstPartialOf<T>.ap(ff: ConstPartialOf<(T) -> U>): ConstPartialOf<U> =
    constAp(MA(), ff).unnest()
}

@extension
interface ConstApplicative<A> : Applicative<ForConst> {

  fun MA(): Monoid<A>

  override fun <T, U> ConstPartialOf<T>.map(f: (T) -> U): ConstPartialOf<U> =
    fix().retag<U>().unnest()

  override fun <T> just(a: T): ConstPartialOf<T> = object : ConstMonoid<A, T> {
    override fun SA(): Semigroup<A> = MA()
    override fun MA(): Monoid<A> = this@ConstApplicative.MA()
  }.empty().fix().unnest()

  override fun <T, U> ConstPartialOf<T>.ap(ff: ConstPartialOf<(T) -> U>): ConstPartialOf<U> =
    constAp(MA(), ff).unnest()
}

@extension
interface ConstFoldable : Foldable<ForConst> {

  override fun <T, U> ConstPartialOf<T>.foldLeft(b: U, f: (U, T) -> U): U = b

  override fun <T, U> ConstPartialOf<T>.foldRight(lb: Eval<U>, f: (T, Eval<U>) -> Eval<U>): Eval<U> = lb
}

@extension
interface ConstTraverse : Traverse<ForConst>, ConstFoldable {

  override fun <T, U> ConstPartialOf<T>.map(f: (T) -> U): ConstPartialOf<U> =
    fix().retag<U>().unnest()

  override fun <G, A, B> ConstPartialOf<A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, ConstPartialOf<B>> =
    fix().traverse(AP, f).unnest()
}

@extension
interface ConstTraverseFilter : TraverseFilter<ForConst>, ConstTraverse {

  override fun <T, U> ConstPartialOf<T>.map(f: (T) -> U): ConstPartialOf<U> =
    fix().retag<U>().unnest()

  override fun <G, A, B> ConstPartialOf<A>.traverseFilter(AP: Applicative<G>, f: (A) -> Kind<G, Option<B>>): Kind<G, ConstPartialOf<B>> =
    fix().traverseFilter(AP, f).unnest()
}

@extension
interface ConstSemigroup<A, T> : Semigroup<ConstOf<A, T>> {

  fun SA(): Semigroup<A>

  override fun ConstOf<A, T>.combine(b: ConstOf<A, T>): Const<A, T> =
    combineAp(SA(), b)
}

@extension
interface ConstMonoid<A, T> : Monoid<ConstOf<A, T>>, ConstSemigroup<A, T> {

  fun MA(): Monoid<A>

  override fun SA(): Semigroup<A> = MA()

  override fun empty(): Const<A, T> = Const(MA().empty())
}

@extension
interface ConstEq<A, T> : Eq<Const<A, T>> {

  fun EQ(): Eq<A>

  override fun Const<A, T>.eqv(b: Const<A, T>): Boolean =
    EQ().run { value().eqv(b.value()) }
}

@extension
interface ConstEqK<A> : EqK<ForConst> {

  fun EQA(): Eq<A>

  override fun <T> ConstPartialOf<T>.eqK(other: ConstPartialOf<T>, EQ: Eq<T>): Boolean =
    Const.eq<A, T>(EQA()).run {
      this@eqK.nest<A>().fix().eqv(other.nest<A>().fix())
    }
}

@extension
interface ConstShow<A, T> : Show<Const<A, T>> {
  fun SA(): Show<A>
  override fun Const<A, T>.show(): String = show(SA())
}

@extension
interface ConstHash<A, T> : Hash<Const<A, T>>, ConstEq<A, T> {
  fun HA(): Hash<A>

  override fun EQ(): Eq<A> = HA()

  override fun Const<A, T>.hash(): Int = HA().run { value().hash() }
}
