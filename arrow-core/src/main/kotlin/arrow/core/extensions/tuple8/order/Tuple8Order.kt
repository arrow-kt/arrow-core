package arrow.core.extensions.tuple8.order

import arrow.core.Tuple2
import arrow.core.Tuple8
import arrow.core.Tuple8.Companion
import arrow.core.extensions.Tuple8Order
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
  "compareTo(OA, OB, OC, OD, OE, OF, OG, OH, arg1)",
  "arrow.core.compareTo"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, F, G, H> Tuple8<A, B, C, D, E, F, G, H>.compareTo(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  OF: Order<F>,
  OG: Order<G>,
  OH: Order<H>,
  arg1: Tuple8<A, B, C, D, E, F, G, H>
): Int = arrow.core.Tuple8.order<A, B, C, D, E, F, G, H>(OA, OB, OC, OD, OE, OF, OG, OH).run {
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
  "eqv(OA, OB, OC, OD, OE, OF, OG, OH, arg1)",
  "arrow.core.eqv"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, F, G, H> Tuple8<A, B, C, D, E, F, G, H>.eqv(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  OF: Order<F>,
  OG: Order<G>,
  OH: Order<H>,
  arg1: Tuple8<A, B, C, D, E, F, G, H>
): Boolean = arrow.core.Tuple8.order<A, B, C, D, E, F, G, H>(OA, OB, OC, OD, OE, OF, OG, OH).run {
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
  "lt(OA, OB, OC, OD, OE, OF, OG, OH, arg1)",
  "arrow.core.lt"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, F, G, H> Tuple8<A, B, C, D, E, F, G, H>.lt(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  OF: Order<F>,
  OG: Order<G>,
  OH: Order<H>,
  arg1: Tuple8<A, B, C, D, E, F, G, H>
): Boolean = arrow.core.Tuple8.order<A, B, C, D, E, F, G, H>(OA, OB, OC, OD, OE, OF, OG, OH).run {
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
  "lte(OA, OB, OC, OD, OE, OF, OG, OH, arg1)",
  "arrow.core.lte"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, F, G, H> Tuple8<A, B, C, D, E, F, G, H>.lte(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  OF: Order<F>,
  OG: Order<G>,
  OH: Order<H>,
  arg1: Tuple8<A, B, C, D, E, F, G, H>
): Boolean = arrow.core.Tuple8.order<A, B, C, D, E, F, G, H>(OA, OB, OC, OD, OE, OF, OG, OH).run {
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
  "gt(OA, OB, OC, OD, OE, OF, OG, OH, arg1)",
  "arrow.core.gt"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, F, G, H> Tuple8<A, B, C, D, E, F, G, H>.gt(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  OF: Order<F>,
  OG: Order<G>,
  OH: Order<H>,
  arg1: Tuple8<A, B, C, D, E, F, G, H>
): Boolean = arrow.core.Tuple8.order<A, B, C, D, E, F, G, H>(OA, OB, OC, OD, OE, OF, OG, OH).run {
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
  "gte(OA, OB, OC, OD, OE, OF, OG, OH, arg1)",
  "arrow.core.gte"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, F, G, H> Tuple8<A, B, C, D, E, F, G, H>.gte(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  OF: Order<F>,
  OG: Order<G>,
  OH: Order<H>,
  arg1: Tuple8<A, B, C, D, E, F, G, H>
): Boolean = arrow.core.Tuple8.order<A, B, C, D, E, F, G, H>(OA, OB, OC, OD, OE, OF, OG, OH).run {
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
  "max(OA, OB, OC, OD, OE, OF, OG, OH, arg1)",
  "arrow.core.max"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, F, G, H> Tuple8<A, B, C, D, E, F, G, H>.max(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  OF: Order<F>,
  OG: Order<G>,
  OH: Order<H>,
  arg1: Tuple8<A, B, C, D, E, F, G, H>
): Tuple8<A, B, C, D, E, F, G, H> = arrow.core.Tuple8.order<A, B, C, D, E, F, G,
    H>(OA, OB, OC, OD, OE, OF, OG, OH).run {
  this@max.max(arg1) as arrow.core.Tuple8<A, B, C, D, E, F, G, H>
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
  "min(OA, OB, OC, OD, OE, OF, OG, OH, arg1)",
  "arrow.core.min"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, F, G, H> Tuple8<A, B, C, D, E, F, G, H>.min(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  OF: Order<F>,
  OG: Order<G>,
  OH: Order<H>,
  arg1: Tuple8<A, B, C, D, E, F, G, H>
): Tuple8<A, B, C, D, E, F, G, H> = arrow.core.Tuple8.order<A, B, C, D, E, F, G,
    H>(OA, OB, OC, OD, OE, OF, OG, OH).run {
  this@min.min(arg1) as arrow.core.Tuple8<A, B, C, D, E, F, G, H>
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
  "sort(OA, OB, OC, OD, OE, OF, OG, OH, arg1)",
  "arrow.core.sort"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, D, E, F, G, H> Tuple8<A, B, C, D, E, F, G, H>.sort(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  OF: Order<F>,
  OG: Order<G>,
  OH: Order<H>,
  arg1: Tuple8<A, B, C, D, E, F, G, H>
): Tuple2<Tuple8<A, B, C, D, E, F, G, H>, Tuple8<A, B, C, D, E, F, G, H>> =
    arrow.core.Tuple8.order<A, B, C, D, E, F, G, H>(OA, OB, OC, OD, OE, OF, OG, OH).run {
  this@sort.sort(arg1) as arrow.core.Tuple2<arrow.core.Tuple8<A, B, C, D, E, F, G, H>,
    arrow.core.Tuple8<A, B, C, D, E, F, G, H>>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <A, B, C, D, E, F, G, H> Companion.order(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  OF: Order<F>,
  OG: Order<G>,
  OH: Order<H>
): Tuple8Order<A, B, C, D, E, F, G, H> = object : arrow.core.extensions.Tuple8Order<A, B, C, D, E,
    F, G, H> { override fun OA(): arrow.typeclasses.Order<A> = OA

  override fun OB(): arrow.typeclasses.Order<B> = OB

  override fun OC(): arrow.typeclasses.Order<C> = OC

  override fun OD(): arrow.typeclasses.Order<D> = OD

  override fun OE(): arrow.typeclasses.Order<E> = OE

  override fun OF(): arrow.typeclasses.Order<F> = OF

  override fun OG(): arrow.typeclasses.Order<G> = OG

  override fun OH(): arrow.typeclasses.Order<H> = OH }