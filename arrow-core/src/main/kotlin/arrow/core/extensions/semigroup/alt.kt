package arrow.core.extensions.semigroup

import arrow.Kind
import arrow.core.AndThen
import arrow.core.Either
import arrow.core.Eval
import arrow.core.semigroup.Alt
import arrow.core.semigroup.ForAlt
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
interface AltEq<F, A> : Eq<Alt<F, A>> {
  fun EQ(): Eq<Kind<F, A>>
  override fun Alt<F, A>.eqv(b: Alt<F, A>): Boolean =
    EQ().run { getAlt.eqv(b.getAlt) }
}

@extension
interface AltEqK<F> : EqK<Kind<ForAlt, F>> {
  fun EQK(): EqK<F>
  override fun <A> Kind<Kind<ForAlt, F>, A>.eqK(other: Kind<Kind<ForAlt, F>, A>, EQ: Eq<A>): Boolean =
    EQK().liftEq(EQ).run { fix().getAlt.eqv(other.fix().getAlt) }
}

@extension
interface AltShow<F, A> : Show<Alt<F, A>> {
  fun SFA(): Show<Kind<F, A>>
  override fun Alt<F, A>.show(): String = "Alt${SFA().run { getAlt.show() }}"
}

@extension
interface AltOrder<F, A> : Order<Alt<F, A>> {
  fun ORDFA(): Order<Kind<F, A>>
  override fun Alt<F, A>.compare(b: Alt<F, A>): Int = ORDFA().run { getAlt.compare(b.getAlt) }
}

@extension
interface AltHash<F, A> : Hash<Alt<F, A>>, AltEq<F, A> {
  fun HA(): Hash<Kind<F, A>>
  override fun EQ(): Eq<Kind<F, A>> = HA()
  override fun Alt<F, A>.hash(): Int = HA().run { getAlt.hash() }
}

@extension
interface AltFunctor<F> : Functor<Kind<ForAlt, F>> {
  fun FF(): Functor<F>
  override fun <A, B> Kind<Kind<ForAlt, F>, A>.map(f: (A) -> B): Kind<Kind<ForAlt, F>, B> =
    FF().run { Alt(fix().getAlt.map(f)) }
}

@extension
interface AltApplicative<F> : Applicative<Kind<ForAlt, F>>, AltFunctor<F> {
  fun AF(): Applicative<F>
  override fun FF(): Functor<F> = AF()
  override fun <A> just(a: A): Kind<Kind<ForAlt, F>, A> = Alt(AF().just(a))
  override fun <A, B> Kind<Kind<ForAlt, F>, A>.ap(ff: Kind<Kind<ForAlt, F>, (A) -> B>): Kind<Kind<ForAlt, F>, B> =
    AF().run { Alt(fix().getAlt.ap(ff.fix().getAlt)) }
  override fun <A, B> Kind<Kind<ForAlt, F>, A>.map(f: (A) -> B): Kind<Kind<ForAlt, F>, B> =
    FF().run { Alt(fix().getAlt.map(f)) }
}

@extension
interface AltMonad<F> : Monad<Kind<ForAlt, F>>, AltApplicative<F> {
  fun MF(): Monad<F>
  override fun AF(): Applicative<F> = MF()
  override fun <A, B> Kind<Kind<ForAlt, F>, A>.flatMap(f: (A) -> Kind<Kind<ForAlt, F>, B>): Kind<Kind<ForAlt, F>, B> =
    MF().run { Alt(fix().getAlt.flatMap(AndThen(f).andThen { it.fix().getAlt })) }

  override fun <A, B> tailRecM(a: A, f: (A) -> Kind<Kind<ForAlt, F>, Either<A, B>>): Kind<Kind<ForAlt, F>, B> =
    Alt(MF().tailRecM(a, AndThen(f).andThen { it.fix().getAlt }))

  override fun <A, B> Kind<Kind<ForAlt, F>, A>.ap(ff: Kind<Kind<ForAlt, F>, (A) -> B>): Kind<Kind<ForAlt, F>, B> =
    AF().run { Alt(fix().getAlt.ap(ff.fix().getAlt)) }
  override fun <A, B> Kind<Kind<ForAlt, F>, A>.map(f: (A) -> B): Kind<Kind<ForAlt, F>, B> =
    FF().run { Alt(fix().getAlt.map(f)) }
}

@extension
interface AltFoldable<F> : Foldable<Kind<ForAlt, F>> {
  fun AF(): Foldable<F>
  override fun <A, B> Kind<Kind<ForAlt, F>, A>.foldLeft(b: B, f: (B, A) -> B): B =
    AF().run { fix().getAlt.foldLeft(b, f) }
  override fun <A, B> Kind<Kind<ForAlt, F>, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    AF().run { fix().getAlt.foldRight(lb, f) }
}

@extension
interface AltTraverse<F> : Traverse<Kind<ForAlt, F>>, AltFoldable<F> {
  fun TF(): Traverse<F>
  override fun AF(): Foldable<F> = TF()
  override fun <G, A, B> Kind<Kind<ForAlt, F>, A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, Kind<Kind<ForAlt, F>, B>> =
    TF().run { AP.run { fix().getAlt.traverse(AP, f).map(::Alt) } }
}

@extension
interface AltAlternative<F> : Alternative<Kind<ForAlt, F>>, AltApplicative<F> {
  fun AltF(): Alternative<F>
  override fun AF(): Applicative<F> = AltF()
  override fun <A> empty(): Kind<Kind<ForAlt, F>, A> = Alt(AltF().empty())
  override fun <A> Kind<Kind<ForAlt, F>, A>.orElse(b: Kind<Kind<ForAlt, F>, A>): Kind<Kind<ForAlt, F>, A> =
    AltF().run { Alt(fix().getAlt.orElse(b.fix().getAlt)) }
}

@extension
interface AltMonadPlus<F> : MonadPlus<Kind<ForAlt, F>>, AltAlternative<F>, AltMonad<F> {
  override fun MF(): Monad<F> = MP()
  override fun AltF(): Alternative<F> = MP()
  override fun AF(): Applicative<F> = MP()
  fun MP(): MonadPlus<F>
}

@extension
interface AltSemigroup<F, A> : Semigroup<Alt<F, A>> {
  fun AF(): Alternative<F>
  override fun Alt<F, A>.combine(b: Alt<F, A>): Alt<F, A> = AF().run { Alt(getAlt.orElse(b.getAlt)) }
}

@extension
interface AltMonoid<F, A> : Monoid<Alt<F, A>>, AltSemigroup<F, A> {
  override fun AF(): Alternative<F>
  override fun empty(): Alt<F, A> = Alt(AF().empty())
}
