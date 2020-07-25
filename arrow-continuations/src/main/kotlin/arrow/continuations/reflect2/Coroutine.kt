package arrow.continuations.reflect2

import arrow.fx.coroutines.UnsafePromise
import kotlinx.atomicfu.atomic
import kotlinx.atomicfu.loop
import kotlin.coroutines.*
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.startCoroutineUninterceptedOrReturn
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn

interface Prompt<S, R> {
  suspend fun suspend(value: S): R
}

class Coroutine<S, R, T>(prog: suspend (Prompt<S, R>) -> T) {

  fun isDone() = co.isDone()

  fun value(): S {
    assert(!isDone());
    return receive()
  }

  fun result(): T {
    assert(isDone())
    return receive()
  }

  fun resume(v: R): Unit {
    assert(!isDone())
    send(v)
    co.run()
  }

  private var channel: Any? = null
  private fun send(v: Any?) {
    channel = v
  }

  private fun <A> receive(): A {
    val v = channel
    return v as A
  }

  val yielder = UnsafePromise<Unit>()
  val prompt = object : Prompt<S, R> {
    override suspend fun suspend(value: S): R {
      send(value)
      yielder.join() // Continuation yield prompt
      return receive()
    }
  }

  private val co = Continuation(prompt, yielder) { send(prog(prompt)) }

  init {
    co.run()
  }
}

class Continuation(
  val prompt: Prompt<*, *>,
  val yielder: UnsafePromise<Unit>,
  val f: suspend () -> Unit
) {

  fun isDone(): Boolean =
    yielder.isNotEmpty()

  // The run method returns true when the continuation terminates, and false if it suspends.
  fun run(): Boolean {
    val a = f.startCoroutineUninterceptedOrReturn(Continuation(EmptyCoroutineContext, yielder::complete))
    return a != COROUTINE_SUSPENDED
  }
}
