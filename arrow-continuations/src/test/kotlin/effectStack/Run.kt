package effectStack

import arrow.Kind
import arrow.continuations.effectStack.prompt
import arrow.core.Either
import arrow.core.EitherPartialOf
import arrow.core.Left
import arrow.core.Right
import arrow.core.Tuple4
import arrow.core.extensions.either.monad.flatten
import arrow.core.fix
import arrow.core.flatMap
import arrow.core.test.UnitSpec
import arrow.typeclasses.Monad
import io.kotlintest.shouldBe
import kotlin.coroutines.RestrictsSuspension
import kotlin.random.Random

interface Monadic<M> {
  suspend operator fun <A> Kind<M, A>.invoke(): A
}

suspend fun <E, A> either(f: suspend Monadic<EitherPartialOf<E>>.() -> A): Kind<EitherPartialOf<E>, A> = prompt {
  val m = object : Monadic<EitherPartialOf<E>> {
    override suspend fun <A> Kind<EitherPartialOf<E>, A>.invoke(): A = control { k -> fix().flatMap { k(it).fix() } }
  }

  Either.Right(f(m))
}

interface Error<E> {
  suspend fun <A> raise(e: E): A
  suspend fun <A> catch(handle: suspend Error<E>.(E) -> A, f: suspend Error<E>.() -> A): A
}

suspend fun <E, A> error(f: suspend Error<E>.() -> A): Either<E, A> = prompt {
  val p = object : Error<E> {
    override suspend fun <A> raise(e: E): A = control { Left(e) }
    override suspend fun <B> catch(handle: suspend Error<E>.(E) -> B, f: suspend Error<E>.() -> B): B =
      control { k ->
        error<E, B> { f() }.fold({ e -> error<E, B> { handle(e) }.flatMap { k(it) } }, { b -> k(b) })
      }
  }
  Right(f(p))
}

interface NonDet {
  suspend fun <B> effect(f: suspend () -> B): B
  suspend fun <A> empty(): A
  suspend fun choose(): Boolean
}

interface ListComputation  {
  suspend operator fun <C> List<C>.invoke(): C
}

suspend fun <A> list(f: suspend ListComputation.() -> A): List<A> =
  prompt {
    val p = object : ListComputation {
      override suspend fun <C> List<C>.invoke(): C =
        control { cb ->
          flatMap {
            cb(it)
          }
        }
    }

    listOf(f(p))
  }


suspend fun <A> nonDet(f: suspend NonDet.() -> A): Sequence<A> = prompt {
  val p = object : NonDet {
    override suspend fun <B> effect(f: suspend () -> B): B = control { it(f()) }
    override suspend fun choose(): Boolean = control { k -> k(true) + k(false) }
    override suspend fun <A> empty(): A = control { emptySequence() }
  }
  sequenceOf(f(p))
}

// I couldn't use a main method because intellij kept complaining about not finding the main file unless I rebuild the entire repo...
// Running tests works fine though, hence I moved it here.
class Test : UnitSpec() {
  init {
    "list" {
      list {
        val a = listOf(1, 2, 3)()
        val b = listOf("a", "b", "c")()
        "$a$b "
      } shouldBe listOf("1a ", "1b ", "1c ", "2a ", "2b ", "2c ", "3a ", "3b ", "3c ")
    }
    "testNondet" {
      nonDet {
        var sum = 0
        val b = choose()
        effect { println("PROGRAM: Here $b") }
        // stacksafe?
        for (i in 0..1000) {
          sum += effect { Random.nextInt(100) }
        }
        val i = effect { Random.nextInt() }
        effect { println("PROGRAM: Rand $i") }
        val b2 = if (b.not()) choose()
          else empty()
        effect { println("PROGRAM: Here2 $b2") }
        Tuple4(i, b, b2, sum)
      }.also { println("PROGRAM: Result ${it.toList()}") }
    }

    "testError" {
      error<String, Int> {
        catch({ e ->
          println("PROGRAM: Got error: $e")
          raise<Int>(e)
        }) {
          val rand = 10
          if (rand.rem(2) == 0) raise("No equal numbers")
          else rand
        }
      }.also { println("PROGRAM: Result $it") }
    }

    "either" {
      either<String, Int> {
        val a = Right(11)()
        if (a > 10) Left("Larger than 10")()
        else a
      }.also { println("PROGRAM: Result $it") }
    }
  }
}
