package arrow.core.computations

import arrow.continuations.Effect
import arrow.core.Either
import arrow.core.right
import kotlin.coroutines.RestrictsSuspension

fun interface EitherEffect<E, A> : Effect<Either<E, A>> {
  suspend operator fun <B> Either<E, B>.invoke(): B =
    when (this) {
      is Either.Right -> b
      is Either.Left -> control().shift(this@invoke)
    }
}

@RestrictsSuspension
fun interface RestrictedEitherEffect<E, A>: EitherEffect<E, A>

@Suppress("ClassName")
object either {
  inline fun <E, A> eager(crossinline c: suspend RestrictedEitherEffect<E, *>.() -> A): Either<E, A> =
    Effect.restricted(eff = { RestrictedEitherEffect { it } }, f = c, just = { it.right() })

  suspend inline operator fun <E, A> invoke(crossinline c: suspend EitherEffect<E, *>.() -> A): Either<E, A> =
    Effect.suspended(eff = { EitherEffect { it } }, f = c, just = { it.right() })
}
