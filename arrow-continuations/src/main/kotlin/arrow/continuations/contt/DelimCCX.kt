package arrow.continuations.conttxxxx

import java.util.*
import kotlin.collections.ArrayList
import kotlin.coroutines.*
import kotlin.coroutines.intrinsics.*

suspend fun <T> reset(body: suspend DelimitedScope<T>.() -> T): T =
  DelimitedScopeImpl<T>(body).run {
    body.startCoroutine(this, this)
    runReset()
  }

interface DelimitedContinuation<T, R>

//@RestrictsSuspension
abstract class DelimitedScope<T> {
  abstract suspend fun <R> shift(block: suspend DelimitedScope<T>.(DelimitedContinuation<T, R>) -> T): R
  abstract suspend operator fun <R> DelimitedContinuation<T, R>.invoke(value: R): T
}

private typealias ShiftedFun<T> = (DelimitedScope<T>, DelimitedContinuation<T, Any?>, Continuation<T>) -> Any?

@Suppress("UNCHECKED_CAST")
private class DelimitedScopeImpl<T>(val body: suspend DelimitedScope<T>.() -> T) : DelimitedScope<T>(), Continuation<T>, DelimitedContinuation<T, Any?> {
  private val shiftedBody: Stack<ShiftedFun<T>> = Stack()
  private var shiftCont: Stack<Continuation<Any?>> = Stack()
  private var invokeCont: Stack<Continuation<T>> = Stack()
  private var invokeValue: Stack<Any?> = Stack()
  private var completions: Stack<Result<T>> = Stack()
  private var lastShiftedBody: ShiftedFun<T>? = null

  private fun stateHeader(): String =
    "shiftedBody\tshiftCont\tinvokeCont\tinvokeValue\tcompletions"

  private fun stateLog(): String =
    "${shiftedBody.size}\t\t\t${shiftCont.size}\t\t\t${invokeCont.size}\t\t\t${invokeValue.size}\t\t\t${completions.size}"


  private fun log(value: String): Unit {
    println("${stateLog()}\t\t\t$value")
  }

  override val context: CoroutineContext = EmptyCoroutineContext

  override fun resumeWith(result: Result<T>) {
    completions.push(result)
  }

  override suspend fun <R> shift(block: suspend DelimitedScope<T>.(DelimitedContinuation<T, R>) -> T): R =
    suspendCoroutineUninterceptedOrReturn {
      this.lastShiftedBody = block as ShiftedFun<T>
      this.shiftedBody.push(block as ShiftedFun<T>)
      this.shiftCont.push(it as Continuation<Any?>)
      COROUTINE_SUSPENDED
    }

  override suspend fun <R> DelimitedContinuation<T, R>.invoke(value: R): T =
    suspendCoroutineUninterceptedOrReturn sc@{
      //check(invokeCont == null)
      invokeCont.push(it)
      invokeValue.push(value)
      COROUTINE_SUSPENDED
    }

  // This is the stack of continuation in the `shift { ... }` after call to delimited continuation
  var currentCont: Continuation<T> = this

  suspend fun runReset(): T =
    suspendCoroutineUninterceptedOrReturn<T> { parent ->
      println(stateHeader())
      log("[SUSPENDED] runReset()")
      //var result: Result<T>? = null
      // Trampoline loop to avoid call stack usage
      resetLoop@ while (true) {
        log("\t-> resetLoop")
        if (pushCompletion(currentCont)) break
        // Shift has suspended - check if shift { ... } body had invoked continuation
        shiftLoop@ while (shiftCont.isNotEmpty()) {
          log("\t\t-> shiftLoop")
          currentCont = takeInvokeCont() ?: continue@resetLoop
          val shift = takeShiftCont()
            ?: error("Delimited continuation is single-shot and cannot be invoked twice")
          val invokeVal = invokeValue.pop()
          log("\t\t!! resumeWith: $invokeVal")
          shift.resumeWith(Result.success(invokeVal as T))
          log("\t\t<- shift loop")
        }
        // Propagate the result to all pending continuations in shift { ... } bodies
        if (propagateCompletions()) continue@resetLoop
        log("\t<- resetLoop")
      }
      COROUTINE_SUSPENDED
    }

  private fun propagateCompletions(): Boolean {
    if (completions.isNotEmpty()) {
      when (val r = completions.pop()) {
//        null -> TODO("Impossible result is null")
        else -> {
          log("\t<- propagateCompletion: $r")
          //completions.push(r)
          //resume first shot if invoked

          suspend {
            suspendCoroutine<T> {
              //shiftedBody.push(lastShiftedBody)
              //invokeValue.push(r.getOrThrow())
              it.resumeWith(r)
            }
          }.createCoroutine(this).resumeWith(Result.success(Unit))

          //proceed to next value if invoked
          //invokeValue.push(r.getOrThrow())

          return true
          // Return the final result
          //resetCont.resumeWith(r)
        }
      }
    }
    return true
  }

  private fun pushCompletion(currentCont: Continuation<T>): Boolean {
    try {
      // Call shift { ... } body or break if there are no more shift calls
      // If shift does not call any continuation, then its value is pushed and break out of the loop
      val shifted = takeShifted() ?: return true
      val value = shifted.invoke(this, this, currentCont)
      if (value !== COROUTINE_SUSPENDED) {
        log("-> pushCompletion: $value")
        completions.push(Result.success(value as T))
        //continue@loop
        //break //TODO or loop again?
      }
    } catch (e: Throwable) {
      log("-> pushCompletion: $e")
      completions.push(Result.failure(e))
      //continue@loop
      //break //TODO or loop again?
    }
    return false
  }

  private fun takeShifted() = if (shiftedBody.isNotEmpty()) shiftedBody.pop() else null
  private fun takeShiftCont() = if (shiftCont.isNotEmpty()) shiftCont.pop() else null
  private fun takeInvokeCont() = if (invokeCont.isNotEmpty()) invokeCont.pop() else null
}

suspend fun <A, B> DelimitedScope<List<A>>.bind(list: List<B>): B {
  val result: ArrayList<A> = arrayListOf()
  return shift { cb ->
    for (el in list) {
      reset<Unit> {
        result.addAll(
          shift { cb(el) }
        )
      }
    }
    result
  }
}

suspend fun main() {
  val result: List<String> = reset {
    val a = bind(listOf(1, 2, 3))
    val b = bind(listOf("a", "b", "c"))
    listOf("$a$b ")
  }
  println(result)
}

