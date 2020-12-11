package arrow.typeclasses

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.Eval.Companion.always
import arrow.core.Left
import arrow.core.None
import arrow.core.Option
import arrow.core.Right
import arrow.core.Some
import arrow.core.flatMap
import arrow.core.identity
import arrow.core.left
import arrow.core.right

/**
 * ank_macro_hierarchy(arrow.typeclasses.Foldable)
 *
 * Data structures that can be folded to a summary value.
 *
 * Foldable<F> is implemented in terms of two basic methods:
 *
 * - `fa.foldLeft(init, f)` eagerly folds `fa` from left-to-right.
 * - `fa.foldRight(init, f)` lazily folds `fa` from right-to-left.
 *
 * Beyond these it provides many other useful methods related to folding over F<A> values.
 */
interface Foldable<F> {

  /**
   * Left associative fold on F using the provided function.
   */
  fun <A, B> Kind<F, A>.foldLeft(b: B, f: (B, A) -> B): B

  /**
   * Right associative lazy fold on F using the provided function.
   *
   * This method evaluates lb lazily (in some cases it will not be needed), and returns a lazy value. We are using
   * (A, Eval<B>) => Eval<B> to support laziness in a stack-safe way. Chained computation should be performed via
   * .map and .flatMap.
   *
   * For more detailed information about how this method works see the documentation for Eval<A>.
   */
  fun <A, B> Kind<F, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B>

  /**
   * Fold/combine elements into a single value using the given Monoid<A>
   */
  fun <A> Kind<F, A>.fold(MN: Monoid<A>): A = MN.run {
    foldLeft(empty()) { acc, a -> acc.combine(a) }
  }

  @Deprecated("Please use reduceLeftToNullable")
  fun <A, B> Kind<F, A>.reduceLeftToOption(f: (A) -> B, g: (B, A) -> B): Option<B> =
    foldLeft(Option.empty()) { option, a ->
      when (option) {
        is Some<B> -> Some(g(option.t, a))
        is None -> Some(f(a))
      }
    }

  fun <A, B> Kind<F, A>.reduceLeftToNullable(f: (A) -> B, g: (B, A) -> B): B? =
    foldLeft(null) { acc: B?, a: A ->
      acc?.let { g(it, a) } ?: f(a)
    }

  @Deprecated("Please use reduceRightToNullable")
  fun <A, B> Kind<F, A>.reduceRightToOption(
    f: (A) -> B,
    g: (A, Eval<B>) -> Eval<B>
  ): Eval<Option<B>> =
    foldRight(Eval.Now(Option.empty())) { a: A, lb: Eval<Option<B>> ->
      lb.flatMap { option ->
        when (option) {
          is Some<B> -> g(a, Eval.Now(option.t)).map { Some(it) }
          is None -> Eval.Later { Some(f(a)) }
        }
      }
    }

  fun <A, B> Kind<F, A>.reduceRightToNullable(
    f: (A) -> B,
    g: (A, Eval<B>) -> Eval<B>
  ): Eval<B?> =
    foldRight(Eval.now(null)) { a: A, lb: Eval<B?> ->
      lb.flatMap {
        it?.let { g(a, Eval.now(it)) } ?: Eval.later { f(a) }
      }
    }

  /**
   * Reduce the elements of this structure down to a single value by applying the provided aggregation function in
   * a left-associative manner.
   *
   * @return None if the structure is empty, otherwise the result of combining the cumulative left-associative result
   * of the f operation over all of the elements.
   */
  @Deprecated("Please use reduceLeftNullable")
  fun <A> Kind<F, A>.reduceLeftOption(f: (A, A) -> A): Option<A> =
    reduceLeftToOption({ a -> a }, f)

  /**
   * Reduce the elements of this structure down to a single value by applying the provided aggregation function in
   * a left-associative manner.
   *
   * @return null if the structure is empty, otherwise the result of combining the cumulative left-associative result
   * of the f operation over all of the elements.
   */
  fun <A> Kind<F, A>.reduceLeftNullable(f: (A, A) -> A): A? =
    reduceLeftToNullable(::identity, f)

