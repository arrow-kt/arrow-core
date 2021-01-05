package arrow.core.extensions.mapk.semialign

import arrow.Kind
import arrow.core.ForMapK
import arrow.core.Ior
import arrow.core.MapK
import arrow.core.MapK.Companion
import arrow.core.Option
import arrow.core.Tuple2
import arrow.core.extensions.MapKSemialign
import arrow.typeclasses.Semigroup
import kotlin.Any
import kotlin.Deprecated
import kotlin.Function1
import kotlin.Function2
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val semialign_singleton: MapKSemialign<Any?> = object : MapKSemialign<Any?> {}

@JvmName("align")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "align(arg0, arg1)",
  "arrow.core.MapK.align"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B> align(arg0: Kind<Kind<ForMapK, K>, A>, arg1: Kind<Kind<ForMapK, K>, B>): MapK<K,
    Ior<A, B>> = arrow.core.MapK
   .semialign<K>()
   .align<A, B>(arg0, arg1) as arrow.core.MapK<K, arrow.core.Ior<A, B>>

@JvmName("alignWith")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "alignWith(arg0, arg1, arg2)",
  "arrow.core.MapK.alignWith"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C> alignWith(
  arg0: Kind<Kind<ForMapK, K>, A>,
  arg1: Kind<Kind<ForMapK, K>, B>,
  arg2: Function1<Ior<A, B>, C>
): MapK<K, C> = arrow.core.MapK
   .semialign<K>()
   .alignWith<A, B, C>(arg0, arg1, arg2) as arrow.core.MapK<K, C>

@JvmName("salign")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "salign(arg1, arg2)",
  "arrow.core.salign"
  ),
  DeprecationLevel.WARNING
)
fun <K, A> Kind<Kind<ForMapK, K>, A>.salign(arg1: Semigroup<A>, arg2: Kind<Kind<ForMapK, K>, A>):
    MapK<K, A> = arrow.core.MapK.semialign<K>().run {
  this@salign.salign<A>(arg1, arg2) as arrow.core.MapK<K, A>
}

@JvmName("padZip")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "padZip(arg1)",
  "arrow.core.padZip"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B> Kind<Kind<ForMapK, K>, A>.padZip(arg1: Kind<Kind<ForMapK, K>, B>): MapK<K,
    Tuple2<Option<A>, Option<B>>> = arrow.core.MapK.semialign<K>().run {
  this@padZip.padZip<A, B>(arg1) as arrow.core.MapK<K, arrow.core.Tuple2<arrow.core.Option<A>,
    arrow.core.Option<B>>>
}

@JvmName("padZipWith")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "padZipWith(arg1, arg2)",
  "arrow.core.padZipWith"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C> Kind<Kind<ForMapK, K>, A>.padZipWith(arg1: Kind<Kind<ForMapK, K>, B>,
    arg2: Function2<Option<A>, Option<B>, C>): MapK<K, C> = arrow.core.MapK.semialign<K>().run {
  this@padZipWith.padZipWith<A, B, C>(arg1, arg2) as arrow.core.MapK<K, C>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <K> Companion.semialign(): MapKSemialign<K> = semialign_singleton as
    arrow.core.extensions.MapKSemialign<K>