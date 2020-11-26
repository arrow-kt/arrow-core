package arrow.core.extensions

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.ForNonEmptyList
import arrow.core.Ior
import arrow.core.ListK
import arrow.core.Nel
import arrow.core.NonEmptyList
import arrow.core.NonEmptyListOf
import arrow.core.Ordering
import arrow.core.Tuple2
import arrow.core.extensions.listk.eq.eq
import arrow.core.extensions.listk.hash.hash
import arrow.core.extensions.listk.order.order
import arrow.core.extensions.nonemptylist.monad.monad
import arrow.core.fix
import arrow.core.k
import arrow.core.leftIor
import arrow.core.rightIor
import arrow.core.toT
import arrow.extension
import arrow.typeclasses.Applicative
import arrow.typeclasses.Apply
import arrow.typeclasses.Bimonad
import arrow.typeclasses.Comonad
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Foldable
import arrow.typeclasses.Functor
import arrow.typeclasses.Hash
import arrow.typeclasses.Monad
import arrow.typeclasses.MonadSyntax
import arrow.typeclasses.Order
import arrow.typeclasses.Reducible
import arrow.typeclasses.Semialign
import arrow.typeclasses.Semigroup
import arrow.typeclasses.SemigroupK
import arrow.typeclasses.Show
import arrow.typeclasses.Traverse
import arrow.typeclasses.Unzip
import arrow.typeclasses.Zip
import arrow.core.combineK as nelCombineK

@extension
interface NonEmptyListSemigroup<A> : Semigroup<NonEmptyList<A>> {
  override fun NonEmptyList<A>.combine(b: NonEmptyList<A>): NonEmptyList<A> = this + b
}

@extension
interface NonEmptyListEq<A> : Eq<NonEmptyList<A>> {

  fun EQ(): Eq<A>

  override fun NonEmptyList<A>.eqv(b: NonEmptyList<A>): Boolean =
    all.zip(b.all) { aa, bb -> EQ().run { aa.eqv(bb) } }.fold(true) { acc, bool ->
      acc && bool
    }
}

@extension
interface NonEmptyListShow<A> : Show<NonEmptyList<A>> {
  fun SA(): Show<A>
  override fun NonEmptyList<A>.show(): String = show(SA())
}

@extension
interface NonEmptyListFunctor : Functor<ForNonEmptyList> {
  override fun <A, B> NonEmptyListOf<A>.map(f: (A) -> B): NonEmptyList<B> =
    fix().map(f)
}

@extension
interface NonEmptyListApply : Apply<ForNonEmptyList> {
  override fun <A, B> NonEmptyListOf<A>.ap(ff: NonEmptyListOf<(A) -> B>): NonEmptyList<B> =
    fix().ap(ff)

  override fun <A, B> NonEmptyListOf<A>.map(f: (A) -> B): NonEmptyList<B> =
    fix().map(f)
}

@extension
interface NonEmptyListApplicative : Applicative<ForNonEmptyList> {
  override fun <A, B> NonEmptyListOf<A>.ap(ff: NonEmptyListOf<(A) -> B>): NonEmptyList<B> =
    fix().ap(ff)

  override fun <A, B> NonEmptyListOf<A>.map(f: (A) -> B): NonEmptyList<B> =
    fix().map(f)

  override fun <A> just(a: A): NonEmptyList<A> =
    NonEmptyList.just(a)
}

@extension
interface NonEmptyListMonad : Monad<ForNonEmptyList> {
  override fun <A, B> NonEmptyListOf<A>.ap(ff: NonEmptyListOf<(A) -> B>): NonEmptyList<B> =
    fix().ap(ff)

  override fun <A, B> NonEmptyListOf<A>.flatMap(f: (A) -> NonEmptyListOf<B>): NonEmptyList<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: (A) -> NonEmptyListOf<Either<A, B>>): NonEmptyList<B> =
    NonEmptyList.tailRecM(a, f)

  override fun <A, B> NonEmptyListOf<A>.map(f: (A) -> B): NonEmptyList<B> =
    fix().map(f)

  override fun <A> just(a: A): NonEmptyList<A> =
    NonEmptyList.just(a)
}

@extension
interface NonEmptyListComonad : Comonad<ForNonEmptyList> {
  override fun <A, B> NonEmptyListOf<A>.coflatMap(f: (NonEmptyListOf<A>) -> B): NonEmptyList<B> =
    fix().coflatMap(f)

  override fun <A> NonEmptyListOf<A>.extract(): A =
    fix().extract()

  override fun <A, B> NonEmptyListOf<A>.map(f: (A) -> B): NonEmptyList<B> =
    fix().map(f)
}

@extension
interface NonEmptyListBimonad : Bimonad<ForNonEmptyList> {
  override fun <A, B> NonEmptyListOf<A>.ap(ff: NonEmptyListOf<(A) -> B>): NonEmptyList<B> =
    fix().ap(ff)

