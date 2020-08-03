package arrow.continuations.generic.effect

import arrow.core.Either

interface Raise<E> {
  suspend fun raise(e: E): Nothing
}

interface Catch<E> {
  /**
   * This is not easy to implement. Best attempt is probably going:
   * reset<Either<E, A>> {
   *   val raiseEff = eitherRaise(this@reset) // define handler for raise here
   *   f(raiseEff)
   * }.fold({ e -> hdl() }, ::identity)
   * This runs into another problem though:
   *  reset as of now is not supporting nested scopes in terms of its runloop or multishot.
   */
  suspend fun <A> catch(f: suspend Raise<E>.() -> A, hdl: suspend (E) -> A): A
}

interface Error<E> : Raise<E>, Catch<E>

interface Empty {
  suspend fun empty(): Nothing
}

interface Choose {
  suspend fun choose(): Boolean
}

suspend inline fun <A> Choose.choice(f: () -> A, g: () -> A): A = if (choose()) f() else g()

interface NonDet : Choose, Empty
