package arrow.core.extensions.ior.traverse

import arrow.Kind
import arrow.core.ForIor
import arrow.core.Ior
import arrow.core.Ior.Companion
import arrow.core.extensions.IorTraverse
import arrow.typeclasses.Applicative
import arrow.typeclasses.Monad
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
internal val traverse_singleton: IorTraverse<Any?> = object : IorTraverse<Any?> {}

@JvmName("traverse")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "traverse(arg1, arg2)",
  "arrow.core.traverse"
  ),
  DeprecationLevel.WARNING
)
fun <L, G, A, B> Kind<Kind<ForIor, L>, A>.traverse(arg1: Applicative<G>, arg2: Function1<A, Kind<G,
    B>>): Kind<G, Kind<Kind<ForIor, L>, B>> = arrow.core.Ior.traverse<L>().run {
  this@traverse.traverse<G, A, B>(arg1, arg2) as arrow.Kind<G,
    arrow.Kind<arrow.Kind<arrow.core.ForIor, L>, B>>
}

@JvmName("sequence")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "sequence(arg1)",
  "arrow.core.sequence"
  ),
  DeprecationLevel.WARNING
)
fun <L, G, A> Kind<Kind<ForIor, L>, Kind<G, A>>.sequence(arg1: Applicative<G>): Kind<G,
    Kind<Kind<ForIor, L>, A>> = arrow.core.Ior.traverse<L>().run {
  this@sequence.sequence<G, A>(arg1) as arrow.Kind<G, arrow.Kind<arrow.Kind<arrow.core.ForIor, L>,
    A>>
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
  "map(arg1)",
  "arrow.core.map"
  ),
  DeprecationLevel.WARNING
)
fun <L, A, B> Kind<Kind<ForIor, L>, A>.map(arg1: Function1<A, B>): Ior<L, B> =
    arrow.core.Ior.traverse<L>().run {
  this@map.map<A, B>(arg1) as arrow.core.Ior<L, B>
}

@JvmName("flatTraverse")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "flatTraverse(arg1, arg2, arg3)",
  "arrow.core.flatTraverse"
  ),
  DeprecationLevel.WARNING
)
fun <L, G, A, B> Kind<Kind<ForIor, L>, A>.flatTraverse(
  arg1: Monad<Kind<ForIor, L>>,
  arg2: Applicative<G>,
  arg3: Function1<A, Kind<G, Kind<Kind<ForIor, L>, B>>>
): Kind<G, Kind<Kind<ForIor, L>, B>> = arrow.core.Ior.traverse<L>().run {
  this@flatTraverse.flatTraverse<G, A, B>(arg1, arg2, arg3) as arrow.Kind<G,
    arrow.Kind<arrow.Kind<arrow.core.ForIor, L>, B>>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <L> Companion.traverse(): IorTraverse<L> = traverse_singleton as
    arrow.core.extensions.IorTraverse<L>
