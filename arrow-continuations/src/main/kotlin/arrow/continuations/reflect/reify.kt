package arrow.continuations.reflect

import arrow.Kind
import arrow.core.Either
import arrow.core.Left
import arrow.core.Right
import arrow.core.identity
import arrow.typeclasses.Monad

typealias In<A, F> = suspend (Reflect<F, A>) -> A

sealed class Reflect<F, A> {
  abstract suspend operator fun Kind<F, A>.invoke(): A
}

class ReflectM<F, A>(val prompt: Prompt<Kind<F, A>, *>) : Reflect<F, A>() {
  override suspend fun Kind<F, A>.invoke(): A {
    println("invokeSuspend: $prompt")
    val result = prompt.suspend(this)
    return result as A
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

fun <F, A> reify(MM: Monad<F>, prog: In<A, F>): Kind<F, A> =
  reifyImpl(MM) { prog(it) }

class ReifyBuilder<F> {
  operator fun <A> invoke(
    MM: Monad<F>,
    prog: In<A, F>
  ): Kind<F, A> = reifyImpl(MM) { prog(it) }
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
): Kind<F, A> {

  var currentPrompt: Prompt<Kind<F, A>, Any?>? = null

  // The coroutine keeps sending monadic values until it completes
  // with a monadic value
  val coroutine = Coroutine<Kind<F, A>, Any?, A> { prompt ->
    currentPrompt = prompt
    // capability to reflect M
    val reflect = ReflectM(prompt)
    prog(reflect)
  }

  fun step(x: A): Either<Kind<F, A>, A> {
    println("Step : $x")
    return if (coroutine.isDone())
      Right(coroutine.result())
    else {
      coroutine.continuation.resumeWith(Result.success(x))
      Left(coroutine.value())
    }
  }

  fun run(): Kind<F, A> =
    MM.run {
      if (coroutine.isDone())
        coroutine.result().just()
      else {
        MM.tailRecM(coroutine.value()) {
          it.map(::step)
        }.flatMap { just(it) }
        // .flatten<Kind<M, Any?>>()
      }
    }


  return run()
}
