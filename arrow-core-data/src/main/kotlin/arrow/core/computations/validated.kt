package arrow.core.computations

import arrow.continuations.Effect
import arrow.core.Validated
import arrow.core.valid
import kotlin.coroutines.RestrictsSuspension

fun interface ValidatedEffect<E, A> : Effect<Validated<E, A>> {
  suspend operator fun <B> Validated<E, B>.invoke(): B =
    when (this) {
      is Validated.Valid -> a
      is Validated.Invalid -> control().shift(this@invoke)
    }
}

@RestrictsSuspension
fun interface RestrictedValidatedEffect<E, A> : ValidatedEffect<E, A>

@Suppress("ClassName")
@Deprecated("The `either` computation block supports validated with the right short-circuiting semantics",
ReplaceWith("either", "arrow.core.computations.either"))
object validated {
  inline fun <E, A> eager(crossinline c: suspend RestrictedValidatedEffect<E, *>.() -> A): Validated<E, A> =
    Effect.restricted(eff = { RestrictedValidatedEffect { it } }, f = c, just = { it.valid() })

  suspend inline operator fun <E, A> invoke(crossinline c: suspend ValidatedEffect<E, *>.() -> A): Validated<E, A> =
    Effect.suspended(eff = { ValidatedEffect { it } }, f = c, just = { it.valid() })
}
