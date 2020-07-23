package arrow.core.semigroup

/**
 * The monoid of endomorphisms under composition.
 */
data class Endo<A>(val appEndo: (A) -> A) {
  companion object
}
