package arrow.core.extensions.semigroup

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.semigroup.ForProduct
import arrow.core.semigroup.Product
import arrow.core.semigroup.fix
import arrow.extension
import arrow.typeclasses.Applicative
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Foldable
import arrow.typeclasses.Functor
import arrow.typeclasses.Hash
import arrow.typeclasses.Monad
import arrow.typeclasses.Monoid
import arrow.typeclasses.Num
import arrow.typeclasses.Order
import arrow.typeclasses.Semigroup
import arrow.typeclasses.Show
import arrow.typeclasses.Traverse

@extension
interface ProductEq<A> : Eq<Product<A>> {
  fun EQA(): Eq<A>
  override fun Product<A>.eqv(b: Product<A>): Boolean = EQA().run { getProduct.eqv(b.getProduct) }
}

@extension
interface ProductEqK : EqK<ForProduct> {
  override fun <A> Kind<ForProduct, A>.eqK(other: Kind<ForProduct, A>, EQ: Eq<A>): Boolean =
    EQ.run { fix().getProduct.eqv(other.fix().getProduct) }
}

@extension
interface ProductOrder<A> : Order<Product<A>> {
  fun ORD(): Order<A>
  override fun Product<A>.compare(b: Product<A>): Int = ORD().run { getProduct.compare(b.getProduct) }
}

@extension
interface ProductShow<A> : Show<Product<A>> {
  fun SA(): Show<A>
  override fun Product<A>.show(): String = SA().run { "Product(${getProduct.show()})" }
}

@extension
interface ProductHash<A> : Hash<Product<A>>, ProductEq<A> {
  fun HA(): Hash<A>
  override fun EQA(): Eq<A> = HA()
  override fun Product<A>.hash(): Int = HA().run { getProduct.hash() }
}

@extension
interface ProductFunctor : Functor<ForProduct> {
  override fun <A, B> Kind<ForProduct, A>.map(f: (A) -> B): Kind<ForProduct, B> = Product(f(fix().getProduct))
}

@extension
interface ProductApplicative : Applicative<ForProduct>, ProductFunctor {
  override fun <A> just(a: A): Kind<ForProduct, A> = Product(a)
  override fun <A, B> Kind<ForProduct, A>.ap(ff: Kind<ForProduct, (A) -> B>): Kind<ForProduct, B> =
    Product(ff.fix().getProduct.invoke(fix().getProduct))
  override fun <A, B> Kind<ForProduct, A>.map(f: (A) -> B): Kind<ForProduct, B> = Product(f(fix().getProduct))
}

@extension
interface ProductMonad : Monad<ForProduct>, ProductApplicative {
  override fun <A, B> Kind<ForProduct, A>.flatMap(f: (A) -> Kind<ForProduct, B>): Kind<ForProduct, B> = f(fix().getProduct)

  private tailrec fun <A, B> loop(a: A, f: (A) -> Kind<ForProduct, Either<A, B>>): Kind<ForProduct, B> =
    when (val fa = f(a).fix().getProduct) {
      is Either.Left -> loop(fa.a, f)
      is Either.Right -> Product(fa.b)
    }

  override fun <A, B> tailRecM(a: A, f: (A) -> Kind<ForProduct, Either<A, B>>): Kind<ForProduct, B> = loop(a, f)

  override fun <A, B> Kind<ForProduct, A>.ap(ff: Kind<ForProduct, (A) -> B>): Kind<ForProduct, B> =
    Product(ff.fix().getProduct.invoke(fix().getProduct))
  override fun <A, B> Kind<ForProduct, A>.map(f: (A) -> B): Kind<ForProduct, B> = Product(f(fix().getProduct))
}

@extension
interface ProductFoldable : Foldable<ForProduct> {
  override fun <A, B> Kind<ForProduct, A>.foldLeft(b: B, f: (B, A) -> B): B = f(b, fix().getProduct)
  override fun <A, B> Kind<ForProduct, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    Eval.defer { f(fix().getProduct, lb) }
}

@extension
interface ProductTraverse : Traverse<ForProduct>, ProductFoldable {
  override fun <G, A, B> Kind<ForProduct, A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, Kind<ForProduct, B>> =
    AP.run { f(fix().getProduct).map(::Product) }
}

@extension
interface ProductSemigroup<A> : Semigroup<Product<A>> {
  fun NA(): Num<A>
  override fun Product<A>.combine(b: Product<A>): Product<A> = Product(NA().run { getProduct * b.getProduct })
}

@extension
interface ProductMonoid<A> : Monoid<Product<A>>, ProductSemigroup<A> {
  override fun NA(): Num<A>
  override fun empty(): Product<A> = Product(NA().run { 1L.fromLong() })
}
