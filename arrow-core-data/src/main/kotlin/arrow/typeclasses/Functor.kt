package arrow.typeclasses

import arrow.Kind
import arrow.core.Tuple2
import arrow.documented

/**
 * ank_macro_hierarchy(arrow.typeclasses.Functor)
 *
 * The [Functor] type class abstracts the ability to [map] over the computational context of a type constructor.
 * Examples of type constructors that can implement instances of the Functor type class include
 * [arrow.core.Either], [arrow.core.NonEmptyList], [List] and many other data types that include a [map] function with the shape
 * `fun <F, A, B> Kind<F, A>.map(f: (A) -> B): Kind<F, B>` where `F` refers to any type constructor whose contents can be transformed.
 *
 * ```kotlin:ank:playground:extension
 * _imports_
 *
 * fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   _extensionFactory_
 *   //sampleEnd
 *   println(result)
 * }
 * ```

 * ### Example
 *
 * Oftentimes we find ourselves in situations where we need to transform the contents of some data type.
 * [map] allows us to safely compute over values under the assumption that they'll be there returning the
 * transformation encapsulated in the same context.
 *
 * Consider [arrow.core.Either] and [arrow.core.Ior]:
 *
 * `Either<L, R>` may have two possible cases `Left(l: L)` and `Right(r: R)`. By convention, `Left` is used to model the exceptional
 * case and `Right` for the successful case. `Ior<L, R>` represent a value that can be `Left<A>`, `Right<B>` or `Both<A, B>`.
 *
 * Both [arrow.core.Either] and [arrow.core.Ior] are examples of data types that can be computed over transforming their inner results.
 *
 * ```kotlin:ank:playground
 * import arrow.*
 * import arrow.core.*
 *
 * suspend fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   Either.catch { "1".toInt() }.map { it * 2 }
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 *
 * ```kotlin:ank:playground
 * import arrow.*
 * import arrow.core.*
 * import arrow.core.Ior.Both
 *
 * fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   Both("left", 5).map { it * 2 }
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 *
 */
@documented
interface Functor<F> : Invariant<F> {

  /**
   * Transform the [F] wrapped value [A] into [B] preserving the [F] structure
   * Kind<F, A> -> Kind<F, B>
   *
   * ```kotlin:ank:playground:extension
   * _imports_
   * _imports_applicative_
   *
   * fun main(args: Array<String>) {
   *   val result =
   *   //sampleStart
   *   "Hello"._just_()._map_({ "$it World" })
   *   //sampleEnd
   *   println(result)
   * }
   * ```
   */
  fun <A, B> Kind<F, A>.map(f: (A) -> B): Kind<F, B>

  override fun <A, B> Kind<F, A>.imap(f: (A) -> B, g: (B) -> A): Kind<F, B> =
    map(f)

  /**
   * Lifts a function `A -> B` to the [F] structure returning a polymorphic function
   * that can be applied over all [F] values in the shape of Kind<F, A>
   *
   * `A -> B -> Kind<F, A> -> Kind<F, B>`
   *
   * ```kotlin:ank:playground:extension
   * _imports_
   * _imports_applicative_
   *
   * fun main(args: Array<String>) {
   *   val result =
   *   //sampleStart
   *   _lift_({ s: CharSequence -> "$s World" })("Hello"._just_())
   *   //sampleEnd
   *   println(result)
   * }
   * ```
   */
  fun <A, B> lift(f: (A) -> B): (Kind<F, A>) -> Kind<F, B> = { fa: Kind<F, A> ->
    fa.map(f)
  }

  /**
   * Discards the [A] value inside [F] signaling this container may be pointing to a noop
   * or an effect whose return value is deliberately ignored. The singleton value [Unit] serves as signal.
   *
   * Kind<F, A> -> Kind<F, Unit>
   *
   * ```kotlin:ank:playground:extension
   * _imports_
   * _imports_applicative_
   *
   * fun main(args: Array<String>) {
   *   val result =
   *   //sampleStart
   *   "Hello World"._just_()._void_()
   *   //sampleEnd
   *   println(result)
   * }
   * ```
   */
  fun <A> Kind<F, A>.void(): Kind<F, Unit> =
    map { Unit }

