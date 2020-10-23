package arrow.typeclasses.suspended

import arrow.Kind

/**
 * All possible approaches to running [Kind] in the context of [Fx]
 *
 * ```
 * fx {
 *   val one = just(1).bind() // using bind
 *   val (two) = just(one + 1) // using destructuring
 *   val three = !just(two + 1) // yelling at it
 * }
 * ```
 */
// TODO: Deprecate
interface BindSyntax<F> {

  // TODO: Deprecate
  suspend fun <A> Kind<F, A>.bind(): A

  @Deprecated("This operator can have problems when you do not capture the value, please use ! or bind() instead", ReplaceWith("bind()"))
  suspend operator fun <A> Kind<F, A>.component1(): A =
    bind()

  // TODO: Deprecate
  suspend operator fun <A> Kind<F, A>.not(): A =
    bind()
}

// TODO: make it fun interface when suspend fun is allowed inside
interface Invoke<F> {
  suspend operator fun <A> Kind<F, A>.invoke(): A
}
