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

  private val ref = atomic<A?>(null)
  private val currF = atomic<(suspend () -> A)?>(null)
  internal open val stack: MutableList<Any?> = mutableListOf()
  private val cbs = mutableListOf<Continuation<A>>()

  override suspend fun <B> shift(func: suspend (DelimitedCont<B, A>) -> A): B {
    return suspendCoroutine { k ->
      // println("Suspending for control: $label")
      // println("Stack: $stack")
      val o = object : DelimitedCont<B, A> {
        val state = atomic<Continuation<B>?>(k)
        val snapshot = stack.toList()
        override suspend fun invoke(a: B): A {
          // println("Invoke cont with state is null: ${state.value == null} && arg $a")
          val cont = state.getAndSet(null)
          // Reexecute f but this time on control we resume the continuation directly with a
          return if (cont == null) startMultiShot(snapshot + a)
          else suspendCoroutineUninterceptedOrReturn {
            // push stuff to the stack
            stack.add(a)
            cbs.add(it)
            // run cont
            cont.resume(a)
            COROUTINE_SUSPENDED
          }
        }
      }
      currF.value = { func(o) }
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
           */
          suspendCoroutine {}
        }
        else it as B
      }
    }

  private fun getValue(): A? =
    // println("Running suspended $label")
    ref.loop {
      // controls function called a continuation which now finished
      if (it != null) return@getValue it
      else {
        val res = currF.getAndSet(null)
        if (res != null)
          res.startCoroutineUninterceptedOrReturn(Continuation(EmptyCoroutineContext) { res ->
            // println("Resumption with ${(res.getOrThrow() as Sequence<Any?>).toList()}")
            ref.value = res.getOrThrow()
          }).let {
            // early return controls function did not call its continuation
            if (it != COROUTINE_SUSPENDED) ref.value = it as A
          }
        // short since we run out of conts to call
        else return@getValue null
      }
    }

  open suspend fun run(): A {
    // println("Running $dbgLabel")
    f.startCoroutineUninterceptedOrReturn(this, Continuation(EmptyCoroutineContext) {
      // println("Put value ${(it.getOrThrow() as Sequence<Any?>).toList()}")
      ref.value = it.getOrThrow()
    }).let { res ->
      if (res == COROUTINE_SUSPENDED) {
        // if it is null we are done calling through our control fns and we need a value from a parent scope now
        //  this will block indefinitely if there is no parent scope, but a program like that should not typecheck
        //  at least not when using control
        getValue() ?: return@run suspendCoroutine {}
      } else return@run res as A
    }

    // control has been called and its continuations have been invoked, resume the continuations in reverse order
    cbs.asReversed().forEach { it.resume(ref.value!!) }
    return ref.value!!
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
