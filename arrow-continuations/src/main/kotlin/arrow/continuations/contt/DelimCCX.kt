package arrow.continuations.conttxxxx

import java.util.*
import kotlin.coroutines.*
import kotlin.coroutines.intrinsics.*

suspend fun <T> reset(body: suspend DelimitedScope<T>.() -> T): T =
  DelimitedScopeImpl<T>().also { impl ->
    body.startCoroutine(impl, impl)
  }.runReset()

interface DelimitedContinuation<T, R> {
  fun addResult(result: Result<T>): Unit
}

//@RestrictsSuspension
abstract class DelimitedScope<T> {
  abstract suspend fun <R> shift(block: suspend DelimitedScope<T>.(DelimitedContinuation<T, R>) -> T): R
  abstract suspend operator fun <R> DelimitedContinuation<T, R>.invoke(value: R): T
}

private typealias ShiftedFun<T> = (DelimitedScope<T>, DelimitedContinuation<T, Any?>, Continuation<T>) -> Any?

@Suppress("UNCHECKED_CAST")
private class DelimitedScopeImpl<T> : DelimitedScope<T>(), Continuation<T>, DelimitedContinuation<T, Any?> {
  private val shiftedBody: Stack<ShiftedFun<T>> = Stack()
  private var shiftCont: Stack<Continuation<Any?>> = Stack()
  private var invokeCont: Stack<Continuation<T>> = Stack()
  private var invokeValue: Stack<Any?> = Stack()
  private var completions: Stack<Result<T>> = Stack()

  override val context: CoroutineContext
    get() = EmptyCoroutineContext

  override fun resumeWith(result: Result<T>) {
    completions.push(result)
  }

  override fun addResult(result: Result<T>) {
    completions.push(result)
  }

  override suspend fun <R> shift(block: suspend DelimitedScope<T>.(DelimitedContinuation<T, R>) -> T): R =
    suspendCoroutineUninterceptedOrReturn {
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

  suspend fun runReset(): T =
    suspendCoroutineUninterceptedOrReturn<T> { resetCont ->
      println("starts runReset")
      // This is the stack of continuation in the `shift { ... }` after call to delimited continuation
      var currentCont: Continuation<T> = this
      //var result: Result<T>? = null
      // Trampoline loop to avoid call stack usage
      loop@ while (true) {
        println("start looping while true")
        try {
          // Call shift { ... } body or break if there are no more shift calls
          // If shift does not call any continuation, then its value is pushed and break out of the loop
          val shifted = takeShifted() ?: break
          val value = shifted.invoke(this, this, currentCont)
          println("value from shift: $value")
          if (value !== COROUTINE_SUSPENDED) {
            println("completion: $value")
            completions.push(Result.success(value as T))
            //continue@loop
            //break //TODO or loop again?
          }
        } catch (e: Throwable) {
          println("completion: $e")
          completions.push(Result.failure(e))
          //continue@loop
          //break //TODO or loop again?
        }
        // Shift has suspended - check if shift { ... } body had invoked continuation
        while (shiftCont.isNotEmpty()) {
          println("starts shift loop")
          currentCont = takeInvokeCont() ?: continue@loop
          val shift = takeShiftCont()
            ?: error("Delimited continuation is single-shot and cannot be invoked twice")
          val invokeVal = invokeValue.pop()
          println("invoke Value: $invokeVal")
          shift.resumeWith(Result.success(invokeVal as T))
          println("after shift resume with $invokeVal")
          println("end shift loop")
          continue@loop
        }
      }
      // Propagate the result to all pending continuations in shift { ... } bodies
      if (completions.isNotEmpty()) {
        when (val r = completions.pop()) {
          null -> TODO("Impossible result is null")
          else -> {
            suspend {
              completions.push(r)
              currentCont.resumeWith(r)
              runReset()
            }.startCoroutine(this)
            // Return the final result
            //resetCont.resumeWith(r)
          }
        }
      }
      println("SUSPENDING RESET")
      COROUTINE_SUSPENDED
    }

  private fun takeShifted() = if (shiftedBody.isNotEmpty()) shiftedBody.pop() else null
  private fun takeShiftCont() = if (shiftCont.isNotEmpty()) shiftCont.pop() else null
  private fun takeInvokeCont() = if (invokeCont.isNotEmpty()) invokeCont.pop() else null
}

suspend fun <A, B> DelimitedScope<List<A>>.bind(list: List<B>): B =
  shift { cb ->
    list.fold(emptyList<A>()) { acc, b ->
      acc + cb(b)
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

