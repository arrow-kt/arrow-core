package arrow.core.extensions.option.apply

import arrow.Kind
import arrow.core.Eval
import arrow.core.ForOption
import arrow.core.Option
import arrow.core.Option.Companion
import arrow.core.Tuple10
import arrow.core.Tuple2
import arrow.core.Tuple3
import arrow.core.Tuple4
import arrow.core.Tuple5
import arrow.core.Tuple6
import arrow.core.Tuple7
import arrow.core.Tuple8
import arrow.core.Tuple9
import arrow.core.extensions.OptionApply
import kotlin.Deprecated
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val apply_singleton: OptionApply = object : arrow.core.extensions.OptionApply {}

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
fun <A, B> Kind<ForOption, A>.ap(arg1: Kind<ForOption, Function1<A, B>>): Option<B> =
    arrow.core.Option.apply().run {
  this@ap.ap<A, B>(arg1) as arrow.core.Option<B>
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
fun <A, B> Kind<ForOption, A>.apEval(arg1: Eval<Kind<ForOption, Function1<A, B>>>):
    Eval<Kind<ForOption, B>> = arrow.core.Option.apply().run {
  this@apEval.apEval<A, B>(arg1) as arrow.core.Eval<arrow.Kind<arrow.core.ForOption, B>>
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
fun <A, B, Z> Kind<ForOption, A>.map2Eval(arg1: Eval<Kind<ForOption, B>>, arg2: Function1<Tuple2<A,
    B>, Z>): Eval<Kind<ForOption, Z>> = arrow.core.Option.apply().run {
  this@map2Eval.map2Eval<A, B, Z>(arg1, arg2) as arrow.core.Eval<arrow.Kind<arrow.core.ForOption,
    Z>>
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
  "arrow.core.Option.map"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, Z> map(
  arg0: Kind<ForOption, A>,
  arg1: Kind<ForOption, B>,
  arg2: Function1<Tuple2<A, B>, Z>
): Option<Z> = arrow.core.Option
   .apply()
   .map<A, B, Z>(arg0, arg1, arg2) as arrow.core.Option<Z>

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
  "arrow.core.Option.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, Z> mapN(
  arg0: Kind<ForOption, A>,
  arg1: Kind<ForOption, B>,
  arg2: Function1<Tuple2<A, B>, Z>
): Option<Z> = arrow.core.Option
   .apply()
   .mapN<A, B, Z>(arg0, arg1, arg2) as arrow.core.Option<Z>

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
  "arrow.core.Option.map"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, Z> map(
  arg0: Kind<ForOption, A>,
  arg1: Kind<ForOption, B>,
  arg2: Kind<ForOption, C>,
  arg3: Function1<Tuple3<A, B, C>, Z>
): Option<Z> = arrow.core.Option
   .apply()
   .map<A, B, C, Z>(arg0, arg1, arg2, arg3) as arrow.core.Option<Z>

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
  "arrow.core.Option.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, Z> mapN(
  arg0: Kind<ForOption, A>,
  arg1: Kind<ForOption, B>,
  arg2: Kind<ForOption, C>,
  arg3: Function1<Tuple3<A, B, C>, Z>
): Option<Z> = arrow.core.Option
   .apply()
   .mapN<A, B, C, Z>(arg0, arg1, arg2, arg3) as arrow.core.Option<Z>

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
  "arrow.core.Option.map"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, Z> map(
  arg0: Kind<ForOption, A>,
  arg1: Kind<ForOption, B>,
  arg2: Kind<ForOption, C>,
  arg3: Kind<ForOption, D>,
  arg4: Function1<Tuple4<A, B, C, D>, Z>
): Option<Z> = arrow.core.Option
   .apply()
   .map<A, B, C, D, Z>(arg0, arg1, arg2, arg3, arg4) as arrow.core.Option<Z>

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
  "arrow.core.Option.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, Z> mapN(
  arg0: Kind<ForOption, A>,
  arg1: Kind<ForOption, B>,
  arg2: Kind<ForOption, C>,
  arg3: Kind<ForOption, D>,
  arg4: Function1<Tuple4<A, B, C, D>, Z>
): Option<Z> = arrow.core.Option
   .apply()
   .mapN<A, B, C, D, Z>(arg0, arg1, arg2, arg3, arg4) as arrow.core.Option<Z>

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
  "arrow.core.Option.map"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, Z> map(
  arg0: Kind<ForOption, A>,
  arg1: Kind<ForOption, B>,
  arg2: Kind<ForOption, C>,
  arg3: Kind<ForOption, D>,
  arg4: Kind<ForOption, E>,
  arg5: Function1<Tuple5<A, B, C, D, E>, Z>
): Option<Z> = arrow.core.Option
   .apply()
   .map<A, B, C, D, E, Z>(arg0, arg1, arg2, arg3, arg4, arg5) as arrow.core.Option<Z>

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
  "arrow.core.Option.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, Z> mapN(
  arg0: Kind<ForOption, A>,
  arg1: Kind<ForOption, B>,
  arg2: Kind<ForOption, C>,
  arg3: Kind<ForOption, D>,
  arg4: Kind<ForOption, E>,
  arg5: Function1<Tuple5<A, B, C, D, E>, Z>
): Option<Z> = arrow.core.Option
   .apply()
   .mapN<A, B, C, D, E, Z>(arg0, arg1, arg2, arg3, arg4, arg5) as arrow.core.Option<Z>

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
  "arrow.core.Option.map"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, Z> map(
  arg0: Kind<ForOption, A>,
  arg1: Kind<ForOption, B>,
  arg2: Kind<ForOption, C>,
  arg3: Kind<ForOption, D>,
  arg4: Kind<ForOption, E>,
  arg5: Kind<ForOption, FF>,
  arg6: Function1<Tuple6<A, B, C, D, E, FF>, Z>
): Option<Z> = arrow.core.Option
   .apply()
   .map<A, B, C, D, E, FF, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as arrow.core.Option<Z>

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
  "arrow.core.Option.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, Z> mapN(
  arg0: Kind<ForOption, A>,
  arg1: Kind<ForOption, B>,
  arg2: Kind<ForOption, C>,
  arg3: Kind<ForOption, D>,
  arg4: Kind<ForOption, E>,
  arg5: Kind<ForOption, FF>,
  arg6: Function1<Tuple6<A, B, C, D, E, FF>, Z>
): Option<Z> = arrow.core.Option
   .apply()
   .mapN<A, B, C, D, E, FF, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as arrow.core.Option<Z>

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
  "arrow.core.Option.map"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, Z> map(
  arg0: Kind<ForOption, A>,
  arg1: Kind<ForOption, B>,
  arg2: Kind<ForOption, C>,
  arg3: Kind<ForOption, D>,
  arg4: Kind<ForOption, E>,
  arg5: Kind<ForOption, FF>,
  arg6: Kind<ForOption, G>,
  arg7: Function1<Tuple7<A, B, C, D, E, FF, G>, Z>
): Option<Z> = arrow.core.Option
   .apply()
   .map<A, B, C, D, E, FF, G, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as
    arrow.core.Option<Z>

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
  "arrow.core.Option.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, Z> mapN(
  arg0: Kind<ForOption, A>,
  arg1: Kind<ForOption, B>,
  arg2: Kind<ForOption, C>,
  arg3: Kind<ForOption, D>,
  arg4: Kind<ForOption, E>,
  arg5: Kind<ForOption, FF>,
  arg6: Kind<ForOption, G>,
  arg7: Function1<Tuple7<A, B, C, D, E, FF, G>, Z>
): Option<Z> = arrow.core.Option
   .apply()
   .mapN<A, B, C, D, E, FF, G, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as
    arrow.core.Option<Z>

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
  "arrow.core.Option.map"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, Z> map(
  arg0: Kind<ForOption, A>,
  arg1: Kind<ForOption, B>,
  arg2: Kind<ForOption, C>,
  arg3: Kind<ForOption, D>,
  arg4: Kind<ForOption, E>,
  arg5: Kind<ForOption, FF>,
  arg6: Kind<ForOption, G>,
  arg7: Kind<ForOption, H>,
  arg8: Function1<Tuple8<A, B, C, D, E, FF, G, H>, Z>
): Option<Z> = arrow.core.Option
   .apply()
   .map<A, B, C, D, E, FF, G, H, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
    arrow.core.Option<Z>

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
  "arrow.core.Option.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, Z> mapN(
  arg0: Kind<ForOption, A>,
  arg1: Kind<ForOption, B>,
  arg2: Kind<ForOption, C>,
  arg3: Kind<ForOption, D>,
  arg4: Kind<ForOption, E>,
  arg5: Kind<ForOption, FF>,
  arg6: Kind<ForOption, G>,
  arg7: Kind<ForOption, H>,
  arg8: Function1<Tuple8<A, B, C, D, E, FF, G, H>, Z>
): Option<Z> = arrow.core.Option
   .apply()
   .mapN<A, B, C, D, E, FF, G, H, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
    arrow.core.Option<Z>

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
  "arrow.core.Option.map"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, I, Z> map(
  arg0: Kind<ForOption, A>,
  arg1: Kind<ForOption, B>,
  arg2: Kind<ForOption, C>,
  arg3: Kind<ForOption, D>,
  arg4: Kind<ForOption, E>,
  arg5: Kind<ForOption, FF>,
  arg6: Kind<ForOption, G>,
  arg7: Kind<ForOption, H>,
  arg8: Kind<ForOption, I>,
  arg9: Function1<Tuple9<A, B, C, D, E, FF, G, H, I>, Z>
): Option<Z> = arrow.core.Option
   .apply()
   .map<A, B, C, D, E, FF, G, H, I, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)
    as arrow.core.Option<Z>

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
  "arrow.core.Option.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, I, Z> mapN(
  arg0: Kind<ForOption, A>,
  arg1: Kind<ForOption, B>,
  arg2: Kind<ForOption, C>,
  arg3: Kind<ForOption, D>,
  arg4: Kind<ForOption, E>,
  arg5: Kind<ForOption, FF>,
  arg6: Kind<ForOption, G>,
  arg7: Kind<ForOption, H>,
  arg8: Kind<ForOption, I>,
  arg9: Function1<Tuple9<A, B, C, D, E, FF, G, H, I>, Z>
): Option<Z> = arrow.core.Option
   .apply()
   .mapN<A, B, C, D, E, FF, G, H, I, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)
    as arrow.core.Option<Z>

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
  "arrow.core.Option.map"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, I, J, Z> map(
  arg0: Kind<ForOption, A>,
  arg1: Kind<ForOption, B>,
  arg2: Kind<ForOption, C>,
  arg3: Kind<ForOption, D>,
  arg4: Kind<ForOption, E>,
  arg5: Kind<ForOption, FF>,
  arg6: Kind<ForOption, G>,
  arg7: Kind<ForOption, H>,
  arg8: Kind<ForOption, I>,
  arg9: Kind<ForOption, J>,
  arg10: Function1<Tuple10<A, B, C, D, E, FF, G, H, I, J>, Z>
): Option<Z> = arrow.core.Option
   .apply()
   .map<A, B, C, D, E, FF, G, H, I, J,
    Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10) as arrow.core.Option<Z>

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
  "arrow.core.Option.mapN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, I, J, Z> mapN(
  arg0: Kind<ForOption, A>,
  arg1: Kind<ForOption, B>,
  arg2: Kind<ForOption, C>,
  arg3: Kind<ForOption, D>,
  arg4: Kind<ForOption, E>,
  arg5: Kind<ForOption, FF>,
  arg6: Kind<ForOption, G>,
  arg7: Kind<ForOption, H>,
  arg8: Kind<ForOption, I>,
  arg9: Kind<ForOption, J>,
  arg10: Function1<Tuple10<A, B, C, D, E, FF, G, H, I, J>, Z>
): Option<Z> = arrow.core.Option
   .apply()
   .mapN<A, B, C, D, E, FF, G, H, I, J,
    Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10) as arrow.core.Option<Z>

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
fun <A, B, Z> Kind<ForOption, A>.map2(arg1: Kind<ForOption, B>, arg2: Function1<Tuple2<A, B>, Z>):
    Option<Z> = arrow.core.Option.apply().run {
  this@map2.map2<A, B, Z>(arg1, arg2) as arrow.core.Option<Z>
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
fun <A, B> Kind<ForOption, A>.product(arg1: Kind<ForOption, B>): Option<Tuple2<A, B>> =
    arrow.core.Option.apply().run {
  this@product.product<A, B>(arg1) as arrow.core.Option<arrow.core.Tuple2<A, B>>
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
fun <A, B, Z> Kind<ForOption, Tuple2<A, B>>.product(arg1: Kind<ForOption, Z>): Option<Tuple3<A, B,
    Z>> = arrow.core.Option.apply().run {
  this@product.product<A, B, Z>(arg1) as arrow.core.Option<arrow.core.Tuple3<A, B, Z>>
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
fun <A, B, C, Z> Kind<ForOption, Tuple3<A, B, C>>.product(arg1: Kind<ForOption, Z>):
    Option<Tuple4<A, B, C, Z>> = arrow.core.Option.apply().run {
  this@product.product<A, B, C, Z>(arg1) as arrow.core.Option<arrow.core.Tuple4<A, B, C, Z>>
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
fun <A, B, C, D, Z> Kind<ForOption, Tuple4<A, B, C, D>>.product(arg1: Kind<ForOption, Z>):
    Option<Tuple5<A, B, C, D, Z>> = arrow.core.Option.apply().run {
  this@product.product<A, B, C, D, Z>(arg1) as arrow.core.Option<arrow.core.Tuple5<A, B, C, D, Z>>
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
fun <A, B, C, D, E, Z> Kind<ForOption, Tuple5<A, B, C, D, E>>.product(arg1: Kind<ForOption, Z>):
    Option<Tuple6<A, B, C, D, E, Z>> = arrow.core.Option.apply().run {
  this@product.product<A, B, C, D, E, Z>(arg1) as arrow.core.Option<arrow.core.Tuple6<A, B, C, D, E,
    Z>>
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
fun <A, B, C, D, E, FF, Z> Kind<ForOption, Tuple6<A, B, C, D, E, FF>>.product(arg1: Kind<ForOption,
    Z>): Option<Tuple7<A, B, C, D, E, FF, Z>> = arrow.core.Option.apply().run {
  this@product.product<A, B, C, D, E, FF, Z>(arg1) as arrow.core.Option<arrow.core.Tuple7<A, B, C,
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
fun <A, B, C, D, E, FF, G, Z> Kind<ForOption, Tuple7<A, B, C, D, E, FF,
    G>>.product(arg1: Kind<ForOption, Z>): Option<Tuple8<A, B, C, D, E, FF, G, Z>> =
    arrow.core.Option.apply().run {
  this@product.product<A, B, C, D, E, FF, G, Z>(arg1) as arrow.core.Option<arrow.core.Tuple8<A, B,
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
fun <A, B, C, D, E, FF, G, H, Z> Kind<ForOption, Tuple8<A, B, C, D, E, FF, G,
    H>>.product(arg1: Kind<ForOption, Z>): Option<Tuple9<A, B, C, D, E, FF, G, H, Z>> =
    arrow.core.Option.apply().run {
  this@product.product<A, B, C, D, E, FF, G, H, Z>(arg1) as arrow.core.Option<arrow.core.Tuple9<A,
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
fun <A, B, C, D, E, FF, G, H, I, Z> Kind<ForOption, Tuple9<A, B, C, D, E, FF, G, H,
    I>>.product(arg1: Kind<ForOption, Z>): Option<Tuple10<A, B, C, D, E, FF, G, H, I, Z>> =
    arrow.core.Option.apply().run {
  this@product.product<A, B, C, D, E, FF, G, H, I, Z>(arg1) as
    arrow.core.Option<arrow.core.Tuple10<A, B, C, D, E, FF, G, H, I, Z>>
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
  "arrow.core.Option.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> tupled(arg0: Kind<ForOption, A>, arg1: Kind<ForOption, B>): Option<Tuple2<A, B>> =
    arrow.core.Option
   .apply()
   .tupled<A, B>(arg0, arg1) as arrow.core.Option<arrow.core.Tuple2<A, B>>

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
  "arrow.core.Option.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> tupledN(arg0: Kind<ForOption, A>, arg1: Kind<ForOption, B>): Option<Tuple2<A, B>> =
    arrow.core.Option
   .apply()
   .tupledN<A, B>(arg0, arg1) as arrow.core.Option<arrow.core.Tuple2<A, B>>

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
  "arrow.core.Option.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C> tupled(
  arg0: Kind<ForOption, A>,
  arg1: Kind<ForOption, B>,
  arg2: Kind<ForOption, C>
): Option<Tuple3<A, B, C>> = arrow.core.Option
   .apply()
   .tupled<A, B, C>(arg0, arg1, arg2) as arrow.core.Option<arrow.core.Tuple3<A, B, C>>

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
  "arrow.core.Option.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C> tupledN(
  arg0: Kind<ForOption, A>,
  arg1: Kind<ForOption, B>,
  arg2: Kind<ForOption, C>
): Option<Tuple3<A, B, C>> = arrow.core.Option
   .apply()
   .tupledN<A, B, C>(arg0, arg1, arg2) as arrow.core.Option<arrow.core.Tuple3<A, B, C>>

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
  "arrow.core.Option.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D> tupled(
  arg0: Kind<ForOption, A>,
  arg1: Kind<ForOption, B>,
  arg2: Kind<ForOption, C>,
  arg3: Kind<ForOption, D>
): Option<Tuple4<A, B, C, D>> = arrow.core.Option
   .apply()
   .tupled<A, B, C, D>(arg0, arg1, arg2, arg3) as arrow.core.Option<arrow.core.Tuple4<A, B, C, D>>

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
  "arrow.core.Option.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D> tupledN(
  arg0: Kind<ForOption, A>,
  arg1: Kind<ForOption, B>,
  arg2: Kind<ForOption, C>,
  arg3: Kind<ForOption, D>
): Option<Tuple4<A, B, C, D>> = arrow.core.Option
   .apply()
   .tupledN<A, B, C, D>(arg0, arg1, arg2, arg3) as arrow.core.Option<arrow.core.Tuple4<A, B, C, D>>

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
  "arrow.core.Option.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E> tupled(
  arg0: Kind<ForOption, A>,
  arg1: Kind<ForOption, B>,
  arg2: Kind<ForOption, C>,
  arg3: Kind<ForOption, D>,
  arg4: Kind<ForOption, E>
): Option<Tuple5<A, B, C, D, E>> = arrow.core.Option
   .apply()
   .tupled<A, B, C, D, E>(arg0, arg1, arg2, arg3, arg4) as arrow.core.Option<arrow.core.Tuple5<A, B,
    C, D, E>>

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
  "arrow.core.Option.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E> tupledN(
  arg0: Kind<ForOption, A>,
  arg1: Kind<ForOption, B>,
  arg2: Kind<ForOption, C>,
  arg3: Kind<ForOption, D>,
  arg4: Kind<ForOption, E>
): Option<Tuple5<A, B, C, D, E>> = arrow.core.Option
   .apply()
   .tupledN<A, B, C, D, E>(arg0, arg1, arg2, arg3, arg4) as arrow.core.Option<arrow.core.Tuple5<A,
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
  "arrow.core.Option.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF> tupled(
  arg0: Kind<ForOption, A>,
  arg1: Kind<ForOption, B>,
  arg2: Kind<ForOption, C>,
  arg3: Kind<ForOption, D>,
  arg4: Kind<ForOption, E>,
  arg5: Kind<ForOption, FF>
): Option<Tuple6<A, B, C, D, E, FF>> = arrow.core.Option
   .apply()
   .tupled<A, B, C, D, E, FF>(arg0, arg1, arg2, arg3, arg4, arg5) as
    arrow.core.Option<arrow.core.Tuple6<A, B, C, D, E, FF>>

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
  "arrow.core.Option.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF> tupledN(
  arg0: Kind<ForOption, A>,
  arg1: Kind<ForOption, B>,
  arg2: Kind<ForOption, C>,
  arg3: Kind<ForOption, D>,
  arg4: Kind<ForOption, E>,
  arg5: Kind<ForOption, FF>
): Option<Tuple6<A, B, C, D, E, FF>> = arrow.core.Option
   .apply()
   .tupledN<A, B, C, D, E, FF>(arg0, arg1, arg2, arg3, arg4, arg5) as
    arrow.core.Option<arrow.core.Tuple6<A, B, C, D, E, FF>>

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
  "arrow.core.Option.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G> tupled(
  arg0: Kind<ForOption, A>,
  arg1: Kind<ForOption, B>,
  arg2: Kind<ForOption, C>,
  arg3: Kind<ForOption, D>,
  arg4: Kind<ForOption, E>,
  arg5: Kind<ForOption, FF>,
  arg6: Kind<ForOption, G>
): Option<Tuple7<A, B, C, D, E, FF, G>> = arrow.core.Option
   .apply()
   .tupled<A, B, C, D, E, FF, G>(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as
    arrow.core.Option<arrow.core.Tuple7<A, B, C, D, E, FF, G>>

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
  "arrow.core.Option.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G> tupledN(
  arg0: Kind<ForOption, A>,
  arg1: Kind<ForOption, B>,
  arg2: Kind<ForOption, C>,
  arg3: Kind<ForOption, D>,
  arg4: Kind<ForOption, E>,
  arg5: Kind<ForOption, FF>,
  arg6: Kind<ForOption, G>
): Option<Tuple7<A, B, C, D, E, FF, G>> = arrow.core.Option
   .apply()
   .tupledN<A, B, C, D, E, FF, G>(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as
    arrow.core.Option<arrow.core.Tuple7<A, B, C, D, E, FF, G>>

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
  "arrow.core.Option.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H> tupled(
  arg0: Kind<ForOption, A>,
  arg1: Kind<ForOption, B>,
  arg2: Kind<ForOption, C>,
  arg3: Kind<ForOption, D>,
  arg4: Kind<ForOption, E>,
  arg5: Kind<ForOption, FF>,
  arg6: Kind<ForOption, G>,
  arg7: Kind<ForOption, H>
): Option<Tuple8<A, B, C, D, E, FF, G, H>> = arrow.core.Option
   .apply()
   .tupled<A, B, C, D, E, FF, G, H>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as
    arrow.core.Option<arrow.core.Tuple8<A, B, C, D, E, FF, G, H>>

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
  "arrow.core.Option.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H> tupledN(
  arg0: Kind<ForOption, A>,
  arg1: Kind<ForOption, B>,
  arg2: Kind<ForOption, C>,
  arg3: Kind<ForOption, D>,
  arg4: Kind<ForOption, E>,
  arg5: Kind<ForOption, FF>,
  arg6: Kind<ForOption, G>,
  arg7: Kind<ForOption, H>
): Option<Tuple8<A, B, C, D, E, FF, G, H>> = arrow.core.Option
   .apply()
   .tupledN<A, B, C, D, E, FF, G, H>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as
    arrow.core.Option<arrow.core.Tuple8<A, B, C, D, E, FF, G, H>>

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
  "arrow.core.Option.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, I> tupled(
  arg0: Kind<ForOption, A>,
  arg1: Kind<ForOption, B>,
  arg2: Kind<ForOption, C>,
  arg3: Kind<ForOption, D>,
  arg4: Kind<ForOption, E>,
  arg5: Kind<ForOption, FF>,
  arg6: Kind<ForOption, G>,
  arg7: Kind<ForOption, H>,
  arg8: Kind<ForOption, I>
): Option<Tuple9<A, B, C, D, E, FF, G, H, I>> = arrow.core.Option
   .apply()
   .tupled<A, B, C, D, E, FF, G, H, I>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
    arrow.core.Option<arrow.core.Tuple9<A, B, C, D, E, FF, G, H, I>>

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
  "arrow.core.Option.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, I> tupledN(
  arg0: Kind<ForOption, A>,
  arg1: Kind<ForOption, B>,
  arg2: Kind<ForOption, C>,
  arg3: Kind<ForOption, D>,
  arg4: Kind<ForOption, E>,
  arg5: Kind<ForOption, FF>,
  arg6: Kind<ForOption, G>,
  arg7: Kind<ForOption, H>,
  arg8: Kind<ForOption, I>
): Option<Tuple9<A, B, C, D, E, FF, G, H, I>> = arrow.core.Option
   .apply()
   .tupledN<A, B, C, D, E, FF, G, H, I>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
    arrow.core.Option<arrow.core.Tuple9<A, B, C, D, E, FF, G, H, I>>

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
  "arrow.core.Option.tupled"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, I, J> tupled(
  arg0: Kind<ForOption, A>,
  arg1: Kind<ForOption, B>,
  arg2: Kind<ForOption, C>,
  arg3: Kind<ForOption, D>,
  arg4: Kind<ForOption, E>,
  arg5: Kind<ForOption, FF>,
  arg6: Kind<ForOption, G>,
  arg7: Kind<ForOption, H>,
  arg8: Kind<ForOption, I>,
  arg9: Kind<ForOption, J>
): Option<Tuple10<A, B, C, D, E, FF, G, H, I, J>> = arrow.core.Option
   .apply()
   .tupled<A, B, C, D, E, FF, G, H, I,
    J>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9) as
    arrow.core.Option<arrow.core.Tuple10<A, B, C, D, E, FF, G, H, I, J>>

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
  "arrow.core.Option.tupledN"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, FF, G, H, I, J> tupledN(
  arg0: Kind<ForOption, A>,
  arg1: Kind<ForOption, B>,
  arg2: Kind<ForOption, C>,
  arg3: Kind<ForOption, D>,
  arg4: Kind<ForOption, E>,
  arg5: Kind<ForOption, FF>,
  arg6: Kind<ForOption, G>,
  arg7: Kind<ForOption, H>,
  arg8: Kind<ForOption, I>,
  arg9: Kind<ForOption, J>
): Option<Tuple10<A, B, C, D, E, FF, G, H, I, J>> = arrow.core.Option
   .apply()
   .tupledN<A, B, C, D, E, FF, G, H, I,
    J>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9) as
    arrow.core.Option<arrow.core.Tuple10<A, B, C, D, E, FF, G, H, I, J>>

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
fun <A, B> Kind<ForOption, A>.followedBy(arg1: Kind<ForOption, B>): Option<B> =
    arrow.core.Option.apply().run {
  this@followedBy.followedBy<A, B>(arg1) as arrow.core.Option<B>
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
fun <A, B> Kind<ForOption, A>.apTap(arg1: Kind<ForOption, B>): Option<A> =
    arrow.core.Option.apply().run {
  this@apTap.apTap<A, B>(arg1) as arrow.core.Option<A>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun Companion.apply(): OptionApply = apply_singleton
