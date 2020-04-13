package arrow.core.extensions

import arrow.Kind
import arrow.core.Eval
import arrow.core.ForSortedMapK
import arrow.core.Ior
import arrow.core.SetK
import arrow.core.SortedMapK
import arrow.core.SortedMapKOf
import arrow.core.SortedMapKPartialOf
import arrow.core.Tuple2
import arrow.core.extensions.list.functorFilter.flattenOption
import arrow.core.extensions.set.foldable.foldLeft
import arrow.core.extensions.setk.eq.eq
import arrow.core.extensions.setk.hash.hash
import arrow.core.extensions.sortedmapk.eq.eq
import arrow.core.fix
import arrow.core.getOption
import arrow.core.k
import arrow.core.toOption
import arrow.core.toT
import arrow.core.updated
import arrow.extension
import arrow.typeclasses.Align
import arrow.typeclasses.Applicative
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Foldable
import arrow.typeclasses.Functor
import arrow.typeclasses.Hash
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semialign
import arrow.typeclasses.Semigroup
import arrow.typeclasses.Show
import arrow.typeclasses.Traverse
import arrow.typeclasses.Unalign
import arrow.typeclasses.Unzip
import arrow.typeclasses.Zip

interface SortedMapKFunctor<A : Comparable<A>> : Functor<ForSortedMapK> {
  override fun <B, C> SortedMapKPartialOf<B>.map(f: (B) -> C): SortedMapKPartialOf<C> =
    fix().map(f).unnest()
}

fun <A : Comparable<A>> SortedMapK.Companion.functor(): SortedMapKFunctor<A> =
  object : SortedMapKFunctor<A> {}

interface SortedMapKFoldable<A : Comparable<A>> : Foldable<ForSortedMapK> {
  override fun <B, C> SortedMapKPartialOf<B>.foldLeft(b: C, f: (C, B) -> C): C =
    fix().foldLeft(b, f)

  override fun <B, C> SortedMapKPartialOf<B>.foldRight(lb: Eval<C>, f: (B, Eval<C>) -> Eval<C>): Eval<C> =
    fix().foldRight(lb, f)
}

fun <A : Comparable<A>> SortedMapK.Companion.foldable(): SortedMapKFoldable<A> =
  object : SortedMapKFoldable<A> {}

interface SortedMapKTraverse<A : Comparable<A>> : Traverse<ForSortedMapK>, SortedMapKFoldable<A> {
  override fun <G, B, C> SortedMapKPartialOf<B>.traverse(AP: Applicative<G>, f: (B) -> Kind<G, C>): Kind<G, SortedMapKPartialOf<C>> =
    fix().traverse(AP, f).unnest()
}

fun <A : Comparable<A>> SortedMapK.Companion.traverse(): SortedMapKTraverse<A> =
  object : SortedMapKTraverse<A> {}

interface SortedMapKSemigroup<A : Comparable<A>, B> : Semigroup<SortedMapK<A, B>> {
  fun SG(): Semigroup<B>

  override fun SortedMapK<A, B>.combine(b: SortedMapK<A, B>): SortedMapK<A, B> =
    if (this.fix().size < b.fix().size) this.fix().foldLeft<B>(b.fix()) { my, (k, b) ->
      my.updated(k, SG().run { b.maybeCombine(my[k]) })
    }
    else b.fix().foldLeft<B>(this.fix()) { my: SortedMapK<A, B>, (k, a) -> my.updated(k, SG().run { a.maybeCombine(my[k]) }) }
}

fun <A : Comparable<A>, B> SortedMapK.Companion.semigroup(SB: Semigroup<B>): SortedMapKSemigroup<A, B> =
  object : SortedMapKSemigroup<A, B> {
    override fun SG(): Semigroup<B> = SB
  }

interface SortedMapKMonoid<A : Comparable<A>, B> : Monoid<SortedMapK<A, B>>, SortedMapKSemigroup<A, B> {
  override fun empty(): SortedMapK<A, B> = sortedMapOf<A, B>().k()
}

fun <A : Comparable<A>, B> SortedMapK.Companion.monoid(SB: Semigroup<B>): SortedMapKMonoid<A, B> =
  object : SortedMapKMonoid<A, B> {
    override fun SG(): Semigroup<B> = SB
  }

interface SortedMapKShow<A : Comparable<A>, B> : Show<SortedMapKOf<A, B>> {
  fun SA(): Show<A>
  fun SB(): Show<B>
  override fun SortedMapKOf<A, B>.show(): String = fix().show(SA(), SB())
}

fun <A : Comparable<A>, B> SortedMapK.Companion.show(SA: Show<A>, SB: Show<B>): SortedMapKShow<A, B> =
  object : SortedMapKShow<A, B> {
    override fun SA(): Show<A> = SA
    override fun SB(): Show<B> = SB
  }

@extension
interface SortedMapKEq<K : Comparable<K>, A> : Eq<SortedMapK<K, A>> {
  fun EQK(): Eq<K>

  fun EQA(): Eq<A>

