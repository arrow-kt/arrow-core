package generic

import arrow.continuations.Effect
import arrow.continuations.Reset

fun interface MaybeEffect<A> : Effect<Maybe<A>> {
  suspend operator fun <B> Maybe<B>.invoke(): B =
    when (this) {
      is Just -> a
      is None -> control().shift(None)
    }
}
