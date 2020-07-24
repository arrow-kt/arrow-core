package arrow.core.semigroup

import arrow.higherkind

/**
 * Monoid which chooses the smaller element on combine
 *
 * ```kotlin:ank
 * import arrow.core.semigroup.Min
 * import arrow.core.extensions.semigroup.min.semigroup.semigroup
 * import arrow.core.extensions.order
 *
 * //sampleStart
 * Min.semigroup(Int.order()).run { Min(10) + Min(15) }.getMin
 * //sampleEnd
 * ```
 */
@higherkind
data class Min<A>(val getMin: A) : MinOf<A> {
  companion object
}
