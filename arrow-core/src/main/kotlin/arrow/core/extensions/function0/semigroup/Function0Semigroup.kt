package arrow.core.extensions.function0.semigroup

import arrow.core.Function0
import arrow.core.Function0.Companion
import arrow.core.extensions.Function0Semigroup
import arrow.typeclasses.Semigroup
import kotlin.Deprecated
import kotlin.Suppress
import kotlin.jvm.JvmName

@JvmName("plus")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "plus(SA, arg1)",
  "arrow.core.plus"
  ),
  DeprecationLevel.WARNING
)
fun <A> Function0<A>.plus(SA: Semigroup<A>, arg1: Function0<A>): Function0<A> =
    arrow.core.Function0.semigroup<A>(SA).run {
  this@plus.plus(arg1) as arrow.core.Function0<A>
}

@JvmName("maybeCombine")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "maybeCombine(SA, arg1)",
  "arrow.core.maybeCombine"
  ),
  DeprecationLevel.WARNING
)
fun <A> Function0<A>.maybeCombine(SA: Semigroup<A>, arg1: Function0<A>): Function0<A> =
    arrow.core.Function0.semigroup<A>(SA).run {
  this@maybeCombine.maybeCombine(arg1) as arrow.core.Function0<A>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <A> Companion.semigroup(SA: Semigroup<A>): Function0Semigroup<A> = object :
    arrow.core.extensions.Function0Semigroup<A> { override fun SA(): arrow.typeclasses.Semigroup<A>
    = SA }