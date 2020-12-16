package arrow.core.extensions.either.bifunctor

import arrow.Kind
import arrow.core.Either
import arrow.core.Either.Companion
import arrow.core.ForEither
import arrow.core.extensions.EitherBifunctor
import arrow.typeclasses.Conested
import arrow.typeclasses.Functor
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val bifunctor_singleton: EitherBifunctor = object : arrow.core.extensions.EitherBifunctor {}

@JvmName("bimap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B, C, D> Kind<Kind<ForEither, A>, B>.bimap(arg1: Function1<A, C>, arg2: Function1<B, D>):
  Either<C, D> = arrow.core.Either.bifunctor().run {
  this@bimap.bimap<A, B, C, D>(arg1, arg2) as arrow.core.Either<C, D>
}

@JvmName("lift")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B, C, D> lift(arg0: Function1<A, C>, arg1: Function1<B, D>): Function1<Kind<Kind<ForEither,
  A>, B>, Kind<Kind<ForEither, C>, D>> = arrow.core.Either
  .bifunctor()
  .lift<A, B, C, D>(arg0, arg1) as kotlin.Function1<arrow.Kind<arrow.Kind<arrow.core.ForEither, A>,
  B>, arrow.Kind<arrow.Kind<arrow.core.ForEither, C>, D>>

@JvmName("mapLeft")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B, C> Kind<Kind<ForEither, A>, B>.mapLeft(arg1: Function1<A, C>): Either<C, B> =
  arrow.core.Either.bifunctor().run {
    this@mapLeft.mapLeft<A, B, C>(arg1) as arrow.core.Either<C, B>
  }

@JvmName("rightFunctor")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <X> rightFunctor(): Functor<Kind<ForEither, X>> = arrow.core.Either
  .bifunctor()
  .rightFunctor<X>() as arrow.typeclasses.Functor<arrow.Kind<arrow.core.ForEither, X>>

@JvmName("leftFunctor")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <X> leftFunctor(): Functor<Conested<ForEither, X>> = arrow.core.Either
  .bifunctor()
  .leftFunctor<X>() as arrow.typeclasses.Functor<arrow.typeclasses.Conested<arrow.core.ForEither,
  X>>

@JvmName("leftWiden")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <AA, B, A : AA> Kind<Kind<ForEither, A>, B>.leftWiden(): Either<AA, B> =
  arrow.core.Either.bifunctor().run {
    this@leftWiden.leftWiden<AA, B, A>() as arrow.core.Either<AA, B>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun Companion.bifunctor(): EitherBifunctor = bifunctor_singleton
