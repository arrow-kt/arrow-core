package arrow.core.extensions

import arrow.Kind
import arrow.core.Either
import arrow.core.Eval
import arrow.core.ForListK
import arrow.core.GT
import arrow.core.Ior
import arrow.core.LT
import arrow.core.ListK
import arrow.core.ListKOf
import arrow.core.Option
import arrow.core.Ordering
import arrow.core.Tuple2
import arrow.core.extensions.list.foldable.fold
import arrow.core.extensions.listk.eq.eq
import arrow.core.extensions.listk.foldable.foldLeft
import arrow.core.extensions.listk.monad.monad
import arrow.core.extensions.listk.semigroup.plus
import arrow.core.extensions.ordering.monoid.monoid
import arrow.core.fix
import arrow.core.identity
import arrow.core.k
import arrow.core.toT
import arrow.extension
import arrow.typeclasses.Align
import arrow.typeclasses.Alternative
import arrow.typeclasses.Applicative
import arrow.typeclasses.Apply
import arrow.typeclasses.Crosswalk
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Foldable
import arrow.typeclasses.Functor
import arrow.typeclasses.FunctorFilter
import arrow.typeclasses.Hash
import arrow.typeclasses.Monad
import arrow.typeclasses.MonadCombine
import arrow.typeclasses.MonadFilter
import arrow.typeclasses.MonadLogic
import arrow.typeclasses.MonadPlus
import arrow.typeclasses.MonadSyntax
import arrow.typeclasses.Monoid
import arrow.typeclasses.MonoidK
import arrow.typeclasses.Monoidal
import arrow.typeclasses.Order
import arrow.typeclasses.Semialign
import arrow.typeclasses.Semigroup
import arrow.typeclasses.SemigroupK
import arrow.typeclasses.Semigroupal
import arrow.typeclasses.Show
import arrow.typeclasses.Traverse
import arrow.typeclasses.Unalign
import arrow.typeclasses.Unzip
import arrow.typeclasses.Zip
import arrow.typeclasses.hashWithSalt
import arrow.core.combineK as listCombineK
import kotlin.collections.plus as listPlus
import kotlin.collections.zip as listZip

interface ListKSemigroup<A> : Semigroup<ListK<A>> {
  override fun ListK<A>.combine(b: ListK<A>): ListK<A> =
    (this.listPlus(b)).k()
}

interface ListKMonoid<A> : Monoid<ListK<A>>, ListKSemigroup<A> {
  override fun empty(): ListK<A> =
    emptyList<A>().k()
}

interface ListKEq<A> : Eq<ListKOf<A>> {

  fun EQ(): Eq<A>

  override fun ListKOf<A>.eqv(b: ListKOf<A>): Boolean =
    if (fix().size == b.fix().size) fix().listZip(b.fix()) { aa, bb ->
      EQ().run { aa.eqv(bb) }
    }.fold(true) { acc, bool ->
      acc && bool
    }
    else false
}

interface ListKShow<A> : Show<ListKOf<A>> {
  fun SA(): Show<A>
  override fun ListKOf<A>.show(): String = fix().show(SA())
}

interface ListKFunctor : Functor<ForListK> {
  override fun <A, B> Kind<ForListK, A>.map(f: (A) -> B): ListK<B> =
    fix().map(f)
}

interface ListKApply : Apply<ForListK> {
  override fun <A, B> Kind<ForListK, A>.ap(ff: Kind<ForListK, (A) -> B>): ListK<B> =
    fix().ap(ff)

  override fun <A, B> Kind<ForListK, A>.map(f: (A) -> B): ListK<B> =
    fix().map(f)

  override fun <A, B, Z> Kind<ForListK, A>.map2(fb: Kind<ForListK, B>, f: (Tuple2<A, B>) -> Z): ListK<Z> =
    fix().map2(fb, f)
}

