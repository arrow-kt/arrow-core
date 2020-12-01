package arrow.continuations

import arrow.continuations.generic.DelimContScope
import arrow.continuations.generic.DelimitedScope
import arrow.continuations.generic.SuspendMonadContinuation
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn

object Reset {
  /**
   * Implements delimited continuations with no multi-shot support.
   *
   * For a version that simulates multi-shot (albeit with drawbacks) see [multi].
   * For a version that allows nesting [reset] and calling parent scopes inside inner scopes see [nested].
   */
  suspend fun <A> single(block: suspend DelimitedScope<A>.() -> A): A =
    suspendCoroutineUninterceptedOrReturn { cont ->
      SuspendMonadContinuation(cont, block).invoke()
    }

}
