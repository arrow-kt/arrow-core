package arrow.core.extensions.list.show

import arrow.core.extensions.ListKShow
import arrow.core.show as _show
import arrow.typeclasses.Show
import kotlin.String
import kotlin.Suppress
import kotlin.collections.List
import kotlin.jvm.JvmName

@JvmName("show")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("show(arg1)", "arrow.core.show"))
fun <A> List<A>.show(SA: Show<A>): String =
  _show(SA)

object List {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated("@extension projected functions are deprecated", ReplaceWith("listShow(arg1)", "arrow.core.listShow"))
  inline fun <A> show(SA: Show<A>): ListKShow<A> = object : arrow.core.extensions.ListKShow<A> {
      override fun SA(): arrow.typeclasses.Show<A> = SA }}
