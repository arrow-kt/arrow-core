package arrow.core.computations.suspended

import arrow.continuations.generic.DelimitedScope

class NullableBindSyntax<R>(
  scope: DelimitedScope<R?>
) : BindSyntax, DelimitedScope<R?> by scope {
  override suspend fun <A> A?.invoke(): A =
    this ?: shift { null }
}
