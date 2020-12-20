package arrow.core.extensions.list.show

import arrow.core.extensions.ListKShow
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
fun <A> List<A>.show(SA: Show<A>): String = arrow.core.extensions.list.show.List.show<A>(SA).run {
  arrow.core.ListK(this@show).show() as kotlin.String
}

object List {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  inline fun <A> show(SA: Show<A>): ListKShow<A> = object : arrow.core.extensions.ListKShow<A> {
      override fun SA(): arrow.typeclasses.Show<A> = SA }}
