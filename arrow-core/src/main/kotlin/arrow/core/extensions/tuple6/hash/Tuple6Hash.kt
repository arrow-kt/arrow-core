package arrow.core.extensions.tuple6.hash

import arrow.core.Tuple6
import arrow.core.Tuple6.Companion
import arrow.core.extensions.Tuple6Hash
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
  "hash(HA, HB, HC, HD, HE, HF)",
  "arrow.core.hash"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, F> Tuple6<A, B, C, D, E, F>.hash(
  HA: Hash<A>,
  HB: Hash<B>,
  HC: Hash<C>,
  HD: Hash<D>,
  HE: Hash<E>,
  HF: Hash<F>
): Int = arrow.core.Tuple6.hash<A, B, C, D, E, F>(HA, HB, HC, HD, HE, HF).run {
  this@hash.hash() as kotlin.Int
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <A, B, C, D, E, F> Companion.hash(
  HA: Hash<A>,
  HB: Hash<B>,
  HC: Hash<C>,
  HD: Hash<D>,
  HE: Hash<E>,
  HF: Hash<F>
): Tuple6Hash<A, B, C, D, E, F> = object : arrow.core.extensions.Tuple6Hash<A, B, C, D, E, F> {
    override fun HA(): arrow.typeclasses.Hash<A> = HA

  override fun HB(): arrow.typeclasses.Hash<B> = HB

  override fun HC(): arrow.typeclasses.Hash<C> = HC

  override fun HD(): arrow.typeclasses.Hash<D> = HD

  override fun HE(): arrow.typeclasses.Hash<E> = HE

  override fun HF(): arrow.typeclasses.Hash<F> = HF }