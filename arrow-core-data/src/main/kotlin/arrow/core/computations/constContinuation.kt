package arrow.core.computations

import arrow.Kind
import arrow.continuations.generic.DelimContScope
import arrow.core.Const
import arrow.core.ConstPartialOf
import arrow.core.EagerInvoke
import arrow.core.const
import arrow.core.fix
import arrow.typeclasses.suspended.Invoke

object constContinuation {

  fun <A, T> eager(c: suspend EagerInvoke<ConstPartialOf<A>>.() -> A): Const<A, T> =
    DelimContScope.reset {
      c(object : EagerInvoke<ConstPartialOf<A>> {
        override suspend fun <T> Kind<ConstPartialOf<A>, T>.invoke(): T =
          fix().value() as T
      }).const()
    }

  suspend operator fun <A, T> invoke(c: suspend Invoke<ConstPartialOf<A>>.() -> A): Const<A, T> =
    DelimContScope.reset {
      c(object : Invoke<ConstPartialOf<A>> {
        override suspend fun <T> Kind<ConstPartialOf<A>, T>.invoke(): T =
          fix().value() as T
      }).const()
    }
}
