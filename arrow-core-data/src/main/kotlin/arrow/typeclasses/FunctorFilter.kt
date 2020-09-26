package arrow.typeclasses

import arrow.Kind
import arrow.core.Option
import arrow.core.identity

/**
 * ank_macro_hierarchy(arrow.typeclasses.FunctorFilter)
 *
 * A Functor with the ability to [filterMap].
 */
interface FunctorFilter<F> : Functor<F> {

  /**
   * A combined map and filter. Filtering is handled via [Option] instead of [Boolean] such that the output type [B] can be different than the input type [A].
   */
  @Deprecated("Please use mapNotNull")
  fun <A, B> Kind<F, A>.filterMap(f: (A) -> Option<B>): Kind<F, B>

  /**
   * A combined map and filter. Filtering is handled via nullable [B] instead of [Boolean] such that the output type [B] can be different than the input type [A].
   */
  fun <A, B> Kind<F, A>.mapNotNull(f: (A) -> B?): Kind<F, B> = filterMap { Option.fromNullable(f(it)) }

  /**
   * "Flatten" out a structure by collapsing Options.
   */
  @Deprecated("Please use filterNotNull")
  fun <A> Kind<F, Option<A>>.flattenOption(): Kind<F, A> = filterMap(::identity)

  /**
   * "Flatten" out a structure by removing null values.
   */
  fun <A> Kind<F, A?>.filterNotNull(): Kind<F, A> = mapNotNull(::identity)

  /**
   * Apply a filter to a structure such that the output structure contains all [A] elements in the input structure that satisfy the predicate [f] but none
   * that don't.
   */
  fun <A> Kind<F, A>.filter(f: (A) -> Boolean): Kind<F, A> =
    mapNotNull { a -> if (f(a)) a else null }

  /**
   * Filter out instances of [B] type.
   */
  fun <A, B> Kind<F, A>.filterIsInstance(klass: Class<B>): Kind<F, B> =
    filter(klass::isInstance)
      .map { klass.cast(it) }
}
