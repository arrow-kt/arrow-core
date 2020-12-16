package arrow.core.extensions.either.bifoldable

import arrow.Kind
import arrow.core.Either.Companion
import arrow.core.Eval
import arrow.core.ForEither
import arrow.core.extensions.EitherBifoldable
import arrow.typeclasses.Monoid
import kotlin.Function1
import kotlin.Function2
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val bifoldable_singleton: EitherBifoldable = object :
  arrow.core.extensions.EitherBifoldable {}

@JvmName("bifoldLeft")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B, C> Kind<Kind<ForEither, A>, B>.bifoldLeft(
  arg1: C,
  arg2: Function2<C, A, C>,
  arg3: Function2<C, B, C>
): C = arrow.core.Either.bifoldable().run {
  this@bifoldLeft.bifoldLeft<A, B, C>(arg1, arg2, arg3) as C
}

@JvmName("bifoldRight")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B, C> Kind<Kind<ForEither, A>, B>.bifoldRight(
  arg1: Eval<C>,
  arg2: Function2<A, Eval<C>, Eval<C>>,
  arg3: Function2<B, Eval<C>, Eval<C>>
): Eval<C> = arrow.core.Either.bifoldable().run {
  this@bifoldRight.bifoldRight<A, B, C>(arg1, arg2, arg3) as arrow.core.Eval<C>
}

@JvmName("bifoldMap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B, C> Kind<Kind<ForEither, A>, B>.bifoldMap(
  arg1: Monoid<C>,
  arg2: Function1<A, C>,
  arg3: Function1<B, C>
): C = arrow.core.Either.bifoldable().run {
  this@bifoldMap.bifoldMap<A, B, C>(arg1, arg2, arg3) as C
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun Companion.bifoldable(): EitherBifoldable = bifoldable_singleton
