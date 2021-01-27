package arrow.core

import arrow.Kind
import arrow.typeclasses.Applicative
import arrow.typeclasses.Eq
import arrow.typeclasses.Hash
import arrow.typeclasses.Monoid
import arrow.typeclasses.Order
import arrow.typeclasses.Semigroup
import arrow.typeclasses.Show
import arrow.typeclasses.hashWithSalt

@Deprecated("Kind is deprecated, and will be removed in 0.13.0. Please use one of the provided concrete methods instead")
class ForIor private constructor() { companion object }
@Deprecated("Kind is deprecated, and will be removed in 0.13.0. Please use one of the provided concrete methods instead")
typealias IorOf<A, B> = arrow.Kind2<ForIor, A, B>
@Deprecated("Kind is deprecated, and will be removed in 0.13.0. Please use one of the provided concrete methods instead")
typealias IorPartialOf<A> = arrow.Kind<ForIor, A>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
@Deprecated("Kind is deprecated, and will be removed in 0.13.0. Please use one of the provided concrete methods instead")
inline fun <A, B> IorOf<A, B>.fix(): Ior<A, B> =
  this as Ior<A, B>

typealias IorNel<A, B> = Ior<Nel<A>, B>

/**
 * Port of https://github.com/typelevel/cats/blob/v0.9.0/core/src/main/scala/cats/data/Ior.scala
 *
 * Represents a right-biased disjunction that is either an `A`, or a `B`, or both an `A` and a `B`.
 *
 * An instance of [Ior]<`A`,`B`> is one of:
 *  - [Ior.Left] <`A`>
 *  - [Ior.Right] <`B`>
 *  - [Ior.Both]<`A`,`B`>
 *
 * [Ior]<`A`,`B`> is similar to [Either]<`A`,`B`>, except that it can represent the simultaneous presence of
 * an `A` and a `B`. It is right-biased so methods such as `map` and `flatMap` operate on the
 * `B` value. Some methods, like `flatMap`, handle the presence of two [Ior.Both] values using a
 * `[Semigroup]<`A`>, while other methods, like [toEither], ignore the `A` value in a [Ior.Both Both].
 *
 * [Ior]<`A`,`B`> is isomorphic to [Either]<[Either]<`A`,`B`>, [Pair]<`A`,`B`>>, but provides methods biased toward `B`
 * values, regardless of whether the `B` values appear in a [Ior.Right] or a [Ior.Both].
 * The isomorphic Either form can be accessed via the [unwrap] method.
 */
sealed class Ior<out A, out B> : IorOf<A, B> {

  /**
   * Returns `true` if this is a [Right], `false` otherwise.
   *
   * Example:
   * ```
   * Left("tulip").isRight           // Result: false
   * Right("venus fly-trap").isRight // Result: true
   * Both("venus", "fly-trap").isRight // Result: false
   * ```
   */
  abstract val isRight: Boolean

  /**
   * Returns `true` if this is a [Left], `false` otherwise.
   *
   * Example:
   * ```
   * Left("tulip").isLeft           // Result: true
   * Right("venus fly-trap").isLeft // Result: false
   * Both("venus", "fly-trap").isLeft // Result: false
   * ```
   */
  abstract val isLeft: Boolean

  /**
   * Returns `true` if this is a [Both], `false` otherwise.
   *
   * Example:
   * ```
   * Left("tulip").isBoth           // Result: false
   * Right("venus fly-trap").isBoth // Result: false
   * Both("venus", "fly-trap").isBoth // Result: true
   * ```
   */
  abstract val isBoth: Boolean

