package arrow.core.computations

import arrow.Kind
import arrow.continuations.generic.DelimContScope
import arrow.core.EagerInvoke
import arrow.core.Eval
import arrow.core.ForEval
import arrow.core.fix
import arrow.typeclasses.suspended.Invoke

object evalContinuation {

  fun <A> eager(c: suspend EagerInvoke<ForEval>.() -> A): Eval<A> =
    DelimContScope.reset {
      Eval.just(
        c(object : EagerInvoke<ForEval> {
          override suspend fun <A> Kind<ForEval, A>.invoke(): A =
            fix().value()
        })
      )
    }

  suspend operator fun <A> invoke(c: suspend Invoke<ForEval>.() -> A): Eval<A> =
    DelimContScope.reset {
      Eval.just(
        c(object : Invoke<ForEval> {
          override suspend fun <A> Kind<ForEval, A>.invoke(): A =
            fix().value()
        })
      )
    }
}
