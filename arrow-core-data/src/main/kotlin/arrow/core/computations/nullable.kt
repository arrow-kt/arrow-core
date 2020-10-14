package arrow.core.computations

import arrow.continuations.generic.DelimContScope
import arrow.continuations.generic.DelimitedScope

@Suppress("ClassName")
object nullable {
  operator fun <A> invoke(func: suspend BindSyntax.() -> A?): A? =
    DelimContScope.reset { NullableBindSyntax(this).func() }
}


data class NullableBindSyntax<R>(
  private val scope: DelimitedScope<R?>,
) : BindSyntax {
  override suspend fun <A> A?.bind(): A =
    scope.shift { cont ->
      this@bind?.let { cont(it) }
    }
}

interface BindSyntax {
  suspend fun <A> A?.bind(): A

  suspend operator fun <A> A?.not(): A = bind()
}