  companion object {
    /**
     * Create an [Ior] from two Options if at least one of them is defined.
     *
     * @param oa an element (optional) for the left side of the [Ior]
     * @param ob an element (optional) for the right side of the [Ior]
     *
     * @return [None] if both [oa] and [ob] are [None]. Otherwise [Some] wrapping
     * an [Ior.Left], [Ior.Right], or [Ior.Both] if [oa], [ob], or both are defined (respectively).
     */
    @Deprecated("Deprecated, use `fromNullables` instead", ReplaceWith("fromNullables(a, b)"))
    fun <A, B> fromOptions(oa: Option<A>, ob: Option<B>): Option<Ior<A, B>> = when (oa) {
      is Some -> when (ob) {
        is Some -> Some(Both(oa.t, ob.t))
        is None -> Some<Ior<A, B>>(Left(oa.t))
      }
      is None -> when (ob) {
        is Some -> Some(Right(ob.t))
        is None -> None
      }
    }

    /**
     * Create an [Ior] from two nullables if at least one of them is defined.
     *
     * @param a an element (nullable) for the left side of the [Ior]
     * @param b an element (nullable) for the right side of the [Ior]
     *
     * @return [null] if both [a] and [b] are [null]. Otherwise
     * an [Ior.Left], [Ior.Right], or [Ior.Both] if [a], [b], or both are defined (respectively).
     */
    fun <A, B> fromNullables(a: A?, b: B?): Ior<A, B>? =
      when (a != null) {
        true -> when (b != null) {
          true -> Both(a, b)
          false -> Left(a)
        }
        false -> when (b != null) {
          true -> Right(b)
          false -> null
        }
      }

    private tailrec fun <L, A, B> Semigroup<L>.loop(v: Ior<L, Either<A, B>>, f: (A) -> IorOf<L, Either<A, B>>): Ior<L, B> = when (v) {
      is Left -> Left(v.value)
      is Right -> when (v.value) {
        is Either.Right -> Right(v.value.b)
        is Either.Left -> loop(f(v.value.a).fix(), f)
      }
      is Both -> when (v.rightValue) {
        is Either.Right -> Both(v.leftValue, v.rightValue.b)
        is Either.Left -> {
          val fnb = f(v.rightValue.a).fix()
          when (fnb) {
            is Left -> Left(v.leftValue.combine(fnb.value))
            is Right -> loop(Both(v.leftValue, fnb.value), f)
            is Both -> loop(Both(v.leftValue.combine(fnb.leftValue), fnb.rightValue), f)
          }
        }
      }
    }

    fun <L, A, B> tailRecM(a: A, f: (A) -> IorOf<L, Either<A, B>>, SL: Semigroup<L>): Ior<L, B> =
      SL.run { loop(f(a).fix(), f) }

    fun <A, B> leftNel(a: A): IorNel<A, B> = Left(NonEmptyList.of(a))

    fun <A, B> bothNel(a: A, b: B): IorNel<A, B> = Both(NonEmptyList.of(a), b)

    /**
     *  Lifts a function `(B) -> C` to the [Ior] structure returning a polymorphic function
     *  that can be applied over all [Ior] values in the shape of Ior<A, B>
     *
     *  ```kotlin:ank:playground
     *  import arrow.core.*
     *
     *  fun main(args: Array<String>) {
     *   //sampleStart
     *   val f = Ior.lift<Int, CharSequence, String> { s: CharSequence -> "$s World" }
     *   val ior: Ior<Int, CharSequence> = Ior.Right("Hello")
     *   val result = f(ior)
     *   //sampleEnd
     *   println(result)
     *  }
     *  ```
     */
    fun <A, B, C> lift(f: (B) -> C): (Ior<A, B>) -> Ior<A, C> =
      { it.map(f) }

    fun <A, B, C, D> lift(fa: (A) -> C, fb: (B) -> D): (Ior<A, B>) -> Ior<C, D> =
      { it.bimap(fa, fb) }

    inline fun <A, B, C, D> mapN(
      SA: Semigroup<A>,
      b: Ior<A, B>,
      c: Ior<A, C>,
      map: (B, C) -> D
    ): Ior<A, D> =
      mapN(SA, b, c, Ior.unit, Ior.unit, Ior.unit, Ior.unit, Ior.unit, Ior.unit, Ior.unit, Ior.unit) { b, c, _, _, _, _, _, _, _, _ -> map(b, c) }

    inline fun <A, B, C, D, E> mapN(
      SA: Semigroup<A>,
      b: Ior<A, B>,
      c: Ior<A, C>,
      d: Ior<A, D>,
      map: (B, C, D) -> E
    ): Ior<A, E> =
      mapN(SA, b, c, d, Ior.unit, Ior.unit, Ior.unit, Ior.unit, Ior.unit, Ior.unit, Ior.unit) { b, c, d, _, _, _, _, _, _, _ -> map(b, c, d) }

    inline fun <A, B, C, D, E, F> mapN(
      SA: Semigroup<A>,
      b: Ior<A, B>,
      c: Ior<A, C>,
      d: Ior<A, D>,
      e: Ior<A, E>,
      map: (B, C, D, E) -> F
    ): Ior<A, F> =
      mapN(SA, b, c, d, e, Ior.unit, Ior.unit, Ior.unit, Ior.unit, Ior.unit, Ior.unit) { b, c, d, e, _, _, _, _, _, _ -> map(b, c, d, e) }

    inline fun <A, B, C, D, E, F, G> mapN(
      SA: Semigroup<A>,
      b: Ior<A, B>,
      c: Ior<A, C>,
      d: Ior<A, D>,
      e: Ior<A, E>,
      f: Ior<A, F>,
      map: (B, C, D, E, F) -> G
    ): Ior<A, G> =
      mapN(SA, b, c, d, e, f, Ior.unit, Ior.unit, Ior.unit, Ior.unit, Ior.unit) { b, c, d, e, f, _, _, _, _, _ -> map(b, c, d, e, f) }

    inline fun <A, B, C, D, E, F, G, H> mapN(
      SA: Semigroup<A>,
      b: Ior<A, B>,
      c: Ior<A, C>,
      d: Ior<A, D>,
      e: Ior<A, E>,
      f: Ior<A, F>,
      g: Ior<A, G>,
      map: (B, C, D, E, F, G) -> H
    ): Ior<A, H> =
      mapN(SA, b, c, d, e, f, g, Ior.unit, Ior.unit, Ior.unit, Ior.unit) { b, c, d, e, f, g, _, _, _, _ -> map(b, c, d, e, f, g) }

    inline fun <A, B, C, D, E, F, G, H, I> mapN(
      SA: Semigroup<A>,
      b: Ior<A, B>,
      c: Ior<A, C>,
      d: Ior<A, D>,
      e: Ior<A, E>,
      f: Ior<A, F>,
      g: Ior<A, G>,
      h: Ior<A, H>,
      map: (B, C, D, E, F, G, H) -> I
    ): Ior<A, I> =
      mapN(SA, b, c, d, e, f, g, h, Ior.unit, Ior.unit, Ior.unit) { b, c, d, e, f, g, h, _, _, _ -> map(b, c, d, e, f, g, h) }

    inline fun <A, B, C, D, E, F, G, H, I, J> mapN(
      SA: Semigroup<A>,
      b: Ior<A, B>,
      c: Ior<A, C>,
      d: Ior<A, D>,
      e: Ior<A, E>,
      f: Ior<A, F>,
      g: Ior<A, G>,
      h: Ior<A, H>,
      i: Ior<A, I>,
      map: (B, C, D, E, F, G, H, I) -> J
    ): Ior<A, J> =
      mapN(SA, b, c, d, e, f, g, h, i, Ior.unit, Ior.unit) { b, c, d, e, f, g, h, i, _, _ -> map(b, c, d, e, f, g, h, i) }

    inline fun <A, B, C, D, E, F, G, H, I, J, K> mapN(
      SA: Semigroup<A>,
      b: Ior<A, B>,
      c: Ior<A, C>,
      d: Ior<A, D>,
      e: Ior<A, E>,
      f: Ior<A, F>,
      g: Ior<A, G>,
      h: Ior<A, H>,
      i: Ior<A, I>,
      j: Ior<A, J>,
      map: (B, C, D, E, F, G, H, I, J) -> K
    ): Ior<A, K> =
      mapN(SA, b, c, d, e, f, g, h, i, j, Ior.unit) { b, c, d, e, f, g, h, i, j, _ -> map(b, c, d, e, f, g, h, i, j) }

    inline fun <A, B, C, D, E, F, G, H, I, J, K, L> mapN(
      SA: Semigroup<A>,
      b: Ior<A, B>,
      c: Ior<A, C>,
      d: Ior<A, D>,
      e: Ior<A, E>,
      f: Ior<A, F>,
      g: Ior<A, G>,
      h: Ior<A, H>,
      i: Ior<A, I>,
      j: Ior<A, J>,
      k: Ior<A, K>,
      map: (B, C, D, E, F, G, H, I, J, K) -> L
    ): Ior<A, L> = TODO()
//      b.flatMap(SA) { bb ->
//        c.flatMap(SA) { cc ->
//          d.flatMap(SA) { dd ->
//            e.flatMap(SA) { ee ->
//              f.flatMap(SA) { ff ->
//                g.flatMap(SA) { gg ->
//                  h.flatMap(SA) { hh ->
//                    i.flatMap(SA) { ii ->
//                      j.flatMap(SA) { jj ->
//                        k.map { kk ->
//                          map(bb, cc, dd, ee, ff, gg, hh, ii, jj, kk)
//                        }
//                      }
//                    }
//                  }
//                }
//              }
//            }
//          }
//        }
//      }

    val unit: Ior<Nothing, Unit> = Right(Unit)

    fun <L> unit(): Ior<L, Unit> = unit
  }