  override fun SortedMapK<K, A>.eqv(b: SortedMapK<K, A>): Boolean =
    if (SetK.eq(EQK()).run { keys.k().eqv(b.keys.k()) }) {
      keys.map { key ->
        b[key]?.let {
          EQA().run { getValue(key).eqv(it) }
        } ?: false
      }.fold(true) { b1, b2 -> b1 && b2 }
    } else false
}

@extension
interface SortedMapKHash<K : Comparable<K>, A> : Hash<SortedMapK<K, A>>, SortedMapKEq<K, A> {
  fun HK(): Hash<K>
  fun HA(): Hash<A>

  override fun EQK(): Eq<K> = HK()
  override fun EQA(): Eq<A> = HA()

  // Somewhat mirrors HashMap.Node.hashCode in that the combinator there between key and value is xor
  override fun SortedMapK<K, A>.hash(): Int =
    SetK.hash(HK()).run { keys.k().hash() } xor foldLeft(1) { hash, a ->
      31 * hash + HA().run { a.hash() }
    }
}

interface SortedMapKSemialign<K : Comparable<K>> : Semialign<ForSortedMapK>, SortedMapKFunctor<K> {
  override fun <A, B> align(
    a: SortedMapKPartialOf<A>,
    b: SortedMapKPartialOf<B>
  ): SortedMapKPartialOf<Ior<A, B>> {
    val l = a.fix()
    val r = b.fix()
    val keys = l.keys + r.keys

    return keys.map { key ->
      Ior.fromOptions(l[key].toOption(), r[key].toOption()).map { key to it }
    }.flattenOption().toMap().toSortedMap().k().unnest()
  }
}

fun <K : Comparable<K>> SortedMapK.Companion.semialign(): SortedMapKSemialign<K> =
  object : SortedMapKSemialign<K> {}

interface SortedMapKAlign<K : Comparable<K>> : Align<ForSortedMapK>, SortedMapKSemialign<K> {
  override fun <A> empty(): SortedMapKPartialOf<A> =
    emptyMap<K, A>().toSortedMap().k().unnest()
}

fun <K : Comparable<K>> SortedMapK.Companion.align(): SortedMapKAlign<K> =
  object : SortedMapKAlign<K> {}

@extension
interface SortedMapKEqK<K : Comparable<K>> : EqK<ForSortedMapK> {
  fun EQK(): Eq<K>

  override fun <A> SortedMapKPartialOf<A>.eqK(other: SortedMapKPartialOf<A>, EQ: Eq<A>): Boolean =
    SortedMapK.eq(EQK(), EQ).run {
      this@eqK.nest<K>().fix().eqv(other.nest<K>().fix())
    }
}

interface SortedMapKUnalign<K : Comparable<K>> : Unalign<ForSortedMapK>, SortedMapKSemialign<K> {
  override fun <A, B> unalign(ior: SortedMapKPartialOf<Ior<A, B>>): Tuple2<SortedMapKPartialOf<A>, SortedMapKPartialOf<B>> =
    ior.fix().let { map ->
      map.entries.foldLeft(emptyMap<K, A>() toT emptyMap<K, B>()) { (ls, rs), (k, v) ->
        v.fold(
          { a -> ls.plus(k to a) toT rs },
          { b -> ls toT rs.plus(k to b) },
          { a, b -> ls.plus(k to a) toT rs.plus(k to b) })
      }.bimap({ it.toSortedMap().k().unnest<A>() }, { it.toSortedMap().k().unnest<B>() })
    }
}

fun <K : Comparable<K>> SortedMapK.Companion.unalign() = object : SortedMapKUnalign<K> {}

interface SortedMapKZip<K : Comparable<K>> : Zip<ForSortedMapK>, SortedMapKSemialign<K> {
  override fun <A, B> SortedMapKPartialOf<A>.zip(other: SortedMapKPartialOf<B>): SortedMapKPartialOf<Tuple2<A, B>> =
    (this.fix() to other.fix()).let { (ls, rs) ->
      val keys = (ls.keys.intersect(rs.keys))

      val values = keys.map { key -> ls.getOption(key).flatMap { l -> rs.getOption(key).map { key to (l toT it) } } }.flattenOption()

      return values.toMap().toSortedMap().k().unnest()
    }
}

fun <K : Comparable<K>> SortedMapK.Companion.zip() = object : SortedMapKZip<K> {}

interface SortedMapKUnzip<K : Comparable<K>> : Unzip<ForSortedMapK>, SortedMapKZip<K> {
  override fun <A, B> SortedMapKPartialOf<Tuple2<A, B>>.unzip(): Tuple2<SortedMapKPartialOf<A>, SortedMapKPartialOf<B>> =
    this.fix().let { map ->
      map.entries.fold(emptyMap<K, A>() toT emptyMap<K, B>()) { (ls, rs), (k, v) ->
        ls.plus(k to v.a) toT rs.plus(k to v.b)
      }
    }.bimap({ it.toSortedMap().k().unnest<A>() }, { it.toSortedMap().k().unnest<B>() })
}

fun <K : Comparable<K>> SortedMapK.Companion.unzip() = object : SortedMapKUnzip<K> {}
