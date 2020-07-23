package arrow.core.semigroup

import arrow.core.extensions.eq
import arrow.core.extensions.hash
import arrow.core.extensions.num
import arrow.core.extensions.semigroup.product.applicative.applicative
import arrow.core.extensions.semigroup.product.eq.eq
import arrow.core.extensions.semigroup.product.eqK.eqK
import arrow.core.extensions.semigroup.product.functor.functor
import arrow.core.extensions.semigroup.product.hash.hash
import arrow.core.extensions.semigroup.product.monad.monad
import arrow.core.extensions.semigroup.product.monoid.monoid
import arrow.core.extensions.semigroup.product.show.show
import arrow.core.extensions.semigroup.product.traverse.traverse
import arrow.core.extensions.show
import arrow.core.test.UnitSpec
import arrow.core.test.generators.genK
import arrow.core.test.generators.product
import arrow.core.test.laws.EqKLaws
import arrow.core.test.laws.EqLaws
import arrow.core.test.laws.HashLaws
import arrow.core.test.laws.MonadLaws
import arrow.core.test.laws.MonoidLaws
import arrow.core.test.laws.ShowLaws
import arrow.core.test.laws.TraverseLaws
import arrow.core.test.laws.equalUnderTheLaw
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

class ProductTest : UnitSpec() {
  init {
    testLaws(
      EqLaws.laws(Product.eq(Int.eq()), Gen.int().product()),
      EqKLaws.laws(Product.eqK(), Product.genK()),
      ShowLaws.laws(Product.show(Int.show()), Product.eq(Int.eq()), Gen.int().product()),
      HashLaws.laws(Product.hash(Int.hash()), Gen.int().product(), Product.eq(Int.eq())),
      MonadLaws.laws(Product.monad(), Product.functor(), Product.applicative(), Product.monad(), Product.genK(), Product.eqK()),
      TraverseLaws.laws(Product.traverse(), Product.applicative(), Product.genK(), Product.eqK()),
      MonoidLaws.laws(Product.monoid(Int.num()), Gen.int().product(), Product.eq(Int.eq()))
    )

    "Product combines elements by multiplying them" {
      forAll { i1: Int, i2: Int ->
        (i1 * i2)
          .equalUnderTheLaw(
            Product.monoid(Int.num()).run { Product(i1) + Product(i2) }.getProduct,
            Int.eq()
          )
      }
    }
  }
}
