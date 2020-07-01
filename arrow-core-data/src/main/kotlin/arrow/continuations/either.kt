package arrow.continuations

import arrow.core.Either
import arrow.core.ShortCircuit
import arrow.core.identity
import arrow.core.left
import arrow.core.right
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.experimental.ExperimentalTypeInference

class EitherBuilder<E>(parent: Continuation<*>) : Cont.Strict<Either<E, *>, Any?>(parent) {
  operator fun <A> Either<*, A>.invoke(): A =
    fold({ e -> throw ShortCircuit(e) }, ::identity)

  override suspend fun <A> A.just(): Either<E, A> =
    right()

  override fun ShortCircuit.recover(): Either<E, Nothing> =
    resolve<E>().left()
}

@UseExperimental(ExperimentalTypeInference::class)
@BuilderInference
suspend fun <E, A> either(@BuilderInference f: suspend EitherBuilder<E>.() -> A): Either<E, A> =
  suspendCoroutineUninterceptedOrReturn {
    EitherBuilder<E>(it).strict(f)
  }