  /**
   * Applies `fa` if this is a [Left], `fb` if this is a [Right] or `fab` if this is a [Both]
   *
   * Example:
   * ```
   * val result: Ior<EmailContactInfo, PostalContactInfo> = obtainContactInfo()
   * result.fold(
   *      { log("only have this email info: $it") },
   *      { log("only have this postal info: $it") },
   *      { email, postal -> log("have this postal info: $postal and this email info: $email") }
   * )
   * ```
   *
   * @param fa the function to apply if this is a [Left]
   * @param fb the function to apply if this is a [Right]
   * @param fab the function to apply if this is a [Both]
   * @return the results of applying the function
   */
  inline fun <C> fold(fa: (A) -> C, fb: (B) -> C, fab: (A, B) -> C): C = when (this) {
    is Left -> fa(value)
    is Right -> fb(value)
    is Both -> fab(leftValue, rightValue)
  }

  inline fun <C> foldLeft(c: C, f: (C, B) -> C): C =
    fold({ c }, { f(c, it) }, { _, b -> f(c, b) })

  fun <C> foldRight(lc: Eval<C>, f: (B, Eval<C>) -> Eval<C>): Eval<C> =
    fold({ lc }, { Eval.defer { f(it, lc) } }, { _, b -> Eval.defer { f(b, lc) } })

  fun <G, C> traverse(GA: Applicative<G>, f: (B) -> Kind<G, C>): Kind<G, Ior<A, C>> = GA.run {
    fold({ just(Left(it)) }, { b -> f(b).map { Right(it) } }, { _, b -> f(b).map { Right(it) } })
  }

  inline fun <C> bifoldLeft(c: C, f: (C, A) -> C, g: (C, B) -> C): C =
    fold({ f(c, it) }, { g(c, it) }, { a, b -> g(f(c, a), b) })

  inline fun <C> bifoldRight(c: Eval<C>, f: (A, Eval<C>) -> Eval<C>, g: (B, Eval<C>) -> Eval<C>): Eval<C> =
    fold({ f(it, c) }, { g(it, c) }, { a, b -> f(a, g(b, c)) })

  inline fun <C> bifoldMap(MN: Monoid<C>, f: (A) -> C, g: (B) -> C): C = MN.run {
    bifoldLeft(MN.empty(), { c, a -> c.combine(f(a)) }, { c, b -> c.combine(g(b)) })
  }

  /**
   * The given function is applied if this is a [Right] or [Both] to `B`.
   *
   * Example:
   * ```
   * Ior.Right(12).map { "flower" } // Result: Right("flower")
   * Ior.Left(12).map { "flower" }  // Result: Left(12)
   * Ior.Both(12, "power").map { "flower $it" }  // Result: Both(12, "flower power")
   * ```
   */
  inline fun <D> map(f: (B) -> D): Ior<A, D> = fold(
    ::Left,
    { Right(f(it)) },
    { a, b -> Both(a, f(b)) }
  )

  /**
   * Apply `fa` if this is a [Left] or [Both] to `A`
   * and apply `fb` if this is [Right] or [Both] to `B`
   *
   * Example:
   * ```
   * Ior.Right(12).bimap ({ "flower" }, { 12 }) // Result: Right(12)
   * Ior.Left(12).bimap({ "flower" }, { 12 })  // Result: Left("flower")
   * Ior.Both(12, "power").bimap ({ a, b -> "flower $b" },{ a * 2})  // Result: Both("flower power", 24)
   * ```
   */
  inline fun <C, D> bimap(fa: (A) -> C, fb: (B) -> D): Ior<C, D> = fold(
    { Left(fa(it)) },
    { Right(fb(it)) },
    { a, b -> Both(fa(a), fb(b)) }
  )

