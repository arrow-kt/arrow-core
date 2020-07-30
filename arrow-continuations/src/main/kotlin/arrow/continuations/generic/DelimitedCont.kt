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
  // shift and place an implicit boundary. See shiftCPS for a more accurate definition of what this means
  suspend fun <A> shift(f: suspend (DelimitedContinuation<A, R>) -> R): A
  // shiftCPS passes the arguments with which the continuation is invoked to the supplied continuation/function c.
  // This means it is trivially multishot because c has the stack in its closure. To enforce that this is the last
  // statement of a reset block we return Nothing here.
  suspend fun <A> shiftCPS(f: suspend (DelimitedContinuation<A, R>) -> R, c: suspend DelimitedScope<R>.(A) -> R): Nothing
  fun <A> reset(f: suspend DelimitedScope<A>.() -> A): A
}

interface RunnableDelimitedScope<R> : DelimitedScope<R> {
  operator fun invoke(): R
}