interface ListKApplicative : Applicative<ForListK> {
  override fun <A, B> Kind<ForListK, A>.ap(ff: Kind<ForListK, (A) -> B>): ListK<B> =
    fix().ap(ff)

  override fun <A, B> Kind<ForListK, A>.map(f: (A) -> B): ListK<B> =
    fix().map(f)

  override fun <A, B, Z> Kind<ForListK, A>.map2(fb: Kind<ForListK, B>, f: (Tuple2<A, B>) -> Z): ListK<Z> =
    fix().map2(fb, f)

  override fun <A> just(a: A): ListK<A> =
    ListK.just(a)
}

interface ListKMonad : Monad<ForListK> {
  override fun <A, B> Kind<ForListK, A>.ap(ff: Kind<ForListK, (A) -> B>): ListK<B> =
    fix().ap(ff)

  override fun <A, B> Kind<ForListK, A>.flatMap(f: (A) -> Kind<ForListK, B>): ListK<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: Function1<A, ListKOf<Either<A, B>>>): ListK<B> =
    ListK.tailRecM(a, f)

  override fun <A, B> Kind<ForListK, A>.map(f: (A) -> B): ListK<B> =
    fix().map(f)

  // Special case, since the extension is built with List<List<A>>, then wrapped to ListK<List<A>>, there's no way for that to
  // call the default flatten which is defined for ListK<ListK<A>> due to the inner list, therefore we need to map and redirect.
  fun <A> Kind<ForListK, List<A>>.flatten(dummy: Unit = Unit): Kind<ForListK, A> =
    // explicit cast for proper resolution as ListK extends List
    (map { it.k() } as Kind<ForListK, Kind<ForListK, A>>).flatten()

  override fun <A, B, Z> Kind<ForListK, A>.map2(fb: Kind<ForListK, B>, f: (Tuple2<A, B>) -> Z): ListK<Z> =
    fix().map2(fb, f)

  override fun <A> just(a: A): ListK<A> =
    ListK.just(a)
}

interface ListKFoldable : Foldable<ForListK> {
  override fun <A, B> Kind<ForListK, A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> Kind<ForListK, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().foldRight(lb, f)

  override fun <A> Kind<ForListK, A>.isEmpty(): Boolean =
    fix().isEmpty()
}

interface ListKTraverse : Traverse<ForListK> {
  override fun <A, B> Kind<ForListK, A>.map(f: (A) -> B): ListK<B> =
    fix().map(f)

  override fun <G, A, B> Kind<ForListK, A>.traverse(AP: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, ListK<B>> =
    fix().traverse(AP, f)

  override fun <A, B> Kind<ForListK, A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> Kind<ForListK, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().foldRight(lb, f)

  override fun <A> Kind<ForListK, A>.isEmpty(): Boolean =
    fix().isEmpty()
}

interface ListKSemigroupK : SemigroupK<ForListK> {
  override fun <A> Kind<ForListK, A>.combineK(y: Kind<ForListK, A>): ListK<A> =
    fix().listCombineK(y)
}

interface ListKSemigroupal : Semigroupal<ForListK> {
  override fun <A, B> Kind<ForListK, A>.product(fb: Kind<ForListK, B>): Kind<ForListK, Tuple2<A, B>> =
    fb.fix().ap(fix().map { a: A -> { b: B -> Tuple2(a, b) } })
}

interface ListKMonoidal : Monoidal<ForListK>, ListKSemigroupal {
  override fun <A> identity(): Kind<ForListK, A> = ListK.empty()
}

interface ListKMonoidK : MonoidK<ForListK> {
  override fun <A> empty(): ListK<A> =
    ListK.empty()

  override fun <A> Kind<ForListK, A>.combineK(y: Kind<ForListK, A>): ListK<A> =
    fix().listCombineK(y)
}

interface ListKHash<A> : Hash<ListKOf<A>> {

  fun HA(): Hash<A>

