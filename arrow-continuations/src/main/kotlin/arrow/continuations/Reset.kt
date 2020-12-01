package arrow.continuations

import arrow.continuations.generic.ShortCircuit
import arrow.continuations.generic.ControlThrowable
import arrow.continuations.generic.DelimContScope
import arrow.continuations.generic.DelimitedScope
import arrow.continuations.generic.SuspendMonadContinuation
import arrow.continuations.generic.SuspendingComputation
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn

object Reset {
  /**
   * Allows for building suspending single-shot computation blocks.
   * For short-circuiting, or shifting, a [ShortCircuit] [ControlThrowable] is used.
   * This ensures that any concurrent nested scopes are correctly closed.
   *
   * The usage of `try { ... } catch(e: Throwable) { ... }` will catch the [ShortCircuit] error,
   * and will lead to recover of short-circuiting.
   * You should always prefer to catch the most specific exception class, or
   * use `Either.catch`, `Validated.catch` etc or `e.nonFatalOrThrow()`
   * to ensure you're not catching `ShortCircuit`.
   */
  suspend fun <A> single(block: suspend SuspendingComputation<A>.() -> A): A =
    suspendCoroutineUninterceptedOrReturn { cont ->
      SuspendMonadContinuation(cont, block)
        .startCoroutineUninterceptedOrReturn()
    }

  /**
   * Allows for building eager single-shot computation blocks.
   * For short-circuiting, or shifting, `@RestrictSuspension` state machine is used.
   * This doesn't allow nesting of computation blocks, or foreign suspension.
   */
  // TODO This should be @RestrictSuspension but that breaks because a superclass is not considered to be correct scope
  fun <A> eager(block: suspend DelimitedScope<A>.() -> A): A =
    DelimContScope(block).invoke()
}
