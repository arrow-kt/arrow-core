package arrow.core.computations

import arrow.Kind
import arrow.continuations.generic.DelimContScope
import arrow.core.EagerBindNew
import arrow.core.Either
import arrow.core.Either.Right
import arrow.core.EitherPartialOf
import arrow.core.ShortCircuit
import arrow.core.fix
import arrow.typeclasses.suspended.BindSyntaxNew

object eitherNew {
  fun <E, A> eager(c: suspend EagerBindNew<EitherPartialOf<E>>.() -> A): Either<E, A> =
    DelimContScope.reset {
      Right(
        c(object : EagerBindNew<EitherPartialOf<E>> {
          override suspend fun <A> Kind<EitherPartialOf<E>, A>.invoke(): A =
            fix().fold(
              { e -> throw ShortCircuit(e) },
              { shift { f -> f(it) } }
            )
        })
      )
    }

  suspend operator fun <E, A> invoke(c: suspend BindSyntaxNew<EitherPartialOf<E>>.() -> A): Either<E, A> =
    DelimContScope.reset {
      Right(
        c(object : BindSyntaxNew<EitherPartialOf<E>> {
          override suspend fun <A> Kind<EitherPartialOf<E>, A>.invoke(): A =
            fix().fold(
              { e -> throw ShortCircuit(e) },
              { shift { f -> f(it) } }
            )
        })
      )
    }
}