  override fun ListKOf<A>.hashWithSalt(salt: Int): Int =
    HA().run { foldLeft(salt) { hash, x -> x.hashWithSalt(hash) } }.hashWithSalt(fix().size)
}

interface ListKOrder<A> : Order<ListKOf<A>> {
  fun OA(): Order<A>
  override fun ListKOf<A>.compare(b: ListKOf<A>): Ordering =
    ListK.alignWith(fix(), b.fix()) { ior -> ior.fold({ GT }, { LT }, { a1, a2 -> OA().run { a1.compare(a2) } }) }
      .fold(Ordering.monoid())
}

interface ListKFunctorFilter : FunctorFilter<ForListK> {
  override fun <A, B> Kind<ForListK, A>.filterMap(f: (A) -> Option<B>): ListK<B> =
    fix().filterMap(f)

  override fun <A, B> Kind<ForListK, A>.map(f: (A) -> B): ListK<B> =
    fix().map(f)
}

fun <A> ListK.Companion.fx(c: suspend MonadSyntax<ForListK>.() -> A): ListK<A> =
  ListK.monad().fx.monad(c).fix()

interface ListKMonadCombine : MonadCombine<ForListK>, ListKAlternative {
  override fun <A> empty(): ListK<A> =
    ListK.empty()

  override fun <A, B> Kind<ForListK, A>.filterMap(f: (A) -> Option<B>): ListK<B> =
    fix().filterMap(f)

  override fun <A, B> Kind<ForListK, A>.ap(ff: Kind<ForListK, (A) -> B>): ListK<B> =
    fix().ap(ff)

  override fun <A, B> Kind<ForListK, A>.flatMap(f: (A) -> Kind<ForListK, B>): ListK<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: Function1<A, ListKOf<Either<A, B>>>): ListK<B> =
    ListK.tailRecM(a, f)

  override fun <A, B> Kind<ForListK, A>.map(f: (A) -> B): ListK<B> =
    fix().map(f)

  override fun <A, B, Z> Kind<ForListK, A>.map2(fb: Kind<ForListK, B>, f: (Tuple2<A, B>) -> Z): ListK<Z> =
    fix().map2(fb, f)

  override fun <A> just(a: A): ListK<A> =
    ListK.just(a)
}

interface ListKMonadFilter : MonadFilter<ForListK> {
  override fun <A> empty(): ListK<A> =
    ListK.empty()

  override fun <A, B> Kind<ForListK, A>.filterMap(f: (A) -> Option<B>): ListK<B> =
    fix().filterMap(f)

  override fun <A, B> Kind<ForListK, A>.ap(ff: Kind<ForListK, (A) -> B>): ListK<B> =
    fix().ap(ff)

  override fun <A, B> Kind<ForListK, A>.flatMap(f: (A) -> Kind<ForListK, B>): ListK<B> =
    fix().flatMap(f)

  override fun <A, B> tailRecM(a: A, f: Function1<A, ListKOf<Either<A, B>>>): ListK<B> =
    ListK.tailRecM(a, f)

  override fun <A, B> Kind<ForListK, A>.map(f: (A) -> B): ListK<B> =
    fix().map(f)

  override fun <A, B, Z> Kind<ForListK, A>.map2(fb: Kind<ForListK, B>, f: (Tuple2<A, B>) -> Z): ListK<Z> =
    fix().map2(fb, f)

  override fun <A> just(a: A): ListK<A> =
    ListK.just(a)
}

interface ListKAlternative : Alternative<ForListK>, ListKApplicative {
  override fun <A> empty(): Kind<ForListK, A> = emptyList<A>().k()
  override fun <A> Kind<ForListK, A>.orElse(b: Kind<ForListK, A>): Kind<ForListK, A> =
    (this.fix() + b.fix()).k()
}

