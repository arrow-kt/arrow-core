package arrow.core.extensions.mapk.functorFilter

import arrow.Kind
import arrow.core.ForMapK
import arrow.core.MapK
import arrow.core.MapK.Companion
import arrow.core.Option
import arrow.core.extensions.MapKFunctorFilter
import java.lang.Class
import kotlin.Any
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val functorFilter_singleton: MapKFunctorFilter<Any?> = object : MapKFunctorFilter<Any?> {}

@JvmName("filterMap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "filterMap(arg1)",
  "arrow.core.filterMap"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B> Kind<Kind<ForMapK, K>, A>.filterMap(arg1: Function1<A, Option<B>>): MapK<K, B> =
    arrow.core.MapK.functorFilter<K>().run {
  this@filterMap.filterMap<A, B>(arg1) as arrow.core.MapK<K, B>
}

@JvmName("flattenOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "flattenOption()",
  "arrow.core.flattenOption"
  ),
  DeprecationLevel.WARNING
)
fun <K, A> Kind<Kind<ForMapK, K>, Option<A>>.flattenOption(): MapK<K, A> =
    arrow.core.MapK.functorFilter<K>().run {
  this@flattenOption.flattenOption<A>() as arrow.core.MapK<K, A>
}

@JvmName("filter")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "filter(arg1)",
  "arrow.core.filter"
  ),
  DeprecationLevel.WARNING
)
fun <K, A> Kind<Kind<ForMapK, K>, A>.filter(arg1: Function1<A, Boolean>): MapK<K, A> =
    arrow.core.MapK.functorFilter<K>().run {
  this@filter.filter<A>(arg1) as arrow.core.MapK<K, A>
}

@JvmName("filterIsInstance")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "filterIsInstance(arg1)",
  "arrow.core.filterIsInstance"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B> Kind<Kind<ForMapK, K>, A>.filterIsInstance(arg1: Class<B>): MapK<K, B> =
    arrow.core.MapK.functorFilter<K>().run {
  this@filterIsInstance.filterIsInstance<A, B>(arg1) as arrow.core.MapK<K, B>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <K> Companion.functorFilter(): MapKFunctorFilter<K> = functorFilter_singleton as
    arrow.core.extensions.MapKFunctorFilter<K>