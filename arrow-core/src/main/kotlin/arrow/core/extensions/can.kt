@file:Suppress("UnusedImports")

package arrow.core.extensions

import arrow.Kind
import arrow.Kind2
import arrow.core.Can
import arrow.core.Can.Both
import arrow.core.Can.Left
import arrow.core.Can.Neither
import arrow.core.Can.Right
import arrow.core.CanOf
import arrow.core.CanPartialOf
import arrow.core.Either
import arrow.core.Eval
import arrow.core.ForCan
import arrow.core.ap
import arrow.core.extensions.can.eq.eq
import arrow.core.extensions.can.monad.monad
import arrow.core.fix
import arrow.core.flatMap
import arrow.core.toLeftCan
import arrow.core.toCan
import arrow.extension
import arrow.typeclasses.Align
import arrow.typeclasses.Applicative
import arrow.typeclasses.Apply
import arrow.typeclasses.Bicrosswalk
import arrow.typeclasses.Bifoldable
import arrow.typeclasses.Bifunctor
import arrow.typeclasses.Bitraverse
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.EqK2
import arrow.typeclasses.Foldable
import arrow.typeclasses.Functor
import arrow.typeclasses.Hash
import arrow.typeclasses.Monad
import arrow.typeclasses.MonadSyntax
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup
import arrow.typeclasses.Show
import arrow.typeclasses.Traverse
import arrow.core.extensions.traverse as canTraverse

fun <L, R> Can<L, R>.combine(
  SGL: Semigroup<L>,
  SGR: Semigroup<R>,
  b: Can<L, R>
): Can<L, R> = when (val a = this) {
  is Neither -> when (b) {
    is Neither -> a
    is Left -> b
    is Right -> a
    is Both -> b
  }
  is Left -> when (b) {
    is Neither -> a
    is Left -> Left(SGL.run { a.a.combine(b.a) })
    is Right -> b
    is Both -> Both(SGL.run { a.a.combine(b.a) }, b.b)
  }
  is Right -> when (b) {
    is Neither -> a
    is Left -> Both(b.a, a.b)
    is Right -> Right(SGR.run { a.b.combine(b.b) })
    is Both -> Both(b.a, SGR.run { a.b.combine(b.b) })
  }
  is Both -> when (b) {
    is Neither -> a
    is Left -> Both(SGL.run { a.a.combine(a.a) }, a.b)
    is Right -> Both(a.a, SGR.run { a.b.combine(b.b) })
    is Both -> Both(SGL.run { a.a.combine(b.a) }, SGR.run { a.b.combine(b.b) })
  }
}

@extension
interface CanSemigroup<L, R> : Semigroup<Can<L, R>> {

  fun SGL(): Semigroup<L>
  fun SGR(): Semigroup<R>

  override fun Can<L, R>.combine(b: Can<L, R>): Can<L, R> = fix().combine(SGL(), SGR(), b)
}

@extension
interface CanMonoid<L, R> : Monoid<Can<L, R>>, CanSemigroup<L, R> {
  fun MOL(): Monoid<L>
  fun MOR(): Monoid<R>

  override fun SGL(): Semigroup<L> = MOL()
  override fun SGR(): Semigroup<R> = MOR()

  override fun empty(): Can<L, R> = Neither
}

@extension
interface CanFunctor<L> : Functor<CanPartialOf<L>> {
  override fun <A, B> CanOf<L, A>.map(f: (A) -> B): Can<L, B> = fix().map(f)
}

@extension
interface CanBifunctor : Bifunctor<ForCan> {
  override fun <A, B, C, D> CanOf<A, B>.bimap(fl: (A) -> C, fr: (B) -> D): Can<C, D> =
    fix().bimap(fl, fr)
}

@extension
interface CanApply<L> : Apply<CanPartialOf<L>>, CanFunctor<L> {

  fun SL(): Semigroup<L>

  override fun <A, B> CanOf<L, A>.map(f: (A) -> B): Can<L, B> = fix().map(f)

  override fun <A, B> Kind<CanPartialOf<L>, A>.apEval(ff: Eval<Kind<CanPartialOf<L>, (A) -> B>>): Eval<Kind<CanPartialOf<L>, B>> =
    fix().fold(
      ifNeither = { Eval.now(Neither) },
      ifLeft = { l -> Eval.now(Left(l)) },
      ifRight = { r -> ff.map { it.fix().map { f -> f(r) } } },
      ifBoth = { l, r ->
        ff.map { partial ->
          partial.fix().fold(
            ifNeither = Can.Companion::neither,
            ifLeft = { ll -> SL().run { l + ll }.toLeftCan() },
            ifRight = { f -> Both(l, f(r)) },
            ifBoth = { ll, f -> Both(SL().run { l + ll }, f(r)) }
          )
        }
      }
    )

  override fun <A, B> CanOf<L, A>.ap(ff: CanOf<L, (A) -> B>): Can<L, B> =
    fix().ap(SL(), ff)
}

@extension
interface CanApplicative<L> : Applicative<CanPartialOf<L>>, CanApply<L> {

  override fun SL(): Semigroup<L>

  override fun <A> just(a: A): Can<L, A> = Right(a)

  override fun <A, B> CanOf<L, A>.map(f: (A) -> B): Can<L, B> = fix().map(f)

  override fun <A, B> Kind<CanPartialOf<L>, A>.ap(ff: Kind<CanPartialOf<L>, (A) -> B>): Can<L, B> =
    fix().ap(SL(), ff)
}

@extension
interface CanMonad<L> : Monad<CanPartialOf<L>>, CanApplicative<L> {

  override fun SL(): Semigroup<L>

  override fun <A, B> CanOf<L, A>.map(f: (A) -> B): Can<L, B> = fix().map(f)

