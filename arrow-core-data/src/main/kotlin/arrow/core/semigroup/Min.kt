package arrow.core.semigroup

import arrow.higherkind

/**
 * Monoid which chooses the smaller element on combine
 *
 * ```kotlin:ank
 * import arrow.core.semigroup.Min
 * import arrow.core.extensions.semigroup.min.monoid.monoid
 *
 * //sampleStart
 * Min.monoid(Int.order()).run { Min(10) + Min(15) }.getMin
 * //sampleEnd
 * ```
 * ```kotlin:ank
 * import arrow.core.extensions.list.foldable.foldMap
 * //sampleStart
 * listOf(1, 6, 10, 3, 6).foldMap(Min.monoid(), :: Min).getMin
 * //sampleEnd
 * ```
 */
@higherkind
data class Min<A>(val getMin: A) : MinOf<A> {
  companion object
}
