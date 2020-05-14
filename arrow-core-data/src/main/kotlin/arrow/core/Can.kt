package arrow.core

import arrow.higherkind
import arrow.typeclasses.Semigroup
import arrow.typeclasses.Show

/**
 * ank_macro_hierarchy(arrow.core.Can)
 *
 * Implementation of Haskell's [Can](https://hackage.haskell.org/package/smash-0.1.1.0/docs/Data-Can.html)
 *
 * It's rare, but you may have come across a situation when you need to represent whether you have one of two values or both at the same time.
 * If that's the case, then [Ior] is the ADT for you.
 *
 * However, sometimes you may also want to represent those cases as well as none of them. This is where [Can] comes in handy.
 *
 * ### The coffee maker use case
 *
 * Imagine you are working on a Smart Coffee maker. You are working on an MVP that takes instructions from the server about how to make the nest coffee.
 * You have to define an API that takes optional milk and/or sugar to be added to the coffee.
 *
 * ```kotlin:ank
 * sealed class Milk {
 *   object LowFat : Milk()
 *   object Semi : Milk()
 *   object Whole : Milk()
 * }
 * data class Sugar(val spoons: Int)
 * ```
 *
 * The coffee instructions could be defined using [Can]
 *
 * ```kotlin:ank
 * import arrow.core.Can
 *
 * //sampleStart
 * typealias CoffeeInstructions = Can<Milk, Sugar>
 * ```
 *
 * This means that the called of our API can define what type of milk they want, how many spoons of sugar or neither of them if they prefer to have plain coffee.
 *
 * ```kotlin:ank
 *
 * typealias CoffeeInstructions = Can<Milk, Sugar>
 *
 * data class Coffee(val milkName: String = "", val sugarInGrams : Double = 0.0)
 *
 * //sampleStart
 *  fun prepareCoffee(instructions: CoffeeInstructions): Coffee =
 *    instructions.bifoldLeft(
 *      Coffee(),
 *      { coffee, milk -> coffee.copy(milkName = milk.toString()) },
 *      { coffee, sugar -> coffee.copy(sugarInGrams = sugar.spoons * 4.2) }
 *    )
 * ```
 *
 * From the perspective of the client, be it a terminal on the machine or a remote client, you can create functions to modify the instructions depending on the input from the user:
 *
 * ```kotlin:ank
 * import arrow.core.component1
 * import arrow.core.component2
 * import arrow.core.Some
 * import arrow.core.Can.Companion.fromOptions
 *
 * //sampleStart
 * fun CoffeeInstructions.incrementSugar(): CoffeeInstructions = let { (milk, sugar) ->
 *   CoffeeInstructions.fromOptions(milk, Some(sugar.fold({ Sugar(spoons = 1) }, { it.copy(spoons = it.spoons + 1) })))
 * }
 * ```
 * Thanks to the provided deconstructing operators ([component1] and [component2]) you can easily split both sizes in a safe manner.
 * Both of these components return instances of [Option]<[A]> and [Option]<B> respectively.
 *
 * Then, we apply a [Option.fold] on the current sugar. If absent you create a new sugar instance with one spoon, otherwise you increment the current number of spoons
 *
 * As for decreasing the number of sugar spoon:
 *
 * ```kotlin:ank
 * import arrow.core.component1
 * import arrow.core.component2
 * import arrow.core.Can.Companion.fromOptions
 *
 * //sampleStart
 * fun CoffeeInstructions.decreaseSugar(): CoffeeInstructions = let { (milk, sugar) ->
 *   CoffeeInstructions.fromOptions(milk, sugar.map { it.copy(spoons = it.spoons - 1) }.filter { it.spoons > 0 })
 * }
 * ```
 *
 * In this case you also want to make sure that there is no [Can.Right] of sugar if there are no spoons so you need to add a filter at the end that removes the sugar if `spoons` is less than 1.
 *
 * For the milk operations it's a bit simpler:
 *
 * ```kotlin:ank
 * import arrow.core.Can.Companion.fromOptions
 * import arrow.core.component1
 * import arrow.core.component2
 * import arrow.core.Some
 * import arrow.core.None
 *
 * //sampleStart
 * fun CoffeeInstructions.addMilk(milk: Milk): CoffeeInstructions = let { (_, sugar) ->
 *   CoffeeInstructions.fromOptions(Some(milk), sugar)
 * }
 *
 * fun CoffeeInstructions.removeMilk(): CoffeeInstructions = let { (_, sugar) ->
 *   CoffeeInstructions.fromOptions(None, sugar)
 * }
 * ```
 *
 * Here you can just replace the current milk with either [Some] of the provided milk or [None]
 *
 * ## Constructing [Can] instances
 *
 * All these assume that you have an existing instance of [Can] if you want to create a new one there are a few options:
 *
 *  - fromOptions(Option<A>, Option<B>)
 *  - fromNullables(A?, B?)
 *  - neither()
 *  - left(A)
 *  - right(B)
 *  - both(A, B)
 *
 *  The first two take either an [Option] or nullable values and creates the appropriate instance:
 *  - Both absent -> [Can.Neither]
 *  - Only A present -> [Can.Left]<[A]>
 *  - Only B present -> [Can.Right]<[B]>
 *  - Both present -> [Can.Both]<[A], [B]>
 *
 *  These are used on the previous example to create new instances with the new coffee making instructions.
 *
 * ### Extension functions
 *
 * There are a few extra factory extension functions that can be used to create instances of [Can]:
 *
 * - [Pair]<[A], [B]>.toCan(): [Can.Both]<[A], [B]>
 * ``` kotlin:ank
 * import arrow.core.toCan
 *
 * //sampleStart
 * check(("Over" to 9000).toCan() == Can.Both("Over", 9000))
 * ```
 * - [Tuple2]<[A], [B]>.toCan(): [Can]<[A], [B]>
 * ``` kotlin:ank
 * import arrow.core.toT
 *
 * //sampleStart
 * check(("Over" toT 9000).toCan() == Can.Both("Over", 9000))
 * ```
 * - [A].toLeftCan(): [Can]<[A], [Nothing]>
 * ```kotlin:ank
 * import arrow.core.toLeftCan
 *
 * //sampleStart
 * check(Milk.Semi.toLeftCan() == Can.Left(Milk.Semi))
 * ```
 * - [B].toRightCan(): [Can]<[Nothing], [B]>
 * ```kotlin:ank
 * import arrow.core.toRightCan
 *
 * //sampleStart
 * check(Sugar(spoons = 2).toRightCan() == Can.Right(Sugar(spoons = 2)))
 * ```
 * - [Option]<[A]>.toLeftCan(): [Can]<[A], [Nothing]>
 * ```kotlin:ank
 * check(Some(Milk.Semi).toLeftCan() == Can.Left(Milk.Semi))
 * check(None.toLeftCan<Milk>() == Can.Neither)
 * ```
 * - [Option]<[B]>.toRightCan(): [Can]<[Nothing], [B]>
 * ```kotlin:ank
 * check(Some(Sugar(spoons = 2)).toRightCan() == Can.Right(Sugar(spoons = 2)))
 * check(None.toRightCan<Sugar>() == Can.Neither)
 * ```
 *
 * ## Safe access values
 *
 * Most of the time you'll be transforming the contents of a [Can]. However, there are several ways you can also look into the contents of a [Can].
 *
 * You've already seen that it's possible to deconstruct a [Can] using the component operations:
 *
 * With both sides present:
 * ```kotlin:ank
 * val (left, right) = Can.both("Over", 9000)
 * check(left == Some("Over"))
 * check(right == Some(9000))
 * ```
 *
 * If you are only interested on one side, it possible to get them separately:
 *
 * - [Can]<[A], [B]>.left(): [Option]<[A]>
 * ```kotlin:ank
 * import arrow.core.left
 *
 * //sampleStart
 * check(Can.both("Over", 9000).left() == Some("Over"))
 * check(Can.right("not this").left() == None)
 * ```
 * - [Can]<[A], [B]>.right(): [Option]<[A]>
 * ```kotlin:ank
 * import arrow.core.right
 *
 * //sampleStart
 * check(Can.both("Over", 9000).right() == Some(9000))
 * check(Can.left(42).right() == None)
 * ```
 * - [Can]<[A], [B]>.leftOrNull(): [A]?
 * ```kotlin:ank
 * import arrow.core.leftOrNull
 *
 * //sampleStart
 * check(Can.both("Over", 9000).leftOrNull() == "Over")
 * check(Can.right("not this").leftOrNull() == null)
 * ```
 * - [Can]<[A], [B]>.rightOrNull(): [B]?
 * ```kotlin:ank
 * import arrow.core.rightOrNull
 *
 * //sampleStart
 * check(Can.both("Over", 9000).rightOrNull() == 9000)
 * check(Can.left(42).rightOrNull() == null)
 * ```
 *
 * If you want to provide an alternative in case that the side you are looking for is not present you can use:
 *
 * - [Can]<[A], [B]>.getOrElse(f: () -> [A]): [A]
 * ```kotlin:ank
 * import arrow.core.getOrElse
 *
 * //sampleStart
 * check(Can.Both("Over", 9000).getOrElse { "not this" } == 9000)
 * check(Can.Right("this").getOrElse { "not this" } == "this")
 * check(Can.Left("not this").getOrElse { "this" } == "this")
 * ```
 * - [Can]<[A], [B]>.getLeftOrElse(f: () -> [B]): [B]
 * ```kotlin:ank
 * import arrow.core.getLeftOrElse
 *
 * //sampleStart
 * check(Can.Both("Over", 9000).getLeftOrElse { "not this" } == "Over")
 * check(Can.Left("this").getLeftOrElse { "not this" } == "this")
 * check(Can.Right("not this").getLeftOrElse { "this" } == "this")
 * ```
 *
 * ## Mathematical explanation:
 *
 * [Can] Represents a right-biased disjunction of either [A], [B], both [A] and [B] or none of them.
 *
 * This can be represented mathematically as the product of two components that are optional (see: [Option]):
 *
 * ```
 * (1 + a) * (1 + b)   // (1 + a) is the union of an empty case (None) and a base case (Some)
 * ~ 1 + a + b + a*b   // This is expressed as the union of: Neither (1), Left (a), Right (b), or Both (a*b)
 * ~ Option (Ior a b)  // Ior (or There in Haskell) can be defined as `a + b + a*b`, therefore joining it, with Option, adds the empty case
 * ~ Can a b           // And that's how we get to Can of <A, B>
 * ```
 * It can be easier to visualize in a picture:
 * ```
 * Can:
 *             A (Left)
 *             |
 * Neither +---+---+ A and B (Both)
 *             |
 *             B (Right)
 * ```
 *
 * An instance of [Can]<[A], [B]> can be one of:
 *  - [Can.Neither]
 *  - [Can.Left] <[A]>
 *  - [Can.Right] <[B]>
 *  - [Can.Both]<[A], [B]>
 *
 * Similarly to [Ior], [Can] differs from [Either] in that it can contain both [A] and [B]. On top of that it can contain neither of them.
 * This means that it's isomorphic to using [Option]<[Ior]<[A], [B]>>.
 *
 * Operations available are biased towards [B]
 *
 * Implementation Notes:
 *  - The names of [Can.Left] and [Can.Right] were used instead of the original `One` and `Eno` to match other data classes like [Either] or [Ior]
 *  - The name [Can.Neither] was used instead of `None` to avoid clashing with [None]
 *
 */
