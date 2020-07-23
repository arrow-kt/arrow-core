package arrow.core.extensions.semigroup

import arrow.Kind
import arrow.core.AndThen
import arrow.core.Either
import arrow.core.Eval
import arrow.core.semigroup.Ap
import arrow.core.semigroup.ForAp
import arrow.core.semigroup.fix
import arrow.extension
import arrow.typeclasses.Alternative
import arrow.typeclasses.Applicative
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Foldable
import arrow.typeclasses.Functor
import arrow.typeclasses.Hash
import arrow.typeclasses.Monad
import arrow.typeclasses.MonadPlus
import arrow.typeclasses.Monoid
import arrow.typeclasses.Order
import arrow.typeclasses.Semigroup
import arrow.typeclasses.Show
import arrow.typeclasses.Traverse

@extension
interface ApEq<F, A> : Eq<Ap<F, A>> {
  fun EQ(): Eq<Kind<F, A>>
  override fun Ap<F, A>.eqv(b: Ap<F, A>): Boolean =
    EQ().run { getAp.eqv(b.getAp) }
}

@extension
interface ApEqK<F> : EqK<Kind<ForAp, F>> {
  fun EQK(): EqK<F>
  override fun <A> Kind<Kind<ForAp, F>, A>.eqK(other: Kind<Kind<ForAp, F>, A>, EQ: Eq<A>): Boolean =
    EQK().liftEq(EQ).run { fix().getAp.eqv(other.fix().getAp) }
}

@extension
interface ApShow<F, A> : Show<Ap<F, A>> {
  fun SFA(): Show<Kind<F, A>>
  override fun Ap<F, A>.show(): String = "Ap${SFA().run { getAp.show() }}"
}

@extension
interface ApOrder<F, A> : Order<Ap<F, A>> {
  fun ORDFA(): Order<Kind<F, A>>
  override fun Ap<F, A>.compare(b: Ap<F, A>): Int = ORDFA().run { getAp.compare(b.getAp) }
}

@extension
interface ApHash<F, A> : Hash<Ap<F, A>>, ApEq<F, A> {
  fun HA(): Hash<Kind<F, A>>
  override fun EQ(): Eq<Kind<F, A>> = HA()
  override fun Ap<F, A>.hash(): Int = HA().run { getAp.hash() }
}

@extension
interface ApFunctor<F> : Functor<Kind<ForAp, F>> {
  fun FF(): Functor<F>
  override fun <A, B> Kind<Kind<ForAp, F>, A>.map(f: (A) -> B): Kind<Kind<ForAp, F>, B> =
    FF().run { Ap(fix().getAp.map(f)) }
}

@extension
interface ApApplicative<F> : Applicative<Kind<ForAp, F>>, ApFunctor<F> {
  fun AF(): Applicative<F>
  override fun FF(): Functor<F> = AF()
  override fun <A> just(a: A): Kind<Kind<ForAp, F>, A> = Ap(AF().just(a))
  override fun <A, B> Kind<Kind<ForAp, F>, A>.ap(ff: Kind<Kind<ForAp, F>, (A) -> B>): Kind<Kind<ForAp, F>, B> =
    AF().run { Ap(fix().getAp.ap(ff.fix().getAp)) }
  override fun <A, B> Kind<Kind<ForAp, F>, A>.map(f: (A) -> B): Kind<Kind<ForAp, F>, B> =
    FF().run { Ap(fix().getAp.map(f)) }
}

@extension
interface ApMonad<F> : Monad<Kind<ForAp, F>>, ApApplicative<F> {
  fun MF(): Monad<F>
  override fun AF(): Applicative<F> = MF()
  override fun <A, B> Kind<Kind<ForAp, F>, A>.flatMap(f: (A) -> Kind<Kind<ForAp, F>, B>): Kind<Kind<ForAp, F>, B> =
    MF().run { Ap(fix().getAp.flatMap(AndThen(f).andThen { it.fix().getAp })) }

  override fun <A, B> tailRecM(a: A, f: (A) -> Kind<Kind<ForAp, F>, Either<A, B>>): Kind<Kind<ForAp, F>, B> =
    Ap(MF().tailRecM(a, AndThen(f).andThen { it.fix().getAp }))

  override fun <A, B> Kind<Kind<ForAp, F>, A>.ap(ff: Kind<Kind<ForAp, F>, (A) -> B>): Kind<Kind<ForAp, F>, B> =
    AF().run { Ap(fix().getAp.ap(ff.fix().getAp)) }
  override fun <A, B> Kind<Kind<ForAp, F>, A>.map(f: (A) -> B): Kind<Kind<ForAp, F>, B> =
    FF().run { Ap(fix().getAp.map(f)) }
}

@extension
interface ApFoldable<F> : Foldable<Kind<ForAp, F>> {
  fun AF(): Foldable<F>
  override fun <A, B> Kind<Kind<ForAp, F>, A>.foldLeft(b: B, f: (B, A) -> B): B =
    AF().run { fix().getAp.foldLeft(b, f) }
  override fun <A, B> Kind<Kind<ForAp, F>, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    AF().run { fix().getAp.foldRight(lb, f) }
}

@extension
interface ApTraverse<F> : Traverse<Kind<ForAp, F>>, ApFoldable<F> {
  fun TF(): Traverse<F>
  override fun AF(): Foldable<F> = TF()
  override fun <G, A, B> Kind<Kind<ForAp, F>, A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, Kind<Kind<ForAp, F>, B>> =
    TF().run { AP.run { fix().getAp.traverse(AP, f).map(::Ap) } }
}

@extension
interface ApAlternative<F> : Alternative<Kind<ForAp, F>>, ApApplicative<F> {
  fun AltF(): Alternative<F>
  override fun AF(): Applicative<F> = AltF()
  override fun <A> empty(): Kind<Kind<ForAp, F>, A> = Ap(AltF().empty())
  override fun <A> Kind<Kind<ForAp, F>, A>.orElse(b: Kind<Kind<ForAp, F>, A>): Kind<Kind<ForAp, F>, A> =
    AltF().run { Ap(fix().getAp.orElse(b.fix().getAp)) }
}

@extension
interface ApMonadPlus<F> : MonadPlus<Kind<ForAp, F>>, ApAlternative<F>, ApMonad<F> {
  override fun MF(): Monad<F> = MP()
  override fun AltF(): Alternative<F> = MP()
  override fun AF(): Applicative<F> = MP()
  fun MP(): MonadPlus<F>
}

@extension
interface ApSemigroup<F, A> : Semigroup<Ap<F, A>> {
  fun AF(): Applicative<F>
  fun SA(): Semigroup<A>
  override fun Ap<F, A>.combine(b: Ap<F, A>): Ap<F, A> = AF().run { Ap(getAp.map2(b.getAp) { (a, b) -> SA().run { a + b } }) }
}

@extension
interface ApMonoid<F, A> : Monoid<Ap<F, A>>, ApSemigroup<F, A> {
  override fun AF(): Applicative<F>
  fun MA(): Monoid<A>
  override fun SA(): Semigroup<A> = MA()
  override fun empty(): Ap<F, A> = Ap(AF().just(MA().empty()))
}

