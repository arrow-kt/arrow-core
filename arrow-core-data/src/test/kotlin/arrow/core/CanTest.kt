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
        Can.Neither.bimap({ "5" }, { a * 2 }) == Can.Neither &&
          Can.Right(b).bimap({ "5" }, { a * 2 }) == Can.Right(a * 2) &&
          Can.Left(a).bimap({ a * 3 }, { "5" }) == Can.Left(a * 3) &&
          Can.Both(a, b).bimap({ 2 }, { "power of $it" }) == Can.Both(2, "power of $b")
      }
    }

    "mapLeft() should modify only left value" {
      forAll { a: Int, b: String ->
        Can.Neither.mapLeft { a * 2 } == Can.Neither &&
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

    "swap() should not change when empty" {
      Can.Neither.swap() shouldBe Can.Neither
    }

    "unwrap() should return the isomorphic either" {
      forAll { a: Int, b: String ->
        Can.Neither.unwrap() == None &&
          Can.Left(a).unwrap() == Some(Ior.Left(a)) &&
          Can.Right(b).unwrap() == Some(Ior.Right(b)) &&
          Can.Both(a, b).unwrap() == Some(Ior.Both(a, b))
      }
    }

    "pad() should return the correct Pair of Options" {
      forAll { a: Int, b: String ->
        Can.Neither.pad() == Pair(None, None) &&
          Can.Left(a).pad() == Pair(Some(a), None) &&
          Can.Right(b).pad() == Pair(None, Some(b)) &&
          Can.Both(a, b).pad() == Pair(Some(a), Some(b))
      }
    }

    "toValidated() should convert values into a valid Validated" {
      forAll { a: Int, b: String ->
        Can.Neither.toValidated() == None &&
          Can.Left(a).toValidated() == Some(Invalid(a)) &&
          Can.Right(b).toValidated() == Some(Valid(b)) &&
          Can.Both(a, b).toValidated() == Some(Valid(b))
      }
    }

    "fromOptions() should build a correct Option<Ior>" {
      forAll { a: Int, b: String ->
        Can.fromOptions(Some(a), None) == Can.Left(a) &&
          Can.fromOptions(Some(a), Some(b)) == Can.Both(a, b) &&
          Can.fromOptions(None, Some(b)) == Can.Right(b) &&
          Can.fromOptions(None, None) == Can.Neither
      }
    }

    "getOrElse() should return right value" {
      forAll { a: Int, b: Int ->
        Can.Neither.getOrElse { a } == a &&
          Can.Right(b).getOrElse { a } == b &&
          Can.Left(a).getOrElse { b } == b &&
          Can.Both(a, b).getOrElse { b * 2 } == b
      }
    }

    "getLeftOrElse() should return right value" {
      forAll { a: Int, b: Int ->
        Can.Neither.getLeftOrElse { a } == a &&
          Can.Right(b).getLeftOrElse { a } == a &&
          Can.Left(a).getLeftOrElse { b } == a &&
          Can.Both(a, b).getLeftOrElse { b * 2 } == a
      }
    }

    "Can.monad.flatMap should combine left values" {
      val can = Can.Both(3, "Hello, world!")
      val result = intCanMonad.run { can.flatMap { Can.Left(7) } }
      result shouldBe Can.Left(10)
    }

    "Can<A, B> can be deconstructed" {
      forAll { a: Int, b: Int ->
        Can.Neither.deconstructsTo(None, None) &&
          Can.Left(a).deconstructsTo(Some(a), None) &&
          Can.Right(b).deconstructsTo(None, Some(b)) &&
          Can.Both(a, b).deconstructsTo(Some(a), Some(b))
      }
    }

    "left() returns Option<A>" {
      forAll { a: Int, b: Int ->
        Can.Neither.left() == None &&
          Can.Left(a).left() == Some(a) &&
          Can.Right(b).left() == None &&
          Can.Both(a, b).left() == Some(a)
      }
    }

    "right() returns Option<B>" {
      forAll { a: Int, b: Int ->
        Can.Neither.right() == None &&
          Can.Left(a).right() == None &&
          Can.Right(b).right() == Some(b) &&
          Can.Both(a, b).right() == Some(b)
      }
    }

    "Ior.toCan creates Can<A, B> from Ior<A, B>" {
      forAll { a: Int, b: Int ->
        Ior.Left(a).toCan() == Can.Left(a) &&
          Ior.Right(b).toCan() == Can.Right(b) &&
          Ior.Both(a, b).toCan() == Can.Both(a, b)
      }
    }

    "Option.toRightCan() creates Can<A, B> from Option<B>" {
      forAll { b: Int ->
        Option.empty<Int>().toRightCan() == Can.Neither &&
          Some(b).toRightCan() == Can.Right(b)
      }
    }

    "Option.toLeftCan() creates Can<A, B> from Option<A>" {
      forAll { a: Int ->
        Option.empty<Int>().toLeftCan() == Can.Neither &&
          Some(a).toLeftCan() == Can.Left(a)
      }
    }

    "B?.toRightCan() creates Can<A, B> from nullable B" {
      forAll { b: Int ->
        (null as Int?).toRightCan() == Can.Neither &&
          b.toRightCan() == Can.Right(b)
      }
    }

    "A?.toLeftCan() creates Can<A, B> from nullable A" {
      forAll { a: Int ->
        (null as Int?).toLeftCan() == Can.Neither &&
          a.toLeftCan() == Can.Left(a)
      }
    }

    "Pair<A, B>.toCan() creates Can.Both<A, B>" {
      ("a" to 1).toCan() shouldBe Can.Both("a", 1)
    }

    "Tuple2<A, B>.toCan() creates Can.Both<A, B>" {
      ("a" toT 1).toCan() shouldBe Can.Both("a", 1)
    }

  }
}

private fun <A, B> Can<A, B>.deconstructsTo(a: Option<A>, b: Option<B>): Boolean {
  val (aa, bb) = this
  return a == aa && b == bb
}
