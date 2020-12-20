package arrow.core.extensions.list.foldable

import arrow.Kind
import arrow.core.Eval
import arrow.core.ForListK
import arrow.core.Option
import arrow.core.extensions.ListKFoldable
import arrow.typeclasses.Applicative
import arrow.typeclasses.Monad
import arrow.typeclasses.Monoid
import kotlin.Boolean
import kotlin.Function1
import kotlin.Function2
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.jvm.JvmName

@JvmName("foldLeft")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B> List<A>.foldLeft(arg1: B, arg2: Function2<B, A, B>): B =
    arrow.core.extensions.list.foldable.List.foldable().run {
  arrow.core.ListK(this@foldLeft).foldLeft<A, B>(arg1, arg2) as B
}

@JvmName("foldRight")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B> List<A>.foldRight(arg1: Eval<B>, arg2: Function2<A, Eval<B>, Eval<B>>): Eval<B> =
    arrow.core.extensions.list.foldable.List.foldable().run {
  arrow.core.ListK(this@foldRight).foldRight<A, B>(arg1, arg2) as arrow.core.Eval<B>
}

@JvmName("fold")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A> List<A>.fold(arg1: Monoid<A>): A = arrow.core.extensions.list.foldable.List.foldable().run {
  arrow.core.ListK(this@fold).fold<A>(arg1) as A
}

