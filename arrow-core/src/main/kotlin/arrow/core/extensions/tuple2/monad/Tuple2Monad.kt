package arrow.core.extensions.tuple2.monad

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.ForTuple2
import arrow.core.Tuple2
import arrow.core.Tuple2.Companion
import arrow.core.extensions.Tuple2Monad
import arrow.typeclasses.Monoid
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.Function0
import kotlin.Function1
import kotlin.Suppress
import kotlin.jvm.JvmName

@JvmName("flatMap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith("arg1(this.b)"),
  DeprecationLevel.WARNING
)
fun <F, A, B> Kind<Kind<ForTuple2, F>, A>.flatMap(
  MF: Monoid<F>,
  arg1: Function1<A,
    Kind<Kind<ForTuple2, F>, B>>
): Tuple2<F, B> = arrow.core.Tuple2.monad<F>(MF).run {
  this@flatMap.flatMap<A, B>(arg1) as arrow.core.Tuple2<F, B>
}

@JvmName("tailRecM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "tailRecM is not longer supported for Tuple2. Please write concrete tailrec fun.",
  level = DeprecationLevel.WARNING
)
fun <F, A, B> tailRecM(
  MF: Monoid<F>,
  arg0: A,
  arg1: Function1<A, Kind<Kind<ForTuple2, F>, Either<A, B>>>
): Tuple2<F, B> = arrow.core.Tuple2
  .monad<F>(MF)
  .tailRecM<A, B>(arg0, arg1) as arrow.core.Tuple2<F, B>

@JvmName("map")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "this.a toT arg1(this.b)",
    "arrow.core.toT"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B> Kind<Kind<ForTuple2, F>, A>.map(MF: Monoid<F>, arg1: Function1<A, B>): Tuple2<F, B> =
  arrow.core.Tuple2.monad<F>(MF).run {
    this@map.map<A, B>(arg1) as arrow.core.Tuple2<F, B>
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
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith("Tuple2(a, arg1.b(this.b))", "arrow.core.Tuple2"),
  DeprecationLevel.WARNING
)
fun <F, A, B> Kind<Kind<ForTuple2, F>, A>.ap(
  MF: Monoid<F>,
  arg1: Kind<Kind<ForTuple2, F>,
    Function1<A, B>>
): Tuple2<F, B> = arrow.core.Tuple2.monad<F>(MF).run {
  this@ap.ap<A, B>(arg1) as arrow.core.Tuple2<F, B>
}

@JvmName("flatten")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith("this.b"),
  DeprecationLevel.WARNING
)
fun <F, A> Kind<Kind<ForTuple2, F>, Kind<Kind<ForTuple2, F>, A>>.flatten(MF: Monoid<F>): Tuple2<F,
  A> = arrow.core.Tuple2.monad<F>(MF).run {
  this@flatten.flatten<A>() as arrow.core.Tuple2<F, A>
}

@JvmName("followedBy")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith("arg1"),
  DeprecationLevel.WARNING
)
fun <F, A, B> Kind<Kind<ForTuple2, F>, A>.followedBy(
  MF: Monoid<F>,
  arg1: Kind<Kind<ForTuple2, F>,
    B>
): Tuple2<F, B> = arrow.core.Tuple2.monad<F>(MF).run {
  this@followedBy.followedBy<A, B>(arg1) as arrow.core.Tuple2<F, B>
}

@JvmName("apTap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith("this"),
  DeprecationLevel.WARNING
)
fun <F, A, B> Kind<Kind<ForTuple2, F>, A>.apTap(MF: Monoid<F>, arg1: Kind<Kind<ForTuple2, F>, B>):
  Tuple2<F, A> = arrow.core.Tuple2.monad<F>(MF).run {
    this@apTap.apTap<A, B>(arg1) as arrow.core.Tuple2<F, A>
  }

@JvmName("followedByEval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith("arg1.value()"),
  DeprecationLevel.WARNING
)
fun <F, A, B> Kind<Kind<ForTuple2, F>, A>.followedByEval(
  MF: Monoid<F>,
  arg1: Eval<Kind<Kind<ForTuple2, F>, B>>
): Tuple2<F, B> = arrow.core.Tuple2.monad<F>(MF).run {
  this@followedByEval.followedByEval<A, B>(arg1) as arrow.core.Tuple2<F, B>
}

@JvmName("effectM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith("arg1(this.b).let { this }"),
  DeprecationLevel.WARNING
)
fun <F, A, B> Kind<Kind<ForTuple2, F>, A>.effectM(
  MF: Monoid<F>,
  arg1: Function1<A,
    Kind<Kind<ForTuple2, F>, B>>
): Tuple2<F, A> = arrow.core.Tuple2.monad<F>(MF).run {
  this@effectM.effectM<A, B>(arg1) as arrow.core.Tuple2<F, A>
}

@JvmName("flatTap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith("arg1(this.b).let { this }"),
  DeprecationLevel.WARNING
)
fun <F, A, B> Kind<Kind<ForTuple2, F>, A>.flatTap(
  MF: Monoid<F>,
  arg1: Function1<A,
    Kind<Kind<ForTuple2, F>, B>>
): Tuple2<F, A> = arrow.core.Tuple2.monad<F>(MF).run {
  this@flatTap.flatTap<A, B>(arg1) as arrow.core.Tuple2<F, A>
}

