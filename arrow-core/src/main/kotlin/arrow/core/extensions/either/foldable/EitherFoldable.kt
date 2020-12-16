package arrow.core.extensions.either.foldable

import arrow.Kind
import arrow.core.Either
import arrow.core.Either.Companion
import arrow.core.Eval
import arrow.core.ForEither
import arrow.core.extensions.EitherFoldable
import arrow.typeclasses.Applicative
import arrow.typeclasses.Monad
import arrow.typeclasses.Monoid
import kotlin.Any
import kotlin.Boolean
import kotlin.Function1
import kotlin.Function2
import kotlin.Long
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val foldable_singleton: EitherFoldable<Any?> = object : EitherFoldable<Any?> {}

@JvmName("foldLeft")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B> Kind<Kind<ForEither, L>, A>.foldLeft(arg1: B, arg2: Function2<B, A, B>): B =
  arrow.core.Either.foldable<L>().run {
    this@foldLeft.foldLeft<A, B>(arg1, arg2) as B
  }

@JvmName("foldRight")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B> Kind<Kind<ForEither, L>, A>.foldRight(
  arg1: Eval<B>,
  arg2: Function2<A, Eval<B>, Eval<B>>
): Eval<B> = arrow.core.Either.foldable<L>().run {
  this@foldRight.foldRight<A, B>(arg1, arg2) as arrow.core.Eval<B>
}

@JvmName("fold")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A> Kind<Kind<ForEither, L>, A>.fold(arg1: Monoid<A>): A =
  arrow.core.Either.foldable<L>().run {
    this@fold.fold<A>(arg1) as A
  }

@JvmName("reduceLeftToOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B> Kind<Kind<ForEither, L>, A>.reduceLeftToOption(
  arg1: Function1<A, B>,
  arg2: Function2<B, A, B>
): Option<B> = arrow.core.Either.foldable<L>().run {
  this@reduceLeftToOption.reduceLeftToOption<A, B>(arg1, arg2) as arrow.core.Option<B>
}

@JvmName("reduceRightToOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B> Kind<Kind<ForEither, L>, A>.reduceRightToOption(
  arg1: Function1<A, B>,
  arg2: Function2<A, Eval<B>, Eval<B>>
): Eval<Option<B>> = arrow.core.Either.foldable<L>().run {
  this@reduceRightToOption.reduceRightToOption<A, B>(arg1, arg2) as
    arrow.core.Eval<arrow.core.Option<B>>
}

