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
import io.kotlintest.data.forall
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

    "performant type checkers should match each type" {
      fun Can<Any, Any>.expectToBe(neither: Boolean = false, left: Boolean = false, right: Boolean = false, both: Boolean = false) {
        isNeither shouldBe neither
        isRight shouldBe right
        isLeft shouldBe left
        isBoth shouldBe both
      }
      Can.Neither.expectToBe(neither = true)
      Can.Right(12).expectToBe(right = true)
      Can.Left(11).expectToBe(left = true)
      Can.Both(11, 12).expectToBe(both = true)
    }

    "factory functions create the expected types" {
      Can.neither() shouldBe Can.Neither
      allEqual<Int, String>(
        { Can.left(a) to Can.Left(a) },
        { Can.right(b) to Can.Right(b) },
        { Can.Both(a, b) to Can.Both(a, b) },
        { a.toLeftCan() to Can.Left(a) },
        { b.toRightCan() to Can.Right(b) },
        { (a to b).toCan() to Can.Both(a, b) },
        { (a toT b).toCan() to Can.Both(a, b) },
        { Ior.Left(a).toCan() to Can.Left(a) },
        { Ior.Right(b).toCan() to Can.Right(b) },
        { Ior.Both(a, b).toCan() to Can.Both(a, b) },
        { None.toRightCan() to Can.Neither },
        { Some(b).toRightCan() to Can.Right(b) },
        { None.toLeftCan() to Can.Neither },
        { Some(a).toLeftCan() to Can.Left(a) }
      )
    }

    "toIor() transforms to the expected instance Ior" {
      val callback = Ior.Both("over", 9000)
      allEqual<String, Int>(
        { neither.toIor { callback } to callback },
        { left.toIor { callback } to Ior.Left(a) },
        { right.toIor { callback } to Ior.Right(b) },
        { both.toIor { callback } to Ior.Both(a, b) }
      )
    }

    "bimap() should allow modify both value" {
      allEqual<Int, String>(
        { neither.bimap({ it * 2 }, { "power of $it" }) to neither },
        { left.bimap({ it * 2 }, { "power of $it" }) to Can.Left(a * 2) },
        { right.bimap({ it * 2 }, { "power of $it" }) to Can.Right("power of $b") },
        { both.bimap({ it * 2 }, { "power of $it" }) to Can.Both(a * 2, "power of $b") }
      )
    }

    "mapLeft() should modify only left value" {
      allEqual<Int, String>(
        { neither.mapLeft { it * 2 } to neither },
        { left.mapLeft { it * 2 } to Can.Left(a * 2) },
        { right.mapLeft { it * 2 } to right },
        { both.mapLeft { it * 2 } to Can.Both(a * 2, b) }
      )
    }

    "swap() should reverse values" {
      allEqual<Int, String>(
        { neither.swap() to neither },
        { left.swap() to Can.Right(a) },
        { right.swap() to Can.Left(b) },
        { both.swap() to Can.Both(b, a) }
      )
    }

    "unwrap() should return the isomorphic either" {
      allEqual<Int, String>(
        { neither.unwrap() to None },
        { left.unwrap() to Some(Ior.Left(a)) },
        { right.unwrap() to Some(Ior.Right(b)) },
        { both.unwrap() to Some(Ior.Both(a, b)) }
      )
    }

    "pad() should return the correct Pair of Options" {
      allEqual<Int, String>(
        { neither.pad() to Pair(None, None) },
        { left.pad() to Pair(Some(a), None) },
        { right.pad() to Pair(None, Some(b)) },
        { both.pad() to Pair(Some(a), Some(b)) }
      )
    }

    "toValidated() should convert values into an Option of Validated" {
      allEqual<Int, String>(
        { neither.toValidated() to None },
        { left.toValidated() to Some(Invalid(a)) },
        { right.toValidated() to Some(Valid(b)) },
        { both.toValidated() to Some(Valid(b)) }
      )
    }

    "toValidated {} should convert values into Validated" {
      allEqual<Int, String>(
        { neither.toValidated { Invalid(a) } to Invalid(a) },
        { left.toValidated { Invalid(a) } to Invalid(a) },
        { right.toValidated { Invalid(a) } to Valid(b) },
        { both.toValidated { Invalid(a) } to Valid(b) }
      )
    }

    "toValidatedLeft() should convert values into an Option of Validated" {
      allEqual<Int, String>(
        { neither.toValidatedLeft() to None },
        { left.toValidatedLeft() to Some(Valid(a)) },
        { right.toValidatedLeft() to Some(Invalid(b)) },
        { both.toValidatedLeft() to Some(Valid(a)) }
      )
    }

    "toValidatedLeft {} should convert values into Validated" {
      allEqual<Int, String>(
        { neither.toValidatedLeft { Invalid(a) } to Invalid(a) },
        { left.toValidatedLeft { Invalid(a) } to Valid(a) },
        { right.toValidatedLeft { Invalid(a) } to Invalid(b) },
        { both.toValidatedLeft { Invalid(a) } to Valid(a) }
      )
    }

    "fromOptions() should build a correct Option<Ior>" {
      allEqual<Int, String>(
        { Can.fromOptions(Some(a), None) to left },
        { Can.fromOptions(Some(a), Some(b)) to both },
        { Can.fromOptions(None, Some(b)) to right },
        { Can.fromOptions(None, None) to neither }
      )
    }

    "getOrElse() should return right value" {
      allEqual<Int, String>(
        { neither.getOrElse { a * 2 } to a * 2 },
        { left.getOrElse { a * 2 } to a * 2 },
        { right.getOrElse { a * 2 } to b },
        { both.getOrElse { a * 2 } to b }
      )
    }

    "getLeftOrElse() should return right value" {
      allEqual<Int, String>(
        { neither.getLeftOrElse { "power" } to "power" },
        { left.getLeftOrElse { "power" } to a },
        { right.getLeftOrElse { "power" } to "power" },
        { both.getLeftOrElse { "power" } to a }
      )
    }

    "Can.monad.flatMap should combine left values" {
      val can = Can.Both(3, "Hello, world!")
      val result = intCanMonad.run { can.flatMap { Can.Left(7) } }
      result shouldBe Can.Left(10)
    }

    "Can<A, B> can be deconstructed" {
      allTrue<Int, String>(
        { neither.deconstructsTo(None, None) },
        { left.deconstructsTo(Some(a), None) },
        { right.deconstructsTo(None, Some(b)) },
        { both.deconstructsTo(Some(a), Some(b)) }
      )
    }

    "left() returns Option<A>" {
      allEqual<Int, String>(
        { neither.left() to None },
        { left.left() to Some(a) },
        { right.left() to None },
        { both.left() to Some(a) }
      )
    }

    "right() returns Option<B>" {
      allEqual<Int, String>(
        { neither.right() to None },
        { left.right() to None },
        { right.right() to Some(b) },
        { both.right() to Some(b) }
      )
    }

    "mapNotNull() falls back to Neither when transformation is null" {
      allEqual<Int, String>(
        { neither.mapNotNull { null } to neither },
        { left.mapNotNull { null } to left },
        { right.mapNotNull { null } to neither },
        { both.mapNotNull { null } to left },

        { neither.mapNotNull { "Hello $it" } to neither },
        { left.mapNotNull { "Hello $it" } to left },
        { right.mapNotNull { "Hello $it" } to Can.Right("Hello $b") },
        { both.mapNotNull { "Hello $it" } to Can.Both(a, "Hello $b") }
      )
    }

    "mapLeftNotNull() falls back to Neither when transformation is null" {
      allEqual<Int, String>(
        { neither.mapLeftNotNull { null } to neither },
        { left.mapLeftNotNull { null } to neither },
        { right.mapLeftNotNull { null } to right },
        { both.mapLeftNotNull { null } to right },

        { neither.mapLeftNotNull { it + 30 } to neither },
        { left.mapLeftNotNull { it + 30 } to Can.Left(a + 30) },
        { right.mapLeftNotNull { it + 30 } to right },
        { both.mapLeftNotNull { it + 30 } to Can.Both(a + 30, b) }
      )
    }

    "bimapNotNull() collapses null transformations" {
      allEqual<Int, String>(
        { neither.bimapNotNull({ null }, { null }) to neither },
        { left.bimapNotNull({ null }, { null }) to neither },
        { right.bimapNotNull({ null }, { null }) to neither },
        { both.bimapNotNull({ null }, { null }) to neither },

        { neither.bimapNotNull({ null }, { "max $it" }) to neither },
        { left.bimapNotNull({ null }, { "max $it" }) to neither },
        { right.bimapNotNull({ null }, { "max $it" }) to Can.Right("max $b") },
        { both.bimapNotNull({ null }, { "max $it" }) to Can.Right("max $b") },

        { neither.bimapNotNull({ it + 30 }, { null }) to neither },
        { left.bimapNotNull({ it + 30 }, { null }) to Can.Left(a + 30) },
        { right.bimapNotNull({ it + 30 }, { null }) to neither },
        { both.bimapNotNull({ it + 30 }, { null }) to Can.Left(a + 30) }
      )
    }

    "filter() removes right side when predicate fails" {
      allEqual<Int, String>(
        { neither.filter { false } to neither },
        { left.filter { false } to left },
        { right.filter { false } to neither },
        { both.filter { false } to left },

        { neither.filter { true } to neither },
        { left.filter { true } to left },
        { right.filter { true } to right },
        { both.filter { true } to both }
      )
    }

    "filterNot() removes right side when predicate passes" {
      allEqual<Int, String>(
        { neither.filterNot { false } to neither },
        { left.filterNot { false } to left },
        { right.filterNot { false } to right },
        { both.filterNot { false } to both },

        { neither.filterNot { true } to neither },
        { left.filterNot { true } to left },
        { right.filterNot { true } to neither },
        { both.filterNot { true } to left }
      )
    }

    "filterLeft() removes left side when predicate fails" {
      allEqual<Int, String>(
        { neither.filterLeft { false } to neither },
        { left.filterLeft { false } to neither },
        { right.filterLeft { false } to right },
        { both.filterLeft { false } to right },

        { neither.filterLeft { true } to neither },
        { left.filterLeft { true } to left },
        { right.filterLeft { true } to right },
        { both.filterLeft { true } to both }
      )
    }

    "filterNotLeft() removes left side when predicate passes" {
      allEqual<Int, String>(
        { neither.filterNotLeft { false } to neither },
        { left.filterNotLeft { false } to left },
        { right.filterNotLeft { false } to right },
        { both.filterNotLeft { false } to both },

        { neither.filterNotLeft { true } to neither },
        { left.filterNotLeft { true } to neither },
        { right.filterNotLeft { true } to right },
        { both.filterNotLeft { true } to right }
      )
    }

    "exists(Predicate<B>) is true when right passes predicate" {
      allEqual<Int, String>(
        { neither.exists { false } to false },
        { left.exists { false } to false },
        { right.exists { false } to false },
        { both.exists { false } to false },

        { neither.exists { true } to false },
        { left.exists { true } to false },
        { right.exists { true } to true },
        { both.exists { true } to true }
      )
    }

    "existsLeft(Predicate<A>) is true when left passes predicate" {
      allEqual<Int, String>(
        { neither.existsLeft { false } to false },
        { left.existsLeft { false } to false },
        { right.existsLeft { false } to false },
        { both.existsLeft { false } to false },

        { neither.existsLeft { true } to false },
        { left.existsLeft { true } to true },
        { right.existsLeft { true } to false },
        { both.existsLeft { true } to true }
      )
    }

  }
}

private data class CanCases<A, B>(
  val a: A,
  val b: B,
  val neither: Can<A, B>,
  val left: Can<A, B>,
  val right: Can<A, B>,
  val both: Can<A, B>
)

private inline fun <reified A, reified B> allEqual(
  vararg tests: CanCases<A, B>.() -> Pair<Any, Any>
) = forAll { a: A, b: B ->
  CanCases(a, b, Can.Neither, Can.Left(a), Can.Right(b), Can.Both(a, b)).run {
    tests.map { it() }.all { (actual, expected) ->
      actual shouldBe expected
      actual == expected
    }
  }
}

private inline fun <reified A, reified B> allTrue(
  vararg tests: CanCases<A, B>.() -> Boolean
) = forAll { a: A, b: B ->
  CanCases(a, b, Can.Neither, Can.Left(a), Can.Right(b), Can.Both(a, b)).run { tests.all { it() } }
}

private fun <A, B> Can<A, B>.deconstructsTo(a: Option<A>, b: Option<B>): Boolean {
  val (aa, bb) = this
  return a == aa && b == bb
}
