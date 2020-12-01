package arrow.continuations

import arrow.continuations.generic.DelimitedScope

fun interface Effect<F> {
  fun control(): DelimitedScope<F>
}
