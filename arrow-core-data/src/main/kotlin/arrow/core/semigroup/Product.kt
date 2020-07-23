package arrow.core.semigroup

import arrow.higherkind

/**
 * Monoid under multiplication
 *
 * ```kotlin:ank
 * import arrow.core.semigroup.Product
 * import arrow.core.extensions.num
 * //sampleStart
 * Product.monoid(Int.num()).run {
 *   Product(1) + Product(11)
 * }.getProduct
 * //sampleEnd
 * ```
 * ```kotlin:ank
 * import arrow.core.extensions.list.foldable.foldMap
 * //sampleStart
 * listOf(1,2,3,4).foldMap(Product.monoid(Int.num()), ::Product).getProduct
 * //sampleEnd
 * ```
 */
@higherkind
data class Product<A>(val getProduct: A) : ProductOf<A> {
  companion object
}