  /**
   * The given function is applied if this is a [Left] or [Both] to `A`.
   *
   * Example:
   * ```
   * Ior.Right(12).map { "flower" } // Result: Right(12)
   * Ior.Left(12).map { "flower" }  // Result: Left("power")
   * Ior.Both(12, "power").map { "flower $it" }  // Result: Both("flower 12", "power")
   * ```
   */
  inline fun <C> mapLeft(fa: (A) -> C): Ior<C, B> = fold(
    { Left(fa(it)) },
    ::Right,
    { a, b -> Both(fa(a), b) }
  )

  /**
   * If this is a [Left], then return the left value in [Right] or vice versa,
   * when this is [Both] , left and right values are swap
   *
   * Example:
   * ```
   * Left("left").swap()   // Result: Right("left")
   * Right("right").swap() // Result: Left("right")
   * Both("left", "right").swap() // Result: Both("right", "left")
   * ```
   */
  fun swap(): Ior<B, A> = fold(
    { Right(it) },
    { Left(it) },
    { a, b -> Both(b, a) }
  )

  /**
   * Return the isomorphic [Either] of this [Ior]
   */
  fun unwrap(): Either<Either<A, B>, Pair<A, B>> = fold(
    { Either.Left(Either.Left(it)) },
    { Either.Left(Either.Right(it)) },
    { a, b -> Either.Right(Pair(a, b)) }
  )

  /**
   * Return this [Ior] as [Pair] of [Option]
   *
   * Example:
   * ```
   * Ior.Right(12).pad()          // Result: Pair(None, Some(12))
   * Ior.Left(12).pad()           // Result: Pair(Some(12), None)
   * Ior.Both("power", 12).pad()  // Result: Pair(Some("power"), Some(12))
   * ```
   */
  @Deprecated("Deprecated, use `padNull` instead", ReplaceWith("padNull()"))
  fun pad(): Pair<Option<A>, Option<B>> = fold(
    { Pair(Some(it), None) },
    { Pair(None, Some(it)) },
    { a, b -> Pair(Some(a), Some(b)) }
  )

  /**
   * Return this [Ior] as [Pair] of nullables]
   *
   * Example:
   * ```kotlin:ank:playground
   * import arrow.core.Ior
   *
   * //sampleStart
   * val right = Ior.Right(12).padNull()         // Result: Pair(null, 12)
   * val left = Ior.Left(12).padNull()           // Result: Pair(12, null)
   * val both = Ior.Both("power", 12).padNull()  // Result: Pair("power", 12)
   * //sampleEnd
   *
   * fun main() {
   *   println("right = $right")
   *   println("left = $left")
   *   println("both = $both")
   * }
   * ```
   */
  fun padNull(): Pair<A?, B?> = fold(
    { Pair(it, null) },
    { Pair(null, it) },
    { a, b -> Pair(a, b) }
  )

  /**
   * Returns a [Either.Right] containing the [Right] value or `B` if this is [Right] or [Both]
   * and [Either.Left] if this is a [Left].
   *
   * Example:
   * ```
   * Right(12).toEither() // Result: Either.Right(12)
   * Left(12).toEither()  // Result: Either.Left(12)
   * Both("power", 12).toEither()  // Result: Either.Right(12)
   * ```
   */
  fun toEither(): Either<A, B> =
    fold({ Either.Left(it) }, { Either.Right(it) }, { _, b -> Either.Right(b) })

  /**
   * Returns a [Some] containing the [Right] value or `B` if this is [Right] or [Both]
   * and [None] if this is a [Left].
   *
   * Example:
   * ```
   * Right(12).toOption() // Result: Some(12)
   * Left(12).toOption()  // Result: None
   * Both(12, "power").toOption()  // Result: Some("power")
   * ```
   */
  @Deprecated("Deprecated, use `orNull` instead", ReplaceWith("orNull()"))
  fun toOption(): Option<B> =
    fold({ None }, { Some(it) }, { _, b -> Some(b) })

  /**
   * Returns the [Right] value or `B` if this is [Right] or [Both]
   * and [null] if this is a [Left].
   *
   * Example:
   * ```kotlin:ank:playground
   * import arrow.core.Ior
   *
   * //sampleStart
   * val right = Ior.Right(12).orNull()         // Result: 12
   * val left = Ior.Left(12).orNull()           // Result: null
   * val both = Ior.Both(12, "power").orNull()  // Result: "power"
   * //sampleEnd
   * fun main() {
   *   println("right = $right")
   *   println("left = $left")
   *   println("both = $both")
   * }
   * ```
   */
  fun orNull(): B? =
    fold({ null }, { it }, { _, b -> b })

  /**
   * Returns a [Some] containing the [Left] value or `A` if this is [Left] or [Both]
   * and [None] if this is a [Right].
   *
   * Example:
   * ```
   * Right(12).toLeftOption() // Result: None
   * Left(12).toLeftOption()  // Result: Some(12)
   * Both(12, "power").toLeftOption()  // Result: Some(12)
   * ```
   */
  @Deprecated("Deprecated, use `leftOrNull` instead", ReplaceWith("leftOrNull()"))
  fun toLeftOption(): Option<A> =
    fold({ Option.just(it) }, { Option.empty() }, { a, _ -> Option.just(a) })

  /**
   * Returns the [Left] value or `A` if this is [Left] or [Both]
   * and [null] if this is a [Right].
   *
   * Example:
   * ```kotlin:ank:playground
   * import arrow.core.Ior
   *
   * //sampleStart
   * val right = Ior.Right(12).leftOrNull()         // Result: null
   * val left = Ior.Left(12).leftOrNull()           // Result: 12
   * val both = Ior.Both(12, "power").leftOrNull()  // Result: 12
   * //sampleEnd
   *
   * fun main() {
   *   println("right = $right")
   *   println("left = $left")
   *   println("both = $both")
   * }
   * ```
   */
  fun leftOrNull(): A? =
    fold({ it }, { null }, { a, _ -> a })

