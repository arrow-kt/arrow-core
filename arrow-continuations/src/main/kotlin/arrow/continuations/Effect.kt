package arrow.continuations

import arrow.continuations.generic.DelimitedScope

/**
 * A generic effect handler with access to the
 * [DelimitedScope] of [F]
 */
fun interface Effect<F> {
  fun control(): DelimitedScope<F>
}
