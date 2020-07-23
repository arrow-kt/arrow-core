package arrow.core.semigroup

/**
 * Boolean monoid under conjunction &&
 *
 * ```kotlin:ank
 * import arrow.core.semigroup.All
 * import arrow.core.extensions.semigroup.all.monoid.monoid
 * //sampleStart
 * All.monoid().run { All(false) + All(true) }.getAll
 * //sampleEnd
 * ```
 * ```kotlin:ank
 * import arrow.core.extensions.list.foldable.foldMap
 * //sampleStart
 * listOf(1,2,3,4,5,6,7,8,9).foldMap(All.monoid()) { x -> All(x.rem(2) == 0) }.getAll
 * //sampleEnd
 * ```
 */
data class All(val getAll: Boolean) {
  companion object
}
