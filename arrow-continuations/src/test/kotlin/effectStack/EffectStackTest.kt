package effectStack

import arrow.core.Either
import arrow.core.identity
import arrow.core.test.generators.either
import arrow.core.test.generators.nonEmptyList
import arrow.core.test.generators.throwable
import io.kotlintest.fail
import io.kotlintest.properties.Gen
import io.kotlintest.properties.PropertyContext
import io.kotlintest.properties.assertAll
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import io.kotlintest.specs.StringSpec
import kotlinx.coroutines.runBlocking

class EffectStackTest : StringSpec() {
  init {
    fun <A> forAll2(gena: Gen<A>, fn: suspend PropertyContext.(a: A) -> Boolean) {
      assertAll(gena) { a ->
        unsafeRunSync { fn(a) shouldBe true }
      }
    }

    fun <A, B> forAll2(gena: Gen<A>, genb: Gen<B>, fn: suspend PropertyContext.(a: A, b: B) -> Boolean) {
      assertAll(gena, genb) { a, b ->
        runBlocking { fn(a, b) shouldBe true }
      }
    }

    fun <A, B, C> forAll2(gena: Gen<A>, genb: Gen<B>, genc: Gen<C>, fn: suspend PropertyContext.(a: A, b: B, c: C) -> Boolean) {
      assertAll(gena, genb, genc) { a, b, c ->
        unsafeRunSync { fn(a, b, c) shouldBe true }
      }
    }


    "monadic can bind values" {
      forAll2(Gen.either(Gen.string(), Gen.int())) { value ->
        effectStack.either<String, Int> {
          value.suspend().invoke()
        } == value
      }
    }

    "monadic rethrows exceptions" {
      forAll2(Gen.int(), Gen.throwable()) { value, e ->
        shouldThrow<Throwable> {
          val r = effectStack.either<String, Int> {
            Either.Right(value).suspend().invoke()
            e.suspend()
          }

          fail("$e expected, but found $r")
        } == e
      }
    }

    "error can bind values" {
      forAll2(Gen.either(Gen.string(), Gen.int())) { value ->
        error<String, Int> {
          value.fold(
            { s -> raise(s.suspend()) },
            ::identity
          )
        } == value
      }
    }

    "error can rethrow exceptions" {
      forAll2(Gen.throwable()) { e ->
        shouldThrow<Throwable> {
          val r = error<String, Int> {
            e.suspend()
          }
          fail("$e expected, but found $r")
        } == e
      }
    }

    "list" {
      forAll2(Gen.list(Gen.int()), Gen.list(Gen.int())) { iis, sss ->
        list {
          val a = iis.suspend().invoke()
          val b = sss.suspend().invoke()
          "$a$b ".suspend()
        } == iis.flatMap { a -> sss.map { b -> "$a$b " } }
      }
    }

    "list can rethrow exceptions" {
      forAll2(Gen.nonEmptyList(Gen.int()), Gen.nonEmptyList(Gen.int()), Gen.throwable()) { iis, sss, e ->
        shouldThrow<Throwable> {
          val r = list {
            val a = iis.all.suspend().invoke()
            val b = sss.all.suspend().invoke()
            e.suspend()
            "$a$b "
          }

          fail("$e expected, but found $r")
        } == e
      }
    }
  }
}
