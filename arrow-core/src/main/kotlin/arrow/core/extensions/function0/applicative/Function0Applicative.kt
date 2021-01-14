package arrow.core.extensions.function0.applicative

import arrow.Kind
import arrow.core.ForFunction0
import arrow.core.Function0
import arrow.core.Function0.Companion
import arrow.core.extensions.Function0Applicative
import arrow.typeclasses.Monoid
import kotlin.Deprecated
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
internal val applicative_singleton: Function0Applicative = object :
    arrow.core.extensions.Function0Applicative {}

@JvmName("just1")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "just()",
  "arrow.core.just"
  ),
  DeprecationLevel.WARNING
)
fun <A> A.just(): Function0<A> = arrow.core.Function0.applicative().run {
  this@just.just<A>() as arrow.core.Function0<A>
}

@JvmName("unit")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "unit()",
  "arrow.core.Function0.unit"
  ),
  DeprecationLevel.WARNING
)
fun unit(): Function0<Unit> = arrow.core.Function0
   .applicative()
   .unit() as arrow.core.Function0<kotlin.Unit>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "map(arg1)",
  "arrow.core.map"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForFunction0, A>.map(arg1: Function1<A, B>): Function0<B> =
    arrow.core.Function0.applicative().run {
  this@map.map<A, B>(arg1) as arrow.core.Function0<B>
}

@JvmName("replicate")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "replicate(arg1)",
  "arrow.core.replicate"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForFunction0, A>.replicate(arg1: Int): Function0<List<A>> =
    arrow.core.Function0.applicative().run {
  this@replicate.replicate<A>(arg1) as arrow.core.Function0<kotlin.collections.List<A>>
}

@JvmName("replicate")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "replicate(arg1, arg2)",
  "arrow.core.replicate"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForFunction0, A>.replicate(arg1: Int, arg2: Monoid<A>): Function0<A> =
    arrow.core.Function0.applicative().run {
  this@replicate.replicate<A>(arg1, arg2) as arrow.core.Function0<A>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun Companion.applicative(): Function0Applicative = applicative_singleton
