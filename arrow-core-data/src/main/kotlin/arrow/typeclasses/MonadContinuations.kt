package arrow.typeclasses

import arrow.Kind
import arrow.continuations.generic.DelimitedScope
import arrow.typeclasses.suspended.BindSyntax
import arrow.typeclasses.suspended.Invoke
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.RestrictsSuspension
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.resume

@RestrictsSuspension
interface MonadSyntax<F> : Monad<F>, BindSyntax<F>

@RestrictsSuspension
interface MonadSyntaxNew<F> : Monad<F>, Invoke<F>

open class MonadContinuationNew<F, A>(
  M: Monad<F>,
  delimitedScope: DelimitedScope<Kind<F, A>>,
  override val context: CoroutineContext = EmptyCoroutineContext
) : Continuation<Kind<F, A>>, Monad<F> by M, Invoke<F>, MonadSyntaxNew<F>, DelimitedScope<Kind<F, A>> by delimitedScope {

  override fun resume(value: Kind<F, A>) {
    returnedMonad = value
  }

  @Suppress("UNCHECKED_CAST")
  override fun resumeWithException(exception: Throwable) {
    throw exception
  }

  protected lateinit var returnedMonad: Kind<F, A>

  open fun returnedMonad(): Kind<F, A> = returnedMonad

  // TODO: Deprecate
//  override suspend fun <B> Kind<F, B>.bind(): B =
//    suspendCoroutineUninterceptedOrReturn { c ->
//      val labelHere = c.stateStack // save the whole coroutine stack labels
//      returnedMonad = this.flatMap { x: B ->
//        c.stateStack = labelHere
//        c.resume(x)
//        returnedMonad
//      }
//      COROUTINE_SUSPENDED
//    }

  override suspend fun <A> Kind<F, A>.invoke(): A = TODO()
//    shift { cb ->
//      this@invoke.flatMap { x -> cb(x) }
//    }
}

open class MonadContinuation<F, A>(
  M: Monad<F>,
  override val context: CoroutineContext = EmptyCoroutineContext
) : Continuation<Kind<F, A>>, Monad<F> by M, BindSyntax<F>, MonadSyntax<F> {

  override fun resume(value: Kind<F, A>) {
    returnedMonad = value
  }

  @Suppress("UNCHECKED_CAST")
  override fun resumeWithException(exception: Throwable) {
    throw exception
  }

  protected lateinit var returnedMonad: Kind<F, A>

  open fun returnedMonad(): Kind<F, A> = returnedMonad

  // TODO: Deprecate
  override suspend fun <B> Kind<F, B>.bind(): B =
    suspendCoroutineUninterceptedOrReturn { c ->
      val labelHere = c.stateStack // save the whole coroutine stack labels
      returnedMonad = this.flatMap { x: B ->
        c.stateStack = labelHere
        c.resume(x)
        returnedMonad
      }
      COROUTINE_SUSPENDED
    }
}
