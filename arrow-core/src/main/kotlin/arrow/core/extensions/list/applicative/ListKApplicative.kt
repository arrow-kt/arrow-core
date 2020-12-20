package arrow.core.extensions.list.applicative

import arrow.core.extensions.ListKApplicative
import arrow.typeclasses.Monoid
import kotlin.Function1
import kotlin.Int
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.jvm.JvmName

@JvmName("just1")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A> A.just(): List<A> = arrow.core.extensions.list.applicative.List.applicative().run {
  this@just.just<A>() as kotlin.collections.List<A>
}

@JvmName("unit")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun unit(): List<Unit> = arrow.core.extensions.list.applicative.List
   .applicative()
   .unit() as kotlin.collections.List<kotlin.Unit>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B> List<A>.map(arg1: Function1<A, B>): List<B> =
    arrow.core.extensions.list.applicative.List.applicative().run {
  arrow.core.ListK(this@map).map<A, B>(arg1) as kotlin.collections.List<B>
}

@JvmName("replicate")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A> List<A>.replicate(arg1: Int): List<List<A>> =
    arrow.core.extensions.list.applicative.List.applicative().run {
  arrow.core.ListK(this@replicate).replicate<A>(arg1) as
    kotlin.collections.List<kotlin.collections.List<A>>
}

@JvmName("replicate")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A> List<A>.replicate(arg1: Int, arg2: Monoid<A>): List<A> =
    arrow.core.extensions.list.applicative.List.applicative().run {
  arrow.core.ListK(this@replicate).replicate<A>(arg1, arg2) as kotlin.collections.List<A>
}

/**
 * cached extension
 */
@PublishedApi()
internal val applicative_singleton: ListKApplicative = object :
    arrow.core.extensions.ListKApplicative {}

object List {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  inline fun applicative(): ListKApplicative = applicative_singleton}
