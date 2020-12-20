package arrow.core.extensions.list.alternative

import arrow.Kind
import arrow.core.ForListK
import arrow.core.Option
import arrow.core.SequenceK
import arrow.core.extensions.ListKAlternative
import kotlin.Boolean
import kotlin.Function0
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.jvm.JvmName

@JvmName("some")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A> List<A>.some(): List<SequenceK<A>> =
    arrow.core.extensions.list.alternative.List.alternative().run {
  arrow.core.ListK(this@some).some<A>() as kotlin.collections.List<arrow.core.SequenceK<A>>
}

@JvmName("many")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A> List<A>.many(): List<SequenceK<A>> =
    arrow.core.extensions.list.alternative.List.alternative().run {
  arrow.core.ListK(this@many).many<A>() as kotlin.collections.List<arrow.core.SequenceK<A>>
}

@JvmName("alt")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
infix fun <A> List<A>.alt(arg1: List<A>): List<A> =
    arrow.core.extensions.list.alternative.List.alternative().run {
  arrow.core.ListK(this@alt).alt<A>(arrow.core.ListK(arg1)) as kotlin.collections.List<A>
}

@JvmName("orElse")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A> List<A>.orElse(arg1: List<A>): List<A> =
    arrow.core.extensions.list.alternative.List.alternative().run {
  arrow.core.ListK(this@orElse).orElse<A>(arrow.core.ListK(arg1)) as kotlin.collections.List<A>
}

@JvmName("combineK")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A> List<A>.combineK(arg1: List<A>): List<A> =
    arrow.core.extensions.list.alternative.List.alternative().run {
  arrow.core.ListK(this@combineK).combineK<A>(arrow.core.ListK(arg1)) as kotlin.collections.List<A>
}

@JvmName("optional")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A> List<A>.optional(): List<Option<A>> =
    arrow.core.extensions.list.alternative.List.alternative().run {
  arrow.core.ListK(this@optional).optional<A>() as kotlin.collections.List<arrow.core.Option<A>>
}

@JvmName("guard")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun guard(arg0: Boolean): List<Unit> = arrow.core.extensions.list.alternative.List
   .alternative()
   .guard(arg0) as kotlin.collections.List<kotlin.Unit>

@JvmName("lazyOrElse")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A> List<A>.lazyOrElse(arg1: Function0<Kind<ForListK, A>>): List<A> =
    arrow.core.extensions.list.alternative.List.alternative().run {
  arrow.core.ListK(this@lazyOrElse).lazyOrElse<A>(arg1) as kotlin.collections.List<A>
}

/**
 * cached extension
 */
@PublishedApi()
internal val alternative_singleton: ListKAlternative = object :
    arrow.core.extensions.ListKAlternative {}

object List {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  inline fun alternative(): ListKAlternative = alternative_singleton}
