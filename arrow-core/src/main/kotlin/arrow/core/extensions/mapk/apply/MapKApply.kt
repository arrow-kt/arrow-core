package arrow.core.extensions.mapk.apply

import arrow.Kind
import arrow.core.Eval
import arrow.core.ForMapK
import arrow.core.MapK
import arrow.core.MapK.Companion
import arrow.core.Tuple10
import arrow.core.Tuple2
import arrow.core.Tuple3
import arrow.core.Tuple4
import arrow.core.Tuple5
import arrow.core.Tuple6
import arrow.core.Tuple7
import arrow.core.Tuple8
import arrow.core.Tuple9
import arrow.core.extensions.MapKApply
import kotlin.Any
import kotlin.Deprecated
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val apply_singleton: MapKApply<Any?> = object : MapKApply<Any?> {}

@JvmName("ap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "ap(arg1)",
  "arrow.core.ap"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B> Kind<Kind<ForMapK, K>, A>.ap(arg1: Kind<Kind<ForMapK, K>, Function1<A, B>>): MapK<K,
    B> = arrow.core.MapK.apply<K>().run {
  this@ap.ap<A, B>(arg1) as arrow.core.MapK<K, B>
}

@JvmName("apEval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "apEval(arg1)",
  "arrow.core.apEval"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B> Kind<Kind<ForMapK, K>, A>.apEval(arg1: Eval<Kind<Kind<ForMapK, K>, Function1<A, B>>>):
    Eval<Kind<Kind<ForMapK, K>, B>> = arrow.core.MapK.apply<K>().run {
  this@apEval.apEval<A, B>(arg1) as arrow.core.Eval<arrow.Kind<arrow.Kind<arrow.core.ForMapK, K>,
    B>>
}

@JvmName("map2Eval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "map2Eval(arg1, arg2)",
  "arrow.core.map2Eval"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, Z> Kind<Kind<ForMapK, K>, A>.map2Eval(arg1: Eval<Kind<Kind<ForMapK, K>, B>>,
    arg2: Function1<Tuple2<A, B>, Z>): Eval<Kind<Kind<ForMapK, K>, Z>> =
    arrow.core.MapK.apply<K>().run {
  this@map2Eval.map2Eval<A, B, Z>(arg1, arg2) as
    arrow.core.Eval<arrow.Kind<arrow.Kind<arrow.core.ForMapK, K>, Z>>
}

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "map(arg0, arg1, arg2)",
  "arrow.core.MapK.map"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, Z> map(
  arg0: Kind<Kind<ForMapK, K>, A>,
  arg1: Kind<Kind<ForMapK, K>, B>,
  arg2: Function1<Tuple2<A, B>, Z>
): MapK<K, Z> = arrow.core.MapK
   .apply<K>()
   .map<A, B, Z>(arg0, arg1, arg2) as arrow.core.MapK<K, Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "mapN(arg0, arg1, arg2)",
  "arrow.core.MapK.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, Z> mapN(
  arg0: Kind<Kind<ForMapK, K>, A>,
  arg1: Kind<Kind<ForMapK, K>, B>,
  arg2: Function1<Tuple2<A, B>, Z>
): MapK<K, Z> = arrow.core.MapK
   .apply<K>()
   .mapN<A, B, Z>(arg0, arg1, arg2) as arrow.core.MapK<K, Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "map(arg0, arg1, arg2, arg3)",
  "arrow.core.MapK.map"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, Z> map(
  arg0: Kind<Kind<ForMapK, K>, A>,
  arg1: Kind<Kind<ForMapK, K>, B>,
  arg2: Kind<Kind<ForMapK, K>, C>,
  arg3: Function1<Tuple3<A, B, C>, Z>
): MapK<K, Z> = arrow.core.MapK
   .apply<K>()
   .map<A, B, C, Z>(arg0, arg1, arg2, arg3) as arrow.core.MapK<K, Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "mapN(arg0, arg1, arg2, arg3)",
  "arrow.core.MapK.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, Z> mapN(
  arg0: Kind<Kind<ForMapK, K>, A>,
  arg1: Kind<Kind<ForMapK, K>, B>,
  arg2: Kind<Kind<ForMapK, K>, C>,
  arg3: Function1<Tuple3<A, B, C>, Z>
): MapK<K, Z> = arrow.core.MapK
   .apply<K>()
   .mapN<A, B, C, Z>(arg0, arg1, arg2, arg3) as arrow.core.MapK<K, Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "map(arg0, arg1, arg2, arg3, arg4)",
  "arrow.core.MapK.map"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, Z> map(
  arg0: Kind<Kind<ForMapK, K>, A>,
  arg1: Kind<Kind<ForMapK, K>, B>,
  arg2: Kind<Kind<ForMapK, K>, C>,
  arg3: Kind<Kind<ForMapK, K>, D>,
  arg4: Function1<Tuple4<A, B, C, D>, Z>
): MapK<K, Z> = arrow.core.MapK
   .apply<K>()
   .map<A, B, C, D, Z>(arg0, arg1, arg2, arg3, arg4) as arrow.core.MapK<K, Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "mapN(arg0, arg1, arg2, arg3, arg4)",
  "arrow.core.MapK.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, Z> mapN(
  arg0: Kind<Kind<ForMapK, K>, A>,
  arg1: Kind<Kind<ForMapK, K>, B>,
  arg2: Kind<Kind<ForMapK, K>, C>,
  arg3: Kind<Kind<ForMapK, K>, D>,
  arg4: Function1<Tuple4<A, B, C, D>, Z>
): MapK<K, Z> = arrow.core.MapK
   .apply<K>()
   .mapN<A, B, C, D, Z>(arg0, arg1, arg2, arg3, arg4) as arrow.core.MapK<K, Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "map(arg0, arg1, arg2, arg3, arg4, arg5)",
  "arrow.core.MapK.map"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E, Z> map(
  arg0: Kind<Kind<ForMapK, K>, A>,
  arg1: Kind<Kind<ForMapK, K>, B>,
  arg2: Kind<Kind<ForMapK, K>, C>,
  arg3: Kind<Kind<ForMapK, K>, D>,
  arg4: Kind<Kind<ForMapK, K>, E>,
  arg5: Function1<Tuple5<A, B, C, D, E>, Z>
): MapK<K, Z> = arrow.core.MapK
   .apply<K>()
   .map<A, B, C, D, E, Z>(arg0, arg1, arg2, arg3, arg4, arg5) as arrow.core.MapK<K, Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "mapN(arg0, arg1, arg2, arg3, arg4, arg5)",
  "arrow.core.MapK.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E, Z> mapN(
  arg0: Kind<Kind<ForMapK, K>, A>,
  arg1: Kind<Kind<ForMapK, K>, B>,
  arg2: Kind<Kind<ForMapK, K>, C>,
  arg3: Kind<Kind<ForMapK, K>, D>,
  arg4: Kind<Kind<ForMapK, K>, E>,
  arg5: Function1<Tuple5<A, B, C, D, E>, Z>
): MapK<K, Z> = arrow.core.MapK
   .apply<K>()
   .mapN<A, B, C, D, E, Z>(arg0, arg1, arg2, arg3, arg4, arg5) as arrow.core.MapK<K, Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "map(arg0, arg1, arg2, arg3, arg4, arg5, arg6)",
  "arrow.core.MapK.map"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E, FF, Z> map(
  arg0: Kind<Kind<ForMapK, K>, A>,
  arg1: Kind<Kind<ForMapK, K>, B>,
  arg2: Kind<Kind<ForMapK, K>, C>,
  arg3: Kind<Kind<ForMapK, K>, D>,
  arg4: Kind<Kind<ForMapK, K>, E>,
  arg5: Kind<Kind<ForMapK, K>, FF>,
  arg6: Function1<Tuple6<A, B, C, D, E, FF>, Z>
): MapK<K, Z> = arrow.core.MapK
   .apply<K>()
   .map<A, B, C, D, E, FF, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as arrow.core.MapK<K, Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6)",
  "arrow.core.MapK.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E, FF, Z> mapN(
  arg0: Kind<Kind<ForMapK, K>, A>,
  arg1: Kind<Kind<ForMapK, K>, B>,
  arg2: Kind<Kind<ForMapK, K>, C>,
  arg3: Kind<Kind<ForMapK, K>, D>,
  arg4: Kind<Kind<ForMapK, K>, E>,
  arg5: Kind<Kind<ForMapK, K>, FF>,
  arg6: Function1<Tuple6<A, B, C, D, E, FF>, Z>
): MapK<K, Z> = arrow.core.MapK
   .apply<K>()
   .mapN<A, B, C, D, E, FF, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as arrow.core.MapK<K, Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "map(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7)",
  "arrow.core.MapK.map"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E, FF, G, Z> map(
  arg0: Kind<Kind<ForMapK, K>, A>,
  arg1: Kind<Kind<ForMapK, K>, B>,
  arg2: Kind<Kind<ForMapK, K>, C>,
  arg3: Kind<Kind<ForMapK, K>, D>,
  arg4: Kind<Kind<ForMapK, K>, E>,
  arg5: Kind<Kind<ForMapK, K>, FF>,
  arg6: Kind<Kind<ForMapK, K>, G>,
  arg7: Function1<Tuple7<A, B, C, D, E, FF, G>, Z>
): MapK<K, Z> = arrow.core.MapK
   .apply<K>()
   .map<A, B, C, D, E, FF, G, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as
    arrow.core.MapK<K, Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7)",
  "arrow.core.MapK.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E, FF, G, Z> mapN(
  arg0: Kind<Kind<ForMapK, K>, A>,
  arg1: Kind<Kind<ForMapK, K>, B>,
  arg2: Kind<Kind<ForMapK, K>, C>,
  arg3: Kind<Kind<ForMapK, K>, D>,
  arg4: Kind<Kind<ForMapK, K>, E>,
  arg5: Kind<Kind<ForMapK, K>, FF>,
  arg6: Kind<Kind<ForMapK, K>, G>,
  arg7: Function1<Tuple7<A, B, C, D, E, FF, G>, Z>
): MapK<K, Z> = arrow.core.MapK
   .apply<K>()
   .mapN<A, B, C, D, E, FF, G, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as
    arrow.core.MapK<K, Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "map(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8)",
  "arrow.core.MapK.map"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E, FF, G, H, Z> map(
  arg0: Kind<Kind<ForMapK, K>, A>,
  arg1: Kind<Kind<ForMapK, K>, B>,
  arg2: Kind<Kind<ForMapK, K>, C>,
  arg3: Kind<Kind<ForMapK, K>, D>,
  arg4: Kind<Kind<ForMapK, K>, E>,
  arg5: Kind<Kind<ForMapK, K>, FF>,
  arg6: Kind<Kind<ForMapK, K>, G>,
  arg7: Kind<Kind<ForMapK, K>, H>,
  arg8: Function1<Tuple8<A, B, C, D, E, FF, G, H>, Z>
): MapK<K, Z> = arrow.core.MapK
   .apply<K>()
   .map<A, B, C, D, E, FF, G, H, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
    arrow.core.MapK<K, Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8)",
  "arrow.core.MapK.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E, FF, G, H, Z> mapN(
  arg0: Kind<Kind<ForMapK, K>, A>,
  arg1: Kind<Kind<ForMapK, K>, B>,
  arg2: Kind<Kind<ForMapK, K>, C>,
  arg3: Kind<Kind<ForMapK, K>, D>,
  arg4: Kind<Kind<ForMapK, K>, E>,
  arg5: Kind<Kind<ForMapK, K>, FF>,
  arg6: Kind<Kind<ForMapK, K>, G>,
  arg7: Kind<Kind<ForMapK, K>, H>,
  arg8: Function1<Tuple8<A, B, C, D, E, FF, G, H>, Z>
): MapK<K, Z> = arrow.core.MapK
   .apply<K>()
   .mapN<A, B, C, D, E, FF, G, H, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
    arrow.core.MapK<K, Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "map(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)",
  "arrow.core.MapK.map"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E, FF, G, H, I, Z> map(
  arg0: Kind<Kind<ForMapK, K>, A>,
  arg1: Kind<Kind<ForMapK, K>, B>,
  arg2: Kind<Kind<ForMapK, K>, C>,
  arg3: Kind<Kind<ForMapK, K>, D>,
  arg4: Kind<Kind<ForMapK, K>, E>,
  arg5: Kind<Kind<ForMapK, K>, FF>,
  arg6: Kind<Kind<ForMapK, K>, G>,
  arg7: Kind<Kind<ForMapK, K>, H>,
  arg8: Kind<Kind<ForMapK, K>, I>,
  arg9: Function1<Tuple9<A, B, C, D, E, FF, G, H, I>, Z>
): MapK<K, Z> = arrow.core.MapK
   .apply<K>()
   .map<A, B, C, D, E, FF, G, H, I, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)
    as arrow.core.MapK<K, Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)",
  "arrow.core.MapK.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E, FF, G, H, I, Z> mapN(
  arg0: Kind<Kind<ForMapK, K>, A>,
  arg1: Kind<Kind<ForMapK, K>, B>,
  arg2: Kind<Kind<ForMapK, K>, C>,
  arg3: Kind<Kind<ForMapK, K>, D>,
  arg4: Kind<Kind<ForMapK, K>, E>,
  arg5: Kind<Kind<ForMapK, K>, FF>,
  arg6: Kind<Kind<ForMapK, K>, G>,
  arg7: Kind<Kind<ForMapK, K>, H>,
  arg8: Kind<Kind<ForMapK, K>, I>,
  arg9: Function1<Tuple9<A, B, C, D, E, FF, G, H, I>, Z>
): MapK<K, Z> = arrow.core.MapK
   .apply<K>()
   .mapN<A, B, C, D, E, FF, G, H, I, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)
    as arrow.core.MapK<K, Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "map(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10)",
  "arrow.core.MapK.map"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E, FF, G, H, I, J, Z> map(
  arg0: Kind<Kind<ForMapK, K>, A>,
  arg1: Kind<Kind<ForMapK, K>, B>,
  arg2: Kind<Kind<ForMapK, K>, C>,
  arg3: Kind<Kind<ForMapK, K>, D>,
  arg4: Kind<Kind<ForMapK, K>, E>,
  arg5: Kind<Kind<ForMapK, K>, FF>,
  arg6: Kind<Kind<ForMapK, K>, G>,
  arg7: Kind<Kind<ForMapK, K>, H>,
  arg8: Kind<Kind<ForMapK, K>, I>,
  arg9: Kind<Kind<ForMapK, K>, J>,
  arg10: Function1<Tuple10<A, B, C, D, E, FF, G, H, I, J>, Z>
): MapK<K, Z> = arrow.core.MapK
   .apply<K>()
   .map<A, B, C, D, E, FF, G, H, I, J,
    Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10) as arrow.core.MapK<K, Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10)",
  "arrow.core.MapK.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E, FF, G, H, I, J, Z> mapN(
  arg0: Kind<Kind<ForMapK, K>, A>,
  arg1: Kind<Kind<ForMapK, K>, B>,
  arg2: Kind<Kind<ForMapK, K>, C>,
  arg3: Kind<Kind<ForMapK, K>, D>,
  arg4: Kind<Kind<ForMapK, K>, E>,
  arg5: Kind<Kind<ForMapK, K>, FF>,
  arg6: Kind<Kind<ForMapK, K>, G>,
  arg7: Kind<Kind<ForMapK, K>, H>,
  arg8: Kind<Kind<ForMapK, K>, I>,
  arg9: Kind<Kind<ForMapK, K>, J>,
  arg10: Function1<Tuple10<A, B, C, D, E, FF, G, H, I, J>, Z>
): MapK<K, Z> = arrow.core.MapK
   .apply<K>()
   .mapN<A, B, C, D, E, FF, G, H, I, J,
    Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10) as arrow.core.MapK<K, Z>

