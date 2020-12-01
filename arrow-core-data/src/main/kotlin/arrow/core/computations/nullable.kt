package arrow.core.computations

import arrow.continuations.Reset
import arrow.continuations.generic.DelimitedScope
import arrow.continuations.generic.SuspendingComputation
import arrow.core.computations.suspended.BindSyntax
import arrow.core.computations.suspended.EagerBindSyntax

@Suppress("ClassName")
object nullable {
  fun <A> eager(func: suspend EagerBindSyntax.() -> A?): A? =
    Reset.eager { func(NullableEagerBindSyntax(this)) }

  suspend operator fun <A> invoke(func: suspend BindSyntax.() -> A?): A? =
    Reset.single { func(NullableBindSyntax(this)) }

  private class NullableEagerBindSyntax<R>(
    scope: DelimitedScope<R?>
  ) : EagerBindSyntax, DelimitedScope<R?> by scope {
    override suspend fun <A> A?.invoke(): A =
      this ?: shift(null)
  }

  private class NullableBindSyntax<R>(
    scope: SuspendingComputation<R?>
  ) : BindSyntax, SuspendingComputation<R?> by scope {
    override suspend fun <A> A?.invoke(): A =
      this ?: shift(null)
  }
}
