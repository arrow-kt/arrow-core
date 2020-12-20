package arrow.core.extensions.list.unzip

import arrow.Kind
import arrow.core.ForListK
import arrow.core.Tuple2
import arrow.core.extensions.ListKUnzip
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.collections.List
import kotlin.jvm.JvmName

@JvmName("unzip")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B> List<Tuple2<A, B>>.unzip(): Tuple2<Kind<ForListK, A>, Kind<ForListK, B>> =
    arrow.core.extensions.list.unzip.List.unzip().run {
  arrow.core.ListK(this@unzip).unzip<A, B>() as arrow.core.Tuple2<arrow.Kind<arrow.core.ForListK,
    A>, arrow.Kind<arrow.core.ForListK, B>>
}

@JvmName("unzipWith")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B, C> List<C>.unzipWith(arg1: Function1<C, Tuple2<A, B>>): Tuple2<Kind<ForListK, A>,
    Kind<ForListK, B>> = arrow.core.extensions.list.unzip.List.unzip().run {
  arrow.core.ListK(this@unzipWith).unzipWith<A, B, C>(arg1) as
    arrow.core.Tuple2<arrow.Kind<arrow.core.ForListK, A>, arrow.Kind<arrow.core.ForListK, B>>
}

/**
 * cached extension
 */
@PublishedApi()
internal val unzip_singleton: ListKUnzip = object : arrow.core.extensions.ListKUnzip {}

object List {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  inline fun unzip(): ListKUnzip = unzip_singleton}
