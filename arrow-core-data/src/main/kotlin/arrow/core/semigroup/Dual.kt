package arrow.core.semigroup

import arrow.higherkind

/**
 * Dual of a monoid. Switches argument order of combine.
 *
 * ```kotlin:ank
 * import arrow.core.semigroup.Dual
 * import arrow.core.extensions.semigroup.dual.monoid.monoid
 * //sampleStart
 * Dual.monoid(String.monoid()).run { Dual("World") + Dual("Hello ") }.getDual
 * //sampleEnd
 * ```
 */
@higherkind
data class Dual<A>(val getDual: A) : DualOf<A> {
  companion object
}
