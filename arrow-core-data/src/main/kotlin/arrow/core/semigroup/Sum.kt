package arrow.core.semigroup

import arrow.higherkind

/**
 * Monoid under addition
 *
 * ```kotlin:ank
 * import arrow.core.semigroup.Sum
 * import arrow.core.extensions.semigroup.sum.monoid.monoid
 * import arrow.core.extensions.num
 * //sampleStart
 * Sum.monoid(Int.num()).run {
 *   Sum(1) + Sum(11)
 * }.getSum
 * ```
 * ```kotlin:ank
 * import arrow.core.extensions.list.foldable.foldMap
 * //sampleStart
 * listOf(1,2,3,4).foldMap(Sum.monoid(Int.num()), ::Sum).getSum
 * //sampleEnd
 * ```
 */
@higherkind
data class Sum<A>(val getSum: A) : SumOf<A> {
  companion object
}
