package arrow.core.computations

import arrow.Kind
import arrow.continuations.generic.DelimContScope
import arrow.core.EagerInvoke
import arrow.core.Either
import arrow.core.Either.*
import arrow.core.EitherPartialOf
import arrow.core.fix
import arrow.typeclasses.suspended.Invoke

object eitherNew {
  fun <E, A> eager(c: suspend EagerInvoke<EitherPartialOf<E>>.() -> A): Either<E, A> =
    DelimContScope.reset {
      Right(
        c(object : EagerInvoke<EitherPartialOf<E>> {
          override suspend fun <A> Kind<EitherPartialOf<E>, A>.invoke(): A =
            when (val v = fix()) {
              is Right -> v.b
              is Left -> shift { v }
            }
        })
      )
    }

  suspend operator fun <E, A> invoke(c: suspend Invoke<EitherPartialOf<E>>.() -> A): Either<E, A> =
    DelimContScope.reset {
      Right(
        c(object : Invoke<EitherPartialOf<E>> {
          override suspend fun <A> Kind<EitherPartialOf<E>, A>.invoke(): A =
            when (val v = fix()) {
              is Right -> v.b
              is Left -> shift { v }
            }
        })
      )
    }
}
