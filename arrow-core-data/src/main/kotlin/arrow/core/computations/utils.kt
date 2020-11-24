package arrow.core.computations

import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.startCoroutine

internal fun <A> runRestrictedSuspension(f: suspend () -> A): A {
  var value: A? = null
  f.startCoroutine(Continuation(EmptyCoroutineContext) { res ->
    value = res.getOrThrow()
  })
  return value!!
}
