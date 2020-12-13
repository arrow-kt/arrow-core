package arrow.core.extensions.validated.eq

import arrow.core.Validated
import arrow.core.Validated.Companion
import arrow.core.extensions.ValidatedEq
import arrow.typeclasses.Eq
import kotlin.Boolean
import kotlin.Suppress
import kotlin.jvm.JvmName

@JvmName("neqv")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("neqv(EQL, EQR, arg1)", "arrow.core.neqv"))
fun <L, R> Validated<L, R>.neqv(
  EQL: Eq<L>,
  EQR: Eq<R>,
  arg1: Validated<L, R>
): Boolean = arrow.core.Validated.eq<L, R>(EQL, EQR).run {
  this@neqv.neqv(arg1) as kotlin.Boolean
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("@extension projected functions are deprecated", ReplaceWith("eq(EQL, EQR)", "arrow.core.eq"))
inline fun <L, R> Companion.eq(EQL: Eq<L>, EQR: Eq<R>): ValidatedEq<L, R> = object :
  arrow.core.extensions.ValidatedEq<L, R> {
  override fun EQL(): arrow.typeclasses.Eq<L> = EQL

  override fun EQR(): arrow.typeclasses.Eq<R> = EQR
}
