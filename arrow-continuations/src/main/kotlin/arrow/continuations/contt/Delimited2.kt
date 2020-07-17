package arrow.continuations.conts

import java.util.*
import kotlin.coroutines.Continuation

typealias Prompt<A> = DelimitedScope<A>

typealias CPS<A, R> = suspend DelimitedScope<A>.() -> R

interface Cont<A, B> {
  fun resume(value: A): B
}


// multiprompt delimited continuations in terms of the current API
// this implementation has Felleisen classification -F+
object ContinuationState {

  private var result: Any? = null
  private var arg: Any? = null
  private var body: CPS<*, *>? = null

  fun <A> reset(prompt: Prompt<A>, f: suspend () -> A): A {
    val k = DelimitedScopeImpl<A>(prompt) {
      val a: Any? = f()
      result = a
      a as A
    }
    return runCont(k)
  }

  suspend fun <A, R> shift(prompt: Prompt<R>, body: CPS<A, R>): A {
    this.body = body as CPS<*, *>
    prompt.yield()
    //Continuation.yield(prompt)
    return arg as A
  }

  private fun <A> runCont(k: DelimitedContinuation<*, *>): A {
    k.run()

    val frames: Stack<DelimitedContinuation<*, *>> = Stack()

    while (!k.isDone()) {

      // IDEA:
      //   1) Push a separate (one-time) prompt on `shift`.
      //   2) On resume, capture the continuation on a heap allocated
      //      stack of `Continuation`s
      //   3) Trampoline those continuations at the position of the original
      //      `reset`.
      //
      // This only works since continuations are one-shot. Otherwise the
      // captured frames would contain references to the continuation and
      // would be evaluated out of scope.
      val bodyPrompt = object : DelimitedScopeImpl<Any?>(k as Continuation<Any?>, {

        /**
        final var bodyPrompt = new ContinuationScope() {};
        final var bodyCont = new Continuation(bodyPrompt, () -> {
          result = ((CPS<?, A>) body).apply(value -> {
            // yield and wait until the subcontinuation has been
            // evaluated.
            arg = value;
            // yielding here returns control to the outer continuation
            Continuation.yield(bodyPrompt);
            return (A) result;
          });
          body = null;
        });
         */
      }) {} // TODO what prompt goes here on an empty block?
      val bodyCont = DelimitedScopeImpl(bodyPrompt) {
        result = DelimitedScopeImpl(this) {
          // yield and wait until the subcontinuation has been
          // evaluated.
          arg = this@ContinuationState.body?.invoke(this)

          // yielding here returns control to the outer continuation
          //Continuation.yield(bodyPrompt)
          bodyPrompt.yield()
          result as A
        }
        result
      }
      body = null
      
      bodyCont.run() // start it

      // continuation was called within body
      if (!bodyCont.isDone()) {
        frames.push(bodyCont)
        k.run()

        // continuation was discarded or escaped the dynamic scope of
        // bodyCont.
      } else {
        break
      }
    }

    while (!frames.isEmpty()) {
      var frame = frames.pop()

      if (!frame.isDone()) {
        frame.run()
      }
    }

    return result as A
  }
}
