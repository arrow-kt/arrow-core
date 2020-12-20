package arrow.core.extensions.list.monadCombine

import arrow.Kind
import arrow.core.ForListK
import arrow.core.Tuple2
import arrow.core.extensions.ListKMonadCombine
import arrow.typeclasses.Bifoldable
import arrow.typeclasses.Foldable
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.collections.List
import kotlin.jvm.JvmName

@JvmName("unite")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <G, A> List<Kind<G, A>>.unite(arg1: Foldable<G>): List<A> =
    arrow.core.extensions.list.monadCombine.List.monadCombine().run {
  arrow.core.ListK(this@unite).unite<G, A>(arg1) as kotlin.collections.List<A>
}

@JvmName("separate")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <G, A, B> List<Kind<Kind<G, A>, B>>.separate(arg1: Bifoldable<G>): Tuple2<Kind<ForListK, A>,
    Kind<ForListK, B>> = arrow.core.extensions.list.monadCombine.List.monadCombine().run {
  arrow.core.ListK(this@separate).separate<G, A, B>(arg1) as
    arrow.core.Tuple2<arrow.Kind<arrow.core.ForListK, A>, arrow.Kind<arrow.core.ForListK, B>>
}

/**
 * cached extension
 */
@PublishedApi()
internal val monadCombine_singleton: ListKMonadCombine = object :
    arrow.core.extensions.ListKMonadCombine {}

object List {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  inline fun monadCombine(): ListKMonadCombine = monadCombine_singleton}
