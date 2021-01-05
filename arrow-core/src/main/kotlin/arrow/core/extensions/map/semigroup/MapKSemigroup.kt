package arrow.core.extensions.map.semigroup

import arrow.core.extensions.MapKSemigroup
import arrow.typeclasses.Semigroup
import kotlin.Deprecated
import kotlin.Suppress
import kotlin.collections.Map
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
  "plus(SG, arg1)",
  "arrow.core.plus"
  ),
  DeprecationLevel.WARNING
)
fun <K, A> Map<K, A>.plus(SG: Semigroup<A>, arg1: Map<K, A>): Map<K, A> =
    arrow.core.extensions.map.semigroup.Map.semigroup<K, A>(SG).run {
  arrow.core.MapK(this@plus).plus(arrow.core.MapK(arg1)) as kotlin.collections.Map<K, A>
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
  "maybeCombine(SG, arg1)",
  "arrow.core.maybeCombine"
  ),
  DeprecationLevel.WARNING
)
fun <K, A> Map<K, A>.maybeCombine(SG: Semigroup<A>, arg1: Map<K, A>): Map<K, A> =
    arrow.core.extensions.map.semigroup.Map.semigroup<K, A>(SG).run {
  arrow.core.MapK(this@maybeCombine).maybeCombine(arrow.core.MapK(arg1)) as
    kotlin.collections.Map<K, A>
}

object Map {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  inline fun <K, A> semigroup(SG: Semigroup<A>): MapKSemigroup<K, A> = object :
      arrow.core.extensions.MapKSemigroup<K, A> { override fun SG(): arrow.typeclasses.Semigroup<A>
      = SG }}
