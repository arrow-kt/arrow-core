package arrow.core.extensions.list.semigroupK

import arrow.Kind
import arrow.core.ForListK
import arrow.core.extensions.ListKSemigroupK
import arrow.typeclasses.Semigroup
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.collections.List
import kotlin.jvm.JvmName

@JvmName("combineK")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A> List<A>.combineK(arg1: List<A>): List<A> =
    arrow.core.extensions.list.semigroupK.List.semigroupK().run {
  arrow.core.ListK(this@combineK).combineK<A>(arrow.core.ListK(arg1)) as kotlin.collections.List<A>
}

@JvmName("algebra")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A> algebra(): Semigroup<Kind<ForListK, A>> = arrow.core.extensions.list.semigroupK.List
   .semigroupK()
   .algebra<A>() as arrow.typeclasses.Semigroup<arrow.Kind<arrow.core.ForListK, A>>

/**
 * cached extension
 */
@PublishedApi()
internal val semigroupK_singleton: ListKSemigroupK = object : arrow.core.extensions.ListKSemigroupK
    {}

object List {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  inline fun semigroupK(): ListKSemigroupK = semigroupK_singleton}
