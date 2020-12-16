package arrow.core.extensions.either.monadError

import arrow.Kind
import arrow.core.Either
import arrow.core.Either.Companion
import arrow.core.ForEither
import arrow.core.extensions.EitherMonadError
import kotlin.Any
import kotlin.Boolean
import kotlin.Function0
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val monadError_singleton: EitherMonadError<Any?> = object : EitherMonadError<Any?> {}

@JvmName("ensure")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A> Kind<Kind<ForEither, L>, A>.ensure(arg1: Function0<L>, arg2: Function1<A, Boolean>):
  Either<L, A> = arrow.core.Either.monadError<L>().run {
  this@ensure.ensure<A>(arg1, arg2) as arrow.core.Either<L, A>
}

@JvmName("redeemWith")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B> Kind<Kind<ForEither, L>, A>.redeemWith(
  arg1: Function1<L, Kind<Kind<ForEither, L>,
    B>>,
  arg2: Function1<A, Kind<Kind<ForEither, L>, B>>
): Either<L, B> =
  arrow.core.Either.monadError<L>().run {
    this@redeemWith.redeemWith<A, B>(arg1, arg2) as arrow.core.Either<L, B>
  }

@JvmName("rethrow")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A> Kind<Kind<ForEither, L>, Either<L, A>>.rethrow(): Either<L, A> =
  arrow.core.Either.monadError<L>().run {
    this@rethrow.rethrow<A>() as arrow.core.Either<L, A>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <L> Companion.monadError(): EitherMonadError<L> = monadError_singleton as
  arrow.core.extensions.EitherMonadError<L>
