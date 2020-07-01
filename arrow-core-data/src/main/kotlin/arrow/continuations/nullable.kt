package arrow.continuations

import arrow.core.Either
import arrow.core.ShortCircuit
import arrow.core.identity
import arrow.core.left
import arrow.core.right
import kotlin.coroutines.Continuation
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.experimental.ExperimentalTypeInference

class NullableBuilder(parent: Continuation<*>) : Cont.Strict<Any?, Any?>(parent) {

  operator fun <A> A?.invoke(): A = this ?: throw ShortCircuit(null)

  override suspend fun <A> A.just(): A? = this

  override fun ShortCircuit.recover(): Any? = null
}

@UseExperimental(ExperimentalTypeInference::class)
@BuilderInference
suspend fun <A> nullable(@BuilderInference f: suspend NullableBuilder.() -> A): A? =
  suspendCoroutineUninterceptedOrReturn {
    NullableBuilder(it).strict(f)
  }
