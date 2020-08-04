package arrow.continuations.generic

import kotlinx.atomicfu.atomic
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.startCoroutineUninterceptedOrReturn
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

open class NestedDelimContScope<R>(val f: suspend DelimitedScope<R>.() -> R) : RunnableDelimitedScope<R> {

  private val resultVar = atomic<R?>(null)
  internal fun getResult(): R? = resultVar.value
  internal fun setResult(r: R): Unit {
    resultVar.value = r
  }

  internal inline fun loopNoResult(f: () -> Unit): Unit {
    while (true) {
      if (getResult() == null) f()
      else return
    }
  }

  internal val nextShift = atomic<(suspend () -> R)?>(null)

  // TODO This can be append only and needs fast reversed access
  internal val shiftFnContinuations = mutableListOf<Continuation<R>>()

  data class SingleShotCont<A, R>(
    private val continuation: Continuation<A>,
    private val shiftFnContinuations: MutableList<Continuation<R>>
  ) : DelimitedContinuation<A, R> {
    override suspend fun invoke(a: A): R = suspendCoroutine { resumeShift ->
      shiftFnContinuations.add(resumeShift)
      continuation.resume(a)
    }
  }

  data class CPSCont<A, R>(
    private val runFunc: suspend DelimitedScope<R>.(A) -> R
  ) : DelimitedContinuation<A, R> {
    override suspend fun invoke(a: A): R = DelimContScope<R> { runFunc(a) }.invoke()
  }

  override suspend fun <A> shift(func: suspend DelimitedScope<R>.(DelimitedContinuation<A, R>) -> R): A =
    suspendCoroutine { continueMain ->
      val delCont = SingleShotCont(continueMain, shiftFnContinuations)
      assert(nextShift.compareAndSet(null, suspend { this.func(delCont) }))
    }

  override suspend fun <A, B> shiftCPS(func: suspend (DelimitedContinuation<A, B>) -> R, c: suspend DelimitedScope<B>.(A) -> B): Nothing =
    suspendCoroutine {
      assert(nextShift.compareAndSet(null, suspend { func(CPSCont(c)) }))
    }

  override suspend fun <A> reset(f: suspend DelimitedScope<A>.() -> A): A =
    ChildDelimContScope(this, f)
      .invokeNested()

  internal inline fun step(hdlMisingWork: () -> Unit): Unit {
    val nextShiftFn = nextShift.getAndSet(null)
      ?: return hdlMisingWork()
    nextShiftFn.startCoroutineUninterceptedOrReturn(Continuation(EmptyCoroutineContext) { result ->
      resultVar.value = result.getOrThrow()
    }).let {
      if (it != COROUTINE_SUSPENDED) resultVar.value = it as R
    }
  }

  override fun invoke(): R {
    f.startCoroutineUninterceptedOrReturn(this, Continuation(EmptyCoroutineContext) { result ->
      resultVar.value = result.getOrThrow()
    }).let {
      if (it == COROUTINE_SUSPENDED) {
        loopNoResult {
          step { throw IllegalStateException("Suspended parent scope, but found no further work") }
        }
      } else return@invoke it as R
    }

    assert(resultVar.value != null)
    for (c in shiftFnContinuations.asReversed()) c.resume(resultVar.value!!)
    return resultVar.value!!
  }

  open fun getActiveParent(): NestedDelimContScope<*>? = this.takeIf { nextShift.value != null }

  companion object {
    fun <R> reset(f: suspend DelimitedScope<R>.() -> R): R = NestedDelimContScope(f).invoke()
  }
}

class ChildDelimContScope<R>(
  val parent: NestedDelimContScope<*>,
  f: suspend DelimitedScope<R>.() -> R
) : NestedDelimContScope<R>(f) {
  override fun getActiveParent(): NestedDelimContScope<*>? =
    super.getActiveParent() ?: parent.getActiveParent()

  private suspend fun performParentWorkIfNeeded(): Unit {
    while (true) {
      parent.getActiveParent()?.let { scope ->
        // No need to do anything in steps cb because we handle this case from down here
        scope.step {  }
        // parent short circuited
        if (scope.getResult() != null) suspendCoroutine<Nothing> {  }
      } ?: break
    }
  }

  suspend fun invokeNested(): R {
    f.startCoroutineUninterceptedOrReturn(this, Continuation(EmptyCoroutineContext) { result ->
      setResult(result.getOrThrow())
    }).let {
      if (it == COROUTINE_SUSPENDED) {
        loopNoResult {
          step { performParentWorkIfNeeded() }
        }
      } else return@invokeNested it as R
    }

    assert(getResult() != null)
    for (c in shiftFnContinuations.asReversed()) c.resume(getResult()!!)
    return getResult()!!
  }

  override fun invoke(): R {
    println("""
      Using invoke() for child scope.
      This will break on nested calls to reset and using shift from different scopes inside those.
      Use invokeNested instead.
      """)
    return super.invoke()
  }
}
