package arrow.core

import arrow.core.extensions.component1
import arrow.core.extensions.component2
import arrow.core.extensions.either.eqK.eqK
import arrow.core.extensions.either.semigroupK.semigroupK
import arrow.core.extensions.eq
import arrow.core.extensions.hash
import arrow.core.extensions.ior.applicative.applicative
import arrow.core.extensions.ior.bicrosswalk.bicrosswalk
import arrow.core.extensions.ior.bifunctor.bifunctor
import arrow.core.extensions.ior.bitraverse.bitraverse
import arrow.core.extensions.ior.crosswalk.crosswalk
import arrow.core.extensions.ior.eq.eq
import arrow.core.extensions.ior.eqK.eqK
import arrow.core.extensions.ior.eqK2.eqK2
import arrow.core.extensions.ior.functor.functor
import arrow.core.extensions.ior.hash.hash
import arrow.core.extensions.ior.monad.monad
import arrow.core.extensions.ior.order.order
import arrow.core.extensions.ior.semigroup.semigroup
import arrow.core.extensions.ior.show.show
import arrow.core.extensions.ior.traverse.traverse
import arrow.core.extensions.option.eq.eq
import arrow.core.extensions.order
import arrow.core.extensions.semigroup
import arrow.core.extensions.show
import arrow.core.test.UnitSpec
import arrow.core.test.generators.genK
import arrow.core.test.generators.genK2
import arrow.core.test.generators.ior
import arrow.core.test.laws.BicrosswalkLaws
import arrow.core.test.laws.BifunctorLaws
import arrow.core.test.laws.BitraverseLaws
import arrow.core.test.laws.CrosswalkLaws
import arrow.core.test.laws.EqK2Laws
import arrow.core.test.laws.HashLaws
import arrow.core.test.laws.MonadLaws
import arrow.core.test.laws.OrderLaws
import arrow.core.test.laws.SemigroupKLaws
import arrow.core.test.laws.SemigroupLaws
import arrow.core.test.laws.ShowLaws
import arrow.core.test.laws.TraverseLaws
import arrow.typeclasses.Monad
import io.kotlintest.forAll
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe

class IorTest : UnitSpec() {