interface ListKEqK : EqK<ForListK> {
  override fun <A> Kind<ForListK, A>.eqK(other: Kind<ForListK, A>, EQ: Eq<A>) =
    (this.fix() to other.fix()).let {
      ListK.eq(EQ).run {
        it.first.eqv(it.second)
      }
    }
}

interface ListKSemialign : Semialign<ForListK>, ListKFunctor {
  override fun <A, B> align(
    a: Kind<ForListK, A>,
    b: Kind<ForListK, B>
  ): Kind<ForListK, Ior<A, B>> = ListK.align(a.fix(), b.fix())
}

interface ListKAlign : Align<ForListK>, ListKSemialign {
  override fun <A> empty(): Kind<ForListK, A> = ListK.empty()
}

interface ListKUnalign : Unalign<ForListK>, ListKSemialign {
  override fun <A, B> unalign(ior: Kind<ForListK, Ior<A, B>>): Tuple2<Kind<ForListK, A>, Kind<ForListK, B>> =
    ior.fix().let { list ->
      list.fold(emptyList<A>() toT emptyList<B>()) { (l, r), x ->
        x.fold(
          { l.listPlus(it) toT r },
          { l toT r.listPlus(it) },
          { a, b -> l.listPlus(a) toT r.listPlus(b) }
        )
      }.bimap({ it.k() }, { it.k() })
    }
}

interface ListKZip : Zip<ForListK>, ListKSemialign {
  override fun <A, B> Kind<ForListK, A>.zip(other: Kind<ForListK, B>): Kind<ForListK, Tuple2<A, B>> =
    this.fix().listZip(other.fix()).map { it.first toT it.second }.k()
}

interface ListKUnzip : Unzip<ForListK>, ListKZip {
  override fun <A, B> Kind<ForListK, Tuple2<A, B>>.unzip(): Tuple2<Kind<ForListK, A>, Kind<ForListK, B>> =
    this.fix().let { list ->
      list.fold(emptyList<A>() toT emptyList<B>()) { (l, r), x ->
        l.listPlus(x.a) toT r.listPlus(x.b)
      }
    }.bimap({ it.k() }, { it.k() })
}

interface ListKCrosswalk : Crosswalk<ForListK>, ListKFunctor, ListKFoldable {
  override fun <F, A, B> crosswalk(
    ALIGN: Align<F>,
    a: Kind<ForListK, A>,
    fa: (A) -> Kind<F, B>
  ): Kind<F, Kind<ForListK, B>> =
    a.fix().foldLeft(ALIGN.run { empty<ListK<B>>() }) { xs, x ->
      ALIGN.run {
        alignWith(fa(x), xs) { ior ->
          ior.fold(
            { ListK.just(it) },
            ::identity,
            { l, r -> ListK.just(l) + r.fix() })
        }
      }
    }
}

interface ListKMonadPlus : MonadPlus<ForListK>, ListKMonad, ListKAlternative {
  override fun <A, B> Kind<ForListK, A>.ap(ff: Kind<ForListK, (A) -> B>): ListK<B> =
    fix().ap(ff)

  override fun <A, B> Kind<ForListK, A>.map(f: (A) -> B): ListK<B> =
    fix().map(f)

  override fun <A, B, Z> Kind<ForListK, A>.map2(fb: Kind<ForListK, B>, f: (Tuple2<A, B>) -> Z): ListK<Z> =
    fix().map2(fb, f)

  override fun <A> just(a: A): ListK<A> =
    ListK.just(a)
}

interface ListKMonadLogic : MonadLogic<ForListK>, ListKMonadPlus {

  private fun <E> ListK<E>.tail(): ListK<E> = this.drop(1).k()

  override fun <A> Kind<ForListK, A>.splitM(): Kind<ForListK, Option<Tuple2<Kind<ForListK, A>, A>>> =
    this.fix().let { list ->
      if (list.isEmpty()) {
        just(Option.empty())
      } else {
        just(Option.just(Tuple2(list.tail(), list.first())))
      }
    }
}
