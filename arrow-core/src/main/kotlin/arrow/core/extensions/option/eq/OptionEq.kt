package arrow.core.extensions.option.eq

import arrow.core.Option
import arrow.core.Option.Companion
import arrow.core.extensions.OptionEq
import arrow.typeclasses.Eq

@JvmName("neqv")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "neqv(EQ, arg1)",
    "arrow.core.neqv"
  ),
  DeprecationLevel.WARNING
)
fun <A> Option<A>.neqv(EQ: Eq<A>, arg1: Option<A>): Boolean = arrow.core.Option.eq<A>(EQ).run {
  this@neqv.neqv(arg1) as kotlin.Boolean
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "@extension projected functions are deprecated",
  ReplaceWith(
    "Eq.option<A>(EQ)",
    "arrow.core.option", "arrow.typeclasses.Eq"
  ),
  DeprecationLevel.WARNING
)
inline fun <A> Companion.eq(EQ: Eq<A>): OptionEq<A> = object : arrow.core.extensions.OptionEq<A> {
    override fun EQ(): arrow.typeclasses.Eq<A> = EQ }