  override fun <A, B> CanOf<L, A>.ap(ff: CanOf<L, (A) -> B>): Can<L, B> =
    fix().ap(SL(), ff)

  override fun <A, B> CanOf<L, A>.flatMap(f: (A) -> CanOf<L, B>): Can<L, B> =
    fix().flatMap(SL()) { f(it).fix() }

  override fun <A, B> tailRecM(a: A, f: (A) -> Kind<CanPartialOf<L>, Either<A, B>>): Kind<CanPartialOf<L>, B> =
    Can.tailRecM(a, f, SL())
}

@extension
interface CanFoldable<L> : Foldable<CanPartialOf<L>> {

  override fun <A, B> CanOf<L, A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> CanOf<L, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().foldRight(lb, f)
}

@extension
interface CanBifoldable : Bifoldable<ForCan> {
  override fun <A, B, C> CanOf<A, B>.bifoldLeft(c: C, f: (C, A) -> C, g: (C, B) -> C): C = fix().bifoldLeft(c, f, g)

  override fun <A, B, C> CanOf<A, B>.bifoldRight(c: Eval<C>, f: (A, Eval<C>) -> Eval<C>, g: (B, Eval<C>) -> Eval<C>): Eval<C> =
    fix().bifoldRight(c, f, g)
}

fun <G, A, B, C> CanOf<A, B>.traverse(GA: Applicative<G>, f: (B) -> Kind<G, C>): Kind<G, Can<A, C>> = GA.run {
  fix().fold({ just(Neither) }, { just(Left(it)) }, { b -> f(b).map(::Right) }, { _, b -> f(b).map(::Right) })
}

@extension
interface CanTraverse<L> : Traverse<CanPartialOf<L>>, CanFoldable<L> {

  override fun <G, A, B> CanOf<L, A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, CanOf<L, B>> =
    fix().canTraverse(AP, f)
}

@extension
interface CanBitraverse : Bitraverse<ForCan>, CanBifoldable {
  override fun <G, A, B, C, D> CanOf<A, B>.bitraverse(
    AP: Applicative<G>,
    f: (A) -> Kind<G, C>,
    g: (B) -> Kind<G, D>
  ): Kind<G, CanOf<C, D>> = AP.run {
    fix().fold(
      ifNeither = { just(Neither) },
      ifLeft = { f(it).map(::Left) },
      ifRight = { g(it).map(::Right) },
      ifBoth = { a, b -> mapN(f(a), g(b)) { Both(it.a, it.b) } }
    )
  }
}

@extension
interface CanEq<in L, in R> : Eq<Can<L, R>> {

  fun EQL(): Eq<L>

  fun EQR(): Eq<R>

  override fun Can<L, R>.eqv(b: Can<L, R>): Boolean = when (this) {
    is Neither -> b is Neither
    is Left -> b is Left && EQL().run { a.eqv(b.a) }
    is Right -> b is Right && EQR().run { this@eqv.b.eqv(b.b) }
    is Both -> b is Both && EQL().run { a.eqv(b.a) } && EQR().run { this@eqv.b.eqv(b.b) }
  }
}

@extension
interface CanEqK<L> : EqK<CanPartialOf<L>> {
  fun EQL(): Eq<L>

  override fun <R> Kind<CanPartialOf<L>, R>.eqK(other: Kind<CanPartialOf<L>, R>, EQ: Eq<R>): Boolean =
    Can.eq(EQL(), EQ).run { this@eqK.fix().eqv(other.fix()) }
}

@extension
interface CanEqK2 : EqK2<ForCan> {
  override fun <A, B> Kind2<ForCan, A, B>.eqK(other: Kind2<ForCan, A, B>, EQA: Eq<A>, EQB: Eq<B>): Boolean =
    (this.fix() to other.fix()).let { (a, b) ->
      Can.eq(EQA, EQB).run { a.eqv(b) }
    }
}

@extension
interface CanShow<L, R> : Show<Can<L, R>> {
  fun SL(): Show<L>
  fun SR(): Show<R>
  override fun Can<L, R>.show(): String = show(SL(), SR())
}

@extension
interface CanHash<L, R> : Hash<Can<L, R>>, CanEq<L, R> {

  fun HL(): Hash<L>
  fun HR(): Hash<R>

  override fun EQL(): Eq<L> = HL()

  override fun EQR(): Eq<R> = HR()

  override fun Can<L, R>.hash(): Int =
    fold(
      ifNeither = { 0 },
      ifLeft = { HL().run { it.hash() } },
      ifRight = { HR().run { it.hash() } },
      ifBoth = { a, b -> 31 * HL().run { a.hash() } + HR().run { b.hash() } }
    )
}

fun <L, R> Can.Companion.fx(SL: Semigroup<L>, c: suspend MonadSyntax<CanPartialOf<L>>.() -> R): Can<L, R> =
  Can.monad<L>(SL).fx.monad(c).fix()

@extension
interface CanBicrosswalk : Bicrosswalk<ForCan>, CanBifunctor, CanBifoldable {
  override fun <F, A, B, C, D> bicrosswalk(
    ALIGN: Align<F>,
    tab: Kind2<ForCan, A, B>,
    fa: (A) -> Kind<F, C>,
    fb: (B) -> Kind<F, D>
  ): Kind<F, Kind2<ForCan, C, D>> =
    when (val can = tab.fix()) {
      is Neither -> ALIGN.empty()
      is Left -> ALIGN.run { fa(can.a).map(::Left) }
      is Right -> ALIGN.run { fb(can.b).map(::Right) }
      is Both -> ALIGN.alignWith(fa(can.a), fb(can.b)) { it.toCan() }
    }
}
