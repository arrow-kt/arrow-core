package arrow.core.extensions.list.functorFilter

import arrow.core.Option
import arrow.core.extensions.ListKFunctorFilter
import java.lang.Class
import kotlin.Boolean
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.collections.List
import kotlin.jvm.JvmName

@JvmName("filterMap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B> List<A>.filterMap(arg1: Function1<A, Option<B>>): List<B> =
    arrow.core.extensions.list.functorFilter.List.functorFilter().run {
  arrow.core.ListK(this@filterMap).filterMap<A, B>(arg1) as kotlin.collections.List<B>
}

@JvmName("flattenOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A> List<Option<A>>.flattenOption(): List<A> =
    arrow.core.extensions.list.functorFilter.List.functorFilter().run {
  arrow.core.ListK(this@flattenOption).flattenOption<A>() as kotlin.collections.List<A>
}

@JvmName("filter")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A> List<A>.filter(arg1: Function1<A, Boolean>): List<A> =
    arrow.core.extensions.list.functorFilter.List.functorFilter().run {
  arrow.core.ListK(this@filter).filter<A>(arg1) as kotlin.collections.List<A>
}

@JvmName("filterIsInstance")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B> List<A>.filterIsInstance(arg1: Class<B>): List<B> =
    arrow.core.extensions.list.functorFilter.List.functorFilter().run {
  arrow.core.ListK(this@filterIsInstance).filterIsInstance<A, B>(arg1) as kotlin.collections.List<B>
}

/**
 * cached extension
 */
@PublishedApi()
internal val functorFilter_singleton: ListKFunctorFilter = object :
    arrow.core.extensions.ListKFunctorFilter {}

object List {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  inline fun functorFilter(): ListKFunctorFilter = functorFilter_singleton}
