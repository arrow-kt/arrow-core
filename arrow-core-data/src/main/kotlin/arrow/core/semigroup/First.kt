package arrow.core.semigroup

import arrow.higherkind

/**
 * Semigroup which always chooses the first argument
 *
 * ```kotlin:ank
 * import arrow.core.semigroup.First
 * import arrow.core.extensions.semigroup.first.monoid.monoid
 * //sampleStart
 * First.semigroup<Int>().run { First(1) + First(2) }.getFirst
 * //sampleEnd
 * ```
 * ```kotlin:ank
 * import arrow.core.extensions.list.foldable.foldMap
 * //sampleStart
 * listOf(1,2,3,4,5).foldMap(First.monoid(), ::First).getFirst
 * //sampleEnd
 * ```
 * ```kotlin:ank
 * import arrow.core.None
 * import arrow.core.Option
 * import arrow.core.Some
 * import arrow.core.extensions.option.monoid.monoid
 * //sampleStart
 * listOf(1,2,3,4,5).foldMap(Option.monoid(First.monoid())) { x ->
 *   if (x.rem(2) == 0) None else Some(First(x))
 * }.getFirst
 * //sampleEnd
 * ```
 */
@higherkind
data class First<A>(val getFirst: A) : FirstOf<A> {
  companion object
}
