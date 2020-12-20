package arrow.core.extensions.list.monadPlus

import arrow.core.extensions.ListKMonadPlus
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.collections.List
import kotlin.jvm.JvmName

@JvmName("zeroM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A> zeroM(): List<A> = arrow.core.extensions.list.monadPlus.List
   .monadPlus()
   .zeroM<A>() as kotlin.collections.List<A>

@JvmName("plusM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A> List<A>.plusM(arg1: List<A>): List<A> =
    arrow.core.extensions.list.monadPlus.List.monadPlus().run {
  arrow.core.ListK(this@plusM).plusM<A>(arrow.core.ListK(arg1)) as kotlin.collections.List<A>
}

/**
 * cached extension
 */
@PublishedApi()
internal val monadPlus_singleton: ListKMonadPlus = object : arrow.core.extensions.ListKMonadPlus {}

object List {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  inline fun monadPlus(): ListKMonadPlus = monadPlus_singleton}
