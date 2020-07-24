package arrow.core.semigroup

import arrow.higherkind

/**
 * Monoid which chooses the larger element on combine
 *
 * ```kotlin:ank
 * import arrow.core.semigroup.Max
 * import arrow.core.extensions.semigroup.max.semigroup.semigroup
 * import arrow.core.extensions.order
 *
 * //sampleStart
 * Max.semigroup(Int.order()).run { Max(10) + Max(15) }.getMax
 * //sampleEnd
 * ```
 */
@higherkind
data class Max<A>(val getMax: A) : MaxOf<A> {
  companion object
}
