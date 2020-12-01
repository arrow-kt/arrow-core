package arrow.core.computations

import arrow.Kind
import arrow.continuations.Reset
import arrow.core.EagerBind
import arrow.core.Valid
import arrow.core.Validated
import arrow.core.ValidatedPartialOf
import arrow.core.fix
import arrow.typeclasses.suspended.BindSyntax

object validated {

  fun <E, A> eager(c: suspend EagerBind<ValidatedPartialOf<E>>.() -> A): Validated<E, A> =
    Reset.eager {
      Valid(
        c(object : EagerBind<ValidatedPartialOf<E>> {
          override suspend fun <A> Kind<ValidatedPartialOf<E>, A>.invoke(): A =
            when (val v = fix()) {
              is Validated.Valid -> v.a
              is Validated.Invalid -> shift(v)
            }
        })
      )
    }

  suspend operator fun <E, A> invoke(c: suspend BindSyntax<ValidatedPartialOf<E>>.() -> A): Validated<E, A> =
    Reset.single {
      Valid(
        c(object : BindSyntax<ValidatedPartialOf<E>> {
          override suspend fun <A> Kind<ValidatedPartialOf<E>, A>.invoke(): A =
            when (val v = fix()) {
              is Validated.Valid -> v.a
              is Validated.Invalid -> shift(v)
            }
        })
      )
    }
}
