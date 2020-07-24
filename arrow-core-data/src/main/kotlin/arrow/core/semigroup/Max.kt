package arrow.core.semigroup

import arrow.higherkind

/**
 * Monoid which chooses the larger element on combine
 *
 * ```kotlin:ank
 * import arrow.core.semigroup.Max
 * import arrow.core.extensions.semigroup.max.monoid.monoid
 *
 * //sampleStart
 * Max.monoid(Int.order()).run { Max(10) + Max(15) }.getMax
 * //sampleEnd
 * ```
 * ```kotlin:ank
 * import arrow.core.extensions.list.foldable.foldMap
 * //sampleStart
 * listOf(1, 6, 10, 3, 6).foldMap(Max.monoid(), :: Max).getMax
 * //sampleEnd
 * ```
 */
@higherkind
data class Max<A>(val getMax: A) : MaxOf<A> {
  companion object
}
