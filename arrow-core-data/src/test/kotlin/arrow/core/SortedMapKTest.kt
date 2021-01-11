package arrow.core

import arrow.Kind2
import arrow.core.extensions.align
import arrow.core.extensions.eq
import arrow.core.extensions.foldable
import arrow.core.extensions.hash
import arrow.core.extensions.monoid
import arrow.core.extensions.semialign
import arrow.core.extensions.show
import arrow.core.extensions.traverse
import arrow.core.extensions.unalign
import arrow.core.extensions.unzip
import arrow.core.test.UnitSpec
import arrow.core.test.generators.genK
import arrow.core.test.generators.sortedMapK
import arrow.core.test.laws.AlignLaws
import arrow.core.test.laws.HashLaws
import arrow.core.test.laws.MonoidLaws
import arrow.core.test.laws.ShowLaws
import arrow.core.test.laws.TraverseLaws
import arrow.core.test.laws.UnalignLaws
import arrow.core.test.laws.UnzipLaws
import arrow.typeclasses.Eq
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

class SortedMapKTest : UnitSpec() {

  val EQ: Eq<Kind2<ForSortedMapK, String, Int>> = object : Eq<Kind2<ForSortedMapK, String, Int>> {
    override fun Kind2<ForSortedMapK, String, Int>.eqv(b: Kind2<ForSortedMapK, String, Int>): Boolean =
      fix()["key"] == b.fix()["key"]
  }

  init {

    "can align maps" {
      val gen = Gen.sortedMapK(Gen.string(), Gen.bool())

      // aligned keySet is union of a's and b's keys
      forAll(gen, gen) { a, b ->
        SortedMapK.semialign<String>().run {
          val aligned = align(a, b).fix()

          aligned.size == (a.keys + b.keys).size
        }
      }

      // aligned map contains Both for all entries existing in a and b
      forAll(gen, gen) { a, b ->
        SortedMapK.semialign<String>().run {
          val aligned = align(a, b).fix()
          a.keys.intersect(b.keys).all {
            aligned[it]?.isBoth ?: false
          }
        }
      }

      // aligned map contains Left for all entries existing only in a
      forAll(gen, gen) { a, b ->
        SortedMapK.semialign<String>().run {
          val aligned = align(a, b).fix()
          (a.keys - b.keys).all { key ->
            aligned[key]?.let { it.isLeft } ?: false
          }
        }
      }

      // aligned map contains Right for all entries existing only in b
      forAll(gen, gen) { a, b ->
        SortedMapK.semialign<String>().run {
          val aligned = align(a, b).fix()
          (b.keys - a.keys).all { key ->
            aligned[key]?.let { it.isRight } ?: false
          }
        }
      }
    }
  }
}
