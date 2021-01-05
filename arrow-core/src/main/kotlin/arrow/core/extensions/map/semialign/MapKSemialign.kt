package arrow.core.extensions.map.semialign

import arrow.core.Ior
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
import kotlin.collections.Map
import kotlin.jvm.JvmName

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
  "arrow.core.extensions.map.semialign.Map.align"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B> align(arg0: Map<K, A>, arg1: Map<K, B>): Map<K, Ior<A, B>> =
    arrow.core.extensions.map.semialign.Map
   .semialign<K>()
   .align<A, B>(arrow.core.MapK(arg0), arrow.core.MapK(arg1)) as kotlin.collections.Map<K,
    arrow.core.Ior<A, B>>

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
  "arrow.core.extensions.map.semialign.Map.alignWith"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C> alignWith(
  arg0: Map<K, A>,
  arg1: Map<K, B>,
  arg2: Function1<Ior<A, B>, C>
): Map<K, C> = arrow.core.extensions.map.semialign.Map
   .semialign<K>()
   .alignWith<A, B, C>(arrow.core.MapK(arg0), arrow.core.MapK(arg1), arg2) as
    kotlin.collections.Map<K, C>

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
fun <K, A> Map<K, A>.salign(arg1: Semigroup<A>, arg2: Map<K, A>): Map<K, A> =
    arrow.core.extensions.map.semialign.Map.semialign<K>().run {
  arrow.core.MapK(this@salign).salign<A>(arg1, arrow.core.MapK(arg2)) as kotlin.collections.Map<K,
    A>
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
fun <K, A, B> Map<K, A>.padZip(arg1: Map<K, B>): Map<K, Tuple2<Option<A>, Option<B>>> =
    arrow.core.extensions.map.semialign.Map.semialign<K>().run {
  arrow.core.MapK(this@padZip).padZip<A, B>(arrow.core.MapK(arg1)) as kotlin.collections.Map<K,
    arrow.core.Tuple2<arrow.core.Option<A>, arrow.core.Option<B>>>
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
fun <K, A, B, C> Map<K, A>.padZipWith(arg1: Map<K, B>, arg2: Function2<Option<A>, Option<B>, C>):
    Map<K, C> = arrow.core.extensions.map.semialign.Map.semialign<K>().run {
  arrow.core.MapK(this@padZipWith).padZipWith<A, B, C>(arrow.core.MapK(arg1), arg2) as
    kotlin.collections.Map<K, C>
}

/**
 * cached extension
 */
@PublishedApi()
internal val semialign_singleton: MapKSemialign<Any?> = object : MapKSemialign<Any?> {}

object Map {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  inline fun <K> semialign(): MapKSemialign<K> = semialign_singleton as
      arrow.core.extensions.MapKSemialign<K>}
