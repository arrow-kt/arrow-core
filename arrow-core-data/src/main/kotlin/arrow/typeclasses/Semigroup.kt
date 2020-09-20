package arrow.typeclasses

/**
 * ank_macro_hierarchy(arrow.typeclasses.Semigroup)
 */
interface Semigroup<A> {
  /**
   * Combine two [A] values.
   */
  fun A.combine(b: A): A

  operator fun A.plus(b: A): A =
    this.combine(b)

  fun A.maybeCombine(b: A?): A = b?.let { combine(it) } ?: this
}
