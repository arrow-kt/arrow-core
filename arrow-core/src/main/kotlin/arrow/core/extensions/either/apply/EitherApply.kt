package arrow.core.extensions.either.apply

import arrow.Kind
import arrow.core.Either
import arrow.core.Either.Companion
import arrow.core.Eval
import arrow.core.ForEither
import arrow.core.Tuple10
import arrow.core.Tuple2
import arrow.core.Tuple3
import arrow.core.Tuple4
import arrow.core.Tuple5
import arrow.core.Tuple6
import arrow.core.Tuple7
import arrow.core.Tuple8
import arrow.core.Tuple9
import arrow.core.extensions.EitherApply
import kotlin.Any
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val apply_singleton: EitherApply<Any?> = object : EitherApply<Any?> {}

@JvmName("ap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B> Kind<Kind<ForEither, L>, A>.ap(arg1: Kind<Kind<ForEither, L>, Function1<A, B>>):
  Either<L, B> = arrow.core.Either.apply<L>().run {
  this@EitherApply.mapN<A, (A) -> B, B>(this@ap, arg1) { (a, f) -> f(a) } as arrow.core.Either<L, B>
}

@JvmName("apEval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B> Kind<Kind<ForEither, L>, A>.apEval(
  arg1: Eval<Kind<Kind<ForEither, L>, Function1<A,
    B>>>
): Eval<Kind<Kind<ForEither, L>, B>> = arrow.core.Either.apply<L>().run {
  this@apEval.map2Eval(arg1) { (a, f) -> f(a) } as arrow.core.Eval<arrow.Kind<arrow.Kind<arrow.core.ForEither, L>,
    B>>
}

@JvmName("map2Eval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B, Z> Kind<Kind<ForEither, L>, A>.map2Eval(
  arg1: Eval<Kind<Kind<ForEither, L>, B>>,
  arg2: Function1<Tuple2<A, B>, Z>
): Eval<Kind<Kind<ForEither, L>, Z>> =
  arrow.core.Either.apply<L>().run {
    this@map2Eval.map2Eval<A, B, Z>(arg1, arg2) as
      arrow.core.Eval<arrow.Kind<arrow.Kind<arrow.core.ForEither, L>, Z>>
  }

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B, Z> map(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Function1<Tuple2<A, B>, Z>
): Either<L, Z> = arrow.core.Either
  .apply<L>()
  .mapN(arg0, arg1, arg2) as arrow.core.Either<L, Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B, Z> mapN(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Function1<Tuple2<A, B>, Z>
): Either<L, Z> = arrow.core.Either
  .apply<L>()
  .mapN<A, B, Z>(arg0, arg1, arg2) as arrow.core.Either<L, Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B, C, Z> map(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Function1<Tuple3<A, B, C>, Z>
): Either<L, Z> = arrow.core.Either
  .apply<L>()
  .mapN(arg0, arg1, arg2, arg3) as arrow.core.Either<L, Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B, C, Z> mapN(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Function1<Tuple3<A, B, C>, Z>
): Either<L, Z> = arrow.core.Either
  .apply<L>()
  .mapN<A, B, C, Z>(arg0, arg1, arg2, arg3) as arrow.core.Either<L, Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B, C, D, Z> map(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Kind<Kind<ForEither, L>, D>,
  arg4: Function1<Tuple4<A, B, C, D>, Z>
): Either<L, Z> = arrow.core.Either
  .apply<L>()
  .mapN(arg0, arg1, arg2, arg3, arg4) as arrow.core.Either<L, Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B, C, D, Z> mapN(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Kind<Kind<ForEither, L>, D>,
  arg4: Function1<Tuple4<A, B, C, D>, Z>
): Either<L, Z> = arrow.core.Either
  .apply<L>()
  .mapN<A, B, C, D, Z>(arg0, arg1, arg2, arg3, arg4) as arrow.core.Either<L, Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B, C, D, E, Z> map(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Kind<Kind<ForEither, L>, D>,
  arg4: Kind<Kind<ForEither, L>, E>,
  arg5: Function1<Tuple5<A, B, C, D, E>, Z>
): Either<L, Z> = arrow.core.Either
  .apply<L>()
  .mapN(arg0, arg1, arg2, arg3, arg4, arg5) as arrow.core.Either<L, Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B, C, D, E, Z> mapN(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Kind<Kind<ForEither, L>, D>,
  arg4: Kind<Kind<ForEither, L>, E>,
  arg5: Function1<Tuple5<A, B, C, D, E>, Z>
): Either<L, Z> = arrow.core.Either
  .apply<L>()
  .mapN<A, B, C, D, E, Z>(arg0, arg1, arg2, arg3, arg4, arg5) as arrow.core.Either<L, Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B, C, D, E, FF, Z> map(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Kind<Kind<ForEither, L>, D>,
  arg4: Kind<Kind<ForEither, L>, E>,
  arg5: Kind<Kind<ForEither, L>, FF>,
  arg6: Function1<Tuple6<A, B, C, D, E, FF>, Z>
): Either<L, Z> = arrow.core.Either
  .apply<L>()
  .mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as arrow.core.Either<L, Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B, C, D, E, FF, Z> mapN(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Kind<Kind<ForEither, L>, D>,
  arg4: Kind<Kind<ForEither, L>, E>,
  arg5: Kind<Kind<ForEither, L>, FF>,
  arg6: Function1<Tuple6<A, B, C, D, E, FF>, Z>
): Either<L, Z> = arrow.core.Either
  .apply<L>()
  .mapN<A, B, C, D, E, FF, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as arrow.core.Either<L, Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B, C, D, E, FF, G, Z> map(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Kind<Kind<ForEither, L>, D>,
  arg4: Kind<Kind<ForEither, L>, E>,
  arg5: Kind<Kind<ForEither, L>, FF>,
  arg6: Kind<Kind<ForEither, L>, G>,
  arg7: Function1<Tuple7<A, B, C, D, E, FF, G>, Z>
): Either<L, Z> = arrow.core.Either
  .apply<L>()
  .mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as
  arrow.core.Either<L, Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B, C, D, E, FF, G, Z> mapN(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Kind<Kind<ForEither, L>, D>,
  arg4: Kind<Kind<ForEither, L>, E>,
  arg5: Kind<Kind<ForEither, L>, FF>,
  arg6: Kind<Kind<ForEither, L>, G>,
  arg7: Function1<Tuple7<A, B, C, D, E, FF, G>, Z>
): Either<L, Z> = arrow.core.Either
  .apply<L>()
  .mapN<A, B, C, D, E, FF, G, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as
  arrow.core.Either<L, Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B, C, D, E, FF, G, H, Z> map(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Kind<Kind<ForEither, L>, D>,
  arg4: Kind<Kind<ForEither, L>, E>,
  arg5: Kind<Kind<ForEither, L>, FF>,
  arg6: Kind<Kind<ForEither, L>, G>,
  arg7: Kind<Kind<ForEither, L>, H>,
  arg8: Function1<Tuple8<A, B, C, D, E, FF, G, H>, Z>
): Either<L, Z> = arrow.core.Either
  .apply<L>()
  .mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
  arrow.core.Either<L, Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B, C, D, E, FF, G, H, Z> mapN(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Kind<Kind<ForEither, L>, D>,
  arg4: Kind<Kind<ForEither, L>, E>,
  arg5: Kind<Kind<ForEither, L>, FF>,
  arg6: Kind<Kind<ForEither, L>, G>,
  arg7: Kind<Kind<ForEither, L>, H>,
  arg8: Function1<Tuple8<A, B, C, D, E, FF, G, H>, Z>
): Either<L, Z> = arrow.core.Either
  .apply<L>()
  .mapN<A, B, C, D, E, FF, G, H, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
  arrow.core.Either<L, Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B, C, D, E, FF, G, H, I, Z> map(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Kind<Kind<ForEither, L>, D>,
  arg4: Kind<Kind<ForEither, L>, E>,
  arg5: Kind<Kind<ForEither, L>, FF>,
  arg6: Kind<Kind<ForEither, L>, G>,
  arg7: Kind<Kind<ForEither, L>, H>,
  arg8: Kind<Kind<ForEither, L>, I>,
  arg9: Function1<Tuple9<A, B, C, D, E, FF, G, H, I>, Z>
): Either<L, Z> = arrow.core.Either
  .apply<L>()
  .mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)
  as arrow.core.Either<L, Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B, C, D, E, FF, G, H, I, Z> mapN(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Kind<Kind<ForEither, L>, D>,
  arg4: Kind<Kind<ForEither, L>, E>,
  arg5: Kind<Kind<ForEither, L>, FF>,
  arg6: Kind<Kind<ForEither, L>, G>,
  arg7: Kind<Kind<ForEither, L>, H>,
  arg8: Kind<Kind<ForEither, L>, I>,
  arg9: Function1<Tuple9<A, B, C, D, E, FF, G, H, I>, Z>
): Either<L, Z> = arrow.core.Either
  .apply<L>()
  .mapN<A, B, C, D, E, FF, G, H, I, Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9)
  as arrow.core.Either<L, Z>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B, C, D, E, FF, G, H, I, J, Z> map(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Kind<Kind<ForEither, L>, D>,
  arg4: Kind<Kind<ForEither, L>, E>,
  arg5: Kind<Kind<ForEither, L>, FF>,
  arg6: Kind<Kind<ForEither, L>, G>,
  arg7: Kind<Kind<ForEither, L>, H>,
  arg8: Kind<Kind<ForEither, L>, I>,
  arg9: Kind<Kind<ForEither, L>, J>,
  arg10: Function1<Tuple10<A, B, C, D, E, FF, G, H, I, J>, Z>
): Either<L, Z> = arrow.core.Either
  .apply<L>()
  .mapN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10) as arrow.core.Either<L, Z>

