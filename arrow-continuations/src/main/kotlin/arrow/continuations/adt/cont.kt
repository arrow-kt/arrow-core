package arrow.continuations.adt

import kotlin.coroutines.suspendCoroutine

typealias Scope<A, B> = Continuation.Scope<A, B>
typealias Shift<A, B, C> = Continuation.Scope<A, B>.Shift<C>
typealias Invoke<A, B, C> = Continuation.Scope<A, B>.Shift<C>.Invoke
typealias ShortCircuit<A> = Continuation<A, *>.ShortCircuit
typealias KotlinContinuation<A> = kotlin.coroutines.Continuation<A>

sealed class Continuation<A, B> {

  abstract val parent: Continuation<*, *>

  abstract val state: ContinuationState<*, *, *>

  inner class ShortCircuit(val value: A) : Continuation<A, B>() {
    override val parent: Continuation<A, B> = this@Continuation
    override val state: ContinuationState<*, *, *> = this@Continuation.state
  }

  abstract class Scope<A, B>(
    override val state: ContinuationState<B, A, *> = ContinuationState<B, A, Any?>()
  ) : Continuation<A, B>() {

    override val parent: Continuation<A, B> get() = this

    abstract val result: A

    inner class Shift<C>(
      val block: suspend Scope<A, B>.(Shift<C>) -> A,
      val continuation: KotlinContinuation<C>
    ) : Continuation<B, A>(), KotlinContinuation<C> by continuation {
      val scope: Scope<A, B> = this@Scope
      override val parent: Continuation<A, B> = scope
      override val state: ContinuationState<B, A, *> = scope.state

      inner class Invoke(val continuation: KotlinContinuation<A>, val value: C) : Continuation<B, A>(), KotlinContinuation<A> by continuation {
        val shift: Shift<C> = this@Shift
        override val parent: Shift<C> = this@Shift
        override val state: ContinuationState<B, A, *> = shift.state
      }

      private var _result: C? = null

      override fun resumeWith(result: Result<C>) {
        this._result = result.getOrThrow()
      }
    }


  }
}

suspend fun <A, B, C> Scope<A, B>.shift(block: suspend Continuation.Scope<A, B>.(Continuation.Scope<A, B>.Shift<C>) -> A): C =
  suspendCoroutine {
    Shift(block, it).compile(state)
  }

suspend operator fun <A, B, C> Shift<A, B, C>.invoke(value: C): A =
  suspendCoroutine {
    Invoke(it, value).compile(state)
  }

fun <A, B, C> ContinuationState<A, B, C>.unfold(): A =
  when(val prompt = takePrompt()) {
    is ShortCircuit -> prompt.value
    is Scope -> prompt.result
    null -> TODO()
    is Shift<*, *, *> -> TODO()
    is Invoke<*, *, *> -> TODO()
  }


fun <A, B, C> Continuation<A, B>.compile(state: ContinuationState<A, B, C>): A =
    when (this@compile) {
      is Shift<*, *, *> -> {
        state.log("Shift: [parent: $parent, scope: $scope, block: $block]")
        state.push(this@compile)
        state.unfold()
      }
      is Invoke<*, *, *> -> {
        state.log("Invoke: [parent: $parent, value: $value]")
        state.push(this@compile)
        state.unfold()
      }
      is Scope -> {
        state.log("Scope: [parent: $parent, result: $result]")
        state.push(this@compile)
        state.unfold()
      }
      is ShortCircuit -> {
        state.log("ShortCircuit: [parent: $parent, value: $value]")
        value
      }
    }

class ListScope<A> : Scope<List<A>, A>() {
  private var _result: List<A> = emptyList()
  override val result: List<A> get () = _result
  suspend operator fun <C> List<C>.invoke(): C =
    shift { cb ->
      _result = flatMap {
        cb(it)
      }
      result
    }
}

inline fun <A> list(block: ListScope<*>.() -> A): List<A> =
  listOf(block(ListScope<Any?>()))


suspend fun main() {
  val result = list {
    val a = listOf(1, 2, 3)()
    val b = listOf("a", "b", "c")()
    "$a$b "
  }
  println(result)
}

