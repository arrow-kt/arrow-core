package arrow.continuations.effectStackUnpackedResets

import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.loop
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.startCoroutineUninterceptedOrReturn
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.reflect.KProperty

interface DelimitedCont<A, B> {
  suspend operator fun invoke(a: A): B
}

interface Delimited<A> {
  suspend fun <B> shift(func: suspend (DelimitedCont<B, A>) -> A): B
  suspend fun <B> reset(f: suspend Delimited<B>.() -> B): B
}

suspend fun <A> reset(f: suspend Delimited<A>.() -> A): A =
  DelimitedScope("Prompt", f).run {}

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

  open val parentScope: DelimitedScope<*>? = null
  internal val childScope = atomic<DelimitedScope<*>?>(null)
  internal fun setChildScope(s: DelimitedScope<*>?): Unit {
    childScope.value = s
  }

  internal fun getChildScope(): DelimitedScope<*>? = childScope.value

  internal val ret = atomic<A?>(null)

  // TODO More descriptive name
  private val currShiftFn = atomic<(suspend () -> A)?>(null)

  // TODO more efficient data structures. O(1) append + O(1) pop would be best
  internal open val stack: MutableList<Any?> = mutableListOf()

  // TODO for this we could use a datastructure that can O(1) append and has O(1) popLast()
  private val cbs = mutableListOf<Continuation<A>>()

  internal val childCb = atomic<Continuation<Unit>?>(null)
  internal val multiChildCb = atomic<Continuation<Unit>?>(null)

  internal fun trace(msg: String): Unit = println("$dbgLabel: $msg")

  internal fun setMultiChildCb(c: Continuation<Unit>): Unit {
    trace("Set multi child cb")
    multiChildCb.value = c
  }

  internal fun setChildCB(c: Continuation<Unit>): Unit {
    trace("Set child cb")
    childCb.value = c
  }

  override suspend fun <B> shift(func: suspend (DelimitedCont<B, A>) -> A): B {
    when (val cs = childScope.value) {
      is MultiShotDelimScope -> if (cs.done.not() && cs.depth < cs.stack.size) {
        trace("Early return from stack ${cs.stack[cs.depth]}")
        return cs.stack[cs.depth++] as B
      } else Unit
      else -> Unit
    }
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
        val offset: Int = stack.size
        override suspend fun invoke(a: B): A {
          // println("Invoke cont with state is null: ${state.value == null} && arg $a")
          val cont = liveContinuation.getAndSet(null)
          // Re-execute f, but in a new scope which contains the stack slice + a and will use that to fill in the first
          //  calls to shift
          return if (cont == null) startMultiShot(offset, a)
          // we have a "live" continuation to resume to so we suspend the shift block and do exactly that
          else suspendCoroutineUninterceptedOrReturn {
            trace("Invoke $a")
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

  open suspend fun startMultiShot(end: Int, b: Any?): A = startMultiShot(0, end, b)

  suspend fun startMultiShot(start: Int, end: Int, b: Any?): A =
    MultiShotDelimScope(this@DelimitedScope, stack.subList(start, end).toList() + b, f).let { scope ->
      // Tell the parent we are running multishot
      parentScope?.setChildScope(scope)
      scope.run {
        trace("Multi cb")
        parentScope?.setMultiChildCb(it)
      }.also { parentScope?.setChildScope(scope.parentScope) }
    }

  override suspend fun <B> reset(f: suspend Delimited<B>.() -> B): B =
    ChildScope(this@DelimitedScope, f).run {
      trace("Child cb")
      setChildCB(it)
    }

  internal fun getValue(): A? =
    // println("Running suspended $label")
    ret.loop {
      trace("Get loop start")
      // shift function called f's continuation which now finished
      if (it != null) return@getValue it
      // we are not done yet
      else {
        trace("Get loop: no result")
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
        else {
          trace("Get loop: no work")
          multiChildCb.getAndSet(null)?.also { trace("Resuming multishot child") }?.resume(Unit)
          childCb.getAndSet(null)?.also { trace("Resuming child") }?.resume(Unit) ?: return@getValue null
        }
      }
    }

  fun hasWork(): Boolean = (currShiftFn.value != null || multiChildCb.value != null)
    || (parentScope != null && parentScope!!.hasWork())

  open suspend fun run(handleSuspend: (Continuation<Unit>) -> Unit): A {
    trace("Started runloop")
    f.startCoroutineUninterceptedOrReturn(this, Continuation(EmptyCoroutineContext) {
      // println("Put value ${(it.getOrThrow() as Sequence<Any?>).toList()}")
      // f finished after being resumed. Save the value to resume the shift blocks later
      trace("Resumed with $it")
      ret.value = it.getOrThrow()
    }).let { res ->
      if (res == COROUTINE_SUSPENDED) {
        trace("Starting suspend loop")
        while (true) {
          val a = getValue()
          if (a == null) {
            trace("Yielding to parent"); suspendCoroutine(handleSuspend)
          } else break
        }
      } // we finished without ever suspending. This means there is no shift block and we can short circuit run
      else {
        trace("no suspension. Returning $res"); return@run res as A
      }
    }

    trace("Done. Number of cbs: ${cbs.size}")
    cbs.asReversed().forEach {
      it.resume(ret.value!!)
      // This may have spawned additional work for a parent
      trace("Parent has work: ${parentScope != null && parentScope!!.hasWork()}")
      if (parentScope != null && parentScope!!.hasWork()) {
        trace("Yielding to parent"); suspendCoroutine(handleSuspend)
      }

      trace("Value is ${ret.value}")
    }
    trace("Returned with value ${ret.value}")
    // return the final value after all shift blocks finished processing the result
    return ret.value!!
  }
}

open class ChildScope<A>(
  final override val parentScope: DelimitedScope<*>,
  f: suspend Delimited<A>.() -> A
) : DelimitedScope<A>("Child-" + unique++, f) {
  override val stack: MutableList<Any?> = parentScope.stack
  private val offset: Int = parentScope.stack.size
  override suspend fun startMultiShot(end: Int, b: Any?): A =
    startMultiShot(offset, end, b)
}

// TODO I can avoid list copying if I also work with indexes better here!
open class MultiShotDelimScope<A>(
  final override val parentScope: DelimitedScope<*>,
  localStack: List<Any?>,
  func: suspend Delimited<A>.() -> A,
  var startingOffset: Int = 0
) : DelimitedScope<A>("Multishot-" + unique++, func) {
  internal var depth by ParentDepth(0, parentScope, startingOffset)
  internal var done = false
  override val stack: MutableList<Any?> = localStack.toMutableList().also { trace("Running multishot with $localStack") }

  override suspend fun <B> shift(func: suspend (DelimitedCont<B, A>) -> A): B =
    if (stack.size > depth && done.not()) {
      trace("Using the stack for shift: ${stack[depth]}")
      trace("Stack: $stack")
      stack[depth++] as B
    } else {
      done = true
      trace("Done by shift")
      super.shift(func)
    }

  override suspend fun <B> reset(p: suspend Delimited<B>.() -> B): B =
    if (stack.size > depth && done.not()) {
      trace("Reset from multishot with stack left. Depth $depth")
      // there are still elements on the stack, so run f as Multishot in child mode
      MultiShotDelimScope(this@MultiShotDelimScope, stack, p, depth)
        .let {
          setChildScope(it)
          it.run {
            trace("Suspended multi shot in multi shot")
            setChildCB(it)
          }
        }
        .also { done = true }.also { trace("Done by reset1: $it. Stack $stack") }
    } else {
      done = true
      trace("Done by reset2")
      super.reset(p)
    }

  override suspend fun startMultiShot(end: Int, b: Any?): A {
    trace("Multishot from multishot $startingOffset $end")
    return super.startMultiShot(startingOffset, end, b)
  }
}

class ParentDepth(var def: Int, val parentScope: DelimitedScope<*>?, val offset: Int) {
  operator fun getValue(thisRef: Any?, property: KProperty<*>): Int =
    (if (parentScope is MultiShotDelimScope) parentScope.depth else def) + offset
  operator fun setValue(thisRef: Any?, property: KProperty<*>, i: Int): Unit =
    if (parentScope is MultiShotDelimScope) parentScope.depth = i - offset else def = i - offset
}

var unique = 0
