package arrow.continuations

import arrow.continuations.generic.DelimitedScope

fun interface Effect<F> : DelimitedScope<F> {
  fun control(): DelimitedScope<F>
  override suspend fun shift(r: F): Nothing =
    control().shift(r)
}
