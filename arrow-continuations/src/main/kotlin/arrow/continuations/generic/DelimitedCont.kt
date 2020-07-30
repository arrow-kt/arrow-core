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
  suspend fun <A> shift(f: suspend (DelimitedContinuation<A, R>) -> R): A
}

interface RunnableDelimitedScope<R> : DelimitedScope<R> {
  operator fun invoke(): R
}
