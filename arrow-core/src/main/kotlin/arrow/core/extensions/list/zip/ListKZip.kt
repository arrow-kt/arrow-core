package arrow.core.extensions.list.zip

import arrow.core.Tuple2
import arrow.core.extensions.ListKZip
import kotlin.Function2
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.collections.List
import kotlin.jvm.JvmName

@JvmName("zip")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B> List<A>.zip(arg1: List<B>): List<Tuple2<A, B>> =
    arrow.core.extensions.list.zip.List.zip().run {
  arrow.core.ListK(this@zip).zip<A, B>(arrow.core.ListK(arg1)) as
    kotlin.collections.List<arrow.core.Tuple2<A, B>>
}

@JvmName("zipWith")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B, C> List<A>.zipWith(arg1: List<B>, arg2: Function2<A, B, C>): List<C> =
    arrow.core.extensions.list.zip.List.zip().run {
  arrow.core.ListK(this@zipWith).zipWith<A, B, C>(arrow.core.ListK(arg1), arg2) as
    kotlin.collections.List<C>
}

/**
 * cached extension
 */
@PublishedApi()
internal val zip_singleton: ListKZip = object : arrow.core.extensions.ListKZip {}

object List {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  inline fun zip(): ListKZip = zip_singleton}
