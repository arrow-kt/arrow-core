package arrow.continuations.effectStack

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.loop
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.startCoroutineUninterceptedOrReturn
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

interface DelimitedCont<A, B> {
  suspend operator fun invoke(a: A): B
}

interface Delimited<A> {
  suspend fun <B> shift(func: suspend (DelimitedCont<B, A>) -> A): B
  suspend fun <B> reset(f: suspend Delimited<B>.() -> B): B
}

suspend fun <A> reset(f: suspend Delimited<A>.() -> A): A =
  DelimitedScope("Prompt", f).run()

/**
 * Idea we have two paths:
 * One path is the normal coroutine. It fills an effect stack everytime its continuation is resumed with a value.
 * Then if a continuation is run more than once we restart the entire computation [f] and use the effect stack for as long as possible
 * When the effect stack runs out of values we resume normal coroutine behaviour.
 *
 * This can be used to implement nondeterminism together with any other effect and so long as the "pure" code in a function
 *  is fast this won't be a problem, but if it isn't this will result in terrible performance (but only if multishot is actually used)
 */
open class DelimitedScope<A>(val dbgLabel: String, val f: suspend Delimited<A>.() -> A) : Delimited<A> {

  private val ret = atomic<A?>(null)
  // TODO More descriptive name
  private val currShiftFn = atomic<(suspend () -> A)?>(null)
  // TODO more efficient data structures. O(1) append + O(1) pop would be best
  internal open val stack: MutableList<Any?> = mutableListOf()
  // TODO for this we could use a datastructure that can O(1) append and has O(1) popLast()
  private val cbs = mutableListOf<Continuation<A>>()

  override suspend fun <B> shift(func: suspend (DelimitedCont<B, A>) -> A): B {
    // suspend f since we first need a result from DelimitedCont.invoke
    return suspendCoroutine { k ->
      // println("Suspending for shift: $label")
      // println("Stack: $stack")
      // create a continuation which supports invoking either the suspended f or restarting it with a sliced stack
      val o = object : DelimitedCont<B, A> {
        // The "live" continuation for f which is currently suspended. Can only be called once
        val liveContinuation = atomic<Continuation<B>?>(k)
        // TODO better datastructure
        // A snapshot of f's effect-stack up to this shift's function invocation
        val snapshot = stack.toList()
        override suspend fun invoke(a: B): A {
          // println("Invoke cont with state is null: ${state.value == null} && arg $a")
          val cont = liveContinuation.getAndSet(null)
          // Re-execute f, but in a new scope which contains the stack slice + a and will use that to fill in the first
          //  calls to shift
          return if (cont == null) startMultiShot(snapshot + a)
          // we have a "live" continuation to resume to so we suspend the shift block and do exactly that
          else suspendCoroutineUninterceptedOrReturn {
            // a is the result of an effect, push it onto the stack. Note this refers to the outer stack, not
            //  the slice captured here, which is now immutable
            stack.add(a)
            // invoke needs to return A at some point so we need to append the Continuation so that it will be called when this
            //  scope's run method is done
            cbs.add(it)
            // resume f with value a
            cont.resume(a)
            COROUTINE_SUSPENDED
          }
        }
      }
      // the shift function is the next fn to execute
      currShiftFn.value = { func(o) }
    }
  }

  suspend fun startMultiShot(stack: List<Any?>): A = MultiShotDelimScope(stack, f).run()

  override suspend fun <B> reset(f: suspend Delimited<B>.() -> B): B =
    DelimitedScope("inner", f).let { scope ->
      scope::run.startCoroutineUninterceptedOrReturn(Continuation(EmptyCoroutineContext) {
        TODO("Is this ever resumed?")
      }).let fst@{
        if (it == COROUTINE_SUSPENDED) {
          /**
           * Simply suspend again. This is only ever called if we suspend to the parent scope and if we actually call
           *  the continuation it'll lead to an infinite loop anyway. Why? Let's have a look at this example:
           * prompt<Int> fst@{
           *   val a: Int = control { it(5) + it(3) }
           *   a + prompt<Int> snd@{
           *     val i = this@fst.control { it(2) }
           *     i + 1
           *   }
           * }
           * This will first execute `control { it(5) }` which then runs the inner prompt. The inner prompt yields back to
           *  the outer prompt because to return to i it needs the result of the outer prompt. The only sensible way of getting
           *  such a result is to rerun it with it's previous stack. However this means the state upon reaching
           *  the inner prompt again is deterministic and always the same, which is why it'll loop.
           *
           * TODO: Is this actually true? We can consider this@fst.control to capture up to the next control/reset. This means
           *  it could indeed restart the outer continuation with a stack where the top element has been replaced by whatever we invoked with
           *  If there is nothing on the stack or the topmost item is (reference?-)equal to our a we will infinite loop and we
           *  should just crash here to not leave the user wondering wtf is happening. It might also be that a user does side-effects
           *  outside of control which we cannot capture and thus it produces a dirty rerun which might not loop. Idk if that should
           *  be considered valid behaviour
           */
          suspendCoroutine {}
        }
        else it as B
      }
    }

  private fun getValue(): A? =
    // println("Running suspended $label")
    ret.loop {
      // shift function called f's continuation which now finished
      if (it != null) return@getValue it
      // we are not done yet
      else {
        val res = currShiftFn.getAndSet(null)
        if (res != null)
          res.startCoroutineUninterceptedOrReturn(Continuation(EmptyCoroutineContext) { res ->
            // println("Resumption with ${(res.getOrThrow() as Sequence<Any?>).toList()}")
            // a shift block finished processing. This is now our intermediate return value
            ret.value = res.getOrThrow()
          }).let {
            // the shift function did not call its continuation which means we short-circuit
            if (it != COROUTINE_SUSPENDED) ret.value = it as A
            // if we did suspend we have either hit a shift function from the parent scope or another shift function
            //  in both cases we just loop
          }
        // short since we run out of shift functions to call
        else return@getValue null
      }
    }

  open suspend fun run(): A {
    // println("Running $dbgLabel")
    f.startCoroutineUninterceptedOrReturn(this, Continuation(EmptyCoroutineContext) {
      // println("Put value ${(it.getOrThrow() as Sequence<Any?>).toList()}")
      // f finished after being resumed. Save the value to resume the shift blocks later
      ret.value = it.getOrThrow()
    }).let { res ->
      if (res == COROUTINE_SUSPENDED) {
        // if it is null we are done calling through our shift fns and we need a value from a parent scope now
        //  this will block indefinitely if there is no parent scope, but a program like that should not typecheck
        //  at least not when using shift
        getValue() ?: return@run suspendCoroutine {}
      } // we finished without ever suspending. This means there is no shift block and we can short circuit run
      else return@run res as A
    }

    // 1..n shift blocks were called and now need to be resumed with the result. This will sort of bubble up because each
    //  resumed shift block can alter the returned value.
    cbs.asReversed().forEach { it.resume(ret.value!!) }
    // return the final value after all shift blocks finished processing the result
    return ret.value!!
  }
}

class MultiShotDelimScope<A>(
  localStack: List<Any?>,
  f: suspend Delimited<A>.() -> A
) : DelimitedScope<A>("Multishot", f) {
  private var depth = 0
  override val stack: MutableList<Any?> = localStack.toMutableList()
  override suspend fun <B> shift(func: suspend (DelimitedCont<B, A>) -> A): B =
    if (stack.size > depth) stack[depth++] as B
    else {
      // println("EmptyStack")
      depth++
      super.shift(func)
    }
}
