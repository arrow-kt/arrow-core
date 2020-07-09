package arrow.core

import arrow.Kind
import arrow.typeclasses.suspended.BindSyntax
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn

object eitherFx {
  fun <E, A> eager(c: suspend EagerBind<EitherPartialOf<E>>.() -> A): Either<E, A> {
    val continuation: EitherContinuation<E, A> = EitherContinuation()
    return continuation.startCoroutineUninterceptedAndReturn {
      Either.Right(c())
    } as Either<E, A>
  }

  suspend operator fun <E, A> invoke(c: suspend BindSyntax<EitherPartialOf<E>>.() -> A): Either<E, A> =
    suspendCoroutineUninterceptedOrReturn { cont ->
      val continuation = EitherSContinuation(cont as Continuation<EitherOf<E, A>>)
      continuation.startCoroutineUninterceptedOrReturn {
        Either.Right(c())
      }
    }

  internal class EitherSContinuation<E, A>(
    parent: Continuation<EitherOf<E, A>>
  ) : SuspendMonadContinuation<EitherPartialOf<E>, A>(parent) {
    override fun ShortCircuit.recover(): Kind<EitherPartialOf<E>, A> =
      Either.Left(value as E)

    override suspend fun <A> Kind<EitherPartialOf<E>, A>.bind(): A =
      fix().fold({ e -> throw ShortCircuit(e) }, ::identity)
  }

  internal class EitherContinuation<E, A> : MonadContinuation<EitherPartialOf<E>, A>() {
    override fun ShortCircuit.recover(): Kind<EitherPartialOf<E>, A> =
      Either.Left(value as E)

    override suspend fun <A> Kind<EitherPartialOf<E>, A>.bind(): A =
      fix().fold({ e -> throw ShortCircuit(e) }, ::identity)
  }
}
