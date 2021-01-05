package arrow.core.extensions.map.functorFilter

import arrow.core.Option
import arrow.core.extensions.MapKFunctorFilter
import java.lang.Class
import kotlin.Any
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.collections.Map
import kotlin.jvm.JvmName

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
fun <K, A, B> Map<K, A>.filterMap(arg1: Function1<A, Option<B>>): Map<K, B> =
    arrow.core.extensions.map.functorFilter.Map.functorFilter<K>().run {
  arrow.core.MapK(this@filterMap).filterMap<A, B>(arg1) as kotlin.collections.Map<K, B>
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
fun <K, A> Map<K, Option<A>>.flattenOption(): Map<K, A> =
    arrow.core.extensions.map.functorFilter.Map.functorFilter<K>().run {
  arrow.core.MapK(this@flattenOption).flattenOption<A>() as kotlin.collections.Map<K, A>
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
fun <K, A> Map<K, A>.filter(arg1: Function1<A, Boolean>): Map<K, A> =
    arrow.core.extensions.map.functorFilter.Map.functorFilter<K>().run {
  arrow.core.MapK(this@filter).filter<A>(arg1) as kotlin.collections.Map<K, A>
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
fun <K, A, B> Map<K, A>.filterIsInstance(arg1: Class<B>): Map<K, B> =
    arrow.core.extensions.map.functorFilter.Map.functorFilter<K>().run {
  arrow.core.MapK(this@filterIsInstance).filterIsInstance<A, B>(arg1) as kotlin.collections.Map<K,
    B>
}

/**
 * cached extension
 */
@PublishedApi()
internal val functorFilter_singleton: MapKFunctorFilter<Any?> = object : MapKFunctorFilter<Any?> {}

object Map {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  inline fun <K> functorFilter(): MapKFunctorFilter<K> = functorFilter_singleton as
      arrow.core.extensions.MapKFunctorFilter<K>}
