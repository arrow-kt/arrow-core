package arrow.core.extensions.list.eq

import arrow.core.extensions.ListKEq
import arrow.typeclasses.Eq
import arrow.core.eqv as _eqv
import arrow.core.neqv as _neqv
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
@Deprecated("@extension projected functions are deprecated", ReplaceWith("neqv(EQL, EQR, arg1)", "arrow.core.neqv"))
fun <A> List<A>.eqv(EQ: Eq<A>, arg1: List<A>): Boolean =
  _eqv(EQ, arg1)

@JvmName("neqv")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("neqv(EQL, EQR, arg1)", "arrow.core.neqv"))
fun <A> List<A>.neqv(EQ: Eq<A>, arg1: List<A>): Boolean =
  _neqv(EQ, arg1)

object List {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated("@extension projected functions are deprecated", ReplaceWith("listEq(EQ)", "arrow.core.listEq"))
  inline fun <A> eq(EQ: Eq<A>): ListKEq<A> = object : arrow.core.extensions.ListKEq<A> { override
      fun EQ(): arrow.typeclasses.Eq<A> = EQ }}
