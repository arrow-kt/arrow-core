package arrow.core.extensions.either.applicativeError

import arrow.Kind
import arrow.core.Either
import arrow.core.Either.Companion
import arrow.core.ForEither
import arrow.core.ForOption
import arrow.core.extensions.EitherApplicativeError
import arrow.typeclasses.ApplicativeError
import kotlin.Any
import kotlin.Function0
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.Throwable
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val applicativeError_singleton: EitherApplicativeError<Any?> = object :
  EitherApplicativeError<Any?> {}

@JvmName("handleErrorWith")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A> Kind<Kind<ForEither, L>, A>.handleErrorWith(
  arg1: Function1<L, Kind<Kind<ForEither, L>,
    A>>
): Either<L, A> = arrow.core.Either.applicativeError<L>().run {
  this@handleErrorWith.handleErrorWith<A>(arg1) as arrow.core.Either<L, A>
}

@JvmName("raiseError1")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A> L.raiseError(): Either<L, A> = arrow.core.Either.applicativeError<L>().run {
  this@raiseError.raiseError<A>() as arrow.core.Either<L, A>
}

@JvmName("fromOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A> Kind<ForOption, A>.fromOption(arg1: Function0<L>): Either<L, A> =
  arrow.core.Either.applicativeError<L>().run {
    this@fromOption.fromOption<A>(arg1) as arrow.core.Either<L, A>
  }

@JvmName("fromEither")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, EE> Either<EE, A>.fromEither(arg1: Function1<EE, L>): Either<L, A> =
  arrow.core.Either.applicativeError<L>().run {
    this@fromEither.fromEither<A, EE>(arg1) as arrow.core.Either<L, A>
  }

@JvmName("handleError")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A> Kind<Kind<ForEither, L>, A>.handleError(arg1: Function1<L, A>): Either<L, A> =
  arrow.core.Either.applicativeError<L>().run {
    this@handleError.handleError<A>(arg1) as arrow.core.Either<L, A>
  }

@JvmName("redeem")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B> Kind<Kind<ForEither, L>, A>.redeem(arg1: Function1<L, B>, arg2: Function1<A, B>):
  Either<L, B> = arrow.core.Either.applicativeError<L>().run {
  this@redeem.redeem<A, B>(arg1, arg2) as arrow.core.Either<L, B>
}

@JvmName("attempt")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A> Kind<Kind<ForEither, L>, A>.attempt(): Either<L, Either<L, A>> =
  arrow.core.Either.applicativeError<L>().run {
    this@attempt.attempt<A>() as arrow.core.Either<L, arrow.core.Either<L, A>>
  }

@JvmName("catch")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A> catch(arg0: Function1<Throwable, L>, arg1: Function0<A>): Either<L, A> =
  arrow.core.Either
    .applicativeError<L>()
    .effectCatch(arg0, arg1) as arrow.core.Either<L, A>

@JvmName("catch")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A> ApplicativeError<Kind<ForEither, L>, Throwable>.catch(arg1: Function0<A>): Either<L, A> =
  arrow.core.Either.applicativeError<L>().run {
    effectCatch(arg1) as arrow.core.Either<L, A>
  }

@JvmName("effectCatch")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
suspend fun <L, A> effectCatch(arg0: Function1<Throwable, L>, arg1: suspend () -> A): Either<L, A> =
  arrow.core.Either
    .applicativeError<L>()
    .effectCatch<A>(arg0, arg1) as arrow.core.Either<L, A>

@JvmName("effectCatch")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
suspend fun <L, F, A> ApplicativeError<F, Throwable>.effectCatch(arg1: suspend () -> A): Kind<F, A> =
  arrow.core.Either.applicativeError<L>().run {
    this@effectCatch.effectCatch<F, A>(arg1) as arrow.Kind<F, A>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <L> Companion.applicativeError(): EitherApplicativeError<L> = applicativeError_singleton
  as arrow.core.extensions.EitherApplicativeError<L>
