package arrow.core.extensions

import arrow.core.test.UnitSpec
import arrow.core.test.generators.byte
import arrow.core.test.generators.byteSmall
import arrow.core.test.generators.doubleSmall
import arrow.core.test.generators.floatSmall
import arrow.core.test.generators.intSmall
import arrow.core.test.generators.longSmall
import arrow.core.test.generators.short
import arrow.core.test.generators.shortSmall
import arrow.core.test.laws.HashLaws
import arrow.core.test.laws.MonoidLaws
import arrow.core.test.laws.SemiringLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.Hash
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semiring
import io.kotest.property.Arb
import io.kotest.property.forAll

class NumberInstancesTest : UnitSpec() {

  fun <F> testAllLaws(SG: Semiring<F>, M: Monoid<F>, HF: Hash<F>, GEN: Arb<F>, EQ: Eq<F>) {
    testLaws(SemiringLaws.laws(SG, GEN, EQ))
    testLaws(MonoidLaws.laws(M, GEN, EQ))
    testLaws(HashLaws.laws(HF, GEN, EQ))
  }

  init {
    testAllLaws(Byte.semiring(), Byte.monoid(), Byte.hash(), Arb.byteSmall(), Byte.eq())
    testAllLaws(Double.semiring(), Double.monoid(), Double.hash(), Arb.doubleSmall(), Double.eq())
    testAllLaws(Int.semiring(), Int.monoid(), Int.hash(), Arb.intSmall(), Int.eq())
    testAllLaws(Short.semiring(), Short.monoid(), Short.hash(), Arb.shortSmall(), Short.eq())
    testAllLaws(Float.semiring(), Float.monoid(), Float.hash(), Arb.floatSmall(), Float.eq())
    testAllLaws(Long.semiring(), Long.monoid(), Long.hash(), Arb.longSmall(), Long.eq())

    /** Semigroup specific instance check */

    "should semigroup with the instance passed - int" {
      forAll { value: Int ->
        val seen = Int.monoid().run { value.combine(value) }
        val expected = value + value

        expected == seen
      }
    }

    "should semigroup with the instance passed - float" {
      forAll(Arb.numericFloats()) { value: Float ->
        val seen = Float.monoid().run { value.combine(value) }
        val expected = value + value

        expected == seen
      }
    }

    "should semigroup with the instance passed - double" {
      forAll(Arb.numericDoubles()) { value: Double ->
        val seen = Double.monoid().run { value.combine(value) }
        val expected = value + value

        expected == seen
      }
    }

    "should semigroup with the instance passed - long" {
      forAll { value: Long ->
        val seen = Long.monoid().run { value.combine(value) }
        val expected = value + value

        expected == seen
      }
    }

    "should semigroup with the instance passed - short" {
      forAll(Arb.short()) { value: Short ->
        val seen = Short.monoid().run { value.combine(value) }
        val expected = (value + value).toShort()

        expected == seen
      }
    }

    "should semigroup with the instance passed - byte" {
      forAll(Arb.byte()) { value: Byte ->
        val seen = Byte.monoid().run { value.combine(value) }
        val expected = (value + value).toByte()

        expected == seen
      }
    }
  }
}