@JvmName("mapN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B, C, D, E, FF, G, H, I, J, Z> mapN(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Kind<Kind<ForEither, L>, D>,
  arg4: Kind<Kind<ForEither, L>, E>,
  arg5: Kind<Kind<ForEither, L>, FF>,
  arg6: Kind<Kind<ForEither, L>, G>,
  arg7: Kind<Kind<ForEither, L>, H>,
  arg8: Kind<Kind<ForEither, L>, I>,
  arg9: Kind<Kind<ForEither, L>, J>,
  arg10: Function1<Tuple10<A, B, C, D, E, FF, G, H, I, J>, Z>
): Either<L, Z> = arrow.core.Either
  .apply<L>()
  .mapN<A, B, C, D, E, FF, G, H, I, J,
    Z>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10) as arrow.core.Either<L, Z>

@JvmName("map2")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B, Z> Kind<Kind<ForEither, L>, A>.map2(
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Function1<Tuple2<A, B>, Z>
): Either<L, Z> = arrow.core.Either.apply<L>().run {
  this@map2.map2<A, B, Z>(arg1, arg2) as arrow.core.Either<L, Z>
}

@JvmName("product")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B> Kind<Kind<ForEither, L>, A>.product(arg1: Kind<Kind<ForEither, L>, B>): Either<L,
  Tuple2<A, B>> = arrow.core.Either.apply<L>().run {
  this@product.product<A, B>(arg1) as arrow.core.Either<L, arrow.core.Tuple2<A, B>>
}

