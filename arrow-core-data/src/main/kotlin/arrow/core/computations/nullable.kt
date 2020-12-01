package arrow.core.computations

import arrow.continuations.Effect
import arrow.continuations.Reset

fun interface NullableEffect<A> : Effect<A?> {
  suspend operator fun <B> B?.invoke(): B = this ?: control().shift(null)
}

@Suppress("ClassName")
object nullable

fun <A> nullable.eager(func: suspend NullableEffect<A>.() -> A?): A? =
  Reset.restricted { func(NullableEffect { this }) }

suspend operator fun <A> nullable.invoke(func: suspend NullableEffect<*>.() -> A?): A? =
  Reset.suspended { func(NullableEffect {this }) }
