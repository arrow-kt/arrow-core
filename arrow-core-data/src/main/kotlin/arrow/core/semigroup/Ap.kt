package arrow.core.semigroup

import arrow.Kind
import arrow.higherkind

/**
 * Lift's a monoid into the applicative context F
 *
 * ```kotlin:ank
 * import arrow.core.Option
 * import arrow.core.Some
 * import arrow.core.extensions.monoid
 * import arrow.core.extensions.option.applicative.applicative
 * import arrow.core.extensions.semigroup.ap.monoid.monoid
 * import arrow.core.semigroup.Ap
 *
 * //sampleStart
 * Ap.monoid(Option.applicative(), String.monoid()).run { Ap(Some("Hello")) + Ap(Some(" World")) }.getAp
 * //sampleEnd
 * ```
 * ```kotlin:ank
 * import arrow.core.none
 * Ap.monoid(Option.applicative(), String.monoid()).run { Ap(Some("Hello")) + Ap(none<String>()) }.getAp
 * ```
 * ```kotlin:ank
 * import arrow.core.extensions.list.foldable.foldMap
 *
 * //sampleStart
 * listOf(Some("Hello "), Some(" there"), Some("!")).foldMap(Ap.monoid(Option.applicative(), String.monoid())) { Ap(it) }.getAp
 * //sampleEnd
 * ```
 * ```kotlin:ank
 * listOf(none<String>(), Some("Text"), Some("!")).foldMap(Ap.monoid(Option.applicative(), String.monoid())) { Ap(it) }.getAp
 * ```
 */
@higherkind
data class Ap<F, A>(val getAp: Kind<F, A>) : ApOf<F, A> {
  companion object
}
