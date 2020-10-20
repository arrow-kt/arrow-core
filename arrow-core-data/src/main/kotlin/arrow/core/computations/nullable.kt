package arrow.core.computations

import arrow.continuations.generic.DelimContScope
import arrow.core.computations.suspended.BindSyntax
import arrow.core.computations.suspended.NullableBindSyntax

@Suppress("ClassName")
object nullable {
  operator fun <A> invoke(func: suspend BindSyntax.() -> A?): A? =
    DelimContScope.reset { NullableBindSyntax(this).func() }
}
