package arrow.core.extensions.function0.comonad

import arrow.Kind
import arrow.core.ForFunction0
import arrow.core.Function0
import arrow.core.Function0.Companion
import arrow.core.extensions.Function0Comonad
import kotlin.Deprecated
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val comonad_singleton: Function0Comonad = object : arrow.core.extensions.Function0Comonad
    {}

@JvmName("coflatMap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "coflatMap(arg1)",
  "arrow.core.coflatMap"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForFunction0, A>.coflatMap(arg1: Function1<Kind<ForFunction0, A>, B>): Function0<B> =
  arrow.core.Function0.comonad().run {
    this@coflatMap.coflatMap<A, B>(arg1) as arrow.core.Function0<B>
  }

@JvmName("extract")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "extract()",
  "arrow.core.extract"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForFunction0, A>.extract(): A = arrow.core.Function0.comonad().run {
  this@extract.extract<A>() as A
}

@JvmName("duplicate")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "duplicate()",
  "arrow.core.duplicate"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForFunction0, A>.duplicate(): Function0<Function0<A>> =
    arrow.core.Function0.comonad().run {
  this@duplicate.duplicate<A>() as arrow.core.Function0<arrow.core.Function0<A>>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun Companion.comonad(): Function0Comonad = comonad_singleton