  @Deprecated("Deprecated due to collision with Applicative's unit(), use void() instead", ReplaceWith("void()"))
  fun <A> Kind<F, A>.unit(): Kind<F, Unit> =
    void()

  /**
   * Applies [f] to an [A] inside [F] and returns the [F] structure with a tuple of the [A] value and the
   * computed [B] value as result of applying [f]
   *
   * Kind<F, A> -> Kind<F, Tuple2<A, B>>
   *
   * ```kotlin:ank:playground:extension
   * _imports_
   * _imports_applicative_
   *
   * fun main(args: Array<String>) {
   *   val result =
   *   //sampleStart
   *   "Hello"._just_()._fproduct_({ "$it World" })
   *   //sampleEnd
   *   println(result)
   * }
   * ```
   */
  fun <A, B> Kind<F, A>.fproduct(f: (A) -> B): Kind<F, Tuple2<A, B>> =
    map { a -> Tuple2(a, f(a)) }

  /**
   * Replaces [A] inside [F] with [B] resulting in a Kind<F, B>
   *
   * Kind<F, A> -> Kind<F, B>
   *
   * ```kotlin:ank:playground:extension
   * _imports_
   * _imports_applicative_
   *
   * fun main(args: Array<String>) {
   *   val result =
   *   //sampleStart
   *   "Hello World"._just_()._mapConst_("...")
   *   //sampleEnd
   *   println(result)
   * }
   * ```
   */
  fun <A, B> Kind<F, A>.mapConst(b: B): Kind<F, B> =
    map { b }

  /**
   * Replaces the [B] value inside [F] with [A] resulting in a Kind<F, A>
   */
  fun <A, B> A.mapConst(fb: Kind<F, B>): Kind<F, A> =
    fb.mapConst(this)

  /**
   * Pairs [B] with [A] returning a Kind<F, Tuple2<B, A>>
   *
   * Kind<F, A> -> Kind<F, Tuple2<B, A>>
   *
   * ```kotlin:ank:playground:extension
   * _imports_
   * _imports_applicative_
   *
   * fun main(args: Array<String>) {
   *   val result =
   *   //sampleStart
   *   "Hello"._just_()._tupleLeft_("World")
   *   //sampleEnd
   *   println(result)
   * }
   * ```
   */
  fun <A, B> Kind<F, A>.tupleLeft(b: B): Kind<F, Tuple2<B, A>> =
    map { a -> Tuple2(b, a) }

  /**
   * Pairs [A] with [B] returning a Kind<F, Tuple2<A, B>>
   *
   * Kind<F, A> -> Kind<F, Tuple2<A, B>>
   *
   * ```kotlin:ank:playground:extension
   * _imports_
   * _imports_applicative_
   *
   * fun main(args: Array<String>) {
   *   val result =
   *   //sampleStart
   *   "Hello"._just_()._tupleRight_("World")
   *   //sampleEnd
   *   println(result)
   * }
   * ```
   */
  fun <A, B> Kind<F, A>.tupleRight(b: B): Kind<F, Tuple2<A, B>> =
    map { a -> Tuple2(a, b) }

  /**
   * Given [A] is a sub type of [B], re-type this value from Kind<F, A> to Kind<F, B>
   *
   * Kind<F, A> -> Kind<F, B>
   *
   * ```kotlin:ank:playground:extension
   * _imports_
   * _imports_applicative_
   * import arrow.Kind
   *
   * fun main(args: Array<String>) {
   *   val result: Kind<*, CharSequence> =
   *   //sampleStart
   *   "Hello"._just_()._map_({ "$it World" })._widen_(<>)
   *   //sampleEnd
   *   println(result)
   * }
   * ```
   */
  fun <B, A : B> Kind<F, A>.widen(): Kind<F, B> =
    this
}
