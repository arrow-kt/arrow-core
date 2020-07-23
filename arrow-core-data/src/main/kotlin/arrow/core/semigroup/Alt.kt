package arrow.core.semigroup

import arrow.Kind
import arrow.higherkind

/**
 * Monoid under orElse
 *
 * ```kotlin:ank
 * import arrow.core.semigroup.Alt
 * import arrow.core.extensions.alt.monoid.monoid
 * import arrow.core.extensions.option.alternative.alternative
 * import arrow.core.ForOption
 * import arrow.core.Option
 * import arrow.core.Some
 * import arrow.core.none
 *
 * //sampleStart
 * Alt.monoid<ForOption, Int>(Option.alternative()).run { Alt(none<Int>()) + Alt(Some(10)) }.getAlt
 * //sampleEnd
 * ```
 * ```kotlin:ank
 * import arrow.core.extensions.list.foldable.foldMap
 *
 * //sampleStart
 * listOf(None, Some(1), Some(10)).foldMap(Alt.monoid<ForOption, Int>(Option.alternative()), ::Alt).getAlt
 * //sampleEnd
 * ```
 */
@higherkind
data class Alt<F, A>(val getAlt: Kind<F, A>): AltOf<F, A> {
  companion object
}
