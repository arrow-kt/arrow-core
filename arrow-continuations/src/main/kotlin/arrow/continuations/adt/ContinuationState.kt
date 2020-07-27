package arrow.continuations.adt

import arrow.continuations.contxx.Cont
import java.util.*

interface ContinuationState<A, B, C> {

  fun shift(): Shift<B, A, C>? =
    takePrompt() as? Shift<B, A, C>

  fun invoke(): Invoke<A, B, *>? =
    takePrompt() as? Invoke<A, B, C>

  fun scope(): Scope<A, B>? =
    takePrompt() as? Scope<A, B>

  fun takePrompt(): Continuation<A, B>?
  fun push(prompt: Continuation<A, B>): Unit
  fun log(value: String): Unit

  operator fun plusAssign(other: ContinuationState<A, B, C>): Unit

  companion object {
    operator fun <A, B, C> invoke(): ContinuationState<A, B, C> =
      StackContinuationState()

    private class StackContinuationState<A, B, C>(
      private val prompts: Stack<Continuation<A, B>> = Stack()
    ) : ContinuationState<A, B, C> {

      override fun takePrompt(): Continuation<A, B>? =
        if (prompts.isNotEmpty()) prompts.pop() else null

      override fun push(prompt: Continuation<A, B>) {
        prompts.push(prompt)
      }

      private fun stateLog(): String =
        "/size: ${prompts.size}/ $prompts"


      override fun log(value: String): Unit {
        println("${stateLog()}\t\t\t$value")
      }

      override fun plusAssign(other: ContinuationState<A, B, C>): Unit {
        while (other.takePrompt()?.also { prompts.push(it) } != null) {}
      }
    }
  }
}
