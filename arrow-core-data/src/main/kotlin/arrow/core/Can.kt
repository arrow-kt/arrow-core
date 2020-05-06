package arrow.core

import arrow.higherkind
import arrow.typeclasses.Semigroup
import arrow.typeclasses.Show

/**
 * ank_macro_hierarchy(arrow.core.Can)
 *
 *
 * Implementation of Haskell's [Can](https://hackage.haskell.org/package/smash-0.1.1.0/docs/Data-Can.html)
 *
 * Represents a right-biased disjunction of either [A], [B], both [A] and [B] or none of them.
 *
 * This can be represented mathematically as the product of two components that are optional:
 *
 * ```
 * (1 + a) * (1 + b)   // (1 + a) is the union of an empty case and a base case aka [Option]
 * ~ 1 + a + b + a*b   // This is expressed as 4 permutations: None, Left, Right, or Both
 * ~ Option (Ior a b)  // Ior (or There in Haskell) can be defined as `a + b + a*b`, therefore wrapping it with Option adds the empty case
 * ~ Can a b           // And that's how we get to [Can]
 * ```
 * It can be easier to visualize in a picture:
 *
 * ```
 * Can:
 *          A (Left)
 *          |
 * None +---+---+ A and B (Both)
 *          |
 *          B (Right)
 * ```
 *
 * An instance of [Can]<`A`,`B`> is one of:
 *  - [Can.None]
 *  - [Can.Left] <`A`>
 *  - [Can.Right] <`B`>
 *  - [Can.Both]<`A`,`B`>
 *
 * Similarly to [Ior], [Can] differs from [Either] in that it can contain both [A] and [B]. On top of that it can contain neither of them.
 * This means that it's isomorphic to using [Option]<[Ior]<`A`, `B`>>.
 *
 * Operations available are biased towards [B].
 *
 * Implementation Note: The names of [Left] and [Right] were used instead of the original `One` and `Eno` to match other data classes like [Either] or [Ior]
 *
 */
@higherkind
sealed class Can<out A, out B> : CanOf<A, B> {

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

    fun <A, B> fromOptions(oa: Option<A>, ob: Option<B>): Can<A, B> = when (oa) {
      is Some -> when (ob) {
        is Some -> Both(oa.t, ob.t)
        is arrow.core.None -> Left(oa.t)
      }
      is arrow.core.None -> when (ob) {
        is Some -> Right(ob.t)
        is arrow.core.None -> None
      }
    }

    fun none(): Can<Nothing, Nothing> = None
    fun <A> left(left: A): Can<A, Nothing> = Left(left)
    fun <B> right(right: B): Can<Nothing, B> = Right(right)
    fun <A, B> both(left: A, right: B): Can<A, B> = Both(left, right)

    operator fun <A, B> invoke(ior: Ior<A, B>): Can<A, B> = ior.fold(::Left, ::Right, ::Both)

    private tailrec fun <A2, A, B> Semigroup<A2>.loop(v: Can<A2, Either<A, B>>, f: (A) -> CanOf<A2, Either<A, B>>): Can<A2, B> = when (v) {
      is None -> None
      is Left -> Left(v.a)
      is Right -> when (val either = v.b) {
        is Either.Right -> Right(either.b)
        is Either.Left -> loop(f(either.a).fix(), f)
      }
      is Both -> when (val either = v.b) {
        is Either.Right -> Both(v.a, either.b)
        is Either.Left -> when (val fnb = f(either.a).fix()) {
          is None -> None
          is Left -> Left(v.a.combine(fnb.a))
          is Right -> loop(Both(v.a, fnb.b), f)
          is Both -> loop(Both(v.a.combine(fnb.a), fnb.b), f)
        }
      }
    }