@JvmName("map2")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "map2(arg1, arg2)",
  "arrow.core.map2"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, Z> Kind<Kind<ForMapK, K>, A>.map2(arg1: Kind<Kind<ForMapK, K>, B>,
    arg2: Function1<Tuple2<A, B>, Z>): MapK<K, Z> = arrow.core.MapK.apply<K>().run {
  this@map2.map2<A, B, Z>(arg1, arg2) as arrow.core.MapK<K, Z>
}

@JvmName("product")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "product(arg1)",
  "arrow.core.product"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B> Kind<Kind<ForMapK, K>, A>.product(arg1: Kind<Kind<ForMapK, K>, B>): MapK<K, Tuple2<A,
    B>> = arrow.core.MapK.apply<K>().run {
  this@product.product<A, B>(arg1) as arrow.core.MapK<K, arrow.core.Tuple2<A, B>>
}

@JvmName("product1")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "product(arg1)",
  "arrow.core.product"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, Z> Kind<Kind<ForMapK, K>, Tuple2<A, B>>.product(arg1: Kind<Kind<ForMapK, K>, Z>):
    MapK<K, Tuple3<A, B, Z>> = arrow.core.MapK.apply<K>().run {
  this@product.product<A, B, Z>(arg1) as arrow.core.MapK<K, arrow.core.Tuple3<A, B, Z>>
}

