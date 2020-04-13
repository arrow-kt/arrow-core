package arrow.core.extensions

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.ForValidated
import arrow.core.Invalid
import arrow.core.Valid
import arrow.core.Validated
import arrow.core.ValidatedOf
import arrow.core.ValidatedPartialOf
import arrow.core.ap
import arrow.core.combineK
import arrow.core.extensions.validated.eq.eq
import arrow.core.fix
import arrow.extension
import arrow.typeclasses.Applicative
import arrow.typeclasses.ApplicativeError
import arrow.typeclasses.Bifoldable
import arrow.typeclasses.Bitraverse
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.EqK2
import arrow.typeclasses.Foldable
import arrow.typeclasses.Functor
import arrow.typeclasses.Hash
import arrow.typeclasses.Selective
import arrow.typeclasses.Semigroup
import arrow.typeclasses.SemigroupK
import arrow.typeclasses.Show
import arrow.typeclasses.Traverse
import arrow.undocumented
import arrow.core.handleErrorWith as validatedHandleErrorWith
import arrow.core.traverse as validatedTraverse

@extension
@undocumented
interface ValidatedFunctor : Functor<ForValidated> {
  override fun <A, B> ValidatedPartialOf<A>.map(f: (A) -> B): ValidatedPartialOf<B> =
    fix().map(f).unnest()
}

@extension
interface ValidatedApplicative<E> : Applicative<ForValidated>, ValidatedFunctor {

  fun SE(): Semigroup<E>

  override fun <A> just(a: A): ValidatedPartialOf<A> =
    Valid(a).unnest()

  override fun <A, B> ValidatedPartialOf<A>.map(f: (A) -> B): ValidatedPartialOf<B> =
    fix().map(f).unnest()

  override fun <A, B> ValidatedPartialOf<A>.ap(ff: ValidatedPartialOf<(A) -> B>): ValidatedPartialOf<B> =
    fix().ap(SE(), ff.fix()).unnest()
}

@extension
interface ValidatedSelective<E> : Selective<ForValidated>, ValidatedApplicative<E> {

  override fun SE(): Semigroup<E>

  override fun <A, B> ValidatedPartialOf<Either<A, B>>.select(f: ValidatedPartialOf<(A) -> B>): ValidatedPartialOf<B> =
    fix().fold({ Invalid(it) }, { it.fold({ l -> f.map { ff -> ff(l) } }, { r -> just(r) }) }).unnest()
}

@extension
interface ValidatedApplicativeError<E> : ApplicativeError<ForValidated, E>, ValidatedApplicative<E> {

  override fun SE(): Semigroup<E>

  override fun <A> raiseError(e: E): ValidatedPartialOf<A> =
    Invalid(e).unnest()

  override fun <A> ValidatedPartialOf<A>.handleErrorWith(f: (E) -> ValidatedPartialOf<A>): ValidatedPartialOf<A> =
    fix().validatedHandleErrorWith(f).unnest()
}

@extension
interface ValidatedFoldable : Foldable<ForValidated> {

  override fun <A, B> ValidatedPartialOf<A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> ValidatedPartialOf<A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().foldRight(lb, f)
}

@extension
interface ValidatedTraverse : Traverse<ForValidated>, ValidatedFoldable {

  override fun <G, A, B> ValidatedPartialOf<A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, ValidatedPartialOf<B>> =
    fix().validatedTraverse(AP, f).unnest()
}

@extension
interface ValidatedBifoldable : Bifoldable<ForValidated> {
  override fun <A, B, C> ValidatedOf<A, B>.bifoldLeft(c: C, f: (C, A) -> C, g: (C, B) -> C): C =
    fix().fold({ f(c, it) }, { g(c, it) })

  override fun <A, B, C> ValidatedOf<A, B>.bifoldRight(c: Eval<C>, f: (A, Eval<C>) -> Eval<C>, g: (B, Eval<C>) -> Eval<C>): Eval<C> =
    fix().fold({ f(it, c) }, { g(it, c) })
}

@extension
interface ValidatedBitraverse : Bitraverse<ForValidated>, ValidatedBifoldable {
  override fun <G, A, B, C, D> ValidatedOf<A, B>.bitraverse(AP: Applicative<G>, f: (A) -> Kind<G, C>, g: (B) -> Kind<G, D>): Kind<G, ValidatedOf<C, D>> =
    fix().let {
      AP.run {
        it.fold(
          { f(it).map(::Invalid) },
          { g(it).map(::Valid) }
        )
      }
    }
}

@extension
interface ValidatedSemigroupK<E> : SemigroupK<ForValidated> {

  fun SE(): Semigroup<E>

  override fun <B> ValidatedPartialOf<B>.combineK(y: ValidatedPartialOf<B>): ValidatedPartialOf<B> =
    fix().combineK(SE(), y).unnest()
}

@extension
interface ValidatedEq<L, R> : Eq<Validated<L, R>> {

  fun EQL(): Eq<L>

  fun EQR(): Eq<R>

  override fun Validated<L, R>.eqv(b: Validated<L, R>): Boolean = when (this) {
    is Valid -> when (b) {
      is Invalid -> false
      is Valid -> EQR().run { a.eqv(b.a) }
    }
    is Invalid -> when (b) {
      is Invalid -> EQL().run { e.eqv(b.e) }
      is Valid -> false
    }
  }
}

@extension
interface ValidatedEqK<L> : EqK<ForValidated> {
  fun EQL(): Eq<L>

  override fun <R> ValidatedPartialOf<R>.eqK(other: ValidatedPartialOf<R>, EQ: Eq<R>): Boolean =
    Validated.eq(EQL(), EQ).run {
      this@eqK.fix().eqv(other.fix())
    }
}

@extension
interface ValidatedEqK2 : EqK2<ForValidated> {
  override fun <A, B> ValidatedOf<A, B>.eqK(other: ValidatedOf<A, B>, EQA: Eq<A>, EQB: Eq<B>): Boolean =
    (this.fix() to other.fix()).let {
      Validated.eq(EQA, EQB).run {
        it.first.eqv(it.second)
      }
    }
}

@extension
interface ValidatedShow<L, R> : Show<Validated<L, R>> {
  fun SL(): Show<L>
  fun SR(): Show<R>
  override fun Validated<L, R>.show(): String = show(SL(), SR())
}

@extension
interface ValidatedHash<L, R> : Hash<Validated<L, R>>, ValidatedEq<L, R> {
  fun HL(): Hash<L>
  fun HR(): Hash<R>

  override fun EQL(): Eq<L> = HL()
  override fun EQR(): Eq<R> = HR()

  override fun Validated<L, R>.hash(): Int = fold({
    HL().run { it.hash() }
  }, {
    HR().run { it.hash() }
  })
}
