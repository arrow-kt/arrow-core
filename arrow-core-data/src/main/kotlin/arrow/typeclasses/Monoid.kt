package arrow.typeclasses

class ForMonoid private constructor() { companion object }
typealias MonoidOf<A> = arrow.Kind<ForMonoid, A>
fun <A> MonoidOf<A>.fix(): Monoid<A> = this as Monoid<A>

/**
 * ank_macro_hierarchy(arrow.typeclasses.Monoid)
 */
interface Monoid<A> : Semigroup<A>, MonoidOf<A> {
  /**
   * A zero value for this A
   */
  fun empty(): A

  /**
   * Combine an [Collection] of [A] values.
   */
  fun Collection<A>.combineAll(): A =
    if (isEmpty()) empty() else reduce { a, b -> a.combine(b) }

  /**
   * Combine an array of [A] values.
   */
  fun combineAll(elems: List<A>): A = elems.combineAll()

  /**
   * Combine the given monoid [m], [k] times with itself (i.e. m^k).
   * This uses a fast implementation (Exponentiation by Squaring) that calls [combine] `log(k)` times rather than `k` times.
   */
  fun combineTimes(m: A, k: Long): A {
    if (k <= 0) {
      return empty()
    }
    if (k == 1L) {
      return m
    }
    var y = empty()
    var x  = m
    var n = k
    while (n > 1) {
      if (n % 2L == 1L) {
        // n is odd
        y = y.combine(x)
        x = x.combine(x)
        n -= 1
      } else {
        // n is even
        x = x.combine(x)
      }
      n /= 2
    }
    return x.combine(y)
  }

  companion object
}
