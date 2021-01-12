package arrow.core.extensions.tuple7.order

import arrow.core.Tuple2
import arrow.core.Tuple7
import arrow.core.Tuple7.Companion
import arrow.core.extensions.Tuple7Order
import arrow.typeclasses.Order
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.Int
import kotlin.Suppress
import kotlin.jvm.JvmName

@JvmName("compareTo")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "compareTo(OA, OB, OC, OD, OE, OF, OG, arg1)",
  "arrow.core.compareTo"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, F, G> Tuple7<A, B, C, D, E, F, G>.compareTo(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  OF: Order<F>,
  OG: Order<G>,
  arg1: Tuple7<A, B, C, D, E, F, G>
): Int = arrow.core.Tuple7.order<A, B, C, D, E, F, G>(OA, OB, OC, OD, OE, OF, OG).run {
  this@compareTo.compareTo(arg1) as kotlin.Int
}

@JvmName("eqv")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "eqv(OA, OB, OC, OD, OE, OF, OG, arg1)",
  "arrow.core.eqv"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, F, G> Tuple7<A, B, C, D, E, F, G>.eqv(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  OF: Order<F>,
  OG: Order<G>,
  arg1: Tuple7<A, B, C, D, E, F, G>
): Boolean = arrow.core.Tuple7.order<A, B, C, D, E, F, G>(OA, OB, OC, OD, OE, OF, OG).run {
  this@eqv.eqv(arg1) as kotlin.Boolean
}

@JvmName("lt")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "lt(OA, OB, OC, OD, OE, OF, OG, arg1)",
  "arrow.core.lt"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, F, G> Tuple7<A, B, C, D, E, F, G>.lt(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  OF: Order<F>,
  OG: Order<G>,
  arg1: Tuple7<A, B, C, D, E, F, G>
): Boolean = arrow.core.Tuple7.order<A, B, C, D, E, F, G>(OA, OB, OC, OD, OE, OF, OG).run {
  this@lt.lt(arg1) as kotlin.Boolean
}

@JvmName("lte")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "lte(OA, OB, OC, OD, OE, OF, OG, arg1)",
  "arrow.core.lte"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, F, G> Tuple7<A, B, C, D, E, F, G>.lte(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  OF: Order<F>,
  OG: Order<G>,
  arg1: Tuple7<A, B, C, D, E, F, G>
): Boolean = arrow.core.Tuple7.order<A, B, C, D, E, F, G>(OA, OB, OC, OD, OE, OF, OG).run {
  this@lte.lte(arg1) as kotlin.Boolean
}

@JvmName("gt")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "gt(OA, OB, OC, OD, OE, OF, OG, arg1)",
  "arrow.core.gt"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, F, G> Tuple7<A, B, C, D, E, F, G>.gt(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  OF: Order<F>,
  OG: Order<G>,
  arg1: Tuple7<A, B, C, D, E, F, G>
): Boolean = arrow.core.Tuple7.order<A, B, C, D, E, F, G>(OA, OB, OC, OD, OE, OF, OG).run {
  this@gt.gt(arg1) as kotlin.Boolean
}

@JvmName("gte")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "gte(OA, OB, OC, OD, OE, OF, OG, arg1)",
  "arrow.core.gte"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, F, G> Tuple7<A, B, C, D, E, F, G>.gte(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  OF: Order<F>,
  OG: Order<G>,
  arg1: Tuple7<A, B, C, D, E, F, G>
): Boolean = arrow.core.Tuple7.order<A, B, C, D, E, F, G>(OA, OB, OC, OD, OE, OF, OG).run {
  this@gte.gte(arg1) as kotlin.Boolean
}

@JvmName("max")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "max(OA, OB, OC, OD, OE, OF, OG, arg1)",
  "arrow.core.max"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, F, G> Tuple7<A, B, C, D, E, F, G>.max(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  OF: Order<F>,
  OG: Order<G>,
  arg1: Tuple7<A, B, C, D, E, F, G>
): Tuple7<A, B, C, D, E, F, G> = arrow.core.Tuple7.order<A, B, C, D, E, F,
    G>(OA, OB, OC, OD, OE, OF, OG).run {
  this@max.max(arg1) as arrow.core.Tuple7<A, B, C, D, E, F, G>
}

@JvmName("min")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "min(OA, OB, OC, OD, OE, OF, OG, arg1)",
  "arrow.core.min"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, F, G> Tuple7<A, B, C, D, E, F, G>.min(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  OF: Order<F>,
  OG: Order<G>,
  arg1: Tuple7<A, B, C, D, E, F, G>
): Tuple7<A, B, C, D, E, F, G> = arrow.core.Tuple7.order<A, B, C, D, E, F,
    G>(OA, OB, OC, OD, OE, OF, OG).run {
  this@min.min(arg1) as arrow.core.Tuple7<A, B, C, D, E, F, G>
}

@JvmName("sort")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "sort(OA, OB, OC, OD, OE, OF, OG, arg1)",
  "arrow.core.sort"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, F, G> Tuple7<A, B, C, D, E, F, G>.sort(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  OF: Order<F>,
  OG: Order<G>,
  arg1: Tuple7<A, B, C, D, E, F, G>
): Tuple2<Tuple7<A, B, C, D, E, F, G>, Tuple7<A, B, C, D, E, F, G>> = arrow.core.Tuple7.order<A, B,
    C, D, E, F, G>(OA, OB, OC, OD, OE, OF, OG).run {
  this@sort.sort(arg1) as arrow.core.Tuple2<arrow.core.Tuple7<A, B, C, D, E, F, G>,
    arrow.core.Tuple7<A, B, C, D, E, F, G>>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <A, B, C, D, E, F, G> Companion.order(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  OF: Order<F>,
  OG: Order<G>
): Tuple7Order<A, B, C, D, E, F, G> = object : arrow.core.extensions.Tuple7Order<A, B, C, D, E, F,
    G> { override fun OA(): arrow.typeclasses.Order<A> = OA

  override fun OB(): arrow.typeclasses.Order<B> = OB

  override fun OC(): arrow.typeclasses.Order<C> = OC

  override fun OD(): arrow.typeclasses.Order<D> = OD

  override fun OE(): arrow.typeclasses.Order<E> = OE

  override fun OF(): arrow.typeclasses.Order<F> = OF

  override fun OG(): arrow.typeclasses.Order<G> = OG }