package arrow.core.computations

import arrow.continuations.Effect
import arrow.continuations.Reset
import arrow.core.Either
import arrow.core.Right

fun interface EitherEffect<E, A> : Effect<Either<E, A>> {
  suspend fun <B> Either<E, B>.invoke(): B =
    when (this) {
      is Either.Right -> b
      is Either.Left -> shift(this@invoke)
    }
}

object either {
  fun <E, A> eager(c: suspend EitherEffect<E, *>.() -> A): Either<E, A> =
    Reset.eager { Right(c(EitherEffect { this })) }

  suspend operator fun <E, A> invoke(c: suspend EitherEffect<E, *>.() -> A): Either<E, A> =
    Reset.single { Right(c(EitherEffect { this })) }
}