@JvmName("reduceLeftOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A> Kind<Kind<ForEither, L>, A>.reduceLeftOption(arg1: Function2<A, A, A>): Option<A> =
  arrow.core.Either.foldable<L>().run {
    this@reduceLeftOption.reduceLeftOption<A>(arg1) as arrow.core.Option<A>
  }

@JvmName("reduceRightOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A> Kind<Kind<ForEither, L>, A>.reduceRightOption(arg1: Function2<A, Eval<A>, Eval<A>>):
  Eval<Option<A>> = arrow.core.Either.foldable<L>().run {
  this@reduceRightOption.reduceRightOption<A>(arg1) as arrow.core.Eval<arrow.core.Option<A>>
}

@JvmName("combineAll")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A> Kind<Kind<ForEither, L>, A>.combineAll(arg1: Monoid<A>): A =
  arrow.core.Either.foldable<L>().run {
    this@combineAll.combineAll<A>(arg1) as A
  }

@JvmName("foldMap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B> Kind<Kind<ForEither, L>, A>.foldMap(arg1: Monoid<B>, arg2: Function1<A, B>): B =
  arrow.core.Either.foldable<L>().run {
    this@foldMap.foldMap<A, B>(arg1, arg2) as B
  }

@JvmName("orEmpty")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A> orEmpty(arg0: Applicative<Kind<ForEither, L>>, arg1: Monoid<A>): Either<L, A> =
  arrow.core.Either
    .foldable<L>()
    .orEmpty<A>(arg0, arg1) as arrow.core.Either<L, A>

@JvmName("traverse_")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, G, A, B> Kind<Kind<ForEither, L>, A>.traverse_(
  arg1: Applicative<G>,
  arg2: Function1<A, Kind<G, B>>
): Kind<G, Unit> = arrow.core.Either.foldable<L>().run {
  this@traverse_.traverse_<G, A, B>(arg1, arg2) as arrow.Kind<G, kotlin.Unit>
}

@JvmName("sequence_")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, G, A> Kind<Kind<ForEither, L>, Kind<G, A>>.sequence_(arg1: Applicative<G>): Kind<G, Unit> =
  arrow.core.Either.foldable<L>().run {
    this@sequence_.sequence_<G, A>(arg1) as arrow.Kind<G, kotlin.Unit>
  }

@JvmName("find")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A> Kind<Kind<ForEither, L>, A>.find(arg1: Function1<A, Boolean>): Option<A> =
  arrow.core.Either.foldable<L>().run {
    this@find.find<A>(arg1) as arrow.core.Option<A>
  }

@JvmName("exists")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A> Kind<Kind<ForEither, L>, A>.exists(arg1: Function1<A, Boolean>): Boolean =
  arrow.core.Either.foldable<L>().run {
    this@exists.exists<A>(arg1) as kotlin.Boolean
  }

@JvmName("forAll")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A> Kind<Kind<ForEither, L>, A>.forAll(arg1: Function1<A, Boolean>): Boolean =
  arrow.core.Either.foldable<L>().run {
    this@forAll.all(arg1) as kotlin.Boolean
  }

@JvmName("all")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A> Kind<Kind<ForEither, L>, A>.all(arg1: Function1<A, Boolean>): Boolean =
  arrow.core.Either.foldable<L>().run {
    this@all.all<A>(arg1) as kotlin.Boolean
  }

@JvmName("isEmpty")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A> Kind<Kind<ForEither, L>, A>.isEmpty(): Boolean = arrow.core.Either.foldable<L>().run {
  this@isEmpty.isEmpty<A>() as kotlin.Boolean
}

@JvmName("nonEmpty")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A> Kind<Kind<ForEither, L>, A>.nonEmpty(): Boolean = arrow.core.Either.foldable<L>().run {
  this@nonEmpty.isNotEmpty() as kotlin.Boolean
}

@JvmName("isNotEmpty")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A> Kind<Kind<ForEither, L>, A>.isNotEmpty(): Boolean = arrow.core.Either.foldable<L>().run {
  this@isNotEmpty.isNotEmpty<A>() as kotlin.Boolean
}

@JvmName("size")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A> Kind<Kind<ForEither, L>, A>.size(arg1: Monoid<Long>): Long =
  arrow.core.Either.foldable<L>().run {
    this@size.size<A>(arg1) as kotlin.Long
  }

@JvmName("foldMapA")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, G, A, B, AP : Applicative<G>, MO : Monoid<B>> Kind<Kind<ForEither, L>, A>.foldMapA(
  arg1: AP,
  arg2: MO,
  arg3: Function1<A, Kind<G, B>>
): Kind<G, B> = arrow.core.Either.foldable<L>().run {
  this@foldMapA.foldMapA<G, A, B, AP, MO>(arg1, arg2, arg3) as arrow.Kind<G, B>
}

@JvmName("foldMapM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, G, A, B, MA : Monad<G>, MO : Monoid<B>> Kind<Kind<ForEither, L>, A>.foldMapM(
  arg1: MA,
  arg2: MO,
  arg3: Function1<A, Kind<G, B>>
): Kind<G, B> = arrow.core.Either.foldable<L>().run {
  this@foldMapM.foldMapM<G, A, B, MA, MO>(arg1, arg2, arg3) as arrow.Kind<G, B>
}

@JvmName("foldM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, G, A, B> Kind<Kind<ForEither, L>, A>.foldM(
  arg1: Monad<G>,
  arg2: B,
  arg3: Function2<B, A, Kind<G, B>>
): Kind<G, B> = arrow.core.Either.foldable<L>().run {
  this@foldM.foldM<G, A, B>(arg1, arg2, arg3) as arrow.Kind<G, B>
}

@JvmName("get")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A> Kind<Kind<ForEither, L>, A>.get(arg1: Long): Option<A> =
  arrow.core.Either.foldable<L>().run {
    this@get.get<A>(arg1) as arrow.core.Option<A>
  }

@JvmName("firstOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A> Kind<Kind<ForEither, L>, A>.firstOption(): Option<A> =
  arrow.core.Either.foldable<L>().run {
    this@firstOption.firstOrNone() as arrow.core.Option<A>
  }

@JvmName("firstOption")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A> Kind<Kind<ForEither, L>, A>.firstOption(arg1: Function1<A, Boolean>): Option<A> =
  arrow.core.Either.foldable<L>().run {
    this@firstOption.firstOrNone(arg1) as arrow.core.Option<A>
  }

@JvmName("firstOrNone")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A> Kind<Kind<ForEither, L>, A>.firstOrNone(): Option<A> =
  arrow.core.Either.foldable<L>().run {
    this@firstOrNone.firstOrNone<A>() as arrow.core.Option<A>
  }

@JvmName("firstOrNone")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A> Kind<Kind<ForEither, L>, A>.firstOrNone(arg1: Function1<A, Boolean>): Option<A> =
  arrow.core.Either.foldable<L>().run {
    this@firstOrNone.firstOrNone<A>(arg1) as arrow.core.Option<A>
  }

@JvmName("toList")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A> Kind<Kind<ForEither, L>, A>.toList(): List<A> = arrow.core.Either.foldable<L>().run {
  this@toList.toList<A>() as kotlin.collections.List<A>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <L> Companion.foldable(): EitherFoldable<L> = foldable_singleton as
  arrow.core.extensions.EitherFoldable<L>
