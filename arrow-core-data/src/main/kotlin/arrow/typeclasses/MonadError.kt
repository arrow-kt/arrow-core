package arrow.typeclasses

import arrow.Kind
import arrow.KindDeprecation
import arrow.core.Either
import arrow.core.NonFatal
import arrow.documented
import kotlin.coroutines.startCoroutine

@Deprecated(KindDeprecation)
interface MonadError<F, E> : ApplicativeError<F, E>, Monad<F> {

  fun <A> Kind<F, A>.ensure(error: () -> E, predicate: (A) -> Boolean): Kind<F, A> =
    this.flatMap {
      if (predicate(it)) just(it)
      else raiseError(error())
    }

  fun <A, B> Kind<F, A>.redeemWith(fe: (E) -> Kind<F, B>, fb: (A) -> Kind<F, B>): Kind<F, B> =
    flatMap(fb).handleErrorWith(fe)

  fun <A> Kind<F, Either<E, A>>.rethrow(): Kind<F, A> =
    flatMap { it.fold({ e -> raiseError<A>(e) }, { a -> just(a) }) }
}

/**
 * MonadThrow has the error type fixed to Throwable. It provides [fx.monadThrow] for automatically catching throwable
 * errors in the context of a binding, short-circuiting the complete computation and returning the error raised to the
 * same computational context (through [raiseError]).
 *
 * ```kotlin:ank:playground:extension
 * _imports_
 *
 * fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   _extensionFactory_
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 *
 * ### Example
 *
 * Oftentimes we find ourselves in situations where we need to sequence some computations that could potentially fail.
 * [fx.monadThrow] allows us to safely compute those by automatically catching any exceptions thrown during the process.
 *
 * ```kotlin:ank:playground:extension
 * _imports_
 * import arrow.Kind
 * import arrow.typeclasses.MonadThrow
 *
 * typealias Impacted = Boolean
 *
 * object Lettuce
 * object Knife
 * object Salad
 * class InsufficientAmount(val quantityInGrams : Int) : Throwable("You need $quantityInGrams more grams of ingredient")
 *
 * fun <F> MonadThrow<F>.takeFoodFromRefrigerator(): Kind<F, Lettuce> = just(Lettuce)
 * fun <F> MonadThrow<F>.getKnife(): Kind<F, Knife> = just(Knife)
 * fun <F> MonadThrow<F>.launchImpure(tool: Knife, ingredient: Lettuce): Salad {
 *   throw InsufficientAmount(5)
 * }
 *
 * fun main(args: Array<String>) {
 *    //sampleStart
 *    fun <F> MonadThrow<F>.prepareLunch(): Kind<F, Salad> =
 *      fx.monadThrow {
 *        val lettuce = takeFoodFromRefrigerator()()
 *        val knife = getKnife()()
 *        val salad = launchImpure(knife, lettuce) // this throws!
 *        salad
 *      }
 *
 *    val result = _extensionFactory_.prepareLunch()
 *    //sampleEnd
 *    println(result)
 * }
 * ```
 */
@documented
interface MonadThrow<F> : MonadError<F, Throwable> {

  /**
   * Entry point for monad bindings which enables for comprehensions. The underlying implementation is based on
   * coroutines. A coroutine is initiated and suspended inside [MonadThrowContinuation] yielding to [Monad.flatMap].
   * Once all the flatMap binds are completed, the underlying monad is returned from the act of executing the coroutine.
   *
   * This one operates over [MonadError] instances that can support [Throwable] in their error type automatically
   * lifting errors as failed computations in their monadic context and not letting exceptions thrown as the regular
   * monad binding does.
   *
   * ### Example
   *
   * Oftentimes we find ourselves in situations where we need to sequence some computations that could potentially fail.
   * [fx.monadThrow] allows us to safely compute those by automatically catching any exceptions thrown during the process.
   *
   * ```kotlin:ank:playground:extension
   * _imports_
   * import arrow.Kind
   * import arrow.typeclasses.MonadThrow
   *
   * typealias SaladPrepared = Boolean
   *
   * object Lettuce
   * object Knife
   * class InsufficientAmount(val quantityInGrams : Int) : Throwable("You need $quantityInGrams more grams of ingredient")
   *
   * fun <F> MonadThrow<F>.takeFoodFromRefrigerator(): Kind<F, Lettuce> = just(Lettuce)
   * fun <F> MonadThrow<F>.getKnife(): Kind<F, Knife> = just(Knife)
   * fun <F> MonadThrow<F>.launchImpure(tool: Knife, ingredient: Lettuce): Salad {
   *   throw InsufficientAmount(5)
   * }
   *
   * fun main(args: Array<String>) {
   *    //sampleStart
   *    fun <F> MonadThrow<F>.prepareLunch(): Kind<F, SaladPrepared> =
   *      fx.monadThrow {
   *        val lettuce = takeFoodFromRefrigerator()()
   *        val knife = getKnife()()
   *        val salad = launchImpure(knife, lettuce) // this throws!
   *        salad
   *      }
   *
   *    val result = _extensionFactory_.prepareLunch()
   *    //sampleEnd
   *    println(result)
   * }
   * ```
   *
   */
  override val fx: MonadThrowFx<F>
    get() = object : MonadThrowFx<F> {
      override val M: MonadThrow<F> = this@MonadThrow
    }

  fun <A> Throwable.raiseNonFatal(): Kind<F, A> =
    if (NonFatal(this)) raiseError(this) else throw this
}

interface MonadThrowFx<F> : MonadFx<F> {
  override val M: MonadThrow<F>
  fun <A> monadThrow(c: suspend MonadThrowSyntax<F>.() -> A): Kind<F, A> {
    val continuation = MonadThrowContinuation<F, A>(M)
    val wrapReturn: suspend MonadThrowSyntax<F>.() -> Kind<F, A> = { just(c()) }
    wrapReturn.startCoroutine(continuation, continuation)
    return continuation.returnedMonad()
  }
}