  /**
   * Reduce the elements of this structure down to a single value by applying the provided aggregation function in
   * a right-associative manner.
   *
   * @return None if the structure is empty, otherwise the result of combining the cumulative right-associative
   * result of the f operation over the A elements.
   */
  @Deprecated("Please use reduceRightNullable")
  fun <A> Kind<F, A>.reduceRightOption(f: (A, Eval<A>) -> Eval<A>): Eval<Option<A>> =
    reduceRightToOption({ a -> a }, f)

  /**
   * Reduce the elements of this structure down to a single value by applying the provided aggregation function in
   * a right-associative manner.
   *
   * @return null if the structure is empty, otherwise the result of combining the cumulative right-associative
   * result of the f operation over the A elements.
   */
  fun <A> Kind<F, A>.reduceRightNullable(f: (A, Eval<A>) -> Eval<A>): Eval<A?> =
    reduceRightToNullable(::identity, f)

  /**
   * Alias for fold.
   */
  fun <A> Kind<F, A>.combineAll(MN: Monoid<A>): A =
    fold(MN)

  /**
   * Fold implemented by mapping A values into B and then combining them using the given Monoid<B> instance.
   */
  fun <A, B> Kind<F, A>.foldMap(MN: Monoid<B>, f: (A) -> B): B = MN.run {
    foldLeft(MN.empty()) { b, a -> b.combine(f(a)) }
  }

  fun <A> orEmpty(AF: Applicative<F>, MA: Monoid<A>): Kind<F, A> =
    AF.run { just(MA.empty()) }

