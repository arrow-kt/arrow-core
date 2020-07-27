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

interface DelimitedCont<A, R> {
  suspend operator fun invoke(a: A): R
}

interface Delimited<R> {
  suspend fun <A> control(func: suspend (DelimitedCont<A, R>) -> R): A
}

fun <A> prompt(f: suspend Delimited<A>.() -> A): A = DelimitedScope("Prompt", f).run()

/**
 * Idea we have two paths:
 * One path is the normal coroutine. It fills an effect stack everytime its continuation is resumed with a value.
 * Then if a continuation is run more than once we restart the entire computation [f] and use the effect stack for as long as possible
 * When the effect stack runs out of values we resume normal coroutine behaviour.
 *
 * This can be used to implement nondeterminism together with any other effect and so long as the "pure" code in a function
 *  is fast this won't be a problem, but if it isn't this will result in terrible performance (but only if multishot is actually used)
 */
open class DelimitedScope<R>(val dbgLabel: String, val f: suspend Delimited<R>.() -> R) : Delimited<R> {

  private val ref = atomic<R?>(null)
  private val currF = atomic<(suspend () -> R)?>(null)
  internal open val stack: MutableList<Any?> = mutableListOf()
  private val cbs = mutableListOf<Continuation<R>>()

  override suspend fun <A> control(func: suspend (DelimitedCont<A, R>) -> R): A {
    return suspendCoroutine { k ->
      // println("Suspending for control: $label")
      // println("Stack: $stack")
      val o = object : DelimitedCont<A, R> {
        val state = atomic<Continuation<A>?>(k)
        val snapshot = stack.toList()
        override suspend fun invoke(a: A): R {
          // println("Invoke cont with state is null: ${state.value == null} && arg $a")
          val cont = state.getAndSet(null)
          // Reexecute f but this time on control we resume the continuation directly with a
          return if (cont == null) startMultiShot(snapshot + a)
          else suspendCoroutineUninterceptedOrReturn {
            // push stuff to the stack
            stack.add(a)
            // run cont
            cont.resume(a)
            cbs.add(it)
            COROUTINE_SUSPENDED
          }
        }
      }
      currF.value = { func(o) }
    }
  }

  fun startMultiShot(stack: List<Any?>): R = MultiShotDelimScope(stack, f).run()

  fun run(): R {
    // println("Running $label")
    f.startCoroutineUninterceptedOrReturn(this, Continuation(EmptyCoroutineContext) {
      // println("Put value ${(it.getOrThrow() as Sequence<Any?>).toList()}")
      ref.value = it.getOrThrow()
    }).let { res ->
      if (res == COROUTINE_SUSPENDED) {
        // println("Running suspended $label")
        ref.loop {
          // controls function called a continuation which now finished
          if (it != null) return@let
          else
            currF.getAndSet(null)!!.startCoroutineUninterceptedOrReturn(Continuation(EmptyCoroutineContext) { res ->
              // println("Resumption with ${(res.getOrThrow() as Sequence<Any?>).toList()}")
              ref.value = res.getOrThrow()
            }).let {
              // early return controls function did not call its continuation
              if (it != COROUTINE_SUSPENDED) ref.value = it as R
            }
        } // control has not been called
      } else return@run res as R
    }
    // control has been called and its continuations have been invoked, resume the continuations in reverse order
    cbs.asReversed().forEach { it.resume(ref.value!!) }
    return ref.value!!
  }
}

class MultiShotDelimScope<R>(
  localStack: List<Any?>,
  f: suspend Delimited<R>.() -> R
) : DelimitedScope<R>("Multishot", f) {
  private var depth = 0
  override val stack: MutableList<Any?> = localStack.toMutableList()
  override suspend fun <A> control(func: suspend (DelimitedCont<A, R>) -> R): A =
    if (stack.size > depth) stack[depth++] as A
    else {
      // println("EmptyStack")
      depth++
      super.control(func)
    }
}
