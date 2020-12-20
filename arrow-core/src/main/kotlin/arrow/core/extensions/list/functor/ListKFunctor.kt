package arrow.core.extensions.list.functor

import arrow.Kind
import arrow.core.ForListK
import arrow.core.Tuple2
import arrow.core.extensions.ListKFunctor
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.jvm.JvmName

/**
 *  Transform the [F] wrapped value [A] into [B] preserving the [F] structure
 *  Kind<F, A> -> Kind<F, B>
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 * import arrow.core.extensions.listk.functor.*
 * import arrow.core.*
 *
 *
 *  import arrow.core.extensions.listk.applicative.just
 *
 *  fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   "Hello".just().map({ "$it World" })
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
fun <A, B> List<A>.map(arg1: Function1<A, B>): List<B> =
    arrow.core.extensions.list.functor.List.functor().run {
  arrow.core.ListK(this@map).map<A, B>(arg1) as kotlin.collections.List<B>
}

@JvmName("imap")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A, B> List<A>.imap(arg1: Function1<A, B>, arg2: Function1<B, A>): List<B> =
    arrow.core.extensions.list.functor.List.functor().run {
  arrow.core.ListK(this@imap).imap<A, B>(arg1, arg2) as kotlin.collections.List<B>
}

/**
 *  Lifts a function `A -> B` to the [F] structure returning a polymorphic function
 *  that can be applied over all [F] values in the shape of Kind<F, A>
 *
 *  `A -> B -> Kind<F, A> -> Kind<F, B>`
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 * import arrow.core.extensions.listk.functor.*
 * import arrow.core.*
 *
 *
 *  import arrow.core.extensions.listk.applicative.just
 *
 *  fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   lift({ s: CharSequence -> "$s World" })("Hello".just())
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
fun <A, B> lift(arg0: Function1<A, B>): Function1<Kind<ForListK, A>, Kind<ForListK, B>> =
    arrow.core.extensions.list.functor.List
   .functor()
   .lift<A, B>(arg0) as kotlin.Function1<arrow.Kind<arrow.core.ForListK, A>,
    arrow.Kind<arrow.core.ForListK, B>>

@JvmName("void")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
fun <A> List<A>.void(): List<Unit> = arrow.core.extensions.list.functor.List.functor().run {
  arrow.core.ListK(this@void).void<A>() as kotlin.collections.List<kotlin.Unit>
}

/**
 *  Applies [f] to an [A] inside [F] and returns the [F] structure with a tuple of the [A] value and the
 *  computed [B] value as result of applying [f]
 *
 *  Kind<F, A> -> Kind<F, Tuple2<A, B>>
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 * import arrow.core.extensions.listk.functor.*
 * import arrow.core.*
 *
 *
 *  import arrow.core.extensions.listk.applicative.just
 *
 *  fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   "Hello".just().fproduct({ "$it World" })
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
fun <A, B> List<A>.fproduct(arg1: Function1<A, B>): List<Tuple2<A, B>> =
    arrow.core.extensions.list.functor.List.functor().run {
  arrow.core.ListK(this@fproduct).fproduct<A, B>(arg1) as
    kotlin.collections.List<arrow.core.Tuple2<A, B>>
}

/**
 *  Replaces [A] inside [F] with [B] resulting in a Kind<F, B>
 *
 *  Kind<F, A> -> Kind<F, B>
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 * import arrow.core.extensions.listk.functor.*
 * import arrow.core.*
 *
 *
 *  import arrow.core.extensions.listk.applicative.just
 *
 *  fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   "Hello World".just().mapConst("...")
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
fun <A, B> List<A>.mapConst(arg1: B): List<B> =
    arrow.core.extensions.list.functor.List.functor().run {
  arrow.core.ListK(this@mapConst).mapConst<A, B>(arg1) as kotlin.collections.List<B>
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
fun <A, B> A.mapConst(arg1: List<B>): List<A> =
    arrow.core.extensions.list.functor.List.functor().run {
  this@mapConst.mapConst<A, B>(arrow.core.ListK(arg1)) as kotlin.collections.List<A>
}

/**
 *  Pairs [B] with [A] returning a Kind<F, Tuple2<B, A>>
 *
 *  Kind<F, A> -> Kind<F, Tuple2<B, A>>
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 * import arrow.core.extensions.listk.functor.*
 * import arrow.core.*
 *
 *
 *  import arrow.core.extensions.listk.applicative.just
 *
 *  fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   "Hello".just().tupleLeft("World")
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
fun <A, B> List<A>.tupleLeft(arg1: B): List<Tuple2<B, A>> =
    arrow.core.extensions.list.functor.List.functor().run {
  arrow.core.ListK(this@tupleLeft).tupleLeft<A, B>(arg1) as
    kotlin.collections.List<arrow.core.Tuple2<B, A>>
}

/**
 *  Pairs [A] with [B] returning a Kind<F, Tuple2<A, B>>
 *
 *  Kind<F, A> -> Kind<F, Tuple2<A, B>>
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 * import arrow.core.extensions.listk.functor.*
 * import arrow.core.*
 *
 *
 *  import arrow.core.extensions.listk.applicative.just
 *
 *  fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   "Hello".just().tupleRight("World")
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
fun <A, B> List<A>.tupleRight(arg1: B): List<Tuple2<A, B>> =
    arrow.core.extensions.list.functor.List.functor().run {
  arrow.core.ListK(this@tupleRight).tupleRight<A, B>(arg1) as
    kotlin.collections.List<arrow.core.Tuple2<A, B>>
}

/**
 *  Given [A] is a sub type of [B], re-type this value from Kind<F, A> to Kind<F, B>
 *
 *  Kind<F, A> -> Kind<F, B>
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 * import arrow.core.extensions.listk.functor.*
 * import arrow.core.*
 *
 *
 *  import arrow.core.extensions.listk.applicative.just
 *  import arrow.Kind
 *
 *  fun main(args: Array<String>) {
 *   val result: Kind<*, CharSequence> =
 *   //sampleStart
 *   "Hello".just().map({ "$it World" }).widen()
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
fun <B, A : B> List<A>.widen(): List<B> = arrow.core.extensions.list.functor.List.functor().run {
  arrow.core.ListK(this@widen).widen<B, A>() as kotlin.collections.List<B>
}

/**
 * cached extension
 */
@PublishedApi()
internal val functor_singleton: ListKFunctor = object : arrow.core.extensions.ListKFunctor {}

object List {
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
   * import arrow.core.extensions.listk.functor.*
   * import arrow.core.*
   *
   *
   *
   *  fun main(args: Array<String>) {
   *   val result =
   *   //sampleStart
   *   ListK.functor()
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
  inline fun functor(): ListKFunctor = functor_singleton}
