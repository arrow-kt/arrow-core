package arrow.core.extensions.function0.monoid

import arrow.core.Function0
import arrow.core.Function0.Companion
import arrow.core.extensions.Function0Monoid
import arrow.typeclasses.Monoid
import kotlin.Deprecated
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
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "combineAll(MA)",
  "arrow.core.combineAll"
  ),
  DeprecationLevel.WARNING
)
fun <A> Collection<Function0<A>>.combineAll(MA: Monoid<A>): Function0<A> =
    arrow.core.Function0.monoid<A>(MA).run {
  this@combineAll.combineAll() as arrow.core.Function0<A>
}

@JvmName("combineAll")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "combineAll(MA, arg0)",
  "arrow.core.Function0.combineAll"
  ),
  DeprecationLevel.WARNING
)
fun <A> combineAll(MA: Monoid<A>, arg0: List<Function0<A>>): Function0<A> = arrow.core.Function0
   .monoid<A>(MA)
   .combineAll(arg0) as arrow.core.Function0<A>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <A> Companion.monoid(MA: Monoid<A>): Function0Monoid<A> = object :
    arrow.core.extensions.Function0Monoid<A> { override fun MA(): arrow.typeclasses.Monoid<A> = MA }