@higherkind
sealed class Can<out A, out B>(
  /**
   * Returns true if the option is [Can.Neither], false otherwise.
   * @note Used only for performance instead of fold.
   *
   * Example:
   * ```
   * Can.Neither.isEmpty                   // Result: true
   * Can.Left("tulip").isEmpty             // Result: false
   * Can.Right("venus fly-trap").isEmpty   // Result: false
   * Can.Both("venus", "fly-trap").isEmpty // Result: false
   * ```
   */
  val isNeither: Boolean = false,
  /**
   * `true` if this is a [Can.Right], `false` otherwise.
   * @note Used only for performance instead of fold.
   *
   * Example:
   * ```
   * Can.Neither.isRight                   // Result: false
   * Can.Left("tulip").isRight             // Result: false
   * Can.Right("venus fly-trap").isRight   // Result: true
   * Can.Both("venus", "fly-trap").isRight // Result: false
   * ```
   */
  val isLeft: Boolean = false,
  /**
   * `true` if this is a [Can.Left], `false` otherwise.
   * @note Used only for performance instead of fold.
   *
   * Example:
   * ```
   * Can.Neither.isLeft                   // Result: false
   * Can.Left("tulip").isLeft             // Result: true
   * Can.Right("venus fly-trap").isLeft   // Result: false
   * Can.Both("venus", "fly-trap").isLeft // Result: false
   * ```
   */
  val isRight: Boolean = false,
  /**
   * `true` if this is a [Can.Both], `false` otherwise.
   * @note Used only for performance instead of fold.
   *
   * Example:
   * ```
   * Can.Neither.isBoth                   // Result: false
   * Can.Left("tulip").isBoth             // Result: false
   * Can.Right("venus fly-trap").isBoth   // Result: false
   * Can.Both("venus", "fly-trap").isBoth // Result: true
   * ```
   */
  val isBoth: Boolean = false
) : CanOf<A, B> {

  companion object {

    /**
     * Create an [Can] from two Options if at least one of them is defined.
     *
     * @param a an element (optional) for the left side of the [Can]
     * @param b an element (optional) for the right side of the [Can]
     *
     * @return [Can.Neither] if both [a] and [b] are [None]. Otherwise [Some] wrapping
     * an [Can.Left], [Can.Right], or [Can.Both] if [a], [b], or both are defined (respectively).
     */
    fun <A, B> fromOptions(a: Option<A>, b: Option<B>): Can<A, B> =
      fromNullables(a.orNull(), b.orNull())

    /**
     * The same as [fromOptions] but with nullable inputs.
     */
    fun <A, B> fromNullables(a: A?, b: B?): Can<A, B> = when {
      a != null && b != null -> Both(a, b)
      b != null -> Right(b)
      a != null -> Left(a)
      else -> Neither
    }

    fun neither(): Can<Nothing, Nothing> = Neither
    fun <A> left(left: A): Can<A, Nothing> = Left(left)
    fun <B> right(right: B): Can<Nothing, B> = Right(right)
    fun <A, B> both(left: A, right: B): Can<A, B> = Both(left, right)

    private tailrec fun <A2, A, B> Semigroup<A2>.loop(v: Can<A2, Either<A, B>>, f: (A) -> CanOf<A2, Either<A, B>>): Can<A2, B> = when (v) {
      is Neither -> Neither
      is Left -> Left(v.a)
      is Right -> when (val either = v.b) {
        is Either.Right -> Right(either.b)
        is Either.Left -> loop(f(either.a).fix(), f)
      }
      is Both -> when (val either = v.b) {
        is Either.Right -> Both(v.a, either.b)
        is Either.Left -> when (val fnb = f(either.a).fix()) {
          is Neither -> Neither
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
   * Transforms the right side from [B] to [C].
   *
   * This has no effect if this is a [Can.Left] or [Can.Neither].
   * In the instance that we have a [Can.Right] instance it will apply [f].
   * Equally, with [Can.Both], `b` will be transformed, but `a` will remain the same.
   *
   * @param f the function to apply
   * @see flatMap
   */
  fun <C> map(f: (B) -> C): Can<A, C> =
    fold({ Neither }, { Left(it) }, { Right(f(it)) }, { a, b -> Both(a, f(b)) })

  /**
   * The given function is applied if this is a [Can.Left] or [Can.Both] to [A].
   *
   * Example:
   * ```
   * Can.Neither.map { "flower" }                // Result: None
   * Can.Right(12).map { "flower" }              // Result: Right(12)
   * Can.Left(12).map { "flower" }               // Result: Left("power")
   * Can.Both(12, "power").map { "flower $it" }  // Result: Both("flower 12", "power")
   * ```
   */
  fun <C> mapLeft(fa: (A) -> C): Can<C, B> =
    fold({ Neither }, { Left(fa(it)) }, { Right((it)) }, { a, b -> Both(fa(a), b) })

  /**
   * Transforms either or both [A] and/or [B] tp [C] and/or [D] respectively.
   *
   * Example:
   * ```
   * Can.Neither.bimap({ 12 }, { "flower $it" })        // Result: Can.Neither
   * Can.Left(12).bimap({ 12 }, { "flower $it" })       // Result: Left("flower power")
   * Can.Right("power").bimap({ 12 }, { "flower $it" }) // Result: Can.Right(12)
   * Can.Both(12, "power").bimap({ 12 }, { "flower" })  // Result: Both(12, "flower power")
   * ```
   * @param fa tranforms left [A] to [C] on [Can.Left] or [Can.Both]
   * @param fb tranforms left [B] to [D] on [Can.Right] or [Can.Both]
   */
  fun <C, D> bimap(fa: (A) -> C, fb: (B) -> D): Can<C, D> =
    fold({ Neither }, { Left(fa(it)) }, { Right(fb(it)) }, { l, r -> Both(fa(l), fb(r)) })

  /**
   * Transforms this into an instance of [C] depending on the case.
   *
   * @param ifNeither produces a value for [C] on [Neither]
   * @param ifLeft transforms from left [A] to [C]
   * @param ifRight transforms from right [B] to [C]
   * @param ifBoth transforms both [A] and [B] to [C]
   */
  fun <C> fold(ifNeither: () -> C, ifLeft: (A) -> C, ifRight: (B) -> C, ifBoth: (A, B) -> C): C = when (this) {
    is Neither -> ifNeither()
    is Left -> ifLeft(a)
    is Right -> ifRight(b)
    is Both -> ifBoth(a, b)
  }

  /**
   * Similar to [map] but removes the right value if the result of [f] is null.
   * This destruction means that, when [f] results in a `null` value then [Can.Right]
   * becomes [Can.Neither] and [Can.Both] becomes [Can.Left], the rest remain the same.
   *
   * Example:
   * ```
   * Can.Neither.mapNotNull { null }           // Result: Can.Neither
   * Can.Left(12).mapNotNull { null }          // Result: Can.Left(12)
   * Can.Right("power").mapNotNull { null }    // Result: Can.Neither
   * Can.Both(12, "power").mapNotNull { null } // Result: Can.Left(12)
   * ```
   *
   * @param f transformation for right side
   */
  fun <C> mapNotNull(f: (B) -> C?): Can<A, C> =
    fromNullables(leftOrNull(), rightOrNull()?.let(f))

  /**
   * Similar to [mapLeft] but removes the left value if the result of [f] is `null`.
   * This destruction means that, when [f] results in a `null` value then [Can.Left]
   * becomes [Can.Neither] and [Can.Both] becomes [Can.Right], the rest remain the same.
   *
   * Example:
   * ```
   * Can.Neither.mapLeftNotNull { null }           // Result: Can.Neither
   * Can.Left(12).mapLeftNotNull { null }          // Result: Can.Neither
   * Can.Right("power").mapLeftNotNull { null }    // Result: Can.Right("power")
   * Can.Both(12, "power").mapLeftNotNull { null } // Result: Can.Right("power")
   *
   * Can.Neither.mapLeftNotNull { it + 30 }           // Result: Can.Neither
   * Can.Left(12).mapLeftNotNull { it + 30 }          // Result: Can.Left(42)
   * Can.Right("power").mapLeftNotNull { it + 30 }    // Result: Can.Right("power")
   * Can.Both(12, "power").mapLeftNotNull { it + 30 } // Result: Can.Both(42, "power")
   * ```
   *
   * @param f transformation for left side
   */
  fun <C> mapLeftNotNull(f: (A) -> C?): Can<C, B> =
    fromNullables(leftOrNull()?.let(f), rightOrNull())

  /**
   * Similar to [mapLeft] but removes the left value if the result of [fa] is `null`
   * and/or the right value if the result from [fb] is null.
   *
   * Example:
   * ```
   * Can.Neither.bimapNotNull({ null }, { null })           // Result: Can.Neither
   * Can.Left(12).bimapNotNull({ null }, { null })          // Result: Can.Neither
   * Can.Right("power").bimapNotNull({ null }, { null })    // Result: Can.Neither
   * Can.Both(12, "power").bimapNotNull({ null }, { null }) // Result: Can.Neither
   *
   * Can.Neither.bimapNotNull({ null }, { "max $it" })           // Result: Can.Neither
   * Can.Left(12).bimapNotNull({ null }, { "max $it" })          // Result: Can.Neither
   * Can.Right("power").bimapNotNull({ null }, { "max $it" })    // Result: Can.Right("max power")
   * Can.Both(12, "power").bimapNotNull({ null }, { "max $it" }) // Result: Can.Right("max power")
   *
   * Can.Neither.bimapNotNull({ it + 30 }, { null })           // Result: Can.Neither
   * Can.Left(12).bimapNotNull({ it + 30 }, { null })          // Result: Can.Left(42)
   * Can.Right("power").bimapNotNull({ it + 30 }, { null })    // Result: Can.Neither
   * Can.Both(12, "power").bimapNotNull({ it + 30 }, { null }) // Result: Can.Left(42)
   * ```
   *
   * @param fa transformation for left side
   * @param fb transformation for right side
   */
  fun <C, D> bimapNotNull(fa: (A) -> C?, fb: (B) -> D?): Can<C, D> =
    fromNullables(leftOrNull()?.let(fa), rightOrNull()?.let(fb))

  /**
   * Removes the right side if it doesn't pass the test from [predicate]
   *
   * Example:
   * ```
   * Can.Neither.filter { false }           // Result: Can.Neither
   * Can.Left(12).filter { false }          // Result: Can.Left(12)
   * Can.Right("power").filter { false }    // Result: Can.Neither
   * Can.Both(12, "power").filter { false } // Result: Can.Left(12)
   *
   * Can.Neither.filter { true }            // Result: Can.Neither
   * Can.Left(12).filter { true }           // Result: Can.Left(12)
   * Can.Right("power").filter { true }     // Result: Can.Right("power")
   * Can.Both(12, "power").filter { true }  // Result: Can.Both(12, "power")
   * ```
   *
   * @param predicate the predicate used for testing.
   */
  fun filter(predicate: Predicate<B>): Can<A, B> =
    fromNullables(leftOrNull(), rightOrNull()?.takeIf(predicate))

  /**
   * Removes the right side if it doesn't pass the test from [predicate]
   *
   * Example:
   * ```
   * Can.Neither.filterNot { false }           // Result: Can.Neither
   * Can.Left(12).filterNot { false }          // Result: Can.Left(12)
   * Can.Right("power").filterNot { false }    // Result: Can.Right("power")
   * Can.Both(12, "power").filterNot { false } // Result: Can.Both(12, "power")
   *
   * Can.Neither.filterNot { true }            // Result: Can.Neither
   * Can.Left(12).filterNot { true }           // Result: Can.Left(12)
   * Can.Right("power").filterNot { true }     // Result: Can.Neither
   * Can.Both(12, "power").filterNot { true }  // Result: Can.Left(12)
   * ```
   *
   * @param predicate the predicate used for testing.
   */
  fun filterNot(predicate: Predicate<B>): Can<A, B> =
    fromNullables(leftOrNull(), rightOrNull()?.takeUnless(predicate))

  /**
   * Returns this if the predicate over [A] passes, or [Neither] otherwise
   *
   * @param predicate the predicate used for testing.
   */
  fun filterLeft(predicate: Predicate<A>): Can<A, B> =
    fromNullables(leftOrNull()?.takeIf(predicate), rightOrNull())

  /**
   * Opposite of [filterLeft]
   *
   * @param predicate the predicate used for testing.
   */
  fun filterNotLeft(predicate: Predicate<A>): Can<A, B> =
    fromNullables(leftOrNull()?.takeUnless(predicate), rightOrNull())

  /**
   * Returns true if [B] passes the provided predicate for [Can.Right], or [Can.Both] instances.
   *
   * @param predicate the predicate used for testing.
   */
  fun exists(predicate: Predicate<B>): Boolean =
    fold({ false }, { false }, predicate, { _, b -> predicate(b) })

  /**
   * Returns true if [A] passes the provided predicate for [Can.Left], or [Can.Both] instances.
   *
   * @param predicate the predicate used for testing.
   */
  fun existsLeft(predicate: Predicate<A>): Boolean =
    fold({ false }, predicate, { false }, { a, _ -> predicate(a) })

  fun <C> foldLeft(c: C, f: (C, B) -> C): C =
    fold({ c }, { c }, { f(c, it) }, { _, b -> f(c, b) })

  fun <C> foldRight(lc: Eval<C>, f: (B, Eval<C>) -> Eval<C>): Eval<C> =
    fold({ lc }, { lc }, { Eval.defer { f(it, lc) } }, { _, b -> Eval.defer { f(b, lc) } })

  fun <C> bifoldLeft(c: C, f: (C, A) -> C, g: (C, B) -> C): C =
    fold({ c }, { f(c, it) }, { g(c, it) }, { a, b -> g(f(c, a), b) })

  fun <C> bifoldRight(c: Eval<C>, f: (A, Eval<C>) -> Eval<C>, g: (B, Eval<C>) -> Eval<C>): Eval<C> =
    fold({ c }, { f(it, c) }, { g(it, c) }, { a, b -> f(a, g(b, c)) })

  /**
   * Return the isomorphic [Option]<[Ior]<[A], [B]>> of this [Can]<[A], [B]>
   */
  fun unwrap(): Option<Ior<A, B>> =
    fold({ None }, { Some(Ior.Left(it)) }, { Some(Ior.Right(it)) }, { a, b -> Some(Ior.Both(a, b)) })

  /**
   * Inverts components:
   *  - If [Neither] is remains [Neither]
   *  - If [Left]<[A]> it returns [Right]<[A]>
   *  - If [Right]<[B]> it returns [Left]<[B]>
   *  - If [Both]<[A], [B]> it returns [Both]<[B], [A]>
   *
   * Example:
   * ```
   * Can.Neither.swap()               // Result: None
   * Can.Left("left").swap()          // Result: Right("left")
   * Can.Right("right").swap()        // Result: Left("right")
   * Can.Both("left", "right").swap() // Result: Both("right", "left")
   * ```
   */
  fun swap(): Can<B, A> = fold({ Neither }, { Right(it) }, { Left(it) }, { a, b -> Both(b, a) })

  /**
   * Return this [Can] as [Pair] of [Option]
   *
   * Example:
   * ```
   * Can.Neither.pad()            // Result: Pair(None, None)
   * Can.Right(12).pad()          // Result: Pair(None, Some(12))
   * Can.Left(12).pad()           // Result: Pair(Some(12), None)
   * Can.Both("power", 12).pad()  // Result: Pair(Some("power"), Some(12))
   * ```
   */
  fun pad(): Pair<Option<A>, Option<B>> =
    fold({ None to None }, { Some(it) to None }, { None to Some(it) }, { a, b -> Some(a) to Some(b) })

  /**
   * Provides a printable description of [Can] given the relevant [Show] instances.
   */
  fun show(SL: Show<A>, SR: Show<B>): String = fold(
    { "Neither" },
    { "Left(${SL.run { it.show() }})" },
    { "Right(${SR.run { it.show() }})" },
    { a, b -> "Both(left=${SL.run { a.show() }}, right=${SR.run { b.show() }})" }
  )

  override fun toString(): String = show(Show.any(), Show.any())

  object Neither : Can<Nothing, Nothing>(isNeither = true)
  data class Left<out A>(val a: A) : Can<A, Nothing>(isLeft = true)
  data class Right<out B>(val b: B) : Can<Nothing, B>(isRight = true)
  data class Both<out A, out B>(val a: A, val b: B) : Can<A, B>(isBoth = true)
}

/**
 * Returns a [Validated.Valid] containing the [Can.Right] value or [B] if this is [Can.Right] or [Can.Both]
 * and [Validated.Invalid] if this is a [Can.Left].
 *
 * Example:
 * ```
 * Can.Neither.toValidated { Invalid(-1) } // Result: Invalid(-1)
 * Can.Right("power").toValidated()        // Result: Valid("power")
 * Can.Left(12).toValidated()              // Result: Invalid(12)
 * Can.Both(12, "power").toValidated()     // Result: Valid("power")
 * ```
 * @param ifNeither used to source an intance of [Validated]
 */
fun <A, B> CanOf<A, B>.toValidated(ifNeither: () -> Validated<A, B>): Validated<A, B> =
  fix().fold(ifNeither, ::Invalid, ::Valid, { _, b -> Valid(b) })

/**
 * Returns a [Validated.Valid] containing the [Can.Left] value or [A] if this is [Can.Left] or [Can.Both]
 * and [Validated.Invalid] if this is a [Can.Right].
 *
 * Example:
 * ```
 * Can.Neither.toValidatedLeft { Invalid(-1) } // Result: Invalid(-1)
 * Can.Right("power").toValidatedLeft()        // Result: Invalid("power")
 * Can.Left(12).toValidatedLeft()              // Result: Valid(12)
 * Can.Both(12, "power").toValidatedLeft()     // Result: Valid(12)
 * ```
 * @param ifNeither used to source an intance of [Validated]
 */
fun <A, B> CanOf<A, B>.toValidatedLeft(ifNeither: () -> Validated<B, A>): Validated<B, A> =
  fix().fold(ifNeither, ::Valid, ::Invalid, { a, _ -> Valid(a) })

/**
 * Similar to [toValidated] but returning [None] if there is nothing to validate.
 *
 * Examples:
 * ```
 * Can.Neither.toValidated()               // Result: None
 * Can.Right("power").toValidated()        // Result: Some(Valid("power"))
 * Can.Left(12).toValidated()              // Result: Some(Invalid(12))
 * Can.Both(12, "power").toValidated()     // Result: Some(Valid("power"))
 * ```
 * @return [None] if the [Can] is [Can.Neither], otherwise the result from [toValidated] inside [Some]
 */
fun <A, B> CanOf<A, B>.toValidated(): Option<Validated<A, B>> =
  fix().fold({ None }, { Some(Invalid(it)) }, { Some(Valid(it)) }) { _: A, b: B -> Some(Valid(b)) }

/**
 * Similar to [toValidatedLeft] but returning [None] if there is nothing to validate.
 *
 * Examples:
 * ```
 * Can.Neither.toValidatedLeft()           // Result: None
 * Can.Right(12).toValidatedLeft()         // Result: Some(Valid(12))
 * Can.Left("power").toValidatedLeft()     // Result: Some(Invalid("power"))
 * Can.Both(12, "power").toValidatedLeft() // Result: Some(Valid(12))
 * ```
 * @return [None] if the [Can] is [Can.Neither], otherwise the result from [toValidatedLeft] inside [Some]
 */
fun <A, B> CanOf<A, B>.toValidatedLeft(): Option<Validated<B, A>> =
  fix().fold({ None }, { Some(Valid(it)) }, { Some(Invalid(it)) }) { a: A, _: B -> Some(Valid(a)) }

/**
 * Similar to [Can.unwrap] with a fallback alternative in case of working with an instance of [Can.Neither]
 */
fun <A, B> CanOf<A, B>.toIor(ifNone: () -> IorOf<A, B>): Ior<A, B> =
  fix().unwrap().getOrElse(ifNone).fix()

/**
 * Binds the given function across [Can.Right] or [Can.Both].
 *
 * @param f The function to bind across [Can.Right] or [Can.Both].
 */
fun <A, B, C> CanOf<A, B>.flatMap(SG: Semigroup<A>, f: (B) -> CanOf<A, C>): Can<A, C> =
  fix().fold({ Can.Neither }, { Can.Left(it) }, { f(it).fix() }, { a, b -> SG.flatMapCombine(a, b, f) })

/**
 * Used internally by [flatMap] for the [Can.Both] case.
 */
private fun <A, B, C> Semigroup<A>.flatMapCombine(a: A, b: B, f: (B) -> CanOf<A, C>) =
  f(b).fix().fold({ Can.Neither }, { Can.Left(a.combine(it)) }, { Can.Both(a, it) }, { ll, rr -> Can.Both(a.combine(ll), rr) })

/**
 * Safe unwrapping of the right side.
 *
 * @return [B] if [Can] is [Can.Right] or [Can.Both], otherwise the result from [default].
 */
fun <A, B> CanOf<A, B>.getOrElse(default: () -> B): B = rightOrNull() ?: default()

/**
 * Safe unwrapping of the left side.
 *
 * @return [A] if [Can] is [Can.Left] or [Can.Both], otherwise the result from [default].
 */
fun <A, B> CanOf<A, B>.getLeftOrElse(default: () -> A): A = leftOrNull() ?: default()

/**
 * Applies the provided "[Can]ned" function given the Semigroup definition provided by [SG]
 *
 * @return The result of applying this [Can] to the function encapsulated [ff]
 */
fun <A, B, C> CanOf<A, B>.ap(SG: Semigroup<A>, ff: CanOf<A, (B) -> C>): Can<A, C> =
  flatMap(SG) { a -> ff.fix().map { f: (B) -> C -> f(a) } }

/**
 * Converts the nullable receiver of [Pair]<[A], [B]> into an instance of [Can].
 *
 * @return an instance of [Can.Both] when not null, or [Can.Neither] otherwise
 */
fun <A, B> Pair<A, B>.toCan(): Can<A, B> = Can.Both(first, second)

/**
 * Converts the nullable receiver of [Tuple2]<[A], [B]> into an instance of [Can].
 *
 * @return an instance of [Can.Both] when not null, or [Can.Neither] otherwise
 */
fun <A, B> Tuple2<A, B>.toCan(): Can<A, B> = Can.Both(a, b)

/**
 * Converts a, potentially nullable, instance of [A] into an instance of [Can]
 *
 * @return [None] when `null` or [Can.Left]<[A]> otherwise
 */
fun <A> A.toLeftCan(): Can<A, Nothing> = Can.Left(this)

/**
 * Same as [Option.toRightCan] but for nullable values
 */
fun <B> B.toRightCan(): Can<Nothing, B> = Can.Right(this)

/**
 * Converts a instance of [Option]<[A]> into an instance of [Can]<[A]>
 *
 * @return [Can.Neither] when [None] or [Can.Right]<[A]> when [Some]<[A]>
 */
fun <A> OptionOf<A>.toLeftCan(): Can<A, Nothing> =
  fix().fold({ Can.Neither }, { a -> Can.Left(a) })

/**
 * Converts a instance of [Option]<[B]> into an instance of [Can]<[B]>
 *
 * @return [Can.Neither] when [None] or [Can.Right]<[B]> when [Some]<[B]>
 */
fun <B> OptionOf<B>.toRightCan(): Can<Nothing, B> =
  fix().fold({ Can.Neither }, { b -> Can.Right(b) })

/**
 * Converts a given [Ior]<[A], [B]> instance into a [Can]<[A], [B]> instance.
 *
 * Mapping:
 * - Never       -> [Can.Neither]
 * - [Ior.Left]  -> [Can.Left]
 * - [Ior.Right] -> [Can.Right]
 * - [Ior.Both]  -> [Can.Both]
 *
 * @return Either [Can.Left], [Can.Right], or [Can.Both], never [Can.Neither]
 */
fun <A, B> IorOf<A, B>.toCan(): Can<A, B> =
  fix().fold({ Can.Left(it) }, { Can.Right(it) }) { a, b -> Can.Both(a, b) }

/**
 * Extraction of the left value, if present.
 *
 * Mapping:
 * - [Can.Neither] -> [None]
 * - [Can.Left]    -> [Some]<[A]>
 * - [Can.Right]   -> [None]
 * - [Can.Both]    -> [Some]<[A]>
 *
 * @return [Some]<[A]> if we have [Can.Left] or [Can.Both], otherwise [None]
 */
fun <A, B> CanOf<A, B>.left(): Option<A> = leftOrNull().toOption()

/**
 * Same as [left] but returning a nullable value instead of an [Option]
 *
 * Mapping:
 * - [Can.Neither] -> `null`
 * - [Can.Left]    -> [A]
 * - [Can.Right]   -> `null`
 * - [Can.Both]    -> [A]
 */
fun <A, B> CanOf<A, B>.leftOrNull(): A? =
  when (val can = fix()) {
    is Can.Left -> can.a
    is Can.Both -> can.a
    else -> null
  }

/**
 * Deconstruction of the left side of this [Can]
 *
 * example:
 * ```
 * fun currentUserAndOrg() : Can<User, Org>
 *
 * val (user, org) = currentUserAndOrg()
 * ```
 */
operator fun <A, B> CanOf<A, B>.component1(): Option<A> = left()

/**
 * Extraction of the right value, if present.
 *
 * Mapping:
 * - [Can.Neither] -> [None]
 * - [Can.Left] -> [None]
 * - [Can.Right] -> [Some]<[A]>
 * - [Can.Both] -> [Some]<[A]>
 *
 * @return [Some]<[A]> if we have [Can.Right] or [Can.Both], otherwise [None]
 */
fun <A, B> CanOf<A, B>.right(): Option<B> = rightOrNull().toOption()

/**
 * Same as [right] but returning a nullable value instead of an [Option]
 *
 * Mapping:
 * - [Can.Neither] -> `null`
 * - [Can.Left]    -> [B]
 * - [Can.Right]   -> `null`
 * - [Can.Both]    -> [B]
 */
fun <A, B> CanOf<A, B>.rightOrNull(): B? = when (val can = fix()) {
  is Can.Right -> can.b
  is Can.Both -> can.b
  else -> null
}

/**
 * Deconstruction of the right side of this [Can]
 *
 * example:
 * ```
 * fun currentUserAndOrg() : Can<User, Org>
 *
 * val (user, org) = currentUserAndOrg()
 * ```
 */
operator fun <A, B> CanOf<A, B>.component2(): Option<B> = right()

fun test() {
  Can.Both("Over", 9000).component1()
  val (left, right) = Can.Both("Over", 9000)
}
