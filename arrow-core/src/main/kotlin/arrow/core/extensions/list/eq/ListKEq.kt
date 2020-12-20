package arrow.core.extensions.list.eq

import arrow.core.extensions.ListKEq
import arrow.typeclasses.Eq
import kotlin.Boolean
import kotlin.Suppress
import kotlin.collections.List
import kotlin.jvm.JvmName

@JvmName("eqv")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A> List<A>.eqv(EQ: Eq<A>, arg1: List<A>): Boolean =
    arrow.core.extensions.list.eq.List.eq<A>(EQ).run {
  arrow.core.ListK(this@eqv).eqv(arrow.core.ListK(arg1)) as kotlin.Boolean
}

@JvmName("neqv")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A> List<A>.neqv(EQ: Eq<A>, arg1: List<A>): Boolean =
    arrow.core.extensions.list.eq.List.eq<A>(EQ).run {
  arrow.core.ListK(this@neqv).neqv(arrow.core.ListK(arg1)) as kotlin.Boolean
}

object List {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  inline fun <A> eq(EQ: Eq<A>): ListKEq<A> = object : arrow.core.extensions.ListKEq<A> { override
      fun EQ(): arrow.typeclasses.Eq<A> = EQ }}