  /**
   * Returns a [Validated.Valid] containing the [Right] value or `B` if this is [Right] or [Both]
   * and [Validated.Invalid] if this is a [Left].
   *
   * Example:
   * ```
   * Right(12).toValidated() // Result: Valid(12)
   * Left(12).toValidated()  // Result: Invalid(12)
   * Both(12, "power").toValidated()  // Result: Valid("power")
   * ```
   */
  fun toValidated(): Validated<A, B> =
    fold({ Invalid(it) }, { Valid(it) }, { _, b -> Valid(b) })

  data class Left<out A>(val value: A) : Ior<A, Nothing>() {
    override val isRight: Boolean get() = false
    override val isLeft: Boolean get() = true
    override val isBoth: Boolean get() = false

    override fun toString(): String = show(Show.any(), Show.any())

    companion object {
      @Deprecated("Deprecated, use the constructor instead", ReplaceWith("Left(a)"))
      operator fun <A> invoke(a: A): Ior<A, Nothing> = Left(a)
    }
  }

  data class Right<out B>(val value: B) : Ior<Nothing, B>() {
    override val isRight: Boolean get() = true
    override val isLeft: Boolean get() = false
    override val isBoth: Boolean get() = false

    override fun toString(): String = show(Show.any(), Show.any())

    companion object {
      @Deprecated("Deprecated, use the constructor instead", ReplaceWith("Right(a)"))
      operator fun <B> invoke(b: B): Ior<Nothing, B> = Right(b)
    }
  }

  data class Both<out A, out B>(val leftValue: A, val rightValue: B) : Ior<A, B>() {
    override val isRight: Boolean get() = false
    override val isLeft: Boolean get() = false
    override val isBoth: Boolean get() = true

    override fun toString(): String = show(Show.any(), Show.any())
  }

  inline fun <C, D> bicrosswalk(
    fa: (A) -> Iterable<C>,
    fb: (B) -> Iterable<D>
  ): List<Ior<C, D>> =
    fold(
      { a  -> fa(a).map { it.leftIor() } },
      { b -> fb(b).map { it.rightIor() } },
      { a, b -> fa(a).align(fb(b)) }
  )

  inline fun <C, D, K> bicrosswalkMap(
    fa: (A) -> Map<K, C>,
    fb: (B) -> Map<K, D>
  ): Map<K, Ior<C, D>> =
    fold(
      { a -> fa(a).mapValues { it.value.leftIor() } },
      { b -> fb(b).mapValues { it.value.rightIor() } },
      { a, b -> fa(a).align(fb(b)) }
  )

  inline fun <C, D> bicrosswalkNull(
    fa: (A) -> C?,
    fb: (B) -> D?
  ): Ior<C, D>? =
    fold(
      { a -> fa(a)?.let { Left(it) } },
      { b -> fb(b)?.let { Right(it) } },
      { a, b -> fromNullables(fa(a), fb(b)) }
    )

  inline fun <AA, C> bitraverse(fa: (A) -> Iterable<AA>, fb: (B) -> Iterable<C>): List<Ior<AA, C>> =
    fold(
      { a -> fa(a).map { Left(it) } },
      { b -> fb(b).map { Right(it) } },
      { a, b -> ListK.mapN(fa(a), fb(b)) { aa, c -> Both(aa, c) } }
    )

  inline fun <AA, C, D> bitraverseEither(
    fa: (A) -> Either<AA, C>,
    fb: (B) -> Either<AA, D>
  ): Either<AA, Ior<C, D>> =
    fold(
      { a -> fa(a).map { Left(it) } },
      { b -> fb(b).map { Right(it) } },
      { a, b -> Either.mapN(fa(a), fb(b)) { aa, c -> Both(aa, c) } }
    )

  inline fun <AA, C, D> bitraverseValidated(
    SA: Semigroup<AA>,
    fa: (A) -> Validated<AA, C>,
    fb: (B) -> Validated<AA, D>
  ): Validated<AA, Ior<C, D>> =
    fold(
      { a -> fa(a).map { Left(it) } },
      { b -> fb(b).map { Right(it) } },
      { a, b -> Validated.mapN(SA, fa(a), fb(b)) { aa, c -> Both(aa, c) } }
    )

  inline fun <C> crosswalk(fa: (B) -> Iterable<C>): List<Ior<A, C>> =
    fold(
      { emptyList() },
      { b -> fa(b).map { Right(it) } },
      { a, b -> fa(b).map { Both(a, it) }}
    )

  inline fun <K, V> crosswalkMap(fa: (B) -> Map<K, V>): Map<K, Ior<A, V>> =
    fold(
      { emptyMap() },
      { b -> fa(b).mapValues { Right(it.value) } },
      { a, b -> fa(b).mapValues { Both(a, it.value) }}
    )

  inline fun <A, B, C> crosswalkNull(ior: Ior<A, B>, fa: (B) -> C?): Ior<A, C>? =
    ior.fold(
      { a -> Left(a) },
      { b -> fa(b)?.let { Right(it) } },
      { a, b -> fa(b)?.let { Both(a, it) }}
    )

  /**
   *  Applies [f] to an [B] inside [Ior] and returns the [Ior] structure with a pair of the [B] value and the
   *  computed [C] value as result of applying [f]
   *
   *  ```kotlin:ank:playground
   *  import arrow.core.*
   *
   *  fun main(args: Array<String>) {
   *   val result =
   *   //sampleStart
   *   Ior.Right("Hello").fproduct<String>({ "$it World" })
   *   //sampleEnd
   *   println(result)
   *  }
   *  ```
   */
  fun <C> fproduct(f: (B) -> C): Ior<A, Pair<B, C>> =
    map { b -> b to f(b) }

