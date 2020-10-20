package arrow.core.computations.suspended

import arrow.continuations.generic.DelimitedScope

data class NullableBindSyntax<R>(
  private val scope: DelimitedScope<R?>,
) : BindSyntax {
  override suspend fun <A> A?.invoke(): A =
    scope.shift { cont ->
      this@invoke?.let { cont(it) }
    }
}
