package arrow.core.extensions.ior.order

import arrow.core.Ior
import arrow.core.Ior.Companion
import arrow.core.Tuple2
import arrow.core.extensions.IorOrder
import arrow.typeclasses.Order

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
  "this.compareTo(OL, OR, arg1)",
  "arrow.core.compareTo"
  ),
  DeprecationLevel.WARNING
)
fun <L, R> Ior<L, R>.compareTo(
  OL: Order<L>,
  OR: Order<R>,
  arg1: Ior<L, R>
): Int = arrow.core.Ior.order<L, R>(OL, OR).run {
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
  "this.eqv(OL, OR, arg1)",
  "arrow.core.eqv"
  ),
  DeprecationLevel.WARNING
)
fun <L, R> Ior<L, R>.eqv(
  OL: Order<L>,
  OR: Order<R>,
  arg1: Ior<L, R>
): Boolean = arrow.core.Ior.order<L, R>(OL, OR).run {
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
  "this.lt(OL, OR, arg1)",
  "arrow.core.lt"
  ),
  DeprecationLevel.WARNING
)
fun <L, R> Ior<L, R>.lt(
  OL: Order<L>,
  OR: Order<R>,
  arg1: Ior<L, R>
): Boolean = arrow.core.Ior.order<L, R>(OL, OR).run {
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
  "this.lte(OL, OR, arg1)",
  "arrow.core.lte"
  ),
  DeprecationLevel.WARNING
)
fun <L, R> Ior<L, R>.lte(
  OL: Order<L>,
  OR: Order<R>,
  arg1: Ior<L, R>
): Boolean = arrow.core.Ior.order<L, R>(OL, OR).run {
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
  "this.gt(OL, OR, arg1)",
  "arrow.core.gt"
  ),
  DeprecationLevel.WARNING
)
fun <L, R> Ior<L, R>.gt(
  OL: Order<L>,
  OR: Order<R>,
  arg1: Ior<L, R>
): Boolean = arrow.core.Ior.order<L, R>(OL, OR).run {
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
  "this.gte(OL, OR, arg1)",
  "arrow.core.gte"
  ),
  DeprecationLevel.WARNING
)
fun <L, R> Ior<L, R>.gte(
  OL: Order<L>,
  OR: Order<R>,
  arg1: Ior<L, R>
): Boolean = arrow.core.Ior.order<L, R>(OL, OR).run {
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
  "this.max(OL, OR, arg1)",
  "arrow.core.max"
  ),
  DeprecationLevel.WARNING
)
fun <L, R> Ior<L, R>.max(
  OL: Order<L>,
  OR: Order<R>,
  arg1: Ior<L, R>
): Ior<L, R> = arrow.core.Ior.order<L, R>(OL, OR).run {
  this@max.max(arg1) as arrow.core.Ior<L, R>
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
  "this.min(OL, OR, arg1)",
  "arrow.core.min"
  ),
  DeprecationLevel.WARNING
)
fun <L, R> Ior<L, R>.min(
  OL: Order<L>,
  OR: Order<R>,
  arg1: Ior<L, R>
): Ior<L, R> = arrow.core.Ior.order<L, R>(OL, OR).run {
  this@min.min(arg1) as arrow.core.Ior<L, R>
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
  "this.sort(OL, OR, arg1)",
  "arrow.core.sort"
  ),
  DeprecationLevel.WARNING
)
fun <L, R> Ior<L, R>.sort(
  OL: Order<L>,
  OR: Order<R>,
  arg1: Ior<L, R>
): Tuple2<Ior<L, R>, Ior<L, R>> = arrow.core.Ior.order<L, R>(OL, OR).run {
  this@sort.sort(arg1) as arrow.core.Tuple2<arrow.core.Ior<L, R>, arrow.core.Ior<L, R>>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "Order.ior(OL, OR)",
    "arrow.core.ior", "arrow.typeclasses.Order"
  ),
  DeprecationLevel.WARNING
)
inline fun <L, R> Companion.order(OL: Order<L>, OR: Order<R>): IorOrder<L, R> = object :
    arrow.core.extensions.IorOrder<L, R> { override fun OL(): arrow.typeclasses.Order<L> = OL

  override fun OR(): arrow.typeclasses.Order<R> = OR }
