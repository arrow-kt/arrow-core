package arrow.core.extensions.either.applicative

import arrow.Kind
import arrow.core.Either
import arrow.core.Either.Companion
import arrow.core.ForEither
import arrow.core.extensions.EitherApplicative
import arrow.typeclasses.Monoid
import kotlin.Any
import kotlin.Function1
import kotlin.Int
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val applicative_singleton: EitherApplicative<Any?> = object : EitherApplicative<Any?> {}

@JvmName("just1")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A> A.just(): Either<L, A> = arrow.core.Either.applicative<L>().run {
  this@just.just<A>() as arrow.core.Either<L, A>
}

@JvmName("unit")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L> unit(): Either<L, Unit> = arrow.core.Either
  .applicative<L>()
  .unit() as arrow.core.Either<L, kotlin.Unit>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B> Kind<Kind<ForEither, L>, A>.map(arg1: Function1<A, B>): Either<L, B> =
  arrow.core.Either.applicative<L>().run {
    this@map.map<A, B>(arg1) as arrow.core.Either<L, B>
  }

@JvmName("replicate")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A> Kind<Kind<ForEither, L>, A>.replicate(arg1: Int): Either<L, List<A>> =
  arrow.core.Either.applicative<L>().run {
    this@replicate.replicate<A>(arg1) as arrow.core.Either<L, kotlin.collections.List<A>>
  }

@JvmName("replicate")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A> Kind<Kind<ForEither, L>, A>.replicate(arg1: Int, arg2: Monoid<A>): Either<L, A> =
  arrow.core.Either.applicative<L>().run {
    this@replicate.replicate<A>(arg1, arg2) as arrow.core.Either<L, A>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <L> Companion.applicative(): EitherApplicative<L> = applicative_singleton as
  arrow.core.extensions.EitherApplicative<L>
