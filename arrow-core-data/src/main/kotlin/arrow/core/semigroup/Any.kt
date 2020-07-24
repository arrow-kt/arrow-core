package arrow.core.semigroup

/**
 * Boolean monoid under disjunction ||
 *
 * ```kotlin:ank
 * import arrow.core.semigroup.Any
 * import arrow.core.extensions.semigroup.any.monoid.monoid
 *
 * //sampleStart
 * arrow.core.semigroup.Any.monoid().run { Any(false) + Any(true) }.getAny
 * //sampleEnd
 * ```
 * ```kotlin:ank
 * import arrow.core.semigroup.Any
 * import arrow.core.extensions.list.foldable.foldMap
 * //sampleStart
 * listOf(1,2,3,4,5,6,7,8,9).foldMap(arrow.core.semigroup.Any.monoid()) { x -> Any(x.rem(2) == 0) }.getAny
 * //sampleEnd
 * ```
 */
data class Any(val getAny: Boolean) {
  companion object
}
