package arrow.typeclasses

import arrow.core.EQ
import arrow.core.GT
import arrow.core.LT
import arrow.core.Ordering
import arrow.core.Tuple2

/**
 * The [Order] type class is used to define a total ordering on some type [F] and is defined by being able to fully determine order between two instances.
 *
 * [Order] is a subtype of [Eq] and defines [eqv] in terms of [compare]
 *
 * @see [Eq]
 * @see <a href="http://arrow-kt.io/docs/arrow/typeclasses/order/">Order documentation</a>
 */
interface Order<F> : Eq<F> {

  /**
   * Compare [this@compare] with [b].
   *
   * @receiver object to compare with [b]
   * @param b object to compare with [this@compare]
   * @returns [Ordering] sum type which defines the ordering
   */
  fun F.compare(b: F): Ordering

  /** Kotlin operator overload */
  operator fun F.compareTo(b: F): Int = compare(b).toInt()

  /** @see [Eq.eqv] */
  override fun F.eqv(b: F): Boolean = this.compare(b) == EQ

  /**
   * Check if [this@lt] is `lower than` [b]
   *
   * @receiver object to compare with [b]
   * @param b object to compare with [this@lt]
   * @returns true if [this@lt] is `lower than` [b] and false otherwise
   */
  fun F.lt(b: F): Boolean = compare(b) == LT

  /**
   * Check if [this@lte] is `lower than or equal to` [b]
   *
   * @receiver object to compare with [b]
   * @param b object to compare with [this@lte]
   * @returns true if [this@lte] is `lower than or equal to` [b] and false otherwise
   */
  fun F.lte(b: F): Boolean = compare(b) != GT

  /**
   * Check if [this@gt] is `greater than` [b]
   *
   * @receiver object to compare with [b]
   * @param b object to compare with [this@gt]
   * @returns true if [this@gt] is `greater than` [b] and false otherwise
   */
  fun F.gt(b: F): Boolean = compare(b) == GT

  /**
   * Check if [this@gte] is `greater than or equal to` [b]
   *
   * @receiver object to compare with [b]
   * @param b object to compare with [this@gte]
   * @returns true if [this@gte] is `greater than or equal to` [b] and false otherwise
   */
  fun F.gte(b: F): Boolean = compare(b) != LT

  /**
   * Determines the maximum of [this@max] and [b] in terms of order.
   *
   * @receiver object to compare with [b]
   * @param b object to compare with [this@max]
   * @returns the maximum [this@max] if it is greater than [b] or [b] otherwise
   */
  fun F.max(b: F): F = if (gt(b)) this else b

  /**
   * Determines the minimum of [this@min] and [b] in terms of order.
   *
   * @receiver object to compare with [b]
   * @param b object to compare with [this@min]
   * @returns the minimum [this@min] if it is less than [b] or [b] otherwise
   */
  fun F.min(b: F): F = if (lt(b)) this else b

  /**
   * Sorts [this@sort] and [b] in terms of order.
   *
   * @receiver object to compare with [b]
   * @param b object to compare with [this@sort]
   * @returns a sorted [Tuple2] of [this@sort] and [b].
   */
  fun F.sort(b: F): Tuple2<F, F> = if (gte(b)) Tuple2(this, b) else Tuple2(b, this)

  companion object {

    /**
     * Construct an [Order] from a function `(F, F) -> Ordering`.
     *
     * @param compare a function that defines the order for 2 objects of type [F].
     * @returns an [Order] instance that is defined by the [compare] function.
     */
    inline operator fun <F> invoke(crossinline compare: (F, F) -> Ordering): Order<F> = object : Order<F> {
      override fun F.compare(b: F): Ordering = compare(this, b)
    }

    /**
     * Construct an [Order] that defines all instances as equal for type [F].
     *
     * @returns an [Order] instance wherefore all instances of type [F] are equal.
     */
    fun <F> allEqual(): Order<F> = object : Order<F> {
      override fun F.compare(b: F): Ordering = EQ
    }
  }
}