@JvmName("product1")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B, Z> Kind<Kind<ForEither, L>, Tuple2<A, B>>.product(arg1: Kind<Kind<ForEither, L>, Z>):
  Either<L, Tuple3<A, B, Z>> = arrow.core.Either.apply<L>().run {
  this@product.product<A, B, Z>(arg1) as arrow.core.Either<L, arrow.core.Tuple3<A, B, Z>>
}

@JvmName("product2")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B, C, Z> Kind<Kind<ForEither, L>, Tuple3<A, B, C>>.product(
  arg1: Kind<Kind<ForEither, L>,
    Z>
): Either<L, Tuple4<A, B, C, Z>> = arrow.core.Either.apply<L>().run {
  this@product.product<A, B, C, Z>(arg1) as arrow.core.Either<L, arrow.core.Tuple4<A, B, C, Z>>
}

@JvmName("product3")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B, C, D, Z> Kind<Kind<ForEither, L>, Tuple4<A, B, C,
  D>>.product(arg1: Kind<Kind<ForEither, L>, Z>): Either<L, Tuple5<A, B, C, D, Z>> =
  arrow.core.Either.apply<L>().run {
    this@product.product<A, B, C, D, Z>(arg1) as arrow.core.Either<L, arrow.core.Tuple5<A, B, C, D,
      Z>>
  }

