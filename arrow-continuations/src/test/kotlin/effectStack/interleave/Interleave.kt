package effectStack.interleave

import arrow.continuations.effectStack.Delimited
import arrow.continuations.effectStack.DelimitedCont
import arrow.continuations.effectStack.DelimitedScope
import arrow.continuations.effectStack.MultiShotDelimScope
import arrow.continuations.effectStack.reset
import arrow.core.Option
import arrow.core.identity
import effectStack.list

suspend inline fun <A> lists(crossinline f: suspend Interleave<*>.() -> A): List<A> =
  reset {
    listOf(f(object : Interleave<List<A>> , Delimited<List<A>> by this {
      override val f: suspend Interleave<*>.() -> List<A> = { listOf(f(this)) }
      override fun shortCircuit(): List<A> = emptyList()
      override fun just(b: Any?): List<A> = listOf(b) as List<A>
      override suspend fun <C> List<C>.invoke(): C =
        shift { cb ->
          flatMap { cb(it) }
        }
    }))
  }

interface Interleave<A> : Delimited<A> {
  val f: suspend Interleave<*>.() -> A
  fun shortCircuit(): A
  fun just(b: Any?): A
  suspend operator fun <C> Option<C>.invoke(): C =
    fold({ shift { shortCircuit() } }, ::identity)

  suspend operator fun <C> C?.invoke(): C =
    this ?: shift { shortCircuit() }

  suspend operator fun <C> List<C>.invoke(): C

}




