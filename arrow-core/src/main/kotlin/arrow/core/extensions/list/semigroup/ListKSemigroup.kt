package arrow.core.extensions.list.semigroup

import arrow.core.extensions.ListKSemigroup
import kotlin.Any
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.collections.List
import kotlin.jvm.JvmName

@JvmName("plus")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("this.plus(arg1)"))
operator fun <A> List<A>.plus(arg1: List<A>): List<A> =
    arrow.core.extensions.list.semigroup.List.semigroup<A>().run {
  arrow.core.ListK(this@plus).plus(arrow.core.ListK(arg1)) as kotlin.collections.List<A>
}

@JvmName("maybeCombine")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("(arg1?.plus(this) ?: emptyList<A>())"))
fun <A> List<A>.maybeCombine(arg1: List<A>): List<A> =
    arrow.core.extensions.list.semigroup.List.semigroup<A>().run {
  arrow.core.ListK(this@maybeCombine).maybeCombine(arrow.core.ListK(arg1)) as
    kotlin.collections.List<A>
}

/**
 * cached extension
 */
@PublishedApi()
internal val semigroup_singleton: ListKSemigroup<Any?> = object : ListKSemigroup<Any?> {}

object List {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated("@extension projected functions are deprecated", ReplaceWith("listMonoid<A>()", "arrow.core.listMonoid"))
  inline fun <A> semigroup(): ListKSemigroup<A> = semigroup_singleton as
      arrow.core.extensions.ListKSemigroup<A>}