  init {

    val intIorMonad: Monad<IorPartialOf<Int>> = Ior.monad(Int.semigroup())

    val EQ = Ior.eq(String.eq(), Int.eq())
    val GEN = Gen.ior(Gen.string(), Gen.int())

    testLaws(
      EqK2Laws.laws(Ior.eqK2(), Ior.genK2()),
      BifunctorLaws.laws(Ior.bifunctor(), Ior.genK2(), Ior.eqK2()),
      ShowLaws.laws(Ior.show(String.show(), Int.show()), EQ, GEN),
      SemigroupLaws.laws(Ior.semigroup(String.semigroup(), Int.semigroup()), GEN, EQ),
      MonadLaws.laws(
        Ior.monad(Int.semigroup()),
        Ior.functor(),
        Ior.applicative(Int.semigroup()),
        Ior.monad(Int.semigroup()),
        Ior.genK(Gen.int()),
        Ior.eqK(Int.eq())
      ),
      TraverseLaws.laws(Ior.traverse(), Ior.applicative(Int.semigroup()), Ior.genK(Gen.int()), Ior.eqK(Int.eq())),
      HashLaws.laws(Ior.hash(String.hash(), Int.hash()), Gen.ior(Gen.string(), Gen.int()), Ior.eq(String.eq(), Int.eq())),
      OrderLaws.laws(Ior.order(String.order(), Int.order()), Gen.ior(Gen.string(), Gen.int())),
      BitraverseLaws.laws(Ior.bitraverse(), Ior.genK2(), Ior.eqK2()),
      SemigroupKLaws.laws(Either.semigroupK(), Either.genK(Gen.int().map(::Some)), Either.eqK(Option.eq(Int.eq()))),
      CrosswalkLaws.laws(Ior.crosswalk(), Ior.genK(Gen.int()), Ior.eqK(Int.eq())),
      BicrosswalkLaws.laws(Ior.bicrosswalk(), Ior.genK2(), Ior.eqK2())
    )

    "bimap() should allow modify both value" {
      forAll { a: Int, b: String ->
        Ior.Right(b).bimap({ "5" }, { a * 2 }) == Ior.Right(a * 2) &&
          Ior.Left(a).bimap({ a * 3 }, { "5" }) == Ior.Left(a * 3) &&
          Ior.Both(a, b).bimap({ 2 }, { "power of $it" }) == Ior.Both(2, "power of $b")
      }
    }

    "mapLeft() should modify only left value" {
      forAll { a: Int, b: String ->
        Ior.Right(b).mapLeft { a * 2 } == Ior.Right(b) &&
          Ior.Left(a).mapLeft { b } == Ior.Left(b) &&
          Ior.Both(a, b).mapLeft { "power of $it" } == Ior.Both("power of $a", b)
      }
    }

    "swap() should interchange value" {
      forAll { a: Int, b: String ->
        Ior.Both(a, b).swap() == Ior.Both(b, a)
      }
    }

    "swap() should interchange entity" {
      forAll { a: Int ->
        Ior.Left(a).swap() == Ior.Right(a) &&
          Ior.Right(a).swap() == Ior.Left(a)
      }
    }

    "unwrap() should return the isomorphic either" {
      forAll { a: Int, b: String ->
        Ior.Left(a).unwrap() == Either.Left(Either.Left(a)) &&
          Ior.Right(b).unwrap() == Either.Left(Either.Right(b)) &&
          Ior.Both(a, b).unwrap() == Either.Right(Pair(a, b))
      }
    }

    "pad() should return the correct Pair of Options" {
      forAll { a: Int, b: String ->
        Ior.Left(a).pad() == Pair(Some(a), None) &&
          Ior.Right(b).pad() == Pair(None, Some(b)) &&
          Ior.Both(a, b).pad() == Pair(Some(a), Some(b))
      }
    }

    "padNull() should return the correct Pair of nullables" {
      forAll { a: Int, b: String ->
        Ior.Left(a).padNull() == Pair(a, null) &&
          Ior.Right(b).padNull() == Pair(null, b) &&
          Ior.Both(a, b).padNull() == Pair(a, b)
      }
    }

    "toEither() should convert values into a valid Either" {
      forAll { a: Int, b: String ->
        Ior.Left(a).toEither() == Either.Left(a) &&
          Ior.Right(b).toEither() == Either.Right(b) &&
          Ior.Both(a, b).toEither() == Either.Right(b)
      }
    }

    "toOption() should convert values into a valid Option" {
      forAll { a: Int, b: String ->
        Ior.Left(a).toOption() == None &&
          Ior.Right(b).toOption() == Some(b) &&
          Ior.Both(a, b).toOption() == Some(b)
      }
    }

    "orNull() should convert right values into a nullable" {
      forAll { a: Int, b: String ->
        Ior.Left(a).orNull() == null &&
          Ior.Right(b).orNull() == b &&
          Ior.Both(a, b).orNull() == b
      }
    }

    "leftOrNull() should convert left values into a nullable" {
      forAll { a: Int, b: String ->
        Ior.Left(a).leftOrNull() == a &&
          Ior.Right(b).leftOrNull() == null &&
          Ior.Both(a, b).leftOrNull() == a
      }
    }

    "toValidated() should convert values into a valid Validated" {
      forAll { a: Int, b: String ->
        Ior.Left(a).toValidated() == Invalid(a) &&
          Ior.Right(b).toValidated() == Valid(b) &&
          Ior.Both(a, b).toValidated() == Valid(b)
      }
    }

    "fromOptions() should build a correct Option<Ior>" {
      forAll { a: Int, b: String ->
        Ior.fromOptions(Some(a), None) == Some(Ior.Left(a)) &&
          Ior.fromOptions(Some(a), Some(b)) == Some(Ior.Both(a, b)) &&
          Ior.fromOptions(None, Some(b)) == Some(Ior.Right(b)) &&
          Ior.fromOptions(None, None) == None
      }
    }

    "fromNullables() should build a correct Ior" {
      forAll { a: Int, b: String ->
        Ior.fromNullables(a, null) == Ior.Left(a) &&
          Ior.fromNullables(a, b) == Ior.Both(a, b) &&
          Ior.fromNullables(null, b) == Ior.Right(b) &&
          Ior.fromNullables(null, null) == null
      }
    }

    "getOrElse() should return value" {
      forAll { a: Int, b: Int ->
        Ior.Right(a).getOrElse { b } == a &&
          Ior.Left(a).getOrElse { b } == b &&
          Ior.Both(a, b).getOrElse { a * 2 } == b
      }
    }

    "Ior.monad.flatMap should combine left values" {
      val ior1 = Ior.Both(3, "Hello, world!")
      val iorResult = intIorMonad.run { ior1.flatMap { Ior.Left(7) } }
      iorResult shouldBe Ior.Left(10)
    }

    "combine cases for Semigroup" {
      fun case(a: Ior<String, Int>, b: Ior<String, Int>, result: Ior<String, Int>) = listOf(a, b, result)
      Ior.semigroup(String.semigroup(), Int.semigroup()).run {
        forAll(
          listOf(
            case("Hello, ".leftIor(), Ior.Left("Arrow!"), Ior.Left("Hello, Arrow!")),
            case(Ior.Left("Hello"), Ior.Right(2020), Ior.Both("Hello", 2020)),
            case(Ior.Left("Hello, "), Ior.Both("number", 1), Ior.Both("Hello, number", 1)),
            case(Ior.Right(9000), Ior.Left("Over"), Ior.Both("Over", 9000)),
            case(Ior.Right(9000), Ior.Right(1), Ior.Right(9001)),
            case(Ior.Right(8000), Ior.Both("Over", 1000), Ior.Both("Over", 9000)),
            case(Ior.Both("Hello ", 1), Ior.Left("number"), Ior.Both("Hello number", 1)),
            case(Ior.Both("Hello number", 1), Ior.Right(1), Ior.Both("Hello number", 2)),
            case(Ior.Both("Hello ", 1), Ior.Both("number", 1), Ior.Both("Hello number", 2))
          )
        ) { (a, b, expectedResult) ->
          a + b shouldBe expectedResult
        }
      }
    }

    "destructuring declarations" {
      data class Case(val ior: Ior<String, Int>, val left: String?, val right: Int?)
      forAll(
        listOf(
          Case(Ior.Left("Hey!"), "Hey!", null),
          Case(Ior.Right(2020), null, 2020),
          Case(Ior.Both("Hey!", 2020), "Hey!", 2020)
        )
      ) { (ior, expectedLeft, expectedRight) ->
        val (actualLeft, actualRight) = ior
        actualLeft shouldBe expectedLeft
        actualRight shouldBe expectedRight
      }
    }
  }
}
