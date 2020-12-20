package arrow.core.extensions.list.hash

import arrow.core.extensions.ListKHash
import arrow.typeclasses.Hash
import kotlin.Int
import kotlin.Suppress
import kotlin.collections.List
import kotlin.jvm.JvmName

@JvmName("hash")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A> List<A>.hash(HA: Hash<A>): Int = arrow.core.extensions.list.hash.List.hash<A>(HA).run {
  arrow.core.ListK(this@hash).hash() as kotlin.Int
}

@JvmName("hashWithSalt")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A> List<A>.hashWithSalt(HA: Hash<A>, arg1: Int): Int =
    arrow.core.extensions.list.hash.List.hash<A>(HA).run {
  arrow.core.ListK(this@hashWithSalt).hashWithSalt(arg1) as kotlin.Int
}

object List {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  inline fun <A> hash(HA: Hash<A>): ListKHash<A> = object : arrow.core.extensions.ListKHash<A> {
      override fun HA(): arrow.typeclasses.Hash<A> = HA }}