  /**
   * Traverse F<A> using Applicative<G>.
   *
   * A typed values will be mapped into G<B> by function f and combined using Applicative#map2.
   *
   * This method is primarily useful when G<_> represents an action or effect, and the specific A aspect of G<A> is
   * not otherwise needed.
   */
  fun <G, A, B> Kind<F, A>.traverse_(GA: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, Unit> =
    foldRight(always { GA.just(Unit) }) { a, acc -> GA.run { f(a).apEval(acc.map { it.map { { _: B -> Unit } } }) } }.value()

  /**
   * Sequence F<G<A>> using Applicative<G>.
   *
   * Similar to traverse except it operates on F<G<A>> values, so no additional functions are needed.
   */
  fun <G, A> Kind<F, Kind<G, A>>.sequence_(GA: Applicative<G>): Kind<G, Unit> =
    traverse_(GA, ::identity)

  /**
   * Find the first element matching the predicate, if one exists.
   */
  @Deprecated("Please use findNullable")
  fun <A> Kind<F, A>.find(f: (A) -> Boolean): Option<A> =
    foldRight(Eval.now<Option<A>>(None)) { a, lb ->
      if (f(a)) Eval.now(Some(a)) else lb
    }.value()

  /**
   * Find the first element matching the predicate, if one exists.
   *
   * @return the element, if one exists, null otherwise
   */
  fun <A> Kind<F, A>.findNullable(f: (A) -> Boolean): A? =
    foldRight(Eval.now(null)) { a: A, lb: Eval<A?> ->
      if (f(a)) Eval.now(a) else lb
    }.value()

  /**
   * Check whether at least one element satisfies the predicate.
   *
   * If there are no elements, the result is false.
   */
  fun <A> Kind<F, A>.exists(p: (A) -> Boolean): Boolean =
    this.foldRight(Eval.False) { a, lb -> if (p(a)) Eval.True else lb }.value()

  /**
   * Check whether all elements satisfy the predicate.
   *
   * If there are no elements, the result is true.
   */
  @Deprecated("In favor of having a more Kotlin idiomatic API", ReplaceWith("all(p)"))
  fun <A> Kind<F, A>.forAll(p: (A) -> Boolean): Boolean =
    all(p)

  /**
   * Check whether all elements satisfy the predicate.
   *
   * If there are no elements, the result is true.
   */
  fun <A> Kind<F, A>.all(p: (A) -> Boolean): Boolean =
    this.foldRight(Eval.True) { a, lb -> if (p(a)) lb else Eval.False }.value()

  /**
   * Returns true if there are no elements. Otherwise false.
   */
  fun <A> Kind<F, A>.isEmpty(): Boolean =
    this.foldRight(Eval.True) { _, _ -> Eval.False }.value()

  @Deprecated("In favor of having a more Kotlin idiomatic API", ReplaceWith("isNotEmpty()"))
  fun <A> Kind<F, A>.nonEmpty(): Boolean =
    isNotEmpty()

  fun <A> Kind<F, A>.isNotEmpty(): Boolean =
    !isEmpty()

  /**
   * The size of this Foldable.
   *
   * Note: will not terminate for infinite-sized collections.
   */
  fun <A> Kind<F, A>.size(MN: Monoid<Long>): Long =
    foldMap(MN) { 1 }

  /**
   * Applicative folding on F by mapping A values to G<B>, combining the B values using the given Monoid<B> instance.
   */
  fun <G, A, B, AP, MO> Kind<F, A>.foldMapA(ap: AP, mo: MO, f: (A) -> Kind<G, B>): Kind<G, B>
    where AP : Applicative<G>, MO : Monoid<B> = foldMap(mo.lift(ap), f)

  /**
   * Monadic folding on F by mapping A values to G<B>, combining the B values using the given Monoid<B> instance.
   *
   * Similar to foldM, but using a Monoid<B>.
   */
  fun <G, A, B, MA, MO> Kind<F, A>.foldMapM(ma: MA, mo: MO, f: (A) -> Kind<G, B>): Kind<G, B>
    where MA : Monad<G>, MO : Monoid<B> = ma.run {
    foldM(ma, mo.empty()) { b, a -> f(a).map { mo.run { b.combine(it) } } }
  }

  /**
   * Left associative monadic folding on F.
   *
   * The default implementation of this is based on foldL, and thus will always fold across the entire structure.
   * Certain structures are able to implement this in such a way that folds can be short-circuited (not traverse the
   * entirety of the structure), depending on the G result produced at a given step.
   */
  fun <G, A, B> Kind<F, A>.foldM(M: Monad<G>, z: B, f: (B, A) -> Kind<G, B>): Kind<G, B> = M.run {
    foldLeft(M.just(z)) { gb, a -> gb.flatMap { f(it, a) } }
  }

  /**
   * Get the element at the index of the Foldable.
   */
  @Deprecated("Use getOrNull")
  fun <A> Kind<F, A>.get(idx: Long): Option<A> =
    if (idx < 0L)
      None
    else
      foldLeft<A, Either<A, Long>>(0L.right()) { i, a ->
        i.flatMap {
          if (it == idx) Left(a)
          else Right(it + 1L)
        }
      }.swap().toOption()

  /**
   * Get the element at the index of the Foldable
   *
   * @return the element or null if the index is out of bounds
   */
  fun <A> Kind<F, A>.getOrNull(idx: Long): A? =
    if (idx < 0L)
      null
    else
      foldLeft(0L.right()) { i: Either<A, Long>, a: A ->
        i.flatMap {
          if (it == idx) a.left()
          else (it + 1L).right()
        }
      }.swap().orNull()

  /**
   * Get the first element of the foldable or none
   */
  @Deprecated("Please use firstOrNull")
  fun <A> Kind<F, A>.firstOrNone(): Option<A> =
    find { true }

  /**
   * Get the first element of the foldable, or null
   */
  fun <A> Kind<F, A>.firstOrNull(): A? =
    findNullable { true }

  /**
   * Get the first element matching the predicate or none
   */
  @Deprecated("Please use firstOrNull")
  fun <A> Kind<F, A>.firstOrNone(predicate: (A) -> Boolean): Option<A> =
    find { predicate(it) }

  /**
   * Get the first element matching the predicate or null
   */
  fun <A> Kind<F, A>.firstOrNull(predicate: (A) -> Boolean): A? =
    findNullable { predicate(it) }

  fun <A> Kind<F, A>.toList(): List<A> =
    foldRight(Eval.now(emptyList<A>())) { v, acc -> acc.map { listOf(v) + it } }.value()

  companion object {
    @Deprecated("This function will be removed soon. Use Iterator.iterateRight from Eval.kt instead")
    fun <A, B> iterateRight(it: Iterator<A>, lb: Eval<B>): (f: (A, Eval<B>) -> Eval<B>) -> Eval<B> = { f: (A, Eval<B>) -> Eval<B> ->
      fun loop(): Eval<B> =
        Eval.defer { if (it.hasNext()) f(it.next(), loop()) else lb }
      loop()
    }
  }
}