@JvmName("product4")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B, C, D, E, Z> Kind<Kind<ForEither, L>, Tuple5<A, B, C, D,
  E>>.product(arg1: Kind<Kind<ForEither, L>, Z>): Either<L, Tuple6<A, B, C, D, E, Z>> =
  arrow.core.Either.apply<L>().run {
    this@product.product<A, B, C, D, E, Z>(arg1) as arrow.core.Either<L, arrow.core.Tuple6<A, B, C, D,
      E, Z>>
  }

@JvmName("product5")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B, C, D, E, FF, Z> Kind<Kind<ForEither, L>, Tuple6<A, B, C, D, E,
  FF>>.product(arg1: Kind<Kind<ForEither, L>, Z>): Either<L, Tuple7<A, B, C, D, E, FF, Z>> =
  arrow.core.Either.apply<L>().run {
    this@product.product<A, B, C, D, E, FF, Z>(arg1) as arrow.core.Either<L, arrow.core.Tuple7<A, B,
      C, D, E, FF, Z>>
  }

@JvmName("product6")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B, C, D, E, FF, G, Z> Kind<Kind<ForEither, L>, Tuple7<A, B, C, D, E, FF,
  G>>.product(arg1: Kind<Kind<ForEither, L>, Z>): Either<L, Tuple8<A, B, C, D, E, FF, G, Z>> =
  arrow.core.Either.apply<L>().run {
    this@product.product<A, B, C, D, E, FF, G, Z>(arg1) as arrow.core.Either<L, arrow.core.Tuple8<A,
      B, C, D, E, FF, G, Z>>
  }

@JvmName("product7")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B, C, D, E, FF, G, H, Z> Kind<Kind<ForEither, L>, Tuple8<A, B, C, D, E, FF, G,
  H>>.product(arg1: Kind<Kind<ForEither, L>, Z>): Either<L, Tuple9<A, B, C, D, E, FF, G, H, Z>> =
  arrow.core.Either.apply<L>().run {
    this@product.product<A, B, C, D, E, FF, G, H, Z>(arg1) as arrow.core.Either<L,
      arrow.core.Tuple9<A, B, C, D, E, FF, G, H, Z>>
  }

