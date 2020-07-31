package arrow.typeclasses

import arrow.core.Option

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

  fun A.maybeCombine(b: A?): A = Option.fromNullable(b).fold({ this }, { combine(it) })
}

class BiSemigroup<L, R>(private val SGL: Semigroup<L>, private val SGR: Semigroup<R>) {

  @JvmName("plusLeft")
  operator fun L.plus(other: L): L = SGL.run { this@plus + other }

  @JvmName("plusRight")
  operator fun R.plus(other: R): R = SGR.run { this@plus + other }

  @JvmName("maybePlusLeft")
  operator fun L?.plus(other: L?): L? = SGL.run { this@plus?.maybeCombine(other) }

  @JvmName("maybePlusRight")
  operator fun R?.plus(other: R?): R? = SGR.run { this@plus?.maybeCombine(other) }

}

operator fun <L, R> Semigroup<L>.plus(other: Semigroup<R>) =
  BiSemigroup(this, other)
