package arrow.core.computations

import arrow.continuations.generic.DelimContScope
import arrow.continuations.generic.DelimitedScope
import arrow.core.computations.suspended.BindSyntax
import arrow.core.computations.suspended.EagerBindSyntax

@Suppress("ClassName")
object nullable {
  fun <A> eager(func: suspend EagerBindSyntax.() -> A?): A? = runRestrictedSuspension {
    DelimContScope.reset { func(NullableEagerBindSyntax(this)) }
  }

  suspend operator fun <A> invoke(func: suspend BindSyntax.() -> A?): A? =
    DelimContScope.reset { func(NullableBindSyntax(this)) }

  private class NullableEagerBindSyntax<R>(
    scope: DelimitedScope<R?>
  ) : EagerBindSyntax, DelimitedScope<R?> by scope {
    override suspend fun <A> A?.invoke(): A =
      this ?: shift { null }
  }

  private class NullableBindSyntax<R>(
    scope: DelimitedScope<R?>
  ) : BindSyntax, DelimitedScope<R?> by scope {
    override suspend fun <A> A?.invoke(): A =
      this ?: shift { null }
  }
}
