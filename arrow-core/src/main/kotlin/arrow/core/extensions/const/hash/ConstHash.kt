package arrow.core.extensions.const.hash

import arrow.core.Const
import arrow.core.Const.Companion
import arrow.core.extensions.ConstHash
import arrow.typeclasses.Hash
import kotlin.Deprecated
import kotlin.Int
import kotlin.Suppress
import kotlin.jvm.JvmName

@JvmName("hash")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "hash(HA)",
  "arrow.core.hash"
  ),
  DeprecationLevel.WARNING
)
fun <A, T> Const<A, T>.hash(HA: Hash<A>): Int = arrow.core.Const.hash<A, T>(HA).run {
  this@hash.hash() as kotlin.Int
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "Hash typeclass is deprecated. Use concrete methods on Const",
  level = DeprecationLevel.WARNING
)
inline fun <A, T> Companion.hash(HA: Hash<A>): ConstHash<A, T> = object :
    arrow.core.extensions.ConstHash<A, T> { override fun HA(): arrow.typeclasses.Hash<A> = HA }
