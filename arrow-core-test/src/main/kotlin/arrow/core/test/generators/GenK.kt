package arrow.core.test.generators

import arrow.Kind
import arrow.core.Const
import arrow.core.ConstPartialOf
import arrow.core.Either
import arrow.core.EitherPartialOf
import arrow.core.ForId
import arrow.core.ForListK
import arrow.core.ForNonEmptyList
import arrow.core.ForOption
import arrow.core.ForSequenceK
import arrow.core.ForSetK
import arrow.core.ForTry
import arrow.core.Id
import arrow.core.Ior
import arrow.core.IorPartialOf
import arrow.core.ListK
import arrow.core.MapK
import arrow.core.MapKPartialOf
import arrow.core.NonEmptyList
import arrow.core.Option
import arrow.core.SequenceK
import arrow.core.SetK
import arrow.core.SortedMapK
import arrow.core.SortedMapKPartialOf
import arrow.core.Success
import arrow.core.Try
import arrow.core.Validated
import arrow.core.ValidatedPartialOf
import io.kotest.property.Arb

interface GenK<F> {
  /**
   * lifts a Arb<A> to the context F. the resulting Arb can be used to create types Kind<F, A>
   */
  fun <A> genK(gen: Arb<A>): Arb<Kind<F, A>>
}

private val DEFAULT_COLLECTION_MAX_SIZE = 100

fun Option.Companion.genK() = object : GenK<ForOption> {
  override fun <A> genK(gen: Arb<A>): Arb<Kind<ForOption, A>> =
    Arb.option(gen) as Arb<Kind<ForOption, A>>
}

fun Id.Companion.genK() = object : GenK<ForId> {
  override fun <A> genK(gen: Arb<A>): Arb<Kind<ForId, A>> =
    Arb.id(gen) as Arb<Kind<ForId, A>>
}

fun ListK.Companion.genK(withMaxSize: Int = DEFAULT_COLLECTION_MAX_SIZE) = object : GenK<ForListK> {
  override fun <A> genK(gen: Arb<A>): Arb<Kind<ForListK, A>> =
    Arb.listK(gen).filter { it.size <= withMaxSize } as Arb<Kind<ForListK, A>>
}

fun NonEmptyList.Companion.genK(withMaxSize: Int = DEFAULT_COLLECTION_MAX_SIZE) = object : GenK<ForNonEmptyList> {
  override fun <A> genK(gen: Arb<A>): Arb<Kind<ForNonEmptyList, A>> =
    Arb.nonEmptyList(gen).filter { it.size <= withMaxSize } as Arb<Kind<ForNonEmptyList, A>>
}

fun SequenceK.Companion.genK() = object : GenK<ForSequenceK> {
  override fun <A> genK(gen: Arb<A>): Arb<Kind<ForSequenceK, A>> =
    Arb.sequenceK(gen) as Arb<Kind<ForSequenceK, A>>
}

fun <K> MapK.Companion.genK(kgen: Arb<K>, withMaxSize: Int = DEFAULT_COLLECTION_MAX_SIZE) =
  object : GenK<MapKPartialOf<K>> {
    override fun <A> genK(gen: Arb<A>): Arb<Kind<MapKPartialOf<K>, A>> =
      Arb.mapK(kgen, gen).filter { it.size <= withMaxSize } as Arb<Kind<MapKPartialOf<K>, A>>
  }

fun <K : Comparable<K>> SortedMapK.Companion.genK(kgen: Arb<K>, withMaxSize: Int = DEFAULT_COLLECTION_MAX_SIZE) =
  object : GenK<SortedMapKPartialOf<K>> {
    override fun <A> genK(gen: Arb<A>): Arb<Kind<SortedMapKPartialOf<K>, A>> =
      Arb.sortedMapK(kgen, gen) as Arb<Kind<SortedMapKPartialOf<K>, A>>
  }

fun SetK.Companion.genK(withMaxSize: Int = DEFAULT_COLLECTION_MAX_SIZE) = object : GenK<ForSetK> {
  override fun <A> genK(gen: Arb<A>): Arb<Kind<ForSetK, A>> =
    Arb.genSetK(gen).filter { it.size <= withMaxSize } as Arb<Kind<ForSetK, A>>
}

fun <A> Ior.Companion.genK(kgen: Arb<A>) =
  object : GenK<IorPartialOf<A>> {
    override fun <B> genK(gen: Arb<B>): Arb<Kind<IorPartialOf<A>, B>> =
      Arb.ior(kgen, gen) as Arb<Kind<IorPartialOf<A>, B>>
  }

fun <A> Either.Companion.genK(genA: Arb<A>) =
  object : GenK<EitherPartialOf<A>> {
    override fun <B> genK(gen: Arb<B>): Arb<Kind<EitherPartialOf<A>, B>> =
      Arb.either(genA, gen) as Arb<Kind<EitherPartialOf<A>, B>>
  }

fun <E> Validated.Companion.genK(genE: Arb<E>) =
  object : GenK<ValidatedPartialOf<E>> {
    override fun <A> genK(gen: Arb<A>): Arb<Kind<ValidatedPartialOf<E>, A>> =
      Arb.validated(genE, gen) as Arb<Kind<ValidatedPartialOf<E>, A>>
  }

fun <A> Const.Companion.genK(genA: Arb<A>) = object : GenK<ConstPartialOf<A>> {
  override fun <T> genK(gen: Arb<T>): Arb<Kind<ConstPartialOf<A>, T>> =
    genA.map {
      Const<A, T>(it)
    }
}

fun Try.Companion.genK() = object : GenK<ForTry> {
  override fun <A> genK(gen: Arb<A>): Arb<Kind<ForTry, A>> =
    Arb.oneOf(
      gen.map {
        Success(it)
      }, Arb.throwable().map { Try.Failure(it) })
}