  fun hash(HA: Hash<A>, HB: Hash<B>): Int =
    fold(
      { HA.run { it.hashWithSalt(0) } },
      { HB.run { it.hashWithSalt(1) } },
      { a, b -> HA.run { a.hashWithSalt(2) } + HB.run { b.hashWithSalt(2) } }
    )

  fun hashWithSalt(HA: Hash<A>, HB: Hash<B>, salt: Int): Int =
    fold(
      { HA.run { it.hashWithSalt(salt.hashWithSalt(0)) } },
      { HB.run { it.hashWithSalt(salt.hashWithSalt(1)) } },
      { a, b ->
        HA.run { a.hashWithSalt(salt.hashWithSalt(2)) } +
          HB.run { b.hashWithSalt(salt.hashWithSalt(2)) } }
    )

  /**
   *  Replaces [B] inside [Ior] with [C] resulting in a Ior<A, C>
   *
   *
   *  ```kotlin:ank:playground
   *  import arrow.core.*
   *
   *  fun main(args: Array<String>) {
   *   val result =
   *   //sampleStart
   *   Ior.Left("Hello World").mapConst<String>("...")
   *   //sampleEnd
   *   println(result)
   *  }
   *  ```
   */
  fun <C> mapConst(c: C): Ior<A, C> =
    map { c }

  fun show(SL: Show<A>, SR: Show<B>): String = fold(
    {
      "Left(${SL.run { it.show() }})"
    },
    {
      "Right(${SR.run { it.show() }})"
    },
    { a, b -> "Both(${SL.run { a.show() }}, ${SR.run { b.show() }})" }
  )

  inline fun <C> traverse(fa: (B) -> Iterable<C>): List<Ior<A, C>> =
    fold(
      { a -> listOf(Left(a)) },
      { b -> fa(b).map { Right(it) } },
      { a, b -> fa(b).map { Both(a, it) }}
    )

  inline fun <AA, C> traverseEither(fa: (B) -> Either<AA, C>): Either<AA, Ior<A, C>> =
    fold(
      { a -> Either.right(Left(a)) },
      { b -> fa(b).map { Right(it) } },
      { a, b -> fa(b).map { Both(a, it) }}
    )

  inline fun <AA, C> traverseValidated(fa: (B) -> Validated<AA, C>): Validated<AA, Ior<A, C>> =
    fold(
      { a -> Valid(Left(a)) },
      { b -> fa(b).map { Right(it) } },
      { a, b -> fa(b).map { Both(a, it) }}
    )

  /**
   *  Pairs [C] with [B] returning a Ior<A, Pair<C, B>>
   *
   *  ```kotlin:ank:playground
   *  import arrow.core.*
   *
   *  fun main(args: Array<String>) {
   *   val result =
   *   //sampleStart
   *   Ior.Right("Hello").tupleLeft<String>("World")
   *   //sampleEnd
   *   println(result)
   *  }
   *  ```
   */
  fun <C> tupleLeft(c: C): Ior<A, Pair<C, B>> =
    map { b -> c to b }

  /**
   *  Pairs [C] with [B] returning a Ior<A, Pair<B, C>>
   *
   *  ```kotlin:ank:playground
   *  import arrow.core.*
   *
   *  fun main(args: Array<String>) {
   *   val result =
   *   //sampleStart
   *   Ior.Right("Hello").tupleRight<String>("World")
   *   //sampleEnd
   *   println(result)
   *  }
   *  ```
   */
  fun <C> tupleRight(c: C): Ior<A, Pair<B, C>> =
    map { b -> b to c }

  fun void(): Ior<A, Unit> =
    mapConst(Unit)
}

/**
 * Binds the given function across [Ior.Right].
 *
 * @param f The function to bind across [Ior.Right].
 */
inline fun <A, B, D> Ior<A, B>.flatMap(SG: Semigroup<A>, f: (B) -> Ior<A, D>): Ior<A, D> = fold(
  { Ior.Left(it) },
  f,
  { l, r ->
    with(SG) {
      f(r).fold(
        {
          Ior.Left(l.combine(it))
        },
        {
          Ior.Both(l, it)
        },
        { ll, rr ->
          Ior.Both(l.combine(ll), rr)
        }
      )
    }
  }
)

fun <A, B, D> Ior<A, B>.ap(SG: Semigroup<A>, ff: IorOf<A, (B) -> D>): Ior<A, D> =
  flatMap(SG) { a -> ff.fix().map { f -> f(a) } }

fun <A, B, D> Ior<A, B>.ap(SG: Semigroup<A>, ff: Eval<Ior<A, (B) -> D>>): Eval<Ior<A, D>> =
  ff.map { ap(SG, it) }

inline fun <A, B> Ior<A, B>.getOrElse(default: () -> B): B =
  fold({ default() }, ::identity, { _, b -> b })

fun <A, B, G> IorOf<A, Kind<G, B>>.sequence(GA: Applicative<G>): Kind<G, Ior<A, B>> =
  fix().traverse(GA, ::identity)

fun <A, B> Pair<A, B>.bothIor(): Ior<A, B> = Ior.Both(this.first, this.second)

fun <A, B> Tuple2<A, B>.bothIor(): Ior<A, B> = Ior.Both(this.a, this.b)

fun <A> A.leftIor(): Ior<A, Nothing> = Ior.Left(this)

fun <A> A.rightIor(): Ior<Nothing, A> = Ior.Right(this)

fun <A, B> Ior<Iterable<A>, Iterable<B>>.bisequence(): List<Ior<A, B>> =
  bitraverse(::identity, ::identity)

fun <A, B, C> Ior<Either<A, B>, Either<A, C>>.bisequenceEither(): Either<A, Ior<B, C>> =
  bitraverseEither(::identity, ::identity)

