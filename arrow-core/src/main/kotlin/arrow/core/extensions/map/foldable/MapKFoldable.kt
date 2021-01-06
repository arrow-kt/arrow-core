package arrow.core.extensions.map.foldable

import arrow.Kind
import arrow.core.Eval
import arrow.core.ForMapK
import arrow.core.Option
import arrow.core.extensions.MapKFoldable
import arrow.typeclasses.Applicative
import arrow.typeclasses.Monad
import arrow.typeclasses.Monoid
import kotlin.Any
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.Function1
import kotlin.Function2
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.Map
import kotlin.jvm.JvmName

@JvmName("foldLeft")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "foldLeft(arg1, arg2)",
    "arrow.core.foldLeft"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B> Map<K, A>.foldLeft(arg1: B, arg2: Function2<B, A, B>): B =
  arrow.core.extensions.map.foldable.Map.foldable<K>().run {
    arrow.core.MapK(this@foldLeft).foldLeft<A, B>(arg1, arg2) as B
  }

@JvmName("foldRight")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "foldRight(arg1, arg2)",
    "arrow.core.foldRight"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B> Map<K, A>.foldRight(arg1: Eval<B>, arg2: Function2<A, Eval<B>, Eval<B>>): Eval<B> =
  arrow.core.extensions.map.foldable.Map.foldable<K>().run {
    arrow.core.MapK(this@foldRight).foldRight<A, B>(arg1, arg2) as arrow.core.Eval<B>
  }

@JvmName("fold")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "fold(arg1)",
    "arrow.core.fold"
  ),
  DeprecationLevel.WARNING
)
fun <K, A> Map<K, A>.fold(arg1: Monoid<A>): A =
  arrow.core.extensions.map.foldable.Map.foldable<K>().run {
    arrow.core.MapK(this@fold).fold<A>(arg1) as A
  }

@JvmName("reduceLeftToOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "reduceLeftToOption(arg1, arg2)",
    "arrow.core.reduceLeftToOption"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B> Map<K, A>.reduceLeftToOption(arg1: Function1<A, B>, arg2: Function2<B, A, B>):
  Option<B> = arrow.core.extensions.map.foldable.Map.foldable<K>().run {
  arrow.core.MapK(this@reduceLeftToOption).reduceLeftToOption<A, B>(arg1, arg2) as
    arrow.core.Option<B>
}

@JvmName("reduceRightToOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "reduceRightToOption(arg1, arg2)",
    "arrow.core.reduceRightToOption"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B> Map<K, A>.reduceRightToOption(
  arg1: Function1<A, B>,
  arg2: Function2<A, Eval<B>, Eval<B>>
): Eval<Option<B>> = arrow.core.extensions.map.foldable.Map.foldable<K>().run {
  arrow.core.MapK(this@reduceRightToOption).reduceRightToOption<A, B>(arg1, arg2) as
    arrow.core.Eval<arrow.core.Option<B>>
}

@JvmName("reduceLeftOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "reduceLeftOption(arg1)",
    "arrow.core.reduceLeftOption"
  ),
  DeprecationLevel.WARNING
)
fun <K, A> Map<K, A>.reduceLeftOption(arg1: Function2<A, A, A>): Option<A> =
  arrow.core.extensions.map.foldable.Map.foldable<K>().run {
    arrow.core.MapK(this@reduceLeftOption).reduceLeftOption<A>(arg1) as arrow.core.Option<A>
  }

@JvmName("reduceRightOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "reduceRightOption(arg1)",
    "arrow.core.reduceRightOption"
  ),
  DeprecationLevel.WARNING
)
fun <K, A> Map<K, A>.reduceRightOption(arg1: Function2<A, Eval<A>, Eval<A>>): Eval<Option<A>> =
  arrow.core.extensions.map.foldable.Map.foldable<K>().run {
    arrow.core.MapK(this@reduceRightOption).reduceRightOption<A>(arg1) as
      arrow.core.Eval<arrow.core.Option<A>>
  }

@JvmName("combineAll")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "combineAll(arg1)",
    "arrow.core.combineAll"
  ),
  DeprecationLevel.WARNING
)
fun <K, A> Map<K, A>.combineAll(arg1: Monoid<A>): A =
  arrow.core.extensions.map.foldable.Map.foldable<K>().run {
    arrow.core.MapK(this@combineAll).combineAll<A>(arg1) as A
  }

