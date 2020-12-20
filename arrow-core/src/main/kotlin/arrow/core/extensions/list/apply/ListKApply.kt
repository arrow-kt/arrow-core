package arrow.core.extensions.list.apply

import arrow.Kind
import arrow.core.Eval
import arrow.core.ForListK
import arrow.core.Tuple10
import arrow.core.Tuple2
import arrow.core.Tuple3
import arrow.core.Tuple4
import arrow.core.Tuple5
import arrow.core.Tuple6
import arrow.core.Tuple7
import arrow.core.Tuple8
import arrow.core.Tuple9
import arrow.core.extensions.ListKApply
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.collections.List
import kotlin.jvm.JvmName

@JvmName("ap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B> List<A>.ap(arg1: List<Function1<A, B>>): List<B> =
    arrow.core.extensions.list.apply.List.apply().run {
  arrow.core.ListK(this@ap).ap<A, B>(arrow.core.ListK(arg1)) as kotlin.collections.List<B>
}

@JvmName("apEval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B> List<A>.apEval(arg1: Eval<Kind<ForListK, Function1<A, B>>>): Eval<Kind<ForListK, B>> =
    arrow.core.extensions.list.apply.List.apply().run {
  arrow.core.ListK(this@apEval).apEval<A, B>(arg1) as
    arrow.core.Eval<arrow.Kind<arrow.core.ForListK, B>>
}

@JvmName("map2Eval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B, Z> List<A>.map2Eval(arg1: Eval<Kind<ForListK, B>>, arg2: Function1<Tuple2<A, B>, Z>):
    Eval<Kind<ForListK, Z>> = arrow.core.extensions.list.apply.List.apply().run {
  arrow.core.ListK(this@map2Eval).map2Eval<A, B, Z>(arg1, arg2) as
    arrow.core.Eval<arrow.Kind<arrow.core.ForListK, Z>>
}

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B, Z> map(
  arg0: List<A>,
  arg1: List<B>,
  arg2: Function1<Tuple2<A, B>, Z>
): List<Z> = arrow.core.extensions.list.apply.List
   .apply()
   .map<A, B, Z>(arrow.core.ListK(arg0), arrow.core.ListK(arg1), arg2) as kotlin.collections.List<Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B, Z> mapN(
  arg0: List<A>,
  arg1: List<B>,
  arg2: Function1<Tuple2<A, B>, Z>
): List<Z> = arrow.core.extensions.list.apply.List
   .apply()
   .mapN<A, B, Z>(arrow.core.ListK(arg0), arrow.core.ListK(arg1), arg2) as
    kotlin.collections.List<Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B, C, Z> map(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: Function1<Tuple3<A, B, C>, Z>
): List<Z> = arrow.core.extensions.list.apply.List
   .apply()
   .map<A, B, C, Z>(arrow.core.ListK(arg0), arrow.core.ListK(arg1), arrow.core.ListK(arg2), arg3) as
    kotlin.collections.List<Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B, C, Z> mapN(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: Function1<Tuple3<A, B, C>, Z>
): List<Z> = arrow.core.extensions.list.apply.List
   .apply()
   .mapN<A, B, C, Z>(arrow.core.ListK(arg0), arrow.core.ListK(arg1), arrow.core.ListK(arg2), arg3)
    as kotlin.collections.List<Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B, C, D, Z> map(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: Function1<Tuple4<A, B, C, D>, Z>
): List<Z> = arrow.core.extensions.list.apply.List
   .apply()
   .map<A, B, C, D,
    Z>(arrow.core.ListK(arg0), arrow.core.ListK(arg1), arrow.core.ListK(arg2), arrow.core.ListK(arg3), arg4)
    as kotlin.collections.List<Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B, C, D, Z> mapN(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: Function1<Tuple4<A, B, C, D>, Z>
): List<Z> = arrow.core.extensions.list.apply.List
   .apply()
   .mapN<A, B, C, D,
    Z>(arrow.core.ListK(arg0), arrow.core.ListK(arg1), arrow.core.ListK(arg2), arrow.core.ListK(arg3), arg4)
    as kotlin.collections.List<Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B, C, D, E, Z> map(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: Function1<Tuple5<A, B, C, D, E>, Z>
): List<Z> = arrow.core.extensions.list.apply.List
   .apply()
   .map<A, B, C, D, E,
    Z>(arrow.core.ListK(arg0), arrow.core.ListK(arg1), arrow.core.ListK(arg2), arrow.core.ListK(arg3), arrow.core.ListK(arg4), arg5)
    as kotlin.collections.List<Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B, C, D, E, Z> mapN(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: Function1<Tuple5<A, B, C, D, E>, Z>
): List<Z> = arrow.core.extensions.list.apply.List
   .apply()
   .mapN<A, B, C, D, E,
    Z>(arrow.core.ListK(arg0), arrow.core.ListK(arg1), arrow.core.ListK(arg2), arrow.core.ListK(arg3), arrow.core.ListK(arg4), arg5)
    as kotlin.collections.List<Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B, C, D, E, FF, Z> map(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: List<FF>,
  arg6: Function1<Tuple6<A, B, C, D, E, FF>, Z>
): List<Z> = arrow.core.extensions.list.apply.List
   .apply()
   .map<A, B, C, D, E, FF,
    Z>(arrow.core.ListK(arg0), arrow.core.ListK(arg1), arrow.core.ListK(arg2), arrow.core.ListK(arg3), arrow.core.ListK(arg4), arrow.core.ListK(arg5), arg6)
    as kotlin.collections.List<Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B, C, D, E, FF, Z> mapN(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: List<FF>,
  arg6: Function1<Tuple6<A, B, C, D, E, FF>, Z>
): List<Z> = arrow.core.extensions.list.apply.List
   .apply()
   .mapN<A, B, C, D, E, FF,
    Z>(arrow.core.ListK(arg0), arrow.core.ListK(arg1), arrow.core.ListK(arg2), arrow.core.ListK(arg3), arrow.core.ListK(arg4), arrow.core.ListK(arg5), arg6)
    as kotlin.collections.List<Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B, C, D, E, FF, G, Z> map(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: List<FF>,
  arg6: List<G>,
  arg7: Function1<Tuple7<A, B, C, D, E, FF, G>, Z>
): List<Z> = arrow.core.extensions.list.apply.List
   .apply()
   .map<A, B, C, D, E, FF, G,
    Z>(arrow.core.ListK(arg0), arrow.core.ListK(arg1), arrow.core.ListK(arg2), arrow.core.ListK(arg3), arrow.core.ListK(arg4), arrow.core.ListK(arg5), arrow.core.ListK(arg6), arg7)
    as kotlin.collections.List<Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B, C, D, E, FF, G, Z> mapN(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: List<FF>,
  arg6: List<G>,
  arg7: Function1<Tuple7<A, B, C, D, E, FF, G>, Z>
): List<Z> = arrow.core.extensions.list.apply.List
   .apply()
   .mapN<A, B, C, D, E, FF, G,
    Z>(arrow.core.ListK(arg0), arrow.core.ListK(arg1), arrow.core.ListK(arg2), arrow.core.ListK(arg3), arrow.core.ListK(arg4), arrow.core.ListK(arg5), arrow.core.ListK(arg6), arg7)
    as kotlin.collections.List<Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B, C, D, E, FF, G, H, Z> map(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: List<FF>,
  arg6: List<G>,
  arg7: List<H>,
  arg8: Function1<Tuple8<A, B, C, D, E, FF, G, H>, Z>
): List<Z> = arrow.core.extensions.list.apply.List
   .apply()
   .map<A, B, C, D, E, FF, G, H,
    Z>(arrow.core.ListK(arg0), arrow.core.ListK(arg1), arrow.core.ListK(arg2), arrow.core.ListK(arg3), arrow.core.ListK(arg4), arrow.core.ListK(arg5), arrow.core.ListK(arg6), arrow.core.ListK(arg7), arg8)
    as kotlin.collections.List<Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B, C, D, E, FF, G, H, Z> mapN(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: List<FF>,
  arg6: List<G>,
  arg7: List<H>,
  arg8: Function1<Tuple8<A, B, C, D, E, FF, G, H>, Z>
): List<Z> = arrow.core.extensions.list.apply.List
   .apply()
   .mapN<A, B, C, D, E, FF, G, H,
    Z>(arrow.core.ListK(arg0), arrow.core.ListK(arg1), arrow.core.ListK(arg2), arrow.core.ListK(arg3), arrow.core.ListK(arg4), arrow.core.ListK(arg5), arrow.core.ListK(arg6), arrow.core.ListK(arg7), arg8)
    as kotlin.collections.List<Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B, C, D, E, FF, G, H, I, Z> map(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: List<FF>,
  arg6: List<G>,
  arg7: List<H>,
  arg8: List<I>,
  arg9: Function1<Tuple9<A, B, C, D, E, FF, G, H, I>, Z>
): List<Z> = arrow.core.extensions.list.apply.List
   .apply()
   .map<A, B, C, D, E, FF, G, H, I,
    Z>(arrow.core.ListK(arg0), arrow.core.ListK(arg1), arrow.core.ListK(arg2), arrow.core.ListK(arg3), arrow.core.ListK(arg4), arrow.core.ListK(arg5), arrow.core.ListK(arg6), arrow.core.ListK(arg7), arrow.core.ListK(arg8), arg9)
    as kotlin.collections.List<Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B, C, D, E, FF, G, H, I, Z> mapN(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: List<FF>,
  arg6: List<G>,
  arg7: List<H>,
  arg8: List<I>,
  arg9: Function1<Tuple9<A, B, C, D, E, FF, G, H, I>, Z>
): List<Z> = arrow.core.extensions.list.apply.List
   .apply()
   .mapN<A, B, C, D, E, FF, G, H, I,
    Z>(arrow.core.ListK(arg0), arrow.core.ListK(arg1), arrow.core.ListK(arg2), arrow.core.ListK(arg3), arrow.core.ListK(arg4), arrow.core.ListK(arg5), arrow.core.ListK(arg6), arrow.core.ListK(arg7), arrow.core.ListK(arg8), arg9)
    as kotlin.collections.List<Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B, C, D, E, FF, G, H, I, J, Z> map(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: List<FF>,
  arg6: List<G>,
  arg7: List<H>,
  arg8: List<I>,
  arg9: List<J>,
  arg10: Function1<Tuple10<A, B, C, D, E, FF, G, H, I, J>, Z>
): List<Z> = arrow.core.extensions.list.apply.List
   .apply()
   .map<A, B, C, D, E, FF, G, H, I, J,
    Z>(arrow.core.ListK(arg0), arrow.core.ListK(arg1), arrow.core.ListK(arg2), arrow.core.ListK(arg3), arrow.core.ListK(arg4), arrow.core.ListK(arg5), arrow.core.ListK(arg6), arrow.core.ListK(arg7), arrow.core.ListK(arg8), arrow.core.ListK(arg9), arg10)
    as kotlin.collections.List<Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B, C, D, E, FF, G, H, I, J, Z> mapN(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: List<FF>,
  arg6: List<G>,
  arg7: List<H>,
  arg8: List<I>,
  arg9: List<J>,
  arg10: Function1<Tuple10<A, B, C, D, E, FF, G, H, I, J>, Z>
): List<Z> = arrow.core.extensions.list.apply.List
   .apply()
   .mapN<A, B, C, D, E, FF, G, H, I, J,
    Z>(arrow.core.ListK(arg0), arrow.core.ListK(arg1), arrow.core.ListK(arg2), arrow.core.ListK(arg3), arrow.core.ListK(arg4), arrow.core.ListK(arg5), arrow.core.ListK(arg6), arrow.core.ListK(arg7), arrow.core.ListK(arg8), arrow.core.ListK(arg9), arg10)
    as kotlin.collections.List<Z>

@JvmName("map2")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B, Z> List<A>.map2(arg1: List<B>, arg2: Function1<Tuple2<A, B>, Z>): List<Z> =
    arrow.core.extensions.list.apply.List.apply().run {
  arrow.core.ListK(this@map2).map2<A, B, Z>(arrow.core.ListK(arg1), arg2) as
    kotlin.collections.List<Z>
}

@JvmName("product")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B> List<A>.product(arg1: List<B>): List<Tuple2<A, B>> =
    arrow.core.extensions.list.apply.List.apply().run {
  arrow.core.ListK(this@product).product<A, B>(arrow.core.ListK(arg1)) as
    kotlin.collections.List<arrow.core.Tuple2<A, B>>
}

@JvmName("product1")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B, Z> List<Tuple2<A, B>>.product(arg1: List<Z>): List<Tuple3<A, B, Z>> =
    arrow.core.extensions.list.apply.List.apply().run {
  arrow.core.ListK(this@product).product<A, B, Z>(arrow.core.ListK(arg1)) as
    kotlin.collections.List<arrow.core.Tuple3<A, B, Z>>
}

@JvmName("product2")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B, C, Z> List<Tuple3<A, B, C>>.product(arg1: List<Z>): List<Tuple4<A, B, C, Z>> =
    arrow.core.extensions.list.apply.List.apply().run {
  arrow.core.ListK(this@product).product<A, B, C, Z>(arrow.core.ListK(arg1)) as
    kotlin.collections.List<arrow.core.Tuple4<A, B, C, Z>>
}

@JvmName("product3")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B, C, D, Z> List<Tuple4<A, B, C, D>>.product(arg1: List<Z>): List<Tuple5<A, B, C, D, Z>> =
    arrow.core.extensions.list.apply.List.apply().run {
  arrow.core.ListK(this@product).product<A, B, C, D, Z>(arrow.core.ListK(arg1)) as
    kotlin.collections.List<arrow.core.Tuple5<A, B, C, D, Z>>
}

@JvmName("product4")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B, C, D, E, Z> List<Tuple5<A, B, C, D, E>>.product(arg1: List<Z>): List<Tuple6<A, B, C, D,
    E, Z>> = arrow.core.extensions.list.apply.List.apply().run {
  arrow.core.ListK(this@product).product<A, B, C, D, E, Z>(arrow.core.ListK(arg1)) as
    kotlin.collections.List<arrow.core.Tuple6<A, B, C, D, E, Z>>
}

@JvmName("product5")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B, C, D, E, FF, Z> List<Tuple6<A, B, C, D, E, FF>>.product(arg1: List<Z>): List<Tuple7<A, B,
    C, D, E, FF, Z>> = arrow.core.extensions.list.apply.List.apply().run {
  arrow.core.ListK(this@product).product<A, B, C, D, E, FF, Z>(arrow.core.ListK(arg1)) as
    kotlin.collections.List<arrow.core.Tuple7<A, B, C, D, E, FF, Z>>
}

@JvmName("product6")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B, C, D, E, FF, G, Z> List<Tuple7<A, B, C, D, E, FF, G>>.product(arg1: List<Z>):
    List<Tuple8<A, B, C, D, E, FF, G, Z>> = arrow.core.extensions.list.apply.List.apply().run {
  arrow.core.ListK(this@product).product<A, B, C, D, E, FF, G, Z>(arrow.core.ListK(arg1)) as
    kotlin.collections.List<arrow.core.Tuple8<A, B, C, D, E, FF, G, Z>>
}

@JvmName("product7")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B, C, D, E, FF, G, H, Z> List<Tuple8<A, B, C, D, E, FF, G, H>>.product(arg1: List<Z>):
    List<Tuple9<A, B, C, D, E, FF, G, H, Z>> = arrow.core.extensions.list.apply.List.apply().run {
  arrow.core.ListK(this@product).product<A, B, C, D, E, FF, G, H, Z>(arrow.core.ListK(arg1)) as
    kotlin.collections.List<arrow.core.Tuple9<A, B, C, D, E, FF, G, H, Z>>
}

@JvmName("product8")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B, C, D, E, FF, G, H, I, Z> List<Tuple9<A, B, C, D, E, FF, G, H, I>>.product(arg1: List<Z>):
    List<Tuple10<A, B, C, D, E, FF, G, H, I, Z>> =
    arrow.core.extensions.list.apply.List.apply().run {
  arrow.core.ListK(this@product).product<A, B, C, D, E, FF, G, H, I, Z>(arrow.core.ListK(arg1)) as
    kotlin.collections.List<arrow.core.Tuple10<A, B, C, D, E, FF, G, H, I, Z>>
}

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B> tupled(arg0: List<A>, arg1: List<B>): List<Tuple2<A, B>> =
    arrow.core.extensions.list.apply.List
   .apply()
   .tupled<A, B>(arrow.core.ListK(arg0), arrow.core.ListK(arg1)) as
    kotlin.collections.List<arrow.core.Tuple2<A, B>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B> tupledN(arg0: List<A>, arg1: List<B>): List<Tuple2<A, B>> =
    arrow.core.extensions.list.apply.List
   .apply()
   .tupledN<A, B>(arrow.core.ListK(arg0), arrow.core.ListK(arg1)) as
    kotlin.collections.List<arrow.core.Tuple2<A, B>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B, C> tupled(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>
): List<Tuple3<A, B, C>> = arrow.core.extensions.list.apply.List
   .apply()
   .tupled<A, B, C>(arrow.core.ListK(arg0), arrow.core.ListK(arg1), arrow.core.ListK(arg2)) as
    kotlin.collections.List<arrow.core.Tuple3<A, B, C>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B, C> tupledN(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>
): List<Tuple3<A, B, C>> = arrow.core.extensions.list.apply.List
   .apply()
   .tupledN<A, B, C>(arrow.core.ListK(arg0), arrow.core.ListK(arg1), arrow.core.ListK(arg2)) as
    kotlin.collections.List<arrow.core.Tuple3<A, B, C>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B, C, D> tupled(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>
): List<Tuple4<A, B, C, D>> = arrow.core.extensions.list.apply.List
   .apply()
   .tupled<A, B, C,
    D>(arrow.core.ListK(arg0), arrow.core.ListK(arg1), arrow.core.ListK(arg2), arrow.core.ListK(arg3))
    as kotlin.collections.List<arrow.core.Tuple4<A, B, C, D>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B, C, D> tupledN(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>
): List<Tuple4<A, B, C, D>> = arrow.core.extensions.list.apply.List
   .apply()
   .tupledN<A, B, C,
    D>(arrow.core.ListK(arg0), arrow.core.ListK(arg1), arrow.core.ListK(arg2), arrow.core.ListK(arg3))
    as kotlin.collections.List<arrow.core.Tuple4<A, B, C, D>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B, C, D, E> tupled(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>
): List<Tuple5<A, B, C, D, E>> = arrow.core.extensions.list.apply.List
   .apply()
   .tupled<A, B, C, D,
    E>(arrow.core.ListK(arg0), arrow.core.ListK(arg1), arrow.core.ListK(arg2), arrow.core.ListK(arg3), arrow.core.ListK(arg4))
    as kotlin.collections.List<arrow.core.Tuple5<A, B, C, D, E>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B, C, D, E> tupledN(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>
): List<Tuple5<A, B, C, D, E>> = arrow.core.extensions.list.apply.List
   .apply()
   .tupledN<A, B, C, D,
    E>(arrow.core.ListK(arg0), arrow.core.ListK(arg1), arrow.core.ListK(arg2), arrow.core.ListK(arg3), arrow.core.ListK(arg4))
    as kotlin.collections.List<arrow.core.Tuple5<A, B, C, D, E>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B, C, D, E, FF> tupled(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: List<FF>
): List<Tuple6<A, B, C, D, E, FF>> = arrow.core.extensions.list.apply.List
   .apply()
   .tupled<A, B, C, D, E,
    FF>(arrow.core.ListK(arg0), arrow.core.ListK(arg1), arrow.core.ListK(arg2), arrow.core.ListK(arg3), arrow.core.ListK(arg4), arrow.core.ListK(arg5))
    as kotlin.collections.List<arrow.core.Tuple6<A, B, C, D, E, FF>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B, C, D, E, FF> tupledN(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: List<FF>
): List<Tuple6<A, B, C, D, E, FF>> = arrow.core.extensions.list.apply.List
   .apply()
   .tupledN<A, B, C, D, E,
    FF>(arrow.core.ListK(arg0), arrow.core.ListK(arg1), arrow.core.ListK(arg2), arrow.core.ListK(arg3), arrow.core.ListK(arg4), arrow.core.ListK(arg5))
    as kotlin.collections.List<arrow.core.Tuple6<A, B, C, D, E, FF>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B, C, D, E, FF, G> tupled(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: List<FF>,
  arg6: List<G>
): List<Tuple7<A, B, C, D, E, FF, G>> = arrow.core.extensions.list.apply.List
   .apply()
   .tupled<A, B, C, D, E, FF,
    G>(arrow.core.ListK(arg0), arrow.core.ListK(arg1), arrow.core.ListK(arg2), arrow.core.ListK(arg3), arrow.core.ListK(arg4), arrow.core.ListK(arg5), arrow.core.ListK(arg6))
    as kotlin.collections.List<arrow.core.Tuple7<A, B, C, D, E, FF, G>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B, C, D, E, FF, G> tupledN(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: List<FF>,
  arg6: List<G>
): List<Tuple7<A, B, C, D, E, FF, G>> = arrow.core.extensions.list.apply.List
   .apply()
   .tupledN<A, B, C, D, E, FF,
    G>(arrow.core.ListK(arg0), arrow.core.ListK(arg1), arrow.core.ListK(arg2), arrow.core.ListK(arg3), arrow.core.ListK(arg4), arrow.core.ListK(arg5), arrow.core.ListK(arg6))
    as kotlin.collections.List<arrow.core.Tuple7<A, B, C, D, E, FF, G>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B, C, D, E, FF, G, H> tupled(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: List<FF>,
  arg6: List<G>,
  arg7: List<H>
): List<Tuple8<A, B, C, D, E, FF, G, H>> = arrow.core.extensions.list.apply.List
   .apply()
   .tupled<A, B, C, D, E, FF, G,
    H>(arrow.core.ListK(arg0), arrow.core.ListK(arg1), arrow.core.ListK(arg2), arrow.core.ListK(arg3), arrow.core.ListK(arg4), arrow.core.ListK(arg5), arrow.core.ListK(arg6), arrow.core.ListK(arg7))
    as kotlin.collections.List<arrow.core.Tuple8<A, B, C, D, E, FF, G, H>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B, C, D, E, FF, G, H> tupledN(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: List<FF>,
  arg6: List<G>,
  arg7: List<H>
): List<Tuple8<A, B, C, D, E, FF, G, H>> = arrow.core.extensions.list.apply.List
   .apply()
   .tupledN<A, B, C, D, E, FF, G,
    H>(arrow.core.ListK(arg0), arrow.core.ListK(arg1), arrow.core.ListK(arg2), arrow.core.ListK(arg3), arrow.core.ListK(arg4), arrow.core.ListK(arg5), arrow.core.ListK(arg6), arrow.core.ListK(arg7))
    as kotlin.collections.List<arrow.core.Tuple8<A, B, C, D, E, FF, G, H>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B, C, D, E, FF, G, H, I> tupled(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: List<FF>,
  arg6: List<G>,
  arg7: List<H>,
  arg8: List<I>
): List<Tuple9<A, B, C, D, E, FF, G, H, I>> = arrow.core.extensions.list.apply.List
   .apply()
   .tupled<A, B, C, D, E, FF, G, H,
    I>(arrow.core.ListK(arg0), arrow.core.ListK(arg1), arrow.core.ListK(arg2), arrow.core.ListK(arg3), arrow.core.ListK(arg4), arrow.core.ListK(arg5), arrow.core.ListK(arg6), arrow.core.ListK(arg7), arrow.core.ListK(arg8))
    as kotlin.collections.List<arrow.core.Tuple9<A, B, C, D, E, FF, G, H, I>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B, C, D, E, FF, G, H, I> tupledN(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: List<FF>,
  arg6: List<G>,
  arg7: List<H>,
  arg8: List<I>
): List<Tuple9<A, B, C, D, E, FF, G, H, I>> = arrow.core.extensions.list.apply.List
   .apply()
   .tupledN<A, B, C, D, E, FF, G, H,
    I>(arrow.core.ListK(arg0), arrow.core.ListK(arg1), arrow.core.ListK(arg2), arrow.core.ListK(arg3), arrow.core.ListK(arg4), arrow.core.ListK(arg5), arrow.core.ListK(arg6), arrow.core.ListK(arg7), arrow.core.ListK(arg8))
    as kotlin.collections.List<arrow.core.Tuple9<A, B, C, D, E, FF, G, H, I>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B, C, D, E, FF, G, H, I, J> tupled(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: List<FF>,
  arg6: List<G>,
  arg7: List<H>,
  arg8: List<I>,
  arg9: List<J>
): List<Tuple10<A, B, C, D, E, FF, G, H, I, J>> = arrow.core.extensions.list.apply.List
   .apply()
   .tupled<A, B, C, D, E, FF, G, H, I,
    J>(arrow.core.ListK(arg0), arrow.core.ListK(arg1), arrow.core.ListK(arg2), arrow.core.ListK(arg3), arrow.core.ListK(arg4), arrow.core.ListK(arg5), arrow.core.ListK(arg6), arrow.core.ListK(arg7), arrow.core.ListK(arg8), arrow.core.ListK(arg9))
    as kotlin.collections.List<arrow.core.Tuple10<A, B, C, D, E, FF, G, H, I, J>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B, C, D, E, FF, G, H, I, J> tupledN(
  arg0: List<A>,
  arg1: List<B>,
  arg2: List<C>,
  arg3: List<D>,
  arg4: List<E>,
  arg5: List<FF>,
  arg6: List<G>,
  arg7: List<H>,
  arg8: List<I>,
  arg9: List<J>
): List<Tuple10<A, B, C, D, E, FF, G, H, I, J>> = arrow.core.extensions.list.apply.List
   .apply()
   .tupledN<A, B, C, D, E, FF, G, H, I,
    J>(arrow.core.ListK(arg0), arrow.core.ListK(arg1), arrow.core.ListK(arg2), arrow.core.ListK(arg3), arrow.core.ListK(arg4), arrow.core.ListK(arg5), arrow.core.ListK(arg6), arrow.core.ListK(arg7), arrow.core.ListK(arg8), arrow.core.ListK(arg9))
    as kotlin.collections.List<arrow.core.Tuple10<A, B, C, D, E, FF, G, H, I, J>>

@JvmName("followedBy")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B> List<A>.followedBy(arg1: List<B>): List<B> =
    arrow.core.extensions.list.apply.List.apply().run {
  arrow.core.ListK(this@followedBy).followedBy<A, B>(arrow.core.ListK(arg1)) as
    kotlin.collections.List<B>
}

@JvmName("apTap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B> List<A>.apTap(arg1: List<B>): List<A> =
    arrow.core.extensions.list.apply.List.apply().run {
  arrow.core.ListK(this@apTap).apTap<A, B>(arrow.core.ListK(arg1)) as kotlin.collections.List<A>
}

/**
 * cached extension
 */
@PublishedApi()
internal val apply_singleton: ListKApply = object : arrow.core.extensions.ListKApply {}

object List {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  inline fun apply(): ListKApply = apply_singleton}
