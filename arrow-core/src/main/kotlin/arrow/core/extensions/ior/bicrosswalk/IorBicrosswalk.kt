package arrow.core.extensions.ior.bicrosswalk

import arrow.Kind
import arrow.core.ForIor
import arrow.core.Ior.Companion
import arrow.core.extensions.IorBicrosswalk
import arrow.typeclasses.Align
import kotlin.Deprecated
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val bicrosswalk_singleton: IorBicrosswalk = object : arrow.core.extensions.IorBicrosswalk
    {}

@JvmName("bicrosswalk")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "bicrosswalk(arg0, arg1, arg2, arg3)",
  "arrow.core.Ior.bicrosswalk"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B, C, D> bicrosswalk(
  arg0: Align<F>,
  arg1: Kind<Kind<ForIor, A>, B>,
  arg2: Function1<A, Kind<F, C>>,
  arg3: Function1<B, Kind<F, D>>
): Kind<F, Kind<Kind<ForIor, C>, D>> = arrow.core.Ior
   .bicrosswalk()
   .bicrosswalk<F, A, B, C, D>(arg0, arg1, arg2, arg3) as arrow.Kind<F,
    arrow.Kind<arrow.Kind<arrow.core.ForIor, C>, D>>

@JvmName("bisequenceL")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "bisequenceL(arg0, arg1)",
  "arrow.core.Ior.bisequenceL"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B> bisequenceL(arg0: Align<F>, arg1: Kind<Kind<ForIor, Kind<F, A>>, Kind<F, B>>): Kind<F,
    Kind<Kind<ForIor, A>, B>> = arrow.core.Ior
   .bicrosswalk()
   .bisequenceL<F, A, B>(arg0, arg1) as arrow.Kind<F, arrow.Kind<arrow.Kind<arrow.core.ForIor, A>,
    B>>

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun Companion.bicrosswalk(): IorBicrosswalk = bicrosswalk_singleton