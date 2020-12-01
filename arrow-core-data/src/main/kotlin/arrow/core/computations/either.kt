package arrow.core.computations

import arrow.continuations.Effect
import arrow.continuations.Reset
import arrow.core.Either
import arrow.core.Right

fun interface EitherEffect<E, A> : Effect<Either<E, A>> {
  suspend operator fun <B> Either<E, B>.invoke(): B =
    when (this) {
      is Either.Right -> b
      is Either.Left -> control().shift(this@invoke)
    }
}

object either {
  inline fun <E, A> eager(crossinline c: suspend EitherEffect<E, *>.() -> A): Either<E, A> =
    Reset.restricted { Right(c(EitherEffect { this })) }

  suspend inline operator fun <E, A> invoke(crossinline c: suspend EitherEffect<E, *>.() -> A): Either<E, A> =
    Reset.suspended { Right(c(EitherEffect { this })) }
}
