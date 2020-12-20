package arrow.core.extensions.list.monadFilter

import arrow.core.ForListK
import arrow.core.Option
import arrow.core.extensions.ListKMonadFilter
import arrow.typeclasses.MonadFilterSyntax
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
    arrow.core.extensions.list.monadFilter.List.monadFilter().run {
  arrow.core.ListK(this@filterMap).filterMap<A, B>(arg1) as kotlin.collections.List<B>
}

@JvmName("bindingFilter")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <B> bindingFilter(arg0: suspend MonadFilterSyntax<ForListK>.() -> B): List<B> =
    arrow.core.extensions.list.monadFilter.List
   .monadFilter()
   .bindingFilter<B>(arg0) as kotlin.collections.List<B>

/**
 * cached extension
 */
@PublishedApi()
internal val monadFilter_singleton: ListKMonadFilter = object :
    arrow.core.extensions.ListKMonadFilter {}

object List {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  inline fun monadFilter(): ListKMonadFilter = monadFilter_singleton}
