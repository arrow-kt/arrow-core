package arrow.core.extensions.tuple2.functor

import arrow.Kind
import arrow.core.ForTuple2
import arrow.core.Tuple2
import arrow.core.Tuple2.Companion
import arrow.core.extensions.Tuple2Functor
import kotlin.Any
import kotlin.Deprecated
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.Unit
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val functor_singleton: Tuple2Functor<Any?> = object : Tuple2Functor<Any?> {}

/**
 *  Transform the [F] wrapped value [A] into [B] preserving the [F] structure
 *  Kind<F, A> -> Kind<F, B>
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 * import arrow.core.extensions.tuple2.functor.*
 * import arrow.core.*
 *
 *
 *  import arrow.core.extensions.tuple2.applicative.just
 * import arrow.core.extensions.monoid
 *
 *  fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   "Hello".just<String, String>(String.monoid()).map<String, String, String>({ "$it World" })
 *   //sampleEnd
 *   println(result)
 *  }
 *  ```
 */
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
  "map(arg1)",
  "arrow.core.map"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B> Kind<Kind<ForTuple2, F>, A>.map(arg1: Function1<A, B>): Tuple2<F, B> =
    arrow.core.Tuple2.functor<F>().run {
  this@map.map<A, B>(arg1) as arrow.core.Tuple2<F, B>
}

@JvmName("imap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "imap(arg1, arg2)",
  "arrow.core.imap"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B> Kind<Kind<ForTuple2, F>, A>.imap(arg1: Function1<A, B>, arg2: Function1<B, A>):
    Tuple2<F, B> = arrow.core.Tuple2.functor<F>().run {
  this@imap.imap<A, B>(arg1, arg2) as arrow.core.Tuple2<F, B>
}

/**
 *  Lifts a function `A -> B` to the [F] structure returning a polymorphic function
 *  that can be applied over all [F] values in the shape of Kind<F, A>
 *
 *  `A -> B -> Kind<F, A> -> Kind<F, B>`
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 * import arrow.core.extensions.tuple2.functor.*
 * import arrow.core.*
 *
 *
 *  import arrow.core.extensions.tuple2.applicative.just
 * import arrow.core.extensions.monoid
 *
 *  fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   lift<String, String, String>({ s: CharSequence -> "$s World" })("Hello".just<String,
 * String>(String.monoid()))
 *   //sampleEnd
 *   println(result)
 *  }
 *  ```
 */
@JvmName("lift")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "lift(arg0)",
  "arrow.core.Tuple2.lift"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B> lift(arg0: Function1<A, B>): Function1<Kind<Kind<ForTuple2, F>, A>,
    Kind<Kind<ForTuple2, F>, B>> = arrow.core.Tuple2
   .functor<F>()
   .lift<A, B>(arg0) as kotlin.Function1<arrow.Kind<arrow.Kind<arrow.core.ForTuple2, F>, A>,
    arrow.Kind<arrow.Kind<arrow.core.ForTuple2, F>, B>>

@JvmName("void")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "void()",
  "arrow.core.void"
  ),
  DeprecationLevel.WARNING
)
fun <F, A> Kind<Kind<ForTuple2, F>, A>.void(): Tuple2<F, Unit> =
    arrow.core.Tuple2.functor<F>().run {
  this@void.void<A>() as arrow.core.Tuple2<F, kotlin.Unit>
}

/**
 *  Applies [f] to an [A] inside [F] and returns the [F] structure with a tuple of the [A] value and the
 *  computed [B] value as result of applying [f]
 *
 *  Kind<F, A> -> Kind<F, Tuple2<A, B>>
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 * import arrow.core.extensions.tuple2.functor.*
 * import arrow.core.*
 *
 *
 *  import arrow.core.extensions.tuple2.applicative.just
 * import arrow.core.extensions.monoid
 *
 *  fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   "Hello".just<String, String>(String.monoid()).fproduct<String, String, String>({ "$it World" })
 *   //sampleEnd
 *   println(result)
 *  }
 *  ```
 */
@JvmName("fproduct")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "fproduct(arg1)",
  "arrow.core.fproduct"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B> Kind<Kind<ForTuple2, F>, A>.fproduct(arg1: Function1<A, B>): Tuple2<F, Tuple2<A, B>> =
    arrow.core.Tuple2.functor<F>().run {
  this@fproduct.fproduct<A, B>(arg1) as arrow.core.Tuple2<F, arrow.core.Tuple2<A, B>>
}

/**
 *  Replaces [A] inside [F] with [B] resulting in a Kind<F, B>
 *
 *  Kind<F, A> -> Kind<F, B>
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 * import arrow.core.extensions.tuple2.functor.*
 * import arrow.core.*
 *
 *
 *  import arrow.core.extensions.tuple2.applicative.just
 * import arrow.core.extensions.monoid
 *
 *  fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   "Hello World".just<String, String>(String.monoid()).mapConst<String, String, String>("...")
 *   //sampleEnd
 *   println(result)
 *  }
 *  ```
 */
@JvmName("mapConst")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "mapConst(arg1)",
  "arrow.core.mapConst"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B> Kind<Kind<ForTuple2, F>, A>.mapConst(arg1: B): Tuple2<F, B> =
    arrow.core.Tuple2.functor<F>().run {
  this@mapConst.mapConst<A, B>(arg1) as arrow.core.Tuple2<F, B>
}

/**
 *  Replaces the [B] value inside [F] with [A] resulting in a Kind<F, A>
 */
