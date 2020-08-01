package arrow.continuations.generic.effect

import arrow.Kind
import arrow.continuations.generic.DelimitedScope
import arrow.core.Either
import arrow.core.EitherPartialOf
import arrow.core.ForListK
import arrow.core.ListK
import arrow.core.extensions.either.applicative.applicative
import arrow.core.extensions.listk.applicative.applicative
import arrow.core.fix
import arrow.core.k
import arrow.typeclasses.Applicative

fun <F, E, A> Either.Companion.errorHandler(
  ap: Applicative<F>,
  delimitedScope: DelimitedScope<Kind<F, Kind<EitherPartialOf<E>, A>>>
): Error<E> =
  object : Error<E> {
    override suspend fun <Eff : Error<E>, A> Eff.catch(f: suspend Eff.() -> A, hdl: suspend Eff.(E) -> A): A =
      TODO("I need more powers over F to do this")

    override suspend fun raise(e: E): Nothing =
      delimitedScope.shift { ap.just(left(e)) }
  }

fun <A> nullableHandler(delimitedScope: DelimitedScope<A?>): NonDet =
  object : NonDet {
    override suspend fun choose(): Boolean =
      delimitedScope.shift { it(true) ?: it(false) }

    override suspend fun empty(): Nothing = delimitedScope.shift { null }
  }

fun <F, A> listHandler(
  ap: Applicative<F>,
  delimitedScope: DelimitedScope<Kind<F, Kind<ForListK, A>>>
): NonDet =
  object : NonDet {
    override suspend fun choose(): Boolean =
      delimitedScope.shift { ap.run { (it(true).map2(it(false)) { (a, b) -> (a.fix() + b.fix()).k() }) } }

    override suspend fun empty(): Nothing = delimitedScope.shift { ap.just(emptyList<A>().k()) }
  }

interface NonDetAndError<E> : NonDet, Error<E>
fun <F, E, A> listEitherHandler(
  ap: Applicative<F>,
  delimitedScope: DelimitedScope<Kind<F, Kind<ForListK, Kind<EitherPartialOf<E>, A>>>>
): NonDetAndError<E> =
  object : NonDetAndError<E>,
    Error<E> by Either.errorHandler(ComposedApplicative(ap, ListK.applicative()), delimitedScope as DelimitedScope<Kind<Nested<F, ForListK>, Kind<EitherPartialOf<E>, A>>>),
    NonDet by listHandler(ap, delimitedScope)
  {}

fun <F, E, A> eitherListHandler(
  ap: Applicative<F>,
  delimitedScope: DelimitedScope<Kind<F, Kind<EitherPartialOf<E>, Kind<ForListK, A>>>>
): NonDetAndError<E> =
  object : NonDetAndError<E>,
    Error<E> by Either.errorHandler(ap, delimitedScope),
    NonDet by listHandler(ComposedApplicative(ap, Either.applicative<E>()), delimitedScope as DelimitedScope<Kind<Nested<F, EitherPartialOf<E>>, Kind<ForListK, A>>>)
  {}

interface Nested<out F, out G>

fun <F, G, A> Kind<F, Kind<G, A>>.nest(): Kind<Nested<F, G>, A> = this as Kind<Nested<F, G>, A>
fun <F, G, A> Kind<Nested<F, G>, A>.unnest(): Kind<F, Kind<G, A>> = this as Kind<F, Kind<G, A>>

fun <F, G> ComposedApplicative(apF: Applicative<F>, apG: Applicative<G>): Applicative<Nested<F, G>> = object : Applicative<Nested<F, G>> {
  override fun <A, B> Kind<Nested<F, G>, A>.ap(ff: Kind<Nested<F, G>, (A) -> B>): Kind<Nested<F, G>, B> =
    apF.run { unnest().ap(ff.unnest().map { gf -> { ga: Kind<G, A> -> apG.run { ga.ap(gf) } } }).nest() }

  override fun <A> just(a: A): Kind<Nested<F, G>, A> = apF.just(apG.just(a)).nest()
}

// #########################
suspend fun <E> E.myFun(): Int where E : NonDet, E : Error<String> = if (choose()) raise("Better luck next time") else 42
