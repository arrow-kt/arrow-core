package arrow.continuations.reflect

import arrow.Kind
import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.typeclasses.Monad

typealias In<A, F> = suspend (Reflect<F>) -> A

sealed class Reflect<F> {
  abstract suspend operator fun <A> F.invoke(): A
}

class ReflectM<A>(val prompt: Prompt<A, *>) : Reflect<A>() {
  override suspend fun <B> A.invoke(): B {
    println("invokeSuspend: $prompt")
    return prompt.suspend(this) as B
  }
  // since we know the receiver of this suspend is the
  // call to flatMap, the casts are safe
}

/**
 * for partially applying type arguments and better type inference
 *
 *   reify [F] in { BLOCK }
 *
 * @usecase def reify[M[_]: Monad] in[R](prog: => R): M[R]
 */
fun <F> reify(): ReifyBuilder<F> = ReifyBuilder()

fun <F, A> reify(MM: Monad<F>, prog: In<A, F>): A =
  reifyImpl(MM) { prog(it) }

class ReifyBuilder<F> {
  operator fun <A> invoke(
    MM: Monad<F>,
    prog: In<A, F>
  ): A = reifyImpl(MM) { prog(it) }
}

// this method is private since overloading and partially applying
// type parameters conflicts and results in non-helpful error messages.
//
// tradeoff of using `reify[M] in BLOCK` syntax over this function:
//   + type inference on R
//   - no type inference on M
// The latter might be a good thing since we want to make explicit
// which monad we are reifying.
private fun <F, A> reifyImpl(
  MM: Monad<F>,
  prog: In<A, F>
): A {

  // The coroutine keeps sending monadic values until it completes
  // with a monadic value
  val coroutine = Coroutine<F, Any?, A> { prompt ->
    // capability to reflect M
    val reflect = ReflectM(prompt)
    MM.just(prog(reflect)) as A
  }

  fun step(x: A): Either<F, A> {
    println("Step : $x")
    coroutine.continuation.resumeWith(Result.success(x))
    return if (coroutine.isDone())
      Right(coroutine.result())
    else
      Left(coroutine.value())
  }

  fun run(): A =
    if (coroutine.isDone())
      coroutine.result()
    else {
      MM.run {
        MM.tailRecM<F, A>(coroutine.value()) { f ->
          when (f) {
            is Kind<*, *> -> {
              f as Kind<F, A>
              f.flatMap { f.map { step(it) } }
            }
            else -> {
              val c = step(f as A)
              just(step(f as A))
            }
          }
        }
        // .flatten<Kind<M, Any?>>()
      } as A
    }

  return run()
}