@JvmName("foldMap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "foldMap(arg1, arg2)",
    "arrow.core.foldMap"
  ),
  DeprecationLevel.WARNING
)
fun <K, A, B> Map<K, A>.foldMap(arg1: Monoid<B>, arg2: Function1<A, B>): B =
  arrow.core.extensions.map.foldable.Map.foldable<K>().run {
    arrow.core.MapK(this@foldMap).foldMap<A, B>(arg1, arg2) as B
  }

@JvmName("orEmpty")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "orEmpty(arg0, arg1)",
    "arrow.core.extensions.map.foldable.Map.orEmpty"
  ),
  DeprecationLevel.WARNING
)
fun <K, A> orEmpty(arg0: Applicative<Kind<ForMapK, K>>, arg1: Monoid<A>): Map<K, A> =
  arrow.core.extensions.map.foldable.Map
    .foldable<K>()
    .orEmpty<A>(arg0, arg1) as kotlin.collections.Map<K, A>

@JvmName("traverse_")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "traverse_(arg1, arg2)",
    "arrow.core.traverse_"
  ),
  DeprecationLevel.WARNING
)
fun <K, G, A, B> Map<K, A>.traverse_(arg1: Applicative<G>, arg2: Function1<A, Kind<G, B>>): Kind<G,
  Unit> = arrow.core.extensions.map.foldable.Map.foldable<K>().run {
  arrow.core.MapK(this@traverse_).traverse_<G, A, B>(arg1, arg2) as arrow.Kind<G, kotlin.Unit>
}

@JvmName("sequence_")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "sequence_(arg1)",
    "arrow.core.sequence_"
  ),
  DeprecationLevel.WARNING
)
fun <K, G, A> Map<K, Kind<G, A>>.sequence_(arg1: Applicative<G>): Kind<G, Unit> =
  arrow.core.extensions.map.foldable.Map.foldable<K>().run {
    arrow.core.MapK(this@sequence_).sequence_<G, A>(arg1) as arrow.Kind<G, kotlin.Unit>
  }

@JvmName("find")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "find(arg1)",
    "arrow.core.find"
  ),
  DeprecationLevel.WARNING
)
fun <K, A> Map<K, A>.find(arg1: Function1<A, Boolean>): Option<A> =
  arrow.core.extensions.map.foldable.Map.foldable<K>().run {
    arrow.core.MapK(this@find).find<A>(arg1) as arrow.core.Option<A>
  }

@JvmName("exists")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "exists(arg1)",
    "arrow.core.exists"
  ),
  DeprecationLevel.WARNING
)
fun <K, A> Map<K, A>.exists(arg1: Function1<A, Boolean>): Boolean =
  arrow.core.extensions.map.foldable.Map.foldable<K>().run {
    arrow.core.MapK(this@exists).exists<A>(arg1) as kotlin.Boolean
  }

@JvmName("forAll")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "forAll(arg1)",
    "arrow.core.forAll"
  ),
  DeprecationLevel.WARNING
)
fun <K, A> Map<K, A>.forAll(arg1: Function1<A, Boolean>): Boolean =
  arrow.core.extensions.map.foldable.Map.foldable<K>().run {
    arrow.core.MapK(this@forAll).forAll<A>(arg1) as kotlin.Boolean
  }

@JvmName("all")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "all(arg1)",
    "arrow.core.all"
  ),
  DeprecationLevel.WARNING
)
fun <K, A> Map<K, A>.all(arg1: Function1<A, Boolean>): Boolean =
  arrow.core.extensions.map.foldable.Map.foldable<K>().run {
    arrow.core.MapK(this@all).all<A>(arg1) as kotlin.Boolean
  }

@JvmName("nonEmpty")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "nonEmpty()",
    "arrow.core.nonEmpty"
  ),
  DeprecationLevel.WARNING
)
fun <K, A> Map<K, A>.nonEmpty(): Boolean =
  arrow.core.extensions.map.foldable.Map.foldable<K>().run {
    arrow.core.MapK(this@nonEmpty).nonEmpty<A>() as kotlin.Boolean
  }

@JvmName("isNotEmpty")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "isNotEmpty()",
    "arrow.core.isNotEmpty"
  ),
  DeprecationLevel.WARNING
)
fun <K, A> Map<K, A>.isNotEmpty(): Boolean =
  arrow.core.extensions.map.foldable.Map.foldable<K>().run {
    arrow.core.MapK(this@isNotEmpty).isNotEmpty<A>() as kotlin.Boolean
  }