@JvmName("product2")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "product(arg1)",
  "arrow.core.product"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, Z> Kind<Kind<ForMapK, K>, Tuple3<A, B, C>>.product(arg1: Kind<Kind<ForMapK, K>,
    Z>): MapK<K, Tuple4<A, B, C, Z>> = arrow.core.MapK.apply<K>().run {
  this@product.product<A, B, C, Z>(arg1) as arrow.core.MapK<K, arrow.core.Tuple4<A, B, C, Z>>
}

@JvmName("product3")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "product(arg1)",
  "arrow.core.product"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, Z> Kind<Kind<ForMapK, K>, Tuple4<A, B, C, D>>.product(arg1: Kind<Kind<ForMapK,
    K>, Z>): MapK<K, Tuple5<A, B, C, D, Z>> = arrow.core.MapK.apply<K>().run {
  this@product.product<A, B, C, D, Z>(arg1) as arrow.core.MapK<K, arrow.core.Tuple5<A, B, C, D, Z>>
}

@JvmName("product4")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "product(arg1)",
  "arrow.core.product"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E, Z> Kind<Kind<ForMapK, K>, Tuple5<A, B, C, D,
    E>>.product(arg1: Kind<Kind<ForMapK, K>, Z>): MapK<K, Tuple6<A, B, C, D, E, Z>> =
    arrow.core.MapK.apply<K>().run {
  this@product.product<A, B, C, D, E, Z>(arg1) as arrow.core.MapK<K, arrow.core.Tuple6<A, B, C, D,
    E, Z>>
}

