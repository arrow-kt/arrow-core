package arrow.core.extensions.list.monoid

import arrow.core.ListK
import arrow.core.extensions.ListKMonoid
import kotlin.Any
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.collections.Collection
import kotlin.collections.List
import kotlin.jvm.JvmName

@JvmName("combineAll")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A> Collection<ListK<A>>.combineAll(): List<A> =
    arrow.core.extensions.list.monoid.List.monoid<A>().run {
  this@combineAll.combineAll() as kotlin.collections.List<A>
}

@JvmName("combineAll")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A> combineAll(arg0: List<ListK<A>>): List<A> = arrow.core.extensions.list.monoid.List
   .monoid<A>()
   .combineAll(arrow.core.ListK(arg0)) as kotlin.collections.List<A>

/**
 * cached extension
 */
@PublishedApi()
internal val monoid_singleton: ListKMonoid<Any?> = object : ListKMonoid<Any?> {}

object List {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  inline fun <A> monoid(): ListKMonoid<A> = monoid_singleton as
      arrow.core.extensions.ListKMonoid<A>}
