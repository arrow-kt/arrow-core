package arrow.core.extensions.list.align

import arrow.core.extensions.ListKAlign
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.collections.List
import kotlin.jvm.JvmName

@JvmName("empty")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A> empty(): List<A> = arrow.core.extensions.list.align.List
   .align()
   .empty<A>() as kotlin.collections.List<A>

/**
 * cached extension
 */
@PublishedApi()
internal val align_singleton: ListKAlign = object : arrow.core.extensions.ListKAlign {}

object List {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  inline fun align(): ListKAlign = align_singleton}