@JvmName("mapConst")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "mapConst(arg1)",
  "arrow.core.mapConst"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B> A.mapConst(arg1: Kind<Kind<ForTuple2, F>, B>): Tuple2<F, A> =
    arrow.core.Tuple2.functor<F>().run {
  this@mapConst.mapConst<A, B>(arg1) as arrow.core.Tuple2<F, A>
}

/**
 *  Pairs [B] with [A] returning a Kind<F, Tuple2<B, A>>
 *
 *  Kind<F, A> -> Kind<F, Tuple2<B, A>>
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 * import arrow.core.extensions.tuple2.functor.*
 * import arrow.core.*
 *
 *
 *  import arrow.core.extensions.tuple2.applicative.just
 * import arrow.core.extensions.monoid
 *
 *  fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   "Hello".just<String, String>(String.monoid()).tupleLeft<String, String, String>("World")
 *   //sampleEnd
 *   println(result)
 *  }
 *  ```
 */
@JvmName("tupleLeft")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "tupleLeft(arg1)",
  "arrow.core.tupleLeft"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B> Kind<Kind<ForTuple2, F>, A>.tupleLeft(arg1: B): Tuple2<F, Tuple2<B, A>> =
    arrow.core.Tuple2.functor<F>().run {
  this@tupleLeft.tupleLeft<A, B>(arg1) as arrow.core.Tuple2<F, arrow.core.Tuple2<B, A>>
}

/**
 *  Pairs [A] with [B] returning a Kind<F, Tuple2<A, B>>
 *
 *  Kind<F, A> -> Kind<F, Tuple2<A, B>>
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 * import arrow.core.extensions.tuple2.functor.*
 * import arrow.core.*
 *
 *
 *  import arrow.core.extensions.tuple2.applicative.just
 * import arrow.core.extensions.monoid
 *
 *  fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   "Hello".just<String, String>(String.monoid()).tupleRight<String, String, String>("World")
 *   //sampleEnd
 *   println(result)
 *  }
 *  ```
 */
@JvmName("tupleRight")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "tupleRight(arg1)",
  "arrow.core.tupleRight"
  ),
  DeprecationLevel.WARNING
)
fun <F, A, B> Kind<Kind<ForTuple2, F>, A>.tupleRight(arg1: B): Tuple2<F, Tuple2<A, B>> =
    arrow.core.Tuple2.functor<F>().run {
  this@tupleRight.tupleRight<A, B>(arg1) as arrow.core.Tuple2<F, arrow.core.Tuple2<A, B>>
}

/**
 *  Given [A] is a sub type of [B], re-type this value from Kind<F, A> to Kind<F, B>
 *
 *  Kind<F, A> -> Kind<F, B>
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 * import arrow.core.extensions.tuple2.functor.*
 * import arrow.core.*
 *
 *
 *  import arrow.core.extensions.tuple2.applicative.just
 * import arrow.core.extensions.monoid
 *  import arrow.Kind
 *
 *  fun main(args: Array<String>) {
 *   val result: Kind<*, CharSequence> =
 *   //sampleStart
 *   "Hello".just<String, String>(String.monoid()).map<String, String,
 * String>({ "$it World" }).widen<String, String, String>()
 *   //sampleEnd
 *   println(result)
 *  }
 *  ```
 */
@JvmName("widen")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "widen()",
  "arrow.core.widen"
  ),
  DeprecationLevel.WARNING
)
fun <F, B, A : B> Kind<Kind<ForTuple2, F>, A>.widen(): Tuple2<F, B> =
    arrow.core.Tuple2.functor<F>().run {
  this@widen.widen<B, A>() as arrow.core.Tuple2<F, B>
}

/**
 *  ank_macro_hierarchy(arrow.typeclasses.Functor)
 *
 *  The [Functor] type class abstracts the ability to [map] over the computational context of a type constructor.
 *  Examples of type constructors that can implement instances of the Functor type class include
 *  [arrow.core.Option], [arrow.core.NonEmptyList], [List] and many other data types that include a [map] function with the shape
 *  `fun <F, A, B> Kind<F, A>.map(f: (A) -> B): Kind<F, B>` where `F` refers to any type constructor whose contents can be transformed.
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 * import arrow.core.extensions.tuple2.functor.*
 * import arrow.core.*
 *
 *
 *
 *  fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   Tuple2.functor<String>()
 *   //sampleEnd
 *   println(result)
 *  }
 *  ```
 *
 *  ### Example
 *
 *  Oftentimes we find ourselves in situations where we need to transform the contents of some data type.
 *  [map] allows us to safely compute over values under the assumption that they'll be there returning the
 *  transformation encapsulated in the same context.
 *
 *  Consider [arrow.core.Option] and [arrow.core.Either]:
 *
 *  `Option<A>` allows us to model absence and has two possible states, `Some(a: A)` if the value is not absent and `None` to represent an empty case.
 *  In a similar fashion `Either<L, R>` may have two possible cases `Left(l: L)` and `Right(r: R)`. By convention, `Left` is used to model the exceptional
 *  case and `Right` for the successful case.
 *
 *  Both [arrow.core.Either] and [arrow.core.Option] are examples of data types that can be computed over transforming their inner results.
 *
 *  ```kotlin:ank:playground
 *  import arrow.*
 *  import arrow.core.*
 *
 *  suspend fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 * 2 }
 *   //sampleEnd
 *   println(result)
 *  }
 *  ```
 *
 *  ```kotlin:ank:playground
 *  import arrow.*
 *  import arrow.core.*
 *
 *  fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 * 2 }
 *   //sampleEnd
 *   println(result)
 *  }
 *  ```
 */
@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun <F> Companion.functor(): Tuple2Functor<F> = functor_singleton as
    arrow.core.extensions.Tuple2Functor<F>