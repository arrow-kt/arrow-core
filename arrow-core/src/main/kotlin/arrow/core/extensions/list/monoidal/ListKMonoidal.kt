package arrow.core.extensions.list.monoidal

import arrow.core.extensions.ListKMonoidal
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.collections.List
import kotlin.jvm.JvmName

@JvmName("identity")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A> identity(): List<A> = arrow.core.extensions.list.monoidal.List
   .monoidal()
   .identity<A>() as kotlin.collections.List<A>

/**
 * cached extension
 */
@PublishedApi()
internal val monoidal_singleton: ListKMonoidal = object : arrow.core.extensions.ListKMonoidal {}

object List {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  inline fun monoidal(): ListKMonoidal = monoidal_singleton}