fun <A, B, C> Ior<Validated<A, B>, Validated<A, C>>.bisequenceValidated(SA: Semigroup<A>): Validated<A, Ior<B, C>> =
  bitraverseValidated(SA, ::identity, ::identity)

fun <A, B> Ior<A, B>.combine(SA: Semigroup<A>, SB: Semigroup<B>, other: Ior<A, B>): Ior<A, B> =
  with(SA) {
    with(SB) {
      when (val a = this@combine) {
        is Ior.Left -> when (other) {
          is Ior.Left -> Ior.Left(a.value + other.value)
          is Ior.Right -> Ior.Both(a.value, other.value)
          is Ior.Both -> Ior.Both(a.value + other.leftValue, other.rightValue)
        }
        is Ior.Right -> when (other) {
          is Ior.Left -> Ior.Both(other.value, a.value)
          is Ior.Right -> Ior.Right(a.value + other.value)
          is Ior.Both -> Ior.Both(other.leftValue, a.value + other.rightValue)
        }
        is Ior.Both -> when (other) {
          is Ior.Left -> Ior.Both(a.leftValue + other.value, a.rightValue)
          is Ior.Right -> Ior.Both(a.leftValue, a.rightValue + other.value)
          is Ior.Both -> Ior.Both(a.leftValue + other.leftValue, a.rightValue + other.rightValue)
        }
      }
    }
  }

fun <A, B> Ior<A, B>.compare(OA: Order<A>, OB: Order<B>, b: Ior<A, B>): Ordering = fold(
  { a1 -> b.fold({ a2 -> OA.run { a1.compare(a2) } }, { LT }, { _, _ -> LT }) },
  { b1 -> b.fold({ GT }, { b2 -> OB.run { b1.compare(b2) } }, { _, _ -> LT }) },
  { a1, b1 -> b.fold({ GT }, { GT }, { a2, b2 -> OA.run { a1.compare(a2) } + OB.run { b1.compare(b2) } }) }
)

fun <A, B> Ior<A, B>.compareTo(OA: Order<A>, OB: Order<B>, b: Ior<A, B>): Int =
  compare(OA, OB, b).toInt()

fun <A, B> Ior<A, B>.lt(OA: Order<A>, OB: Order<B>, b: Ior<A, B>): Boolean =
  compare(OA, OB, b) == LT

fun <A, B> Ior<A, B>.lte(OA: Order<A>, OB: Order<B>, b: Ior<A, B>): Boolean =
  compare(OA, OB, b) != GT

fun <A, B> Ior<A, B>.gt(OA: Order<A>, OB: Order<B>, b: Ior<A, B>): Boolean =
  compare(OA, OB, b) == GT

fun <A, B> Ior<A, B>.gte(OA: Order<A>, OB: Order<B>, b: Ior<A, B>): Boolean =
  compare(OA, OB, b) != LT

fun <A, B> Ior<A, B>.max(OA: Order<A>, OB: Order<B>, b: Ior<A, B>): Ior<A, B> =
  if (gt(OA, OB, b)) this else b

fun <A, B> Ior<A, B>.min(OA: Order<A>, OB: Order<B>, b: Ior<A, B>): Ior<A, B> =
  if (lt(OA, OB, b)) this else b

fun <A, B> Ior<A, B>.sort(OA: Order<A>, OB: Order<B>, b: Ior<A, B>): Pair<Ior<A, B>, Ior<A, B>> =
  if (gte(OA, OB, b)) this to b else b to this

fun <A, B> Ior<A, B>.eqv(EQA: Eq<A>, EQB: Eq<B>, b: Ior<A, B>): Boolean = when (this) {
  is Ior.Left -> when (b) {
    is Ior.Both -> false
    is Ior.Right -> false
    is Ior.Left -> EQA.run { value.eqv(b.value) }
  }
  is Ior.Both -> when (b) {
    is Ior.Left -> false
    is Ior.Both -> EQA.run { leftValue.eqv(b.leftValue) } && EQB.run { rightValue.eqv(b.rightValue) }
    is Ior.Right -> false
  }
  is Ior.Right -> when (b) {
    is Ior.Left -> false
    is Ior.Both -> false
    is Ior.Right -> EQB.run { value.eqv(b.value) }
  }
}

fun <A, B> Ior<A, B>.neqv(EQA: Eq<A>, EQB: Eq<B>, b: Ior<A, B>): Boolean =
  !eqv(EQA, EQB, b)

@Suppress("NOTHING_TO_INLINE")
inline fun <A, B> Ior<A, Ior<A, B>>.flatten(SA: Semigroup<A>): Ior<A, B> =
  flatMap(SA, ::identity)

inline fun <A, B, C> Ior<A, B>.flatTraverse(SA: Semigroup<A>, f: (B) -> Iterable<Ior<A, C>>): List<Ior<A, C>> =
  traverse(f).map { it.flatten(SA) }

inline fun <A, B, C, E> Ior<A, B>.flatTraverseEither(SA: Semigroup<A>, f: (B) -> Either<E, Ior<A, C>>): Either<E, Ior<A, C>> =
  traverseEither(f).map { it.flatten(SA) }

inline fun <A, B, C, E> Ior<A, B>.flatTraverseValidated(SA: Semigroup<A>, f: (B) -> Validated<E, Ior<A, C>>): Validated<E, Ior<A, C>> =
  traverseValidated(f).map { it.flatten(SA) }

inline fun <A, B> Ior<A, Boolean>.ifM(SA: Semigroup<A>, ifTrue: () -> Ior<A, B>, ifFalse: () -> Ior<A, B>): Ior<A, B> =
  flatMap(SA) { if (it) ifTrue() else ifFalse() }

