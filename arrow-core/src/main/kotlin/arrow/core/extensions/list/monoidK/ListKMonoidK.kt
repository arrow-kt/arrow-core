package arrow.core.extensions.list.monoidK

import arrow.Kind
import arrow.core.ForListK
import arrow.core.extensions.ListKMonoidK
import arrow.typeclasses.Monoid
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

@JvmName("algebra")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A> algebra(): Monoid<Kind<ForListK, A>> = arrow.core.extensions.list.monoidK.List
   .monoidK()
   .algebra<A>() as arrow.typeclasses.Monoid<arrow.Kind<arrow.core.ForListK, A>>

/**
 * cached extension
 */
@PublishedApi()
internal val monoidK_singleton: ListKMonoidK = object : arrow.core.extensions.ListKMonoidK {}

object List {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  inline fun monoidK(): ListKMonoidK = monoidK_singleton}
