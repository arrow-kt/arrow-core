package arrow.core.extensions.setk.hash

import arrow.core.SetK
import arrow.core.SetK.Companion
import arrow.core.extensions.SetKHash
import arrow.typeclasses.Hash
import arrow.typeclasses.HashDeprecation
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
@Deprecated(HashDeprecation, ReplaceWith("hashCode()"))
fun <A> SetK<A>.hash(HA: Hash<A>): Int = arrow.core.SetK.hash<A>(HA).run {
  this@hash.hash() as kotlin.Int
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(HashDeprecation)
inline fun <A> Companion.hash(HA: Hash<A>): SetKHash<A> = object : arrow.core.extensions.SetKHash<A> {
  override fun HA(): arrow.typeclasses.Hash<A> = HA
}
