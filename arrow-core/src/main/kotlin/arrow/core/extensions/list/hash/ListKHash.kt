package arrow.core.extensions.list.hash

import arrow.core.extensions.ListKHash
import arrow.typeclasses.Hash
import arrow.core.hash as _hash
import arrow.core.hashWithSalt as _hashWithSalt
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
@Deprecated("@extension projected functions are deprecated", ReplaceWith("hash(HA)", "arrow.core.hash"))
fun <A> List<A>.hash(HA: Hash<A>): Int =
  _hash(HA)

@JvmName("hashWithSalt")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("hashWithSalt(HA, arg1)", "arrow.core.hashWithSalt"))
fun <A> List<A>.hashWithSalt(HA: Hash<A>, arg1: Int): Int =
  _hashWithSalt(HA, arg1)

object List {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  @Deprecated("@extension projected functions are deprecated", ReplaceWith("listHash(HA)", "arrow.core.listHash"))
  inline fun <A> hash(HA: Hash<A>): ListKHash<A> = object : arrow.core.extensions.ListKHash<A> {
      override fun HA(): arrow.typeclasses.Hash<A> = HA }}