@JvmName("reduceLeftToOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B> List<A>.reduceLeftToOption(arg1: Function1<A, B>, arg2: Function2<B, A, B>): Option<B> =
    arrow.core.extensions.list.foldable.List.foldable().run {
  arrow.core.ListK(this@reduceLeftToOption).reduceLeftToOption<A, B>(arg1, arg2) as
    arrow.core.Option<B>
}

@JvmName("reduceRightToOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B> List<A>.reduceRightToOption(arg1: Function1<A, B>, arg2: Function2<A, Eval<B>, Eval<B>>):
    Eval<Option<B>> = arrow.core.extensions.list.foldable.List.foldable().run {
  arrow.core.ListK(this@reduceRightToOption).reduceRightToOption<A, B>(arg1, arg2) as
    arrow.core.Eval<arrow.core.Option<B>>
}

@JvmName("reduceLeftOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A> List<A>.reduceLeftOption(arg1: Function2<A, A, A>): Option<A> =
    arrow.core.extensions.list.foldable.List.foldable().run {
  arrow.core.ListK(this@reduceLeftOption).reduceLeftOption<A>(arg1) as arrow.core.Option<A>
}

@JvmName("reduceRightOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A> List<A>.reduceRightOption(arg1: Function2<A, Eval<A>, Eval<A>>): Eval<Option<A>> =
    arrow.core.extensions.list.foldable.List.foldable().run {
  arrow.core.ListK(this@reduceRightOption).reduceRightOption<A>(arg1) as
    arrow.core.Eval<arrow.core.Option<A>>
}

@JvmName("combineAll")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A> List<A>.combineAll(arg1: Monoid<A>): A =
    arrow.core.extensions.list.foldable.List.foldable().run {
  arrow.core.ListK(this@combineAll).combineAll<A>(arg1) as A
}

@JvmName("foldMap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B> List<A>.foldMap(arg1: Monoid<B>, arg2: Function1<A, B>): B =
    arrow.core.extensions.list.foldable.List.foldable().run {
  arrow.core.ListK(this@foldMap).foldMap<A, B>(arg1, arg2) as B
}

@JvmName("orEmpty")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A> orEmpty(arg0: Applicative<ForListK>, arg1: Monoid<A>): List<A> =
    arrow.core.extensions.list.foldable.List
   .foldable()
   .orEmpty<A>(arg0, arg1) as kotlin.collections.List<A>

@JvmName("traverse_")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <G, A, B> List<A>.traverse_(arg1: Applicative<G>, arg2: Function1<A, Kind<G, B>>): Kind<G, Unit>
    = arrow.core.extensions.list.foldable.List.foldable().run {
  arrow.core.ListK(this@traverse_).traverse_<G, A, B>(arg1, arg2) as arrow.Kind<G, kotlin.Unit>
}

@JvmName("sequence_")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <G, A> List<Kind<G, A>>.sequence_(arg1: Applicative<G>): Kind<G, Unit> =
    arrow.core.extensions.list.foldable.List.foldable().run {
  arrow.core.ListK(this@sequence_).sequence_<G, A>(arg1) as arrow.Kind<G, kotlin.Unit>
}

@JvmName("find")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A> List<A>.find(arg1: Function1<A, Boolean>): Option<A> =
    arrow.core.extensions.list.foldable.List.foldable().run {
  arrow.core.ListK(this@find).find<A>(arg1) as arrow.core.Option<A>
}

@JvmName("exists")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A> List<A>.exists(arg1: Function1<A, Boolean>): Boolean =
    arrow.core.extensions.list.foldable.List.foldable().run {
  arrow.core.ListK(this@exists).exists<A>(arg1) as kotlin.Boolean
}

@JvmName("forAll")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A> List<A>.forAll(arg1: Function1<A, Boolean>): Boolean =
    arrow.core.extensions.list.foldable.List.foldable().run {
  arrow.core.ListK(this@forAll).forAll<A>(arg1) as kotlin.Boolean
}

@JvmName("all")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A> List<A>.all(arg1: Function1<A, Boolean>): Boolean =
    arrow.core.extensions.list.foldable.List.foldable().run {
  arrow.core.ListK(this@all).all<A>(arg1) as kotlin.Boolean
}

@JvmName("nonEmpty")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A> List<A>.nonEmpty(): Boolean = arrow.core.extensions.list.foldable.List.foldable().run {
  arrow.core.ListK(this@nonEmpty).nonEmpty<A>() as kotlin.Boolean
}

@JvmName("isNotEmpty")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A> List<A>.isNotEmpty(): Boolean = arrow.core.extensions.list.foldable.List.foldable().run {
  arrow.core.ListK(this@isNotEmpty).isNotEmpty<A>() as kotlin.Boolean
}

@JvmName("foldMapA")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <G, A, B, AP : Applicative<G>, MO : Monoid<B>> List<A>.foldMapA(
  arg1: AP,
  arg2: MO,
  arg3: Function1<A, Kind<G, B>>
): Kind<G, B> = arrow.core.extensions.list.foldable.List.foldable().run {
  arrow.core.ListK(this@foldMapA).foldMapA<G, A, B, AP, MO>(arg1, arg2, arg3) as arrow.Kind<G, B>
}

@JvmName("foldMapM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <G, A, B, MA : Monad<G>, MO : Monoid<B>> List<A>.foldMapM(
  arg1: MA,
  arg2: MO,
  arg3: Function1<A, Kind<G, B>>
): Kind<G, B> = arrow.core.extensions.list.foldable.List.foldable().run {
  arrow.core.ListK(this@foldMapM).foldMapM<G, A, B, MA, MO>(arg1, arg2, arg3) as arrow.Kind<G, B>
}

@JvmName("foldM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <G, A, B> List<A>.foldM(
  arg1: Monad<G>,
  arg2: B,
  arg3: Function2<B, A, Kind<G, B>>
): Kind<G, B> = arrow.core.extensions.list.foldable.List.foldable().run {
  arrow.core.ListK(this@foldM).foldM<G, A, B>(arg1, arg2, arg3) as arrow.Kind<G, B>
}

@JvmName("firstOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A> List<A>.firstOption(): Option<A> = arrow.core.extensions.list.foldable.List.foldable().run {
  arrow.core.ListK(this@firstOption).firstOption<A>() as arrow.core.Option<A>
}

@JvmName("firstOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A> List<A>.firstOption(arg1: Function1<A, Boolean>): Option<A> =
    arrow.core.extensions.list.foldable.List.foldable().run {
  arrow.core.ListK(this@firstOption).firstOption<A>(arg1) as arrow.core.Option<A>
}

@JvmName("firstOrNone")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A> List<A>.firstOrNone(): Option<A> = arrow.core.extensions.list.foldable.List.foldable().run {
  arrow.core.ListK(this@firstOrNone).firstOrNone<A>() as arrow.core.Option<A>
}

@JvmName("firstOrNone")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A> List<A>.firstOrNone(arg1: Function1<A, Boolean>): Option<A> =
    arrow.core.extensions.list.foldable.List.foldable().run {
  arrow.core.ListK(this@firstOrNone).firstOrNone<A>(arg1) as arrow.core.Option<A>
}

@JvmName("toList")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A> List<A>.toList(): List<A> = arrow.core.extensions.list.foldable.List.foldable().run {
  arrow.core.ListK(this@toList).toList<A>() as kotlin.collections.List<A>
}

/**
 * cached extension
 */
@PublishedApi()
internal val foldable_singleton: ListKFoldable = object : arrow.core.extensions.ListKFoldable {}

object List {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  inline fun foldable(): ListKFoldable = foldable_singleton}