@JvmName("foldMapA")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "foldMapA(arg1, arg2, arg3)",
    "arrow.core.foldMapA"
  ),
  DeprecationLevel.WARNING
)
fun <K, G, A, B, AP : Applicative<G>, MO : Monoid<B>> Map<K, A>.foldMapA(
  arg1: AP,
  arg2: MO,
  arg3: Function1<A, Kind<G, B>>
): Kind<G, B> = arrow.core.extensions.map.foldable.Map.foldable<K>().run {
  arrow.core.MapK(this@foldMapA).foldMapA<G, A, B, AP, MO>(arg1, arg2, arg3) as arrow.Kind<G, B>
}

@JvmName("foldMapM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "foldMapM(arg1, arg2, arg3)",
    "arrow.core.foldMapM"
  ),
  DeprecationLevel.WARNING
)
fun <K, G, A, B, MA : Monad<G>, MO : Monoid<B>> Map<K, A>.foldMapM(
  arg1: MA,
  arg2: MO,
  arg3: Function1<A, Kind<G, B>>
): Kind<G, B> = arrow.core.extensions.map.foldable.Map.foldable<K>().run {
  arrow.core.MapK(this@foldMapM).foldMapM<G, A, B, MA, MO>(arg1, arg2, arg3) as arrow.Kind<G, B>
}

@JvmName("foldM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "foldM(arg1, arg2, arg3)",
    "arrow.core.foldM"
  ),
  DeprecationLevel.WARNING
)
fun <K, G, A, B> Map<K, A>.foldM(
  arg1: Monad<G>,
  arg2: B,
  arg3: Function2<B, A, Kind<G, B>>
): Kind<G, B> = arrow.core.extensions.map.foldable.Map.foldable<K>().run {
  arrow.core.MapK(this@foldM).foldM<G, A, B>(arg1, arg2, arg3) as arrow.Kind<G, B>
}

@JvmName("firstOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "firstOption()",
    "arrow.core.firstOption"
  ),
  DeprecationLevel.WARNING
)
fun <K, A> Map<K, A>.firstOption(): Option<A> =
  arrow.core.extensions.map.foldable.Map.foldable<K>().run {
    arrow.core.MapK(this@firstOption).firstOption<A>() as arrow.core.Option<A>
  }

@JvmName("firstOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "firstOption(arg1)",
    "arrow.core.firstOption"
  ),
  DeprecationLevel.WARNING
)
fun <K, A> Map<K, A>.firstOption(arg1: Function1<A, Boolean>): Option<A> =
  arrow.core.extensions.map.foldable.Map.foldable<K>().run {
    arrow.core.MapK(this@firstOption).firstOption<A>(arg1) as arrow.core.Option<A>
  }

@JvmName("firstOrNone")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "firstOrNone()",
    "arrow.core.firstOrNone"
  ),
  DeprecationLevel.WARNING
)
fun <K, A> Map<K, A>.firstOrNone(): Option<A> =
  arrow.core.extensions.map.foldable.Map.foldable<K>().run {
    arrow.core.MapK(this@firstOrNone).firstOrNone<A>() as arrow.core.Option<A>
  }

@JvmName("firstOrNone")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "firstOrNone(arg1)",
    "arrow.core.firstOrNone"
  ),
  DeprecationLevel.WARNING
)
fun <K, A> Map<K, A>.firstOrNone(arg1: Function1<A, Boolean>): Option<A> =
  arrow.core.extensions.map.foldable.Map.foldable<K>().run {
    arrow.core.MapK(this@firstOrNone).firstOrNone<A>(arg1) as arrow.core.Option<A>
  }

@JvmName("toList")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "toList()",
    "arrow.core.toList"
  ),
  DeprecationLevel.WARNING
)
fun <K, A> Map<K, A>.toList(): List<A> = arrow.core.extensions.map.foldable.Map.foldable<K>().run {
  arrow.core.MapK(this@toList).toList<A>() as kotlin.collections.List<A>
}

/**
 * cached extension
 */
@PublishedApi()
internal val foldable_singleton: MapKFoldable<Any?> = object : MapKFoldable<Any?> {}

object Map {
  @Suppress(
    "UNCHECKED_CAST",
    "NOTHING_TO_INLINE"
  )
  inline fun <K> foldable(): MapKFoldable<K> = foldable_singleton as
    arrow.core.extensions.MapKFoldable<K>
}