  override fun <A, B> NonEmptyListOf<A>.flatMap(f: (A) -> NonEmptyListOf<B>): NonEmptyList<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: (A) -> NonEmptyListOf<Either<A, B>>): NonEmptyList<B> =
    NonEmptyList.tailRecM(a, f)

  override fun <A, B> NonEmptyListOf<A>.map(f: (A) -> B): NonEmptyList<B> =
    fix().map(f)

  override fun <A> just(a: A): NonEmptyList<A> =
    NonEmptyList.just(a)

  override fun <A, B> NonEmptyListOf<A>.coflatMap(f: (NonEmptyListOf<A>) -> B): NonEmptyList<B> =
    fix().coflatMap(f)

  override fun <A> NonEmptyListOf<A>.extract(): A =
    fix().extract()
}

@extension
interface NonEmptyListFoldable : Foldable<ForNonEmptyList> {
  override fun <A, B> NonEmptyListOf<A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> NonEmptyListOf<A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().foldRight(lb, f)

  override fun <A> NonEmptyListOf<A>.isEmpty(): Boolean =
    fix().isEmpty()
}

@extension
interface NonEmptyListTraverse : Traverse<ForNonEmptyList> {
  override fun <A, B> NonEmptyListOf<A>.map(f: (A) -> B): NonEmptyList<B> =
    fix().map(f)

  override fun <G, A, B> NonEmptyListOf<A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, NonEmptyList<B>> =
    fix().traverse(AP, f)

  override fun <A, B> NonEmptyListOf<A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> NonEmptyListOf<A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().foldRight(lb, f)

  override fun <A> NonEmptyListOf<A>.isEmpty(): Boolean =
    fix().isEmpty()
}

@extension
interface NonEmptyListSemigroupK : SemigroupK<ForNonEmptyList> {
  override fun <A> NonEmptyListOf<A>.combineK(y: NonEmptyListOf<A>): NonEmptyList<A> =
    fix().nelCombineK(y)
}

@extension
interface NonEmptyListHash<A> : Hash<NonEmptyList<A>> {
  fun HA(): Hash<A>

  override fun NonEmptyList<A>.hashWithSalt(salt: Int): Int =
    HA().run { head.hashWithSalt(ListK.hash(HA()).run { tail.k().hashWithSalt(salt) }) }
}

@extension
interface NonEmptyListOrder<A> : Order<NonEmptyList<A>> {
  fun OA(): Order<A>
  override fun NonEmptyList<A>.compare(b: NonEmptyList<A>): Ordering =
    ListK.order(OA()).run { all.k().compare(b.all.k()) }
}

fun <F, A> Reducible<F>.toNonEmptyList(fa: Kind<F, A>): NonEmptyList<A> =
  fa.reduceRightTo({ a -> NonEmptyList.of(a) }, { a, lnel ->
    lnel.map { nonEmptyList -> NonEmptyList(a, listOf(nonEmptyList.head) + nonEmptyList.tail) }
  }).value()

fun <A> NonEmptyList.Companion.fx(c: suspend MonadSyntax<ForNonEmptyList>.() -> A): NonEmptyList<A> =
  NonEmptyList.monad().fx.monad(c).fix()

@extension
interface NonEmptyListEqK : EqK<ForNonEmptyList> {
  override fun <A> Kind<ForNonEmptyList, A>.eqK(other: Kind<ForNonEmptyList, A>, EQ: Eq<A>) =
    (this.fix() to other.fix()).let {
      ListK.eq(EQ).run { it.first.all.k().eqv(it.second.all.k()) }
    }
}

@extension
interface NonEmptyListSemialign : Semialign<ForNonEmptyList>, NonEmptyListFunctor {
  override fun <A, B> align(
    a: Kind<ForNonEmptyList, A>,
    b: Kind<ForNonEmptyList, B>
  ): Kind<ForNonEmptyList, Ior<A, B>> =
    NonEmptyList.fromListUnsafe(alignRec(a.fix().all, b.fix().all))

  private fun <X, Y> alignRec(ls: List<X>, rs: List<Y>): List<Ior<X, Y>> = when {
    ls.isEmpty() -> rs.map { it.rightIor() }
    rs.isEmpty() -> ls.map { it.leftIor() }
    else -> listOf(Ior.Both(ls.first(), rs.first())) + alignRec(ls.drop(1), rs.drop(1))
  }
}

@extension
interface NonEmptyListZip : Zip<ForNonEmptyList>, NonEmptyListSemialign {
  override fun <A, B> Kind<ForNonEmptyList, A>.zip(other: Kind<ForNonEmptyList, B>): Kind<ForNonEmptyList, Tuple2<A, B>> =
    (this.fix() to other.fix()).let { nel ->
      Nel.fromListUnsafe(nel.first.all.zip(nel.second.all).map { it.first toT it.second })
    }
}

@extension
interface NonEmptyListUnzip : Unzip<ForNonEmptyList>, NonEmptyListZip {
  override fun <A, B> Kind<ForNonEmptyList, Tuple2<A, B>>.unzip(): Tuple2<Kind<ForNonEmptyList, A>, Kind<ForNonEmptyList, B>> =
    this.fix().all.let { list ->
      list.fold(emptyList<A>() toT emptyList<B>()) { (ls, rs), x ->
        ls + x.a toT rs + x.b
      }
    }.bimap({ Nel.fromListUnsafe(it) }, { Nel.fromListUnsafe(it) })
}
