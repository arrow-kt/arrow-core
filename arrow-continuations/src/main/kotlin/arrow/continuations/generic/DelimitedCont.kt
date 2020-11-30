package arrow.continuations.generic

/**
 * Base interface for a continuation
 */
interface DelimitedContinuation<A, R> {
  suspend operator fun invoke(a: A): R
}

/**
 * Base interface for our scope.
 */
// TODO This should be @RestrictSuspension but that breaks because a superclass is not considered to be correct scope
// @RestrictsSuspension
interface DelimitedScope<R> {
  /**
   * Capture the continuation and pass it to [f].
   */
  suspend fun <A> shift(f: suspend DelimitedScope<R>.(DelimitedContinuation<A, R>) -> R): A

  /**
   * [ControlThrowable] based shifting
   */
  suspend fun <A> shift(a: R): A

}