@JvmName("product5")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "product(arg1)",
  "arrow.core.product"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E, FF, Z> Kind<Kind<ForMapK, K>, Tuple6<A, B, C, D, E,
    FF>>.product(arg1: Kind<Kind<ForMapK, K>, Z>): MapK<K, Tuple7<A, B, C, D, E, FF, Z>> =
    arrow.core.MapK.apply<K>().run {
  this@product.product<A, B, C, D, E, FF, Z>(arg1) as arrow.core.MapK<K, arrow.core.Tuple7<A, B, C,
    D, E, FF, Z>>
}

@JvmName("product6")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "product(arg1)",
  "arrow.core.product"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E, FF, G, Z> Kind<Kind<ForMapK, K>, Tuple7<A, B, C, D, E, FF,
    G>>.product(arg1: Kind<Kind<ForMapK, K>, Z>): MapK<K, Tuple8<A, B, C, D, E, FF, G, Z>> =
    arrow.core.MapK.apply<K>().run {
  this@product.product<A, B, C, D, E, FF, G, Z>(arg1) as arrow.core.MapK<K, arrow.core.Tuple8<A, B,
    C, D, E, FF, G, Z>>
}

@JvmName("product7")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "product(arg1)",
  "arrow.core.product"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E, FF, G, H, Z> Kind<Kind<ForMapK, K>, Tuple8<A, B, C, D, E, FF, G,
    H>>.product(arg1: Kind<Kind<ForMapK, K>, Z>): MapK<K, Tuple9<A, B, C, D, E, FF, G, H, Z>> =
    arrow.core.MapK.apply<K>().run {
  this@product.product<A, B, C, D, E, FF, G, H, Z>(arg1) as arrow.core.MapK<K, arrow.core.Tuple9<A,
    B, C, D, E, FF, G, H, Z>>
}