    fun <A2, A, B> tailRecM(a: A, f: (A) -> CanOf<A2, Either<A, B>>, SL: Semigroup<A2>): Can<A2, B> =
      SL.run { loop(f(a).fix(), f) }
  }

  /**
   * Returns true if the option is [Can.None], false otherwise.
   * @note Used only for performance instead of fold.
   */
  abstract val isEmpty: Boolean

  /**
   * Returns `true` if this is a [Can.Right], `false` otherwise.
   *
   * Example:
   * ```
   * Can.None.isRight                      // Result: false
   * Can.Left("tulip").isRight             // Result: false
   * Can.Right("venus fly-trap").isRight   // Result: true
   * Can.Both("venus", "fly-trap").isRight // Result: false
   * ```
   */
  abstract val isRight: Boolean

  /**
   * Returns `true` if this is a [Can.Left], `false` otherwise.
   *
   * Example:
   * ```
   * Can.None.isLeft                      // Result: false
   * Can.Left("tulip").isLeft             // Result: true
   * Can.Right("venus fly-trap").isLeft   // Result: false
   * Can.Both("venus", "fly-trap").isLeft // Result: false
   * ```
   */
  abstract val isLeft: Boolean

  /**
   * Returns `true` if this is a [Can.Both], `false` otherwise.
   *
   * Example:
   * ```
   * Can.None.isBoth                      // Result: false
   * Can.Left("tulip").isBoth             // Result: false
   * Can.Right("venus fly-trap").isBoth   // Result: false
   * Can.Both("venus", "fly-trap").isBoth // Result: true
   * ```
   */
  abstract val isBoth: Boolean

  /**
   * alias for [isDefined]
   */
  fun nonEmpty(): Boolean = isDefined()

  /**
   * Returns true if it's [Left], [Right] or [Both], false otherwise.
   * @note Used only for performance instead of fold.
   */
  fun isDefined(): Boolean = !isEmpty

  /**
   * Transforms the right side from [B] to [C].
   *
   * This has no effect if this is a [Left] or [None].
   * In the instance that we have a [Right] instance it will apply [f].
   * Equally, with [Both], `b` will be transformed, but `a` will remain the same.
   *
   * @param f the function to apply
   * @see flatMap
   */
  fun <C> map(f: (B) -> C): Can<A, C> =
    fold({ None }, { Left(it) }, { Right(f(it)) }, { a, b -> Both(a, f(b)) })

  /**
   * The given function is applied if this is a [Left] or [Both] to `A`.
   *
   * Example:
   * ```
   * None.map { "flower" }                   // Result: None
   * Right(12).map { "flower" }              // Result: Right(12)
   * Left(12).map { "flower" }               // Result: Left("power")
   * Both(12, "power").map { "flower $it" }  // Result: Both("flower 12", "power")
   * ```
   */
  fun <C> mapLeft(fa: (A) -> C): Can<C, B> =
    fold({ None }, { Left(fa(it)) }, { Right((it)) }, { a, b -> Both(fa(a), b) })

  fun <C, D> bimap(fa: (A) -> C, fb: (B) -> D): Can<C, D> =
    fix().fold({ None }, { Left(fa(it)) }, { Right(fb(it)) }, { l, r -> Both(fa(l), fb(r)) })

  /**
   * Transforms this into an instance of [C] depending on the case.
   */
  fun <C> fold(
    ifNone: () -> C,
    ifLeft: (A) -> C,
    ifRight: (B) -> C,
    ifBoth: (A, B) -> C
  ): C = when (this) {
    is None -> ifNone()
    is Left -> ifLeft(a)
    is Right -> ifRight(b)
    is Both -> ifBoth(a, b)
  }

  /**
   * Similar to [map] but results in a [None] if the result fo [f] is null.
   */
  fun <C> mapNotNull(f: (B) -> C?): Can<A, C> =
    fromOptions(left(), right().mapNotNull(f))

  /**
   * Returns this if the predicate over [B] passes, or [None] otherwise
   *
   * @param predicate the predicate used for testing.
   */
  fun filter(predicate: Predicate<B>): Can<A, B> =
    fromOptions(left(), right().filter(predicate))

  /**
   * Opposite of [filter]
   *
   * @param predicate the predicate used for testing.
   */
  fun filterNot(predicate: Predicate<B>): Can<A, B> =
    fromOptions(left(), right().filterNot(predicate))

  /**
   * Returns this if the predicate over [A] passes, or [None] otherwise
   *
   * @param predicate the predicate used for testing.
   */
  fun filterLeft(predicate: Predicate<A>): Can<A, B> =
    fromOptions(left().filter(predicate), right())

  /**
   * Opposite of [filterLeft]
   *
   * @param predicate the predicate used for testing.
   */
  fun filterNotLeft(predicate: Predicate<A>): Can<A, B> =
    fromOptions(left().filterNot(predicate), right())

  /**
   * Returns true if [B] passes the provided predicate for [Right], or [Both] instances.
   *
   * @param predicate the predicate used for testing.
   */
  fun exists(predicate: Predicate<B>): Boolean = fold(
    ifNone = { false },
    ifLeft = { false },
    ifRight = { predicate(it) },
    ifBoth = { _, b -> predicate(b) }
  )

  fun <C> foldLeft(c: C, f: (C, B) -> C): C =
    fold({ c }, { c }, { f(c, it) }, { _, b -> f(c, b) })

  fun <C> foldRight(lc: Eval<C>, f: (B, Eval<C>) -> Eval<C>): Eval<C> =
    fold({ lc }, { lc }, { Eval.defer { f(it, lc) } }, { _, b -> Eval.defer { f(b, lc) } })

  fun <C> bifoldLeft(c: C, f: (C, A) -> C, g: (C, B) -> C): C =
    fold({ c }, { f(c, it) }, { g(c, it) }, { a, b -> g(f(c, a), b) })

  fun <C> bifoldRight(c: Eval<C>, f: (A, Eval<C>) -> Eval<C>, g: (B, Eval<C>) -> Eval<C>): Eval<C> =
    fold({ c }, { f(it, c) }, { g(it, c) }, { a, b -> f(a, g(b, c)) })

  /**
   * Return the isomorphic [Option]<[Ior]> of this [Can]
   */
  fun unwrap(): Option<Ior<A, B>> = fold(
    ifNone = { Option.empty() },
    ifLeft = { Option.just(Ior.Left(it)) },
    ifRight = { Option.just(Ior.Right(it)) },
    ifBoth = { a, b -> Option.just(Ior.Both(a, b)) }
  )

  /**
   * Inverts the components:
   *  - If [None] is remains [None]
   *  - If [Left]<`A`> it returns [Right]<`A`>
   *  - If [Right]<`B`> it returns [Left]<`B`>
   *  - If [Both]<`A`, `B`> it returns [Both]<`B`, `A`>
   *
   * Example:
   * ```
   * None.swap()                  // Result: None
   * Left("left").swap()          // Result: Right("left")
   * Right("right").swap()        // Result: Left("right")
   * Both("left", "right").swap() // Result: Both("right", "left")
   * ```
   */
  fun swap(): Can<B, A> =
    fold({ None }, { Right(it) }, { Left(it) }, { a, b -> Both(b, a) })

  /**
   * Return this [Can] as [Pair] of [Option]
   *
   * Example:
   * ```
   * None.pad()               // Result: Pair(None, None)
   * Right(12).pad()          // Result: Pair(None, Some(12))
   * Left(12).pad()           // Result: Pair(Some(12), None)
   * Both("power", 12).pad()  // Result: Pair(Some("power"), Some(12))
   * ```
   */
  fun pad(): Pair<Option<A>, Option<B>> = fold(
    { Pair(Option.empty(), Option.empty()) },
    { Pair(Some(it), Option.empty()) },
    { Pair(Option.empty(), Some(it)) },
    { a, b -> Pair(Some(a), Some(b)) }
  )

  /**
   * Returns a [Either.Right] containing the [Right] value or `B` if this is [Right] or [Both]
   * and [Either.Left] if this is a [Left].
   *
   * Example:
   * ```
  //   * Right(12).toEither() // Result: Either.Right(12)
  //   * Left(12).toEither()  // Result: Either.Left(12)
  //   * Both("power", 12).toEither()  // Result: Either.Right(12)
   * ```
   */
  fun toEither(): Option<Either<A, B>> =
    fold({ Option.empty() }, { Option.just(Either.Left(it)) }, { Option.just(Either.Right(it)) }, { _, b -> Option.just(Either.Right(b)) })

  /**
   * Returns a [Some] containing the [Right] value or `B` if this is [Right] or [Both]
   * and [None] if this is a [Left].
   *
   * Example:
   * ```
  //   * Right(12).toOption() // Result: Some(12)
  //   * Left(12).toOption()  // Result: None
  //   * Both(12, "power").toOption()  // Result: Some("power")
   * ```
   */
  fun toOption(): Option<B> =
    fold({ Option.empty() }, { Option.empty() }, { Some(it) }, { _, b -> Some(b) })

  /**
   * Returns a [Some] containing the [Left] value or `A` if this is [Left] or [Both]
   * and [None] if this is a [Right].
   *
   * Example:
   * ```
  //   * Right(12).toLeftOption() // Result: None
  //   * Left(12).toLeftOption()  // Result: Some(12)
  //   * Both(12, "power").toLeftOption()  // Result: Some(12)
   * ```
   */
  fun toLeftOption(): Option<A> =
    fold({ Option.empty() }, { Some(it) }, { Option.empty() }, { a, _ -> Option.just(a) })

  /**
   * Returns a [Validated.Valid] containing the [Right] value or `B` if this is [Right] or [Both]
   * and [Validated.Invalid] if this is a [Left].
   *
   * Example:
   * ```
  //   * Right(12).toValidated() // Result: Valid(12)
  //   * Left(12).toValidated()  // Result: Invalid(12)
  //   * Both(12, "power").toValidated()  // Result: Valid("power")
   * ```
   */
  fun toValidated(): Validated<A, B> =
    fold({ TODO() }, { Invalid(it) }, { Valid(it) }, { _, b -> Valid(b) })

  /**
   * Provides a printable description of [Can] given the relevant [Show] instances.
   */
  fun <A, B> CanOf<A, B>.show(SL: Show<A>, SR: Show<B>): String =
    fix().fold(
      ifNone = { "None" },
      ifLeft = { "Left(${SL.run { it.show() }})" },
      ifRight = { "Right(${SR.run { it.show() }})" },
      ifBoth = { a, b -> "Both(left=${SL.run { a.show() }}, right=${SR.run { b.show() }})" }
    )

  object None : Can<Nothing, Nothing>() {
    override val isEmpty: Boolean = true
    override val isRight: Boolean = false
    override val isLeft: Boolean = false
    override val isBoth: Boolean = false

    override fun toString(): String = show(Show.any(), Show.any())
  }

  data class Left<A>(val a: A) : Can<A, Nothing>() {
    override val isEmpty: Boolean = false
    override val isRight: Boolean = false
    override val isLeft: Boolean = true
    override val isBoth: Boolean = false

    override fun toString(): String = show(Show.any(), Show.any())
  }

  data class Right<B>(val b: B) : Can<Nothing, B>() {
    override val isEmpty: Boolean = false
    override val isRight: Boolean = true
    override val isLeft: Boolean = false
    override val isBoth: Boolean = false

    override fun toString(): String = show(Show.any(), Show.any())
  }

  data class Both<A, B>(val a: A, val b: B) : Can<A, B>() {
    override val isEmpty: Boolean = false
    override val isRight: Boolean = false
    override val isLeft: Boolean = false
    override val isBoth: Boolean = true

    override fun toString(): String = show(Show.any(), Show.any())
  }
}