inline fun <A, B, C> Ior<A, B>.mproduct(SA: Semigroup<A>, f: (B) -> Ior<A, C>): Ior<A, Pair<B, C>> =
  flatMap(SA) { a ->
    f(a).map { b -> a to b }
  }

fun <A, B> Ior<A, B>.replicate(SA: Semigroup<A>, n: Int): Ior<A, List<B>> =
  if (n <= 0) Ior.Right(emptyList())
  else when (this) {
    is Ior.Right -> Ior.Right(List(n) { value })
    is Ior.Left -> this
    is Ior.Both -> bimap(
      { List(n - 1) { leftValue}.fold(leftValue, { acc, a -> SA.run { acc + a }}) },
      { List(n) { rightValue } }
    )
  }

fun <A, B> Ior<A, B>.replicate(SA: Semigroup<A>, n: Int, MB: Monoid<B>): Ior<A, B> =
  if (n <= 0) Ior.Right(MB.empty())
  else when (this) {
    is Ior.Right -> Ior.Right(MB.run { List(n) { value }.combineAll() })
    is Ior.Left -> this
    is Ior.Both -> bimap(
      { List(n - 1) { leftValue}.fold(leftValue, { acc, a -> SA.run { acc + a }}) },
      { MB.run { List(n) { rightValue }.combineAll() } }
    )
  }

fun <A, B, C> Ior<A, Either<B, C>>.selectM(SA: Semigroup<A>, f: Ior<A, (B) -> C>): Ior<A, C> =
  flatMap(SA) {
    it.fold(
      { b -> f.map { ff -> ff(b) } },
      { c -> Ior.Right(c) }
    )
  }

fun <A, B> Ior<A, Iterable<B>>.sequence(): List<Ior<A, B>> =
  traverse(::identity)

fun <A, B, C> Ior<A, Either<B, C>>.sequenceEither(): Either<B, Ior<A, C>> =
  traverseEither(::identity)

fun <A, B, C> Ior<A, Validated<B, C>>.sequenceValidated(): Validated<B, Ior<A, C>> =
  traverseValidated(::identity)

/**
 * Given [B] is a sub type of [C], re-type this value from Ior<A, B> to Ior<A, B>
 *
 * ```kotlin:ank:playground:extension
 * import arrow.core.*
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val string: Ior<Int, String> = Ior.Right("Hello")
 *   val chars: Ior<Int, CharSequence> =
 *     string.widen<Int, CharSequence, String>()
 *   //sampleEnd
 *   println(chars)
 * }
 * ```
 */
fun <A, C, B : C> Ior<A, B>.widen(): Ior<A, C> =
  this

fun <AA, A : AA, B> Ior<A, B>.leftWiden(): Ior<AA, B> =
  this

fun <A, B, C, Z> Ior<A, B>.zip(SA: Semigroup<A>, fb: Ior<A, C>, f: (B, C) -> Z): Ior<A, Z> =
  ap(SA, fb.map { c: C -> { b: B -> f(b, c) } })

fun <A, B, C> Ior<A, B>.zip(SA: Semigroup<A>, fb: Ior<A, C>): Ior<A, Pair<B, C>> =
  zip(SA, fb, ::Pair)

fun <A, B, C, Z> Ior<A, B>.zipEval(SA: Semigroup<A>, other: Eval<Ior<A, C>>, f: (B, C) -> Z): Eval<Ior<A, Z>> =
  other.map {zip(SA, it).map { a -> f(a.first, a.second) }}

fun <A, B> Eq.Companion.ior(EQA: Eq<A>, EQB: Eq<B>): Eq<Ior<A, B>> =
  IorEq(EQA, EQB)

fun <A, B> Hash.Companion.ior(HA: Hash<A>, HB: Hash<B>): Hash<Ior<A, B>> =
  IorHash(HA, HB)

fun <A, B> Order.Companion.ior(OA: Order<A>, OB: Order<B>): Order<Ior<A, B>> =
  IorOrder(OA, OB)

fun <A, B> Semigroup.Companion.ior(SA: Semigroup<A>, SB: Semigroup<B>): Semigroup<Ior<A, B>> =
  IorSemigroup(SA, SB)

fun <A, B> Show.Companion.ior(SA: Show<A>, SB: Show<B>): Show<Ior<A, B>> =
  IorShow(SA, SB)

private class IorEq<A, B>(
  private val EQA: Eq<A>,
  private val EQB: Eq<B>
) : Eq<Ior<A, B>> {
  override fun Ior<A, B>.eqv(b: Ior<A, B>): Boolean =
    eqv(EQA, EQB, b)
}

private class IorHash<A, B>(
  private val HA: Hash<A>,
  private val HB: Hash<B>
) : Hash<Ior<A, B>> {
  override fun Ior<A, B>.hash(): Int =
    hash(HA, HB)

  override fun Ior<A, B>.hashWithSalt(salt: Int): Int =
    hashWithSalt(HA, HB, salt)
}

private class IorOrder<A, B>(
  private val OA: Order<A>,
  private val OB: Order<B>
) : Order<Ior<A, B>> {
  override fun Ior<A, B>.compare(b: Ior<A, B>): Ordering =
    compare(OA, OB, b)
}

private class IorSemigroup<A, B>(
  private val SGA: Semigroup<A>,
  private val SGB: Semigroup<B>
) : Semigroup<Ior<A, B>> {

  override fun Ior<A, B>.combine(b: Ior<A, B>): Ior<A, B> =
    combine(SGA, SGB, b)

  override fun Ior<A, B>.maybeCombine(b: Ior<A, B>?): Ior<A, B> =
    b?.let { combine(SGA, SGB, it) } ?: this
}

private class IorShow<A, B>(
  private val SA: Show<A>,
  private val SB: Show<B>
) : Show<Ior<A, B>> {
  override fun Ior<A, B>.show(): String =
    show(SA, SB)
}