@JvmName("product8")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "product(arg1)",
  "arrow.core.product"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E, FF, G, H, I, Z> Kind<Kind<ForMapK, K>, Tuple9<A, B, C, D, E, FF, G, H,
    I>>.product(arg1: Kind<Kind<ForMapK, K>, Z>): MapK<K, Tuple10<A, B, C, D, E, FF, G, H, I, Z>> =
    arrow.core.MapK.apply<K>().run {
  this@product.product<A, B, C, D, E, FF, G, H, I, Z>(arg1) as arrow.core.MapK<K,
    arrow.core.Tuple10<A, B, C, D, E, FF, G, H, I, Z>>
}

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "tupled(arg0, arg1)",
  "arrow.core.MapK.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B> tupled(arg0: Kind<Kind<ForMapK, K>, A>, arg1: Kind<Kind<ForMapK, K>, B>): MapK<K,
    Tuple2<A, B>> = arrow.core.MapK
   .apply<K>()
   .tupled<A, B>(arg0, arg1) as arrow.core.MapK<K, arrow.core.Tuple2<A, B>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "tupledN(arg0, arg1)",
  "arrow.core.MapK.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B> tupledN(arg0: Kind<Kind<ForMapK, K>, A>, arg1: Kind<Kind<ForMapK, K>, B>): MapK<K,
    Tuple2<A, B>> = arrow.core.MapK
   .apply<K>()
   .tupledN<A, B>(arg0, arg1) as arrow.core.MapK<K, arrow.core.Tuple2<A, B>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "tupled(arg0, arg1, arg2)",
  "arrow.core.MapK.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C> tupled(
  arg0: Kind<Kind<ForMapK, K>, A>,
  arg1: Kind<Kind<ForMapK, K>, B>,
  arg2: Kind<Kind<ForMapK, K>, C>
): MapK<K, Tuple3<A, B, C>> = arrow.core.MapK
   .apply<K>()
   .tupled<A, B, C>(arg0, arg1, arg2) as arrow.core.MapK<K, arrow.core.Tuple3<A, B, C>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "tupledN(arg0, arg1, arg2)",
  "arrow.core.MapK.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C> tupledN(
  arg0: Kind<Kind<ForMapK, K>, A>,
  arg1: Kind<Kind<ForMapK, K>, B>,
  arg2: Kind<Kind<ForMapK, K>, C>
): MapK<K, Tuple3<A, B, C>> = arrow.core.MapK
   .apply<K>()
   .tupledN<A, B, C>(arg0, arg1, arg2) as arrow.core.MapK<K, arrow.core.Tuple3<A, B, C>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "tupled(arg0, arg1, arg2, arg3)",
  "arrow.core.MapK.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D> tupled(
  arg0: Kind<Kind<ForMapK, K>, A>,
  arg1: Kind<Kind<ForMapK, K>, B>,
  arg2: Kind<Kind<ForMapK, K>, C>,
  arg3: Kind<Kind<ForMapK, K>, D>
): MapK<K, Tuple4<A, B, C, D>> = arrow.core.MapK
   .apply<K>()
   .tupled<A, B, C, D>(arg0, arg1, arg2, arg3) as arrow.core.MapK<K, arrow.core.Tuple4<A, B, C, D>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "tupledN(arg0, arg1, arg2, arg3)",
  "arrow.core.MapK.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D> tupledN(
  arg0: Kind<Kind<ForMapK, K>, A>,
  arg1: Kind<Kind<ForMapK, K>, B>,
  arg2: Kind<Kind<ForMapK, K>, C>,
  arg3: Kind<Kind<ForMapK, K>, D>
): MapK<K, Tuple4<A, B, C, D>> = arrow.core.MapK
   .apply<K>()
   .tupledN<A, B, C, D>(arg0, arg1, arg2, arg3) as arrow.core.MapK<K, arrow.core.Tuple4<A, B, C, D>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "tupled(arg0, arg1, arg2, arg3, arg4)",
  "arrow.core.MapK.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E> tupled(
  arg0: Kind<Kind<ForMapK, K>, A>,
  arg1: Kind<Kind<ForMapK, K>, B>,
  arg2: Kind<Kind<ForMapK, K>, C>,
  arg3: Kind<Kind<ForMapK, K>, D>,
  arg4: Kind<Kind<ForMapK, K>, E>
): MapK<K, Tuple5<A, B, C, D, E>> = arrow.core.MapK
   .apply<K>()
   .tupled<A, B, C, D, E>(arg0, arg1, arg2, arg3, arg4) as arrow.core.MapK<K, arrow.core.Tuple5<A,
    B, C, D, E>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "tupledN(arg0, arg1, arg2, arg3, arg4)",
  "arrow.core.MapK.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E> tupledN(
  arg0: Kind<Kind<ForMapK, K>, A>,
  arg1: Kind<Kind<ForMapK, K>, B>,
  arg2: Kind<Kind<ForMapK, K>, C>,
  arg3: Kind<Kind<ForMapK, K>, D>,
  arg4: Kind<Kind<ForMapK, K>, E>
): MapK<K, Tuple5<A, B, C, D, E>> = arrow.core.MapK
   .apply<K>()
   .tupledN<A, B, C, D, E>(arg0, arg1, arg2, arg3, arg4) as arrow.core.MapK<K, arrow.core.Tuple5<A,
    B, C, D, E>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "tupled(arg0, arg1, arg2, arg3, arg4, arg5)",
  "arrow.core.MapK.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E, FF> tupled(
  arg0: Kind<Kind<ForMapK, K>, A>,
  arg1: Kind<Kind<ForMapK, K>, B>,
  arg2: Kind<Kind<ForMapK, K>, C>,
  arg3: Kind<Kind<ForMapK, K>, D>,
  arg4: Kind<Kind<ForMapK, K>, E>,
  arg5: Kind<Kind<ForMapK, K>, FF>
): MapK<K, Tuple6<A, B, C, D, E, FF>> = arrow.core.MapK
   .apply<K>()
   .tupled<A, B, C, D, E, FF>(arg0, arg1, arg2, arg3, arg4, arg5) as arrow.core.MapK<K,
    arrow.core.Tuple6<A, B, C, D, E, FF>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "tupledN(arg0, arg1, arg2, arg3, arg4, arg5)",
  "arrow.core.MapK.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E, FF> tupledN(
  arg0: Kind<Kind<ForMapK, K>, A>,
  arg1: Kind<Kind<ForMapK, K>, B>,
  arg2: Kind<Kind<ForMapK, K>, C>,
  arg3: Kind<Kind<ForMapK, K>, D>,
  arg4: Kind<Kind<ForMapK, K>, E>,
  arg5: Kind<Kind<ForMapK, K>, FF>
): MapK<K, Tuple6<A, B, C, D, E, FF>> = arrow.core.MapK
   .apply<K>()
   .tupledN<A, B, C, D, E, FF>(arg0, arg1, arg2, arg3, arg4, arg5) as arrow.core.MapK<K,
    arrow.core.Tuple6<A, B, C, D, E, FF>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "tupled(arg0, arg1, arg2, arg3, arg4, arg5, arg6)",
  "arrow.core.MapK.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E, FF, G> tupled(
  arg0: Kind<Kind<ForMapK, K>, A>,
  arg1: Kind<Kind<ForMapK, K>, B>,
  arg2: Kind<Kind<ForMapK, K>, C>,
  arg3: Kind<Kind<ForMapK, K>, D>,
  arg4: Kind<Kind<ForMapK, K>, E>,
  arg5: Kind<Kind<ForMapK, K>, FF>,
  arg6: Kind<Kind<ForMapK, K>, G>
): MapK<K, Tuple7<A, B, C, D, E, FF, G>> = arrow.core.MapK
   .apply<K>()
   .tupled<A, B, C, D, E, FF, G>(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as arrow.core.MapK<K,
    arrow.core.Tuple7<A, B, C, D, E, FF, G>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6)",
  "arrow.core.MapK.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E, FF, G> tupledN(
  arg0: Kind<Kind<ForMapK, K>, A>,
  arg1: Kind<Kind<ForMapK, K>, B>,
  arg2: Kind<Kind<ForMapK, K>, C>,
  arg3: Kind<Kind<ForMapK, K>, D>,
  arg4: Kind<Kind<ForMapK, K>, E>,
  arg5: Kind<Kind<ForMapK, K>, FF>,
  arg6: Kind<Kind<ForMapK, K>, G>
): MapK<K, Tuple7<A, B, C, D, E, FF, G>> = arrow.core.MapK
   .apply<K>()
   .tupledN<A, B, C, D, E, FF, G>(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as arrow.core.MapK<K,
    arrow.core.Tuple7<A, B, C, D, E, FF, G>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "tupled(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7)",
  "arrow.core.MapK.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E, FF, G, H> tupled(
  arg0: Kind<Kind<ForMapK, K>, A>,
  arg1: Kind<Kind<ForMapK, K>, B>,
  arg2: Kind<Kind<ForMapK, K>, C>,
  arg3: Kind<Kind<ForMapK, K>, D>,
  arg4: Kind<Kind<ForMapK, K>, E>,
  arg5: Kind<Kind<ForMapK, K>, FF>,
  arg6: Kind<Kind<ForMapK, K>, G>,
  arg7: Kind<Kind<ForMapK, K>, H>
): MapK<K, Tuple8<A, B, C, D, E, FF, G, H>> = arrow.core.MapK
   .apply<K>()
   .tupled<A, B, C, D, E, FF, G, H>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as
    arrow.core.MapK<K, arrow.core.Tuple8<A, B, C, D, E, FF, G, H>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7)",
  "arrow.core.MapK.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E, FF, G, H> tupledN(
  arg0: Kind<Kind<ForMapK, K>, A>,
  arg1: Kind<Kind<ForMapK, K>, B>,
  arg2: Kind<Kind<ForMapK, K>, C>,
  arg3: Kind<Kind<ForMapK, K>, D>,
  arg4: Kind<Kind<ForMapK, K>, E>,
  arg5: Kind<Kind<ForMapK, K>, FF>,
  arg6: Kind<Kind<ForMapK, K>, G>,
  arg7: Kind<Kind<ForMapK, K>, H>
): MapK<K, Tuple8<A, B, C, D, E, FF, G, H>> = arrow.core.MapK
   .apply<K>()
   .tupledN<A, B, C, D, E, FF, G, H>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as
    arrow.core.MapK<K, arrow.core.Tuple8<A, B, C, D, E, FF, G, H>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "tupled(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8)",
  "arrow.core.MapK.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E, FF, G, H, I> tupled(
  arg0: Kind<Kind<ForMapK, K>, A>,
  arg1: Kind<Kind<ForMapK, K>, B>,
  arg2: Kind<Kind<ForMapK, K>, C>,
  arg3: Kind<Kind<ForMapK, K>, D>,
  arg4: Kind<Kind<ForMapK, K>, E>,
  arg5: Kind<Kind<ForMapK, K>, FF>,
  arg6: Kind<Kind<ForMapK, K>, G>,
  arg7: Kind<Kind<ForMapK, K>, H>,
  arg8: Kind<Kind<ForMapK, K>, I>
): MapK<K, Tuple9<A, B, C, D, E, FF, G, H, I>> = arrow.core.MapK
   .apply<K>()
   .tupled<A, B, C, D, E, FF, G, H, I>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
    arrow.core.MapK<K, arrow.core.Tuple9<A, B, C, D, E, FF, G, H, I>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8)",
  "arrow.core.MapK.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E, FF, G, H, I> tupledN(
  arg0: Kind<Kind<ForMapK, K>, A>,
  arg1: Kind<Kind<ForMapK, K>, B>,
  arg2: Kind<Kind<ForMapK, K>, C>,
  arg3: Kind<Kind<ForMapK, K>, D>,
  arg4: Kind<Kind<ForMapK, K>, E>,
  arg5: Kind<Kind<ForMapK, K>, FF>,
  arg6: Kind<Kind<ForMapK, K>, G>,
  arg7: Kind<Kind<ForMapK, K>, H>,
  arg8: Kind<Kind<ForMapK, K>, I>
): MapK<K, Tuple9<A, B, C, D, E, FF, G, H, I>> = arrow.core.MapK
   .apply<K>()
   .tupledN<A, B, C, D, E, FF, G, H, I>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
    arrow.core.MapK<K, arrow.core.Tuple9<A, B, C, D, E, FF, G, H, I>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "tupled(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)",
  "arrow.core.MapK.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E, FF, G, H, I, J> tupled(
  arg0: Kind<Kind<ForMapK, K>, A>,
  arg1: Kind<Kind<ForMapK, K>, B>,
  arg2: Kind<Kind<ForMapK, K>, C>,
  arg3: Kind<Kind<ForMapK, K>, D>,
  arg4: Kind<Kind<ForMapK, K>, E>,
  arg5: Kind<Kind<ForMapK, K>, FF>,
  arg6: Kind<Kind<ForMapK, K>, G>,
  arg7: Kind<Kind<ForMapK, K>, H>,
  arg8: Kind<Kind<ForMapK, K>, I>,
  arg9: Kind<Kind<ForMapK, K>, J>
): MapK<K, Tuple10<A, B, C, D, E, FF, G, H, I, J>> = arrow.core.MapK
   .apply<K>()
   .tupled<A, B, C, D, E, FF, G, H, I,
    J>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9) as arrow.core.MapK<K,
    arrow.core.Tuple10<A, B, C, D, E, FF, G, H, I, J>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)",
  "arrow.core.MapK.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B, C, D, E, FF, G, H, I, J> tupledN(
  arg0: Kind<Kind<ForMapK, K>, A>,
  arg1: Kind<Kind<ForMapK, K>, B>,
  arg2: Kind<Kind<ForMapK, K>, C>,
  arg3: Kind<Kind<ForMapK, K>, D>,
  arg4: Kind<Kind<ForMapK, K>, E>,
  arg5: Kind<Kind<ForMapK, K>, FF>,
  arg6: Kind<Kind<ForMapK, K>, G>,
  arg7: Kind<Kind<ForMapK, K>, H>,
  arg8: Kind<Kind<ForMapK, K>, I>,
  arg9: Kind<Kind<ForMapK, K>, J>
): MapK<K, Tuple10<A, B, C, D, E, FF, G, H, I, J>> = arrow.core.MapK
   .apply<K>()
   .tupledN<A, B, C, D, E, FF, G, H, I,
    J>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9) as arrow.core.MapK<K,
    arrow.core.Tuple10<A, B, C, D, E, FF, G, H, I, J>>

@JvmName("followedBy")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "followedBy(arg1)",
  "arrow.core.followedBy"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B> Kind<Kind<ForMapK, K>, A>.followedBy(arg1: Kind<Kind<ForMapK, K>, B>): MapK<K, B> =
    arrow.core.MapK.apply<K>().run {
  this@followedBy.followedBy<A, B>(arg1) as arrow.core.MapK<K, B>
}

@JvmName("apTap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "apTap(arg1)",
  "arrow.core.apTap"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B> Kind<Kind<ForMapK, K>, A>.apTap(arg1: Kind<Kind<ForMapK, K>, B>): MapK<K, A> =
    arrow.core.MapK.apply<K>().run {
  this@apTap.apTap<A, B>(arg1) as arrow.core.MapK<K, A>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <K> Companion.apply(): MapKApply<K> = apply_singleton as
    arrow.core.extensions.MapKApply<K>