@JvmName("productL")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "arg1.let { (f, _) -> f toT this.b }",
    "arrow.core.toT"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B> Kind<Kind<ForTuple2, F>, A>.productL(
  MF: Monoid<F>,
  arg1: Kind<Kind<ForTuple2, F>,
    B>
): Tuple2<F, A> = arrow.core.Tuple2.monad<F>(MF).run {
  this@productL.productL<A, B>(arg1) as arrow.core.Tuple2<F, A>
}

@JvmName("forEffect")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "arg1.let { (f, _) -> f toT this.b }",
    "arrow.core.toT"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B> Kind<Kind<ForTuple2, F>, A>.forEffect(
  MF: Monoid<F>,
  arg1: Kind<Kind<ForTuple2, F>,
    B>
): Tuple2<F, A> = arrow.core.Tuple2.monad<F>(MF).run {
  this@forEffect.forEffect<A, B>(arg1) as arrow.core.Tuple2<F, A>
}

@JvmName("productLEval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "arg1.value().let { (f, _) -> f toT this.b }",
    "arrow.core.toT"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B> Kind<Kind<ForTuple2, F>, A>.productLEval(
  MF: Monoid<F>,
  arg1: Eval<Kind<Kind<ForTuple2, F>, B>>
): Tuple2<F, A> = arrow.core.Tuple2.monad<F>(MF).run {
  this@productLEval.productLEval<A, B>(arg1) as arrow.core.Tuple2<F, A>
}

@JvmName("forEffectEval")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "arg1.value().let { (f, _) -> f toT this.b }",
    "arrow.core.toT"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B> Kind<Kind<ForTuple2, F>, A>.forEffectEval(
  MF: Monoid<F>,
  arg1: Eval<Kind<Kind<ForTuple2, F>, B>>
): Tuple2<F, A> = arrow.core.Tuple2.monad<F>(MF).run {
  this@forEffectEval.forEffectEval<A, B>(arg1) as arrow.core.Tuple2<F, A>
}

@JvmName("mproduct")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "arg1(this.b).value().let { (f, _) -> f toT this.b }",
    "arrow.core.toT"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B> Kind<Kind<ForTuple2, F>, A>.mproduct(
  MF: Monoid<F>,
  arg1: Function1<A,
    Kind<Kind<ForTuple2, F>, B>>
): Tuple2<F, Tuple2<A, B>> = arrow.core.Tuple2.monad<F>(MF).run {
  this@mproduct.mproduct<A, B>(arg1) as arrow.core.Tuple2<F, arrow.core.Tuple2<A, B>>
}

@JvmName("ifM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "if(this.b) arg1.invoke() else arg2.invoke()"
  ),
  DeprecationLevel.WARNING
)
fun <F, B> Kind<Kind<ForTuple2, F>, Boolean>.ifM(
  MF: Monoid<F>,
  arg1: Function0<Kind<Kind<ForTuple2, F>, B>>,
  arg2: Function0<Kind<Kind<ForTuple2, F>, B>>
): Tuple2<F, B> = arrow.core.Tuple2.monad<F>(MF).run {
  this@ifM.ifM<B>(arg1, arg2) as arrow.core.Tuple2<F, B>
}

@JvmName("selectM")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "this.b.fold({ a -> arg1.a toT arg1.b(a)  }, { b -> MF.empty() toT b })",
    "arrow.core.toT"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B> Kind<Kind<ForTuple2, F>, Either<A, B>>.selectM(
  MF: Monoid<F>,
  arg1: Kind<Kind<ForTuple2, F>, Function1<A, B>>
): Tuple2<F, B> =
  arrow.core.Tuple2.monad<F>(MF).run {
    this@selectM.selectM<A, B>(arg1) as arrow.core.Tuple2<F, B>
  }

@JvmName("select")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
    "this.b.fold({ a -> arg1.a toT arg1.b(a)  }, { b -> MF.empty() toT b })",
    "arrow.core.toT"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B> Kind<Kind<ForTuple2, F>, Either<A, B>>.select(
  MF: Monoid<F>,
  arg1: Kind<Kind<ForTuple2, F>, Function1<A, B>>
): Tuple2<F, B> =
  arrow.core.Tuple2.monad<F>(MF).run {
    this@select.select<A, B>(arg1) as arrow.core.Tuple2<F, B>
  }

/**
 *  [Monad] abstract over the ability to declare sequential computations that are dependent in the order or
 *  the results of previous computations.
 *
 *  Given a type constructor [F] with a value of [A] we can compose multiple operations of type
 *  `Kind<F, ?>` where `?` denotes a value being transformed.
 *
 *  This is true for all type constructors that can support the [Monad] type class including and not limited to [Option], [Either], [List] ...
 *
 *  [The Monad Tutorial](https://arrow-kt.io/docs/patterns/monads/)
 */
@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
@Deprecated("Monad typeclasses is deprecated. Use concrete methods on Pair")
inline fun <F> Companion.monad(MF: Monoid<F>): Tuple2Monad<F> = object :
  arrow.core.extensions.Tuple2Monad<F> {
  override fun MF(): arrow.typeclasses.Monoid<F> = MF
}
