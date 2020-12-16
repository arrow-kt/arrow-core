package arrow.core.extensions.either.monad

import arrow.Kind
import arrow.core.Either
import arrow.core.Either.Companion
import arrow.core.Eval
import arrow.core.ForEither
import arrow.core.Tuple2
import arrow.core.extensions.EitherMonad
import kotlin.Any
import kotlin.Boolean
import kotlin.Function0
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val monad_singleton: EitherMonad<Any?> = object : EitherMonad<Any?> {}

@JvmName("flatMap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B> Kind<Kind<ForEither, L>, A>.flatMap(arg1: Function1<A, Kind<Kind<ForEither, L>, B>>):
  Either<L, B> = arrow.core.Either.monad<L>().run {
  this@flatMap.flatMap<A, B>(arg1) as arrow.core.Either<L, B>
}

@JvmName("tailRecM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B> tailRecM(arg0: A, arg1: Function1<A, Kind<Kind<ForEither, L>, Either<A, B>>>):
  Either<L, B> = arrow.core.Either
  .monad<L>()
  .tailRecM<A, B>(arg0, arg1) as arrow.core.Either<L, B>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B> Kind<Kind<ForEither, L>, A>.map(arg1: Function1<A, B>): Either<L, B> =
  arrow.core.Either.monad<L>().run {
    this@map.map<A, B>(arg1) as arrow.core.Either<L, B>
  }

/**
 *  @see [Apply.ap]
 */
@JvmName("ap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B> Kind<Kind<ForEither, L>, A>.ap(arg1: Kind<Kind<ForEither, L>, Function1<A, B>>):
  Either<L, B> = arrow.core.Either.monad<L>().run {
  this@EitherMonad.mapN<A, (A) -> B, B>(this@ap, arg1) { (a, f) -> f(a) } as arrow.core.Either<L, B>
}

@JvmName("flatten")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A> Kind<Kind<ForEither, L>, Kind<Kind<ForEither, L>, A>>.flatten(): Either<L, A> =
  arrow.core.Either.monad<L>().run {
    this@flatten.flatten<A>() as arrow.core.Either<L, A>
  }

@JvmName("followedBy")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B> Kind<Kind<ForEither, L>, A>.followedBy(arg1: Kind<Kind<ForEither, L>, B>): Either<L,
  B> = arrow.core.Either.monad<L>().run {
  this@followedBy.followedBy<A, B>(arg1) as arrow.core.Either<L, B>
}

@JvmName("apTap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B> Kind<Kind<ForEither, L>, A>.apTap(arg1: Kind<Kind<ForEither, L>, B>): Either<L, A> =
  arrow.core.Either.monad<L>().run {
    this@apTap.apTap<A, B>(arg1) as arrow.core.Either<L, A>
  }

@JvmName("followedByEval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B> Kind<Kind<ForEither, L>, A>.followedByEval(arg1: Eval<Kind<Kind<ForEither, L>, B>>):
  Either<L, B> = arrow.core.Either.monad<L>().run {
  this@followedByEval.followedByEval<A, B>(arg1) as arrow.core.Either<L, B>
}

@JvmName("effectM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B> Kind<Kind<ForEither, L>, A>.effectM(arg1: Function1<A, Kind<Kind<ForEither, L>, B>>):
  Either<L, A> = arrow.core.Either.monad<L>().run {
  this@effectM.flatTap(arg1) as arrow.core.Either<L, A>
}

@JvmName("flatTap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B> Kind<Kind<ForEither, L>, A>.flatTap(arg1: Function1<A, Kind<Kind<ForEither, L>, B>>):
  Either<L, A> = arrow.core.Either.monad<L>().run {
  this@flatTap.flatTap<A, B>(arg1) as arrow.core.Either<L, A>
}

@JvmName("productL")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B> Kind<Kind<ForEither, L>, A>.productL(arg1: Kind<Kind<ForEither, L>, B>): Either<L, A> =
  arrow.core.Either.monad<L>().run {
    this@productL.productL<A, B>(arg1) as arrow.core.Either<L, A>
  }

@JvmName("forEffect")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B> Kind<Kind<ForEither, L>, A>.forEffect(arg1: Kind<Kind<ForEither, L>, B>): Either<L, A> =
  arrow.core.Either.monad<L>().run {
    this@forEffect.productL(arg1) as arrow.core.Either<L, A>
  }

@JvmName("productLEval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B> Kind<Kind<ForEither, L>, A>.productLEval(arg1: Eval<Kind<Kind<ForEither, L>, B>>):
  Either<L, A> = arrow.core.Either.monad<L>().run {
  this@productLEval.productLEval<A, B>(arg1) as arrow.core.Either<L, A>
}

@JvmName("forEffectEval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B> Kind<Kind<ForEither, L>, A>.forEffectEval(arg1: Eval<Kind<Kind<ForEither, L>, B>>):
  Either<L, A> = arrow.core.Either.monad<L>().run {
  this@forEffectEval.productLEval(arg1) as arrow.core.Either<L, A>
}

@JvmName("mproduct")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B> Kind<Kind<ForEither, L>, A>.mproduct(arg1: Function1<A, Kind<Kind<ForEither, L>, B>>):
  Either<L, Tuple2<A, B>> = arrow.core.Either.monad<L>().run {
  this@mproduct.mproduct<A, B>(arg1) as arrow.core.Either<L, arrow.core.Tuple2<A, B>>
}

@JvmName("ifM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, B> Kind<Kind<ForEither, L>, Boolean>.ifM(
  arg1: Function0<Kind<Kind<ForEither, L>, B>>,
  arg2: Function0<Kind<Kind<ForEither, L>, B>>
): Either<L, B> = arrow.core.Either.monad<L>().run {
  this@ifM.ifM<B>(arg1, arg2) as arrow.core.Either<L, B>
}

@JvmName("selectM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B> Kind<Kind<ForEither, L>, Either<A, B>>.selectM(
  arg1: Kind<Kind<ForEither, L>,
    Function1<A, B>>
): Either<L, B> = arrow.core.Either.monad<L>().run {
  this@selectM.selectM<A, B>(arg1) as arrow.core.Either<L, B>
}

@JvmName("select")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <L, A, B> Kind<Kind<ForEither, L>, Either<A, B>>.select(
  arg1: Kind<Kind<ForEither, L>,
    Function1<A, B>>
): Either<L, B> = arrow.core.Either.monad<L>().run {
  this@select.select<A, B>(arg1) as arrow.core.Either<L, B>
}

/**
 *  ank_macro_hierarchy(arrow.typeclasses.Monad)
 *
 *  [Monad] abstract over the ability to declare sequential computations that are dependent in the order or
 *  the results of previous computations.
 *
 *  Given a type constructor [F] with a value of [A] we can compose multiple operations of type
 *  `Kind<F, ?>` where `?` denotes a value being transformed.
 *
 *  This is true for all type constructors that can support the [Monad] type class including and not limited to
 *  [IO], [ObservableK], [Option], [Either], [List] ...
 *
 *  [The Monad Tutorial](https://arrow-kt.io/docs/patterns/monads/)
 */
@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <L> Companion.monad(): EitherMonad<L> = monad_singleton as
  arrow.core.extensions.EitherMonad<L>
