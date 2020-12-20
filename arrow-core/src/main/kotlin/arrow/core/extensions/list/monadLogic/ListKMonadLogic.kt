package arrow.core.extensions.list.monadLogic

import arrow.Kind
import arrow.core.ForListK
import arrow.core.Option
import arrow.core.Tuple2
import arrow.core.extensions.ListKMonadLogic
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.jvm.JvmName

@JvmName("splitM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A> List<A>.splitM(): List<Option<Tuple2<Kind<ForListK, A>, A>>> =
    arrow.core.extensions.list.monadLogic.List.monadLogic().run {
  arrow.core.ListK(this@splitM).splitM<A>() as
    kotlin.collections.List<arrow.core.Option<arrow.core.Tuple2<arrow.Kind<arrow.core.ForListK, A>,
    A>>>
}

@JvmName("interleave")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A> List<A>.interleave(arg1: List<A>): List<A> =
    arrow.core.extensions.list.monadLogic.List.monadLogic().run {
  arrow.core.ListK(this@interleave).interleave<A>(arrow.core.ListK(arg1)) as
    kotlin.collections.List<A>
}

@JvmName("unweave")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B> List<A>.unweave(arg1: Function1<A, Kind<ForListK, B>>): List<B> =
    arrow.core.extensions.list.monadLogic.List.monadLogic().run {
  arrow.core.ListK(this@unweave).unweave<A, B>(arg1) as kotlin.collections.List<B>
}

@JvmName("ifThen")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B> List<A>.ifThen(arg1: List<B>, arg2: Function1<A, Kind<ForListK, B>>): List<B> =
    arrow.core.extensions.list.monadLogic.List.monadLogic().run {
  arrow.core.ListK(this@ifThen).ifThen<A, B>(arrow.core.ListK(arg1), arg2) as
    kotlin.collections.List<B>
}

@JvmName("once")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A> List<A>.once(): List<A> = arrow.core.extensions.list.monadLogic.List.monadLogic().run {
  arrow.core.ListK(this@once).once<A>() as kotlin.collections.List<A>
}

@JvmName("voidIfValue")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A> List<A>.voidIfValue(): List<Unit> =
    arrow.core.extensions.list.monadLogic.List.monadLogic().run {
  arrow.core.ListK(this@voidIfValue).voidIfValue<A>() as kotlin.collections.List<kotlin.Unit>
}

/**
 * cached extension
 */
@PublishedApi()
internal val monadLogic_singleton: ListKMonadLogic = object : arrow.core.extensions.ListKMonadLogic
    {}

object List {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  inline fun monadLogic(): ListKMonadLogic = monadLogic_singleton}
