package arrow.typeclasses.suspended

import arrow.Kind
import arrow.KindDeprecation

/**
 * All possible approaches to running [Kind] in the context of [Fx]
 *
 * ```
 * fx {
 *   val one = just(1).bind() // using bind (deprecated)
 *   val (two) = just(one + 1) // using destructuring (deprecated)
 *   val three = !just(two + 1) // yelling at it (deprecated)
 *   val four = just(three + 1)() // using invoke
 * }
 * ```
 */
@Deprecated(KindDeprecation)
interface BindSyntax<F> {

  suspend fun <A> Kind<F, A>.bind(): A

  @Deprecated("This operator is being removed to avoid confusion with Boolean operations, and to unify the api to a single method", ReplaceWith("this.bind()"))
  suspend operator fun <A> Kind<F, A>.not(): A = bind()
}
