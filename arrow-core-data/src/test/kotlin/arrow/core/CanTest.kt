package arrow.core

import arrow.core.extensions.can.applicative.applicative
import arrow.core.extensions.can.bicrosswalk.bicrosswalk
import arrow.core.extensions.can.bifunctor.bifunctor
import arrow.core.extensions.can.bitraverse.bitraverse
import arrow.core.extensions.can.eq.eq
import arrow.core.extensions.can.eqK.eqK
import arrow.core.extensions.can.eqK2.eqK2
import arrow.core.extensions.can.functor.functor
import arrow.core.extensions.can.hash.hash
import arrow.core.extensions.can.monad.monad
import arrow.core.extensions.can.show.show
import arrow.core.extensions.can.traverse.traverse
import arrow.core.extensions.eq
import arrow.core.extensions.hash
import arrow.core.extensions.semigroup
import arrow.core.extensions.show
import arrow.core.test.UnitSpec
import arrow.core.test.generators.can
import arrow.core.test.generators.genK
import arrow.core.test.generators.genK2
import arrow.core.test.laws.BicrosswalkLaws
import arrow.core.test.laws.BifunctorLaws
import arrow.core.test.laws.BitraverseLaws
import arrow.core.test.laws.EqK2Laws
import arrow.core.test.laws.HashLaws
import arrow.core.test.laws.MonadLaws
import arrow.core.test.laws.ShowLaws
import arrow.core.test.laws.TraverseLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.Monad
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe

class CanTest : UnitSpec() {

  init {

    val intCanMonad: Monad<CanPartialOf<Int>> = Can.monad(Int.semigroup())

    val EQ = Can.eq(Eq.any(), Eq.any())

    testLaws(
      EqK2Laws.laws(Can.eqK2(), Can.genK2()),
      BifunctorLaws.laws(Can.bifunctor(), Can.genK2(), Can.eqK2()),
      ShowLaws.laws(Can.show(String.show(), Int.show()), EQ, Gen.can(Gen.string(), Gen.int())),
      MonadLaws.laws(
        Can.monad(Int.semigroup()),
        Can.functor(),
        Can.applicative(Int.semigroup()),
        Can.monad(Int.semigroup()),
        Can.genK(Gen.int()),
        Can.eqK(Int.eq())
      ),
      TraverseLaws.laws(Can.traverse(),
        Can.applicative(Int.semigroup()),
        Can.genK(Gen.int()),
        Can.eqK(Int.eq())
      ),
      HashLaws.laws(Can.hash(String.hash(), Int.hash()), Gen.can(Gen.string(), Gen.int()), Can.eq(String.eq(), Int.eq())),
      BitraverseLaws.laws(Can.bitraverse(), Can.genK2(), Can.eqK2()),
      BicrosswalkLaws.laws(Can.bicrosswalk(), Can.genK2(), Can.eqK2())
    )

    "bimap() should allow modify both value" {
      forAll { a: Int, b: String ->
        Can.Right(b).bimap({ "5" }, { a * 2 }) == Can.Right(a * 2) &&
          Can.Left(a).bimap({ a * 3 }, { "5" }) == Can.Left(a * 3) &&
          Can.Both(a, b).bimap({ 2 }, { "power of $it" }) == Can.Both(2, "power of $b")
      }
    }

    "mapLeft() should modify only left value" {
      forAll { a: Int, b: String ->
        Can.Right(b).mapLeft { a * 2 } == Can.Right(b) &&
          Can.Left(a).mapLeft { b } == Can.Left(b) &&
          Can.Both(a, b).mapLeft { "power of $it" } == Can.Both("power of $a", b)
      }
    }

    "swap() should interchange value" {
      forAll { a: Int, b: String ->
        Can.Both(a, b).swap() == Can.Both(b, a)
      }
    }

    "swap() should interchange entity" {
      forAll { a: Int ->
        Can.Left(a).swap() == Can.Right(a) &&
          Can.Right(a).swap() == Can.Left(a)
      }
    }

    "unwrap() should return the isomorphic either" {
      forAll { a: Int, b: String ->
        Can.Left(a).unwrap() == Option.just(Ior.Left(a)) &&
          Can.Right(b).unwrap() == Option.just(Ior.Right(b)) &&
          Can.Both(a, b).unwrap() == Option.just(Ior.Both(a, b))
      }
    }

    "pad() should return the correct Pair of Options" {
      forAll { a: Int, b: String ->
        Can.Left(a).pad() == Pair(Some(a), None) &&
          Can.Right(b).pad() == Pair(None, Some(b)) &&
          Can.Both(a, b).pad() == Pair(Some(a), Some(b))
      }
    }

    "toEither() should convert values into a valid Either" {
      forAll { a: Int, b: String ->
        Can.Left(a).toEither() == Option.just(Either.Left(a)) &&
          Can.Right(b).toEither() == Option.just(Either.Right(b)) &&
          Can.Both(a, b).toEither() == Option.just(Either.Right(b))
      }
    }

    "toOption() should convert values into a valid Option" {
      forAll { a: Int, b: String ->
        Can.Left(a).toOption() == None &&
          Can.Right(b).toOption() == Some(b) &&
          Can.Both(a, b).toOption() == Some(b)
      }
    }

    "toValidated() should convert values into a valid Validated" {
      forAll { a: Int, b: String ->
        Can.Left(a).toValidated() == Invalid(a) &&
          Can.Right(b).toValidated() == Valid(b) &&
          Can.Both(a, b).toValidated() == Valid(b)
      }
    }

    "fromOptions() should build a correct Option<Ior>" {
      forAll { a: Int, b: String ->
        Can.fromOptions(Some(a), None) == Can.Left(a) &&
          Can.fromOptions(Some(a), Some(b)) == Can.Both(a, b) &&
          Can.fromOptions(None, Some(b)) == Can.Right(b) &&
          Can.fromOptions(None, None) == Can.None
      }
    }

    "getOrElse() should return value" {
      forAll { a: Int, b: Int ->
        Can.Right(a).getOrElse { b } == a &&
          Can.Left(a).getOrElse { b } == b &&
          Can.Both(a, b).getOrElse { a * 2 } == b
      }
    }

    "Can.monad.flatMap should combine left values" {
      val can = Can.Both(3, "Hello, world!")
      val result = intCanMonad.run { can.flatMap { Can.Left(7) } }
      result shouldBe Can.Left(10)
    }
  }
}
