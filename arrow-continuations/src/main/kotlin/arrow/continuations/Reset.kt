package arrow.continuations

import arrow.continuations.generic.DelimContScope
import arrow.continuations.generic.DelimitedScope
import arrow.continuations.generic.MultiShotDelimContScope
import arrow.continuations.generic.NestedDelimContScope

object Reset {
  /**
   * Implements delimited continuations with no multi-shot support.
   *
   * For a version that simulates multi-shot (albeit with drawbacks) see [multi].
   * For a version that allows nesting [reset] and calling parent scopes inside inner scopes see [nested].
   */
  suspend fun <A> single(block: suspend DelimitedScope<A>.() -> A): A =
    DelimContScope.reset(block)

  /**
   * (Simulated) Multi-shot capable delimited control scope
   * [block] will rerun completely and only the results of shift are cached.
   * All side-effects outside of shift will rerun on each shift callback invocation
   */
  suspend fun <A> multi(block: suspend DelimitedScope<A>.() -> A): A =
    MultiShotDelimContScope.reset(block)

  /**
   * Delimited control version which allows `f@reset { ... g@reset { f.shift { ... } } }` to function correctly.
   * [single] and [multi] fail when calling shift on the parent scope inside an inner reset.
   */
  suspend fun <A> nested(block: suspend DelimitedScope<A>.() -> A): A =
    NestedDelimContScope.reset(block)
}