@JvmName("product8")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B, C, D, E, FF, G, H, I, Z> Kind<Kind<ForEither, L>, Tuple9<A, B, C, D, E, FF, G, H,
  I>>.product(arg1: Kind<Kind<ForEither, L>, Z>): Either<L, Tuple10<A, B, C, D, E, FF, G, H, I,
  Z>> = arrow.core.Either.apply<L>().run {
  this@product.product<A, B, C, D, E, FF, G, H, I, Z>(arg1) as arrow.core.Either<L,
    arrow.core.Tuple10<A, B, C, D, E, FF, G, H, I, Z>>
}

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B> tupled(arg0: Kind<Kind<ForEither, L>, A>, arg1: Kind<Kind<ForEither, L>, B>):
  Either<L, Tuple2<A, B>> = arrow.core.Either
  .apply<L>()
  .tupledN(arg0, arg1) as arrow.core.Either<L, arrow.core.Tuple2<A, B>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B> tupledN(arg0: Kind<Kind<ForEither, L>, A>, arg1: Kind<Kind<ForEither, L>, B>):
  Either<L, Tuple2<A, B>> = arrow.core.Either
  .apply<L>()
  .tupledN<A, B>(arg0, arg1) as arrow.core.Either<L, arrow.core.Tuple2<A, B>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B, C> tupled(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>
): Either<L, Tuple3<A, B, C>> = arrow.core.Either
  .apply<L>()
  .tupledN(arg0, arg1, arg2) as arrow.core.Either<L, arrow.core.Tuple3<A, B, C>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B, C> tupledN(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>
): Either<L, Tuple3<A, B, C>> = arrow.core.Either
  .apply<L>()
  .tupledN<A, B, C>(arg0, arg1, arg2) as arrow.core.Either<L, arrow.core.Tuple3<A, B, C>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B, C, D> tupled(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Kind<Kind<ForEither, L>, D>
): Either<L, Tuple4<A, B, C, D>> = arrow.core.Either
  .apply<L>()
  .tupledN(arg0, arg1, arg2, arg3) as arrow.core.Either<L, arrow.core.Tuple4<A, B, C,
  D>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B, C, D> tupledN(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Kind<Kind<ForEither, L>, D>
): Either<L, Tuple4<A, B, C, D>> = arrow.core.Either
  .apply<L>()
  .tupledN<A, B, C, D>(arg0, arg1, arg2, arg3) as arrow.core.Either<L, arrow.core.Tuple4<A, B, C,
  D>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B, C, D, E> tupled(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Kind<Kind<ForEither, L>, D>,
  arg4: Kind<Kind<ForEither, L>, E>
): Either<L, Tuple5<A, B, C, D, E>> = arrow.core.Either
  .apply<L>()
  .tupledN(arg0, arg1, arg2, arg3, arg4) as arrow.core.Either<L, arrow.core.Tuple5<A,
  B, C, D, E>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B, C, D, E> tupledN(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Kind<Kind<ForEither, L>, D>,
  arg4: Kind<Kind<ForEither, L>, E>
): Either<L, Tuple5<A, B, C, D, E>> = arrow.core.Either
  .apply<L>()
  .tupledN<A, B, C, D, E>(arg0, arg1, arg2, arg3, arg4) as arrow.core.Either<L,
  arrow.core.Tuple5<A, B, C, D, E>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B, C, D, E, FF> tupled(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Kind<Kind<ForEither, L>, D>,
  arg4: Kind<Kind<ForEither, L>, E>,
  arg5: Kind<Kind<ForEither, L>, FF>
): Either<L, Tuple6<A, B, C, D, E, FF>> = arrow.core.Either
  .apply<L>()
  .tupledN(arg0, arg1, arg2, arg3, arg4, arg5) as arrow.core.Either<L,
  arrow.core.Tuple6<A, B, C, D, E, FF>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B, C, D, E, FF> tupledN(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Kind<Kind<ForEither, L>, D>,
  arg4: Kind<Kind<ForEither, L>, E>,
  arg5: Kind<Kind<ForEither, L>, FF>
): Either<L, Tuple6<A, B, C, D, E, FF>> = arrow.core.Either
  .apply<L>()
  .tupledN<A, B, C, D, E, FF>(arg0, arg1, arg2, arg3, arg4, arg5) as arrow.core.Either<L,
  arrow.core.Tuple6<A, B, C, D, E, FF>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B, C, D, E, FF, G> tupled(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Kind<Kind<ForEither, L>, D>,
  arg4: Kind<Kind<ForEither, L>, E>,
  arg5: Kind<Kind<ForEither, L>, FF>,
  arg6: Kind<Kind<ForEither, L>, G>
): Either<L, Tuple7<A, B, C, D, E, FF, G>> = arrow.core.Either
  .apply<L>()
  .tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as arrow.core.Either<L,
  arrow.core.Tuple7<A, B, C, D, E, FF, G>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B, C, D, E, FF, G> tupledN(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Kind<Kind<ForEither, L>, D>,
  arg4: Kind<Kind<ForEither, L>, E>,
  arg5: Kind<Kind<ForEither, L>, FF>,
  arg6: Kind<Kind<ForEither, L>, G>
): Either<L, Tuple7<A, B, C, D, E, FF, G>> = arrow.core.Either
  .apply<L>()
  .tupledN<A, B, C, D, E, FF, G>(arg0, arg1, arg2, arg3, arg4, arg5, arg6) as arrow.core.Either<L,
  arrow.core.Tuple7<A, B, C, D, E, FF, G>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B, C, D, E, FF, G, H> tupled(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Kind<Kind<ForEither, L>, D>,
  arg4: Kind<Kind<ForEither, L>, E>,
  arg5: Kind<Kind<ForEither, L>, FF>,
  arg6: Kind<Kind<ForEither, L>, G>,
  arg7: Kind<Kind<ForEither, L>, H>
): Either<L, Tuple8<A, B, C, D, E, FF, G, H>> = arrow.core.Either
  .apply<L>()
  .tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as
  arrow.core.Either<L, arrow.core.Tuple8<A, B, C, D, E, FF, G, H>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B, C, D, E, FF, G, H> tupledN(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Kind<Kind<ForEither, L>, D>,
  arg4: Kind<Kind<ForEither, L>, E>,
  arg5: Kind<Kind<ForEither, L>, FF>,
  arg6: Kind<Kind<ForEither, L>, G>,
  arg7: Kind<Kind<ForEither, L>, H>
): Either<L, Tuple8<A, B, C, D, E, FF, G, H>> = arrow.core.Either
  .apply<L>()
  .tupledN<A, B, C, D, E, FF, G, H>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7) as
  arrow.core.Either<L, arrow.core.Tuple8<A, B, C, D, E, FF, G, H>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B, C, D, E, FF, G, H, I> tupled(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Kind<Kind<ForEither, L>, D>,
  arg4: Kind<Kind<ForEither, L>, E>,
  arg5: Kind<Kind<ForEither, L>, FF>,
  arg6: Kind<Kind<ForEither, L>, G>,
  arg7: Kind<Kind<ForEither, L>, H>,
  arg8: Kind<Kind<ForEither, L>, I>
): Either<L, Tuple9<A, B, C, D, E, FF, G, H, I>> = arrow.core.Either
  .apply<L>()
  .tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
  arrow.core.Either<L, arrow.core.Tuple9<A, B, C, D, E, FF, G, H, I>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B, C, D, E, FF, G, H, I> tupledN(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Kind<Kind<ForEither, L>, D>,
  arg4: Kind<Kind<ForEither, L>, E>,
  arg5: Kind<Kind<ForEither, L>, FF>,
  arg6: Kind<Kind<ForEither, L>, G>,
  arg7: Kind<Kind<ForEither, L>, H>,
  arg8: Kind<Kind<ForEither, L>, I>
): Either<L, Tuple9<A, B, C, D, E, FF, G, H, I>> = arrow.core.Either
  .apply<L>()
  .tupledN<A, B, C, D, E, FF, G, H, I>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8) as
  arrow.core.Either<L, arrow.core.Tuple9<A, B, C, D, E, FF, G, H, I>>

