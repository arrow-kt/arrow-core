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
  suspend fun <A> shift(f: suspend DelimitedScope<R>.(DelimitedContinuation<A, R>) -> R): A
  // shiftCPS passes the arguments with which the continuation is invoked to the supplied continuation/function c.
  // This means it is trivially multishot because c has the stack in its closure. To enforce that this is the last
  // statement of a reset block we return Nothing here.
  suspend fun <A> shiftCPS(f: suspend (DelimitedContinuation<A, R>) -> R, c: suspend DelimitedScope<R>.(A) -> R): Nothing
  suspend fun <A> reset(f: suspend DelimitedScope<A>.() -> A): A
}

interface RunnableDelimitedScope<R> : DelimitedScope<R> {
  operator fun invoke(): R
}

/**
 * Problems of nested reset:
 *  Scopes cannot be functor, we can not change the return type of a scope through mapping or anything else other than isomorphisms.
 *  This means the inner scope needs its own scope and thus its own runloop.
 *   A few interesting scenarios come up now:
 *   - The function in the inner scope may refer to *any* outer scope and may also call suspend functions on the outer scope
 *    This means the inner scopes runloop may stop with no further work before finishing because the work has been queued
 *     on a parent. There may be 1-n parents and do not know which parent got the work, which leads to n checks if this happens.
 *    When a parent has no further work and is not done it also may have done work for a child.
 *    This means it has to check it's 0..n childs to see which one needs to be resumed.
 *    This means every time we invoke an effect on an outer scope we need to check our 1..n parents for work and if they don't
 *     have work we need to check our 0..m child scopes if we have completed their work.
 *    One rather extreme measure that I haven't tried is collapsing all scopes to one runloop. This could be possible
 *     because the part that the runloop executes is a function to a local result.
 *     The completion still has to be handled by each scope though.
 *    This is the doable but annoying part of the problem btw.
 *  - Another fun bit on the runloop is that within shift we can reference parent scopes as well. Thus when suspending to a parent
 *    we need to somehow tell that scope to call us back before the lowermost child
 *  - This brings us to problem number 2: Multishot
 *    Normally when we run multishot we mark effect stack offsets when shift is called and then we can slice the stack from until offset
 *     when invoke for the continuation is called the second time.
 *    Because multishot may now rerun inner resets we now have the following changes:
 *     - if we call shift in our inner scope we have to store both the global offset on the stack as our current offset and
 *       the local offset from the start of our scope.
 *     - When we rerun we have to notify our parents of this as well so that they can depending on the multishot's current offset
 *       also access the stack rather than running the shift function.
 *     - When we rerun an inner scope from some point and it later calls a parent scope which is outside of our offset we can't use
 *       the stack. This now means that we have a local stack slice that is different from the global stack, we execute the
 *       outer level shift and on invoke put the arguments on the stack. This makes multishot from this point on hard:
 *       -
 *
 */