// TODO(pabs): map2, maybe?

/**
 * Similar to [Can.unwrap] but transforming to [Ior] with an alternative in case of working with an instance of [Can.None]
 */
fun <A, B> CanOf<A, B>.toIor(ifNone: () -> IorOf<A, B>): Ior<A, B> =
  fix().unwrap().getOrElse(ifNone).fix()

/**
 * Binds the given function across [Can.Right] or [Can.Both].
 *
 * @param f The function to bind across [Can.Right] or [Can.Both].
 */
fun <A, B, C> CanOf<A, B>.flatMap(SG: Semigroup<A>, f: (B) -> CanOf<A, C>): Can<A, C> =
  fix().fold(
    ifNone = Can.Companion::none,
    ifLeft = { Can.Left(it) },
    ifRight = { f(it).fix() },
    ifBoth = { l, r ->
      with(SG) {
        f(r).fix().fold(
          ifNone = Can.Companion::none,
          ifLeft = { Can.Left(l.combine(it)) },
          ifRight = { Can.Both(l, it) },
          ifBoth = { ll, rr -> Can.Both(l.combine(ll), rr) }
        )
      }
    }
  )

fun <A, B> Can<A, B>.getOrElse(default: () -> B): B =
  fold({ default() }, { default() }, ::identity, { _, b -> b })

fun <A, B, C> CanOf<A, B>.ap(SG: Semigroup<A>, ff: CanOf<A, (B) -> C>): Can<A, C> =
  fix().flatMap(SG) { a -> ff.fix().map { f -> f(a) } }

fun <A, B> Pair<A, B>.bothCan(): Can<A, B> = Can.Both(first, second)

fun <A, B> Tuple2<A, B>.bothCan(): Can<A, B> = Can.Both(a, b)

fun <A> A.leftCan(): Can<A, Nothing> = Can.Left(this)

fun <B> B.rightCan(): Can<Nothing, B> = Can.Right(this)

fun <A, B> CanOf<A, B>.left(): Option<A> =
  fix().fold(
    ifNone = { None },
    ifLeft = { Option.just(it) },
    ifRight = { None },
    ifBoth = { l, _ -> Option.just(l) }
  )

operator fun <A, B> CanOf<A, B>.component1(): Option<A> = left()

fun <A, B> CanOf<A, B>.right(): Option<B> =
  fix().fold(
    ifNone = { None },
    ifLeft = { None },
    ifRight = { Option.just(it) },
    ifBoth = { _, r -> Option.just(r) }
  )

operator fun <A, B> CanOf<A, B>.component2(): Option<B> = right()
