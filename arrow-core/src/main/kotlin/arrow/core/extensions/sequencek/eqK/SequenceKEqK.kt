package arrow.core.extensions.sequencek.eqK

import arrow.Kind
import arrow.core.ForSequenceK
import arrow.core.SequenceK.Companion
import arrow.core.extensions.SequenceKEqK
import arrow.typeclasses.Eq
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val eqK_singleton: SequenceKEqK = object : arrow.core.extensions.SequenceKEqK {}

@JvmName("eqK")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "eqK(arg1, arg2)",
  "arrow.core.eqK"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForSequenceK, A>.eqK(arg1: Kind<ForSequenceK, A>, arg2: Eq<A>): Boolean =
    arrow.core.SequenceK.eqK().run {
  this@eqK.eqK<A>(arg1, arg2) as kotlin.Boolean
}

@JvmName("liftEq")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "liftEq(arg0)",
  "arrow.core.SequenceK.liftEq"
  ),
  DeprecationLevel.WARNING
)
fun <A> liftEq(arg0: Eq<A>): Eq<Kind<ForSequenceK, A>> = arrow.core.SequenceK
   .eqK()
   .liftEq<A>(arg0) as arrow.typeclasses.Eq<arrow.Kind<arrow.core.ForSequenceK, A>>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun Companion.eqK(): SequenceKEqK = eqK_singleton