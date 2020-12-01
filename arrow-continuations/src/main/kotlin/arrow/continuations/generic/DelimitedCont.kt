package arrow.continuations.generic

import kotlin.coroutines.RestrictsSuspension

/**
 * Base interface for a continuation
 */
interface DelimitedContinuation<A, R> {
  suspend operator fun invoke(a: A): R
}

/**
 * Base interface for our scope.
 */
interface DelimitedScope<R> {

  /**
   * Exit the [DelimitedScope] with [R]
   */
  suspend fun shift(r: R): Nothing
}


interface RestrictedScope<R> : DelimitedScope<R> {
  /**
   * Capture the continuation and pass it to [f].
   */
  suspend fun <A> shift(f: suspend DelimitedScope<R>.(DelimitedContinuation<A, R>) -> R): A

  override suspend fun shift(r: R): Nothing = shift { r }

}

interface SuspendedScope<R> : DelimitedScope<R>