@JvmName("tupled")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B, C, D, E, FF, G, H, I, J> tupled(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Kind<Kind<ForEither, L>, D>,
  arg4: Kind<Kind<ForEither, L>, E>,
  arg5: Kind<Kind<ForEither, L>, FF>,
  arg6: Kind<Kind<ForEither, L>, G>,
  arg7: Kind<Kind<ForEither, L>, H>,
  arg8: Kind<Kind<ForEither, L>, I>,
  arg9: Kind<Kind<ForEither, L>, J>
): Either<L, Tuple10<A, B, C, D, E, FF, G, H, I, J>> = arrow.core.Either
  .apply<L>()
  .tupledN(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9) as arrow.core.Either<L,
  arrow.core.Tuple10<A, B, C, D, E, FF, G, H, I, J>>

@JvmName("tupledN")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B, C, D, E, FF, G, H, I, J> tupledN(
  arg0: Kind<Kind<ForEither, L>, A>,
  arg1: Kind<Kind<ForEither, L>, B>,
  arg2: Kind<Kind<ForEither, L>, C>,
  arg3: Kind<Kind<ForEither, L>, D>,
  arg4: Kind<Kind<ForEither, L>, E>,
  arg5: Kind<Kind<ForEither, L>, FF>,
  arg6: Kind<Kind<ForEither, L>, G>,
  arg7: Kind<Kind<ForEither, L>, H>,
  arg8: Kind<Kind<ForEither, L>, I>,
  arg9: Kind<Kind<ForEither, L>, J>
): Either<L, Tuple10<A, B, C, D, E, FF, G, H, I, J>> = arrow.core.Either
  .apply<L>()
  .tupledN<A, B, C, D, E, FF, G, H, I,
    J>(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9) as arrow.core.Either<L,
  arrow.core.Tuple10<A, B, C, D, E, FF, G, H, I, J>>

@JvmName("followedBy")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B> Kind<Kind<ForEither, L>, A>.followedBy(arg1: Kind<Kind<ForEither, L>, B>): Either<L,
  B> = arrow.core.Either.apply<L>().run {
  this@followedBy.followedBy<A, B>(arg1) as arrow.core.Either<L, B>
}

@JvmName("apTap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B> Kind<Kind<ForEither, L>, A>.apTap(arg1: Kind<Kind<ForEither, L>, B>): Either<L, A> =
  arrow.core.Either.apply<L>().run {
    this@apTap.apTap<A, B>(arg1) as arrow.core.Either<L, A>
  }

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <L> Companion.apply(): EitherApply<L> = apply_singleton as
  arrow.core.extensions.EitherApply<L>
