package arrow.core.test.generators

import arrow.Kind
import arrow.core.Const
import arrow.core.Either
import arrow.core.Endo
import arrow.core.Eval
import arrow.core.Failure
import arrow.core.Id
import arrow.core.Ior
import arrow.core.Left
import arrow.core.ListK
import arrow.core.MapK
import arrow.core.NonEmptyList
import arrow.core.Option
import arrow.core.Right
import arrow.core.SequenceK
import arrow.core.SetK
import arrow.core.SortedMapK
import arrow.core.Success
import arrow.core.Try
import arrow.core.Tuple10
import arrow.core.Tuple2
import arrow.core.Tuple3
import arrow.core.Tuple4
import arrow.core.Tuple5
import arrow.core.Tuple6
import arrow.core.Tuple7
import arrow.core.Tuple8
import arrow.core.Tuple9
import arrow.core.Validated
import arrow.core.extensions.sequence.functorFilter.filterMap
import arrow.core.extensions.sequencek.apply.apply
import arrow.core.extensions.sequencek.functorFilter.filterMap
import arrow.core.k
import arrow.core.toOption
import arrow.typeclasses.Applicative
import arrow.typeclasses.ApplicativeError
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.Sample
import io.kotest.property.arbitrary.DoubleShrinker
import io.kotest.property.arbitrary.FloatShrinker
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.arb
import io.kotest.property.arbitrary.bind
import io.kotest.property.arbitrary.choice
import io.kotest.property.arbitrary.create
import io.kotest.property.arbitrary.filter
import io.kotest.property.arbitrary.flatMap
import io.kotest.property.arbitrary.list
import io.kotest.property.arbitrary.long
import io.kotest.property.arbitrary.map
import io.kotest.property.arbitrary.of
import io.kotest.property.arbitrary.orNull
import io.kotest.property.arbitrary.set

fun Arb.Companion.short(): Arb<Short> =
  Arb.int(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).map { it.toShort() }

fun Arb.Companion.byte(): Arb<Byte> =
  Arb.int(Byte.MIN_VALUE.toInt(), Byte.MAX_VALUE.toInt()).map { it.toByte() }

fun <F, A> Arb<A>.applicative(AP: Applicative<F>): Arb<Kind<F, A>> =
  map { AP.just(it) }

fun <F, A, E> Arb.Companion.applicativeError(genA: Arb<A>, errorGen: Arb<E>, AP: ApplicativeError<F, E>): Arb<Kind<F, A>> =
  Arb.choice(genA.map(::Right), errorGen.map(::Left)).map {
    it.fold(AP::raiseError, AP::just)
  }

fun <F, A> Arb<A>.applicativeError(AP: ApplicativeError<F, Throwable>): Arb<Kind<F, A>> =
  Arb.applicativeError(this, Arb.throwable(), AP)

fun <A, B> Arb.Companion.functionAToB(gen: Arb<B>): Arb<(A) -> B> = gen.map { b: B -> { _: A -> b } }

fun <A> Arb.Companion.functionAAToA(gen: Arb<A>): Arb<(A, A) -> A> = gen.map { a: A -> { _: A, _: A -> a } }

fun <A, B> Arb.Companion.functionBAToB(gen: Arb<B>): Arb<(B, A) -> B> = gen.map { b: B -> { _: B, _: A -> b } }

fun <A, B> Arb.Companion.functionABToB(gen: Arb<B>): Arb<(A, B) -> B> = gen.map { b: B -> { _: A, _: B -> b } }

fun <A> Arb.Companion.functionToA(gen: Arb<A>): Arb<() -> A> = gen.map { a: A -> { a } }

fun Arb.Companion.throwable(): Arb<Throwable> =
  Arb.of(RuntimeException(), NoSuchElementException(), IllegalArgumentException())

fun Arb.Companion.fatalThrowable(): Arb<Throwable> =
  Arb.of(ThreadDeath(), StackOverflowError(), OutOfMemoryError(), InterruptedException())

fun Arb.Companion.doubleSmall(): Arb<Double> =
  arb(DoubleShrinker, listOf(0.0)) { it.random.nextInt(100).toDouble() }

fun Arb.Companion.floatSmall(): Arb<Float> =
  arb(FloatShrinker, listOf(0F)) { it.random.nextInt(100).toFloat() }

fun Arb.Companion.intSmall(): Arb<Int> =
  Arb.choice(Arb.int(Int.MIN_VALUE / 10000, -1), Arb.int(0, Int.MAX_VALUE / 10000))

fun Arb.Companion.byteSmall(): Arb<Byte> =
  Arb.choice(Arb.int(Byte.MIN_VALUE / 10, -1), Arb.int(0, Byte.MAX_VALUE / 10)).map { it.toByte() }

fun Arb.Companion.shortSmall(): Arb<Short> =
  Arb.choice(Arb.int(Short.MIN_VALUE / 1000, -1), Arb.int(0, Short.MAX_VALUE / 1000)).map { it.toShort() }

fun Arb.Companion.longSmall(): Arb<Long> =
  Arb.choice(Arb.long(Long.MIN_VALUE / 100000L, -1L), Arb.long(0L, Long.MAX_VALUE / 100000L))

fun <A, B> Arb.Companion.tuple2(genA: Arb<A>, genB: Arb<B>): Arb<Tuple2<A, B>> =
  Arb.bind(genA, genB) { a: A, b: B -> Tuple2(a, b) }

fun <A, B, C> Arb.Companion.tuple3(genA: Arb<A>, genB: Arb<B>, genC: Arb<C>): Arb<Tuple3<A, B, C>> =
  Arb.bind(genA, genB, genC) { a: A, b: B, c: C -> Tuple3(a, b, c) }

fun <A, B, C, D> Arb.Companion.tuple4(genA: Arb<A>, genB: Arb<B>, genC: Arb<C>, genD: Arb<D>): Arb<Tuple4<A, B, C, D>> =
  Arb.bind(genA, genB, genC, genD) { a: A, b: B, c: C, d: D -> Tuple4(a, b, c, d) }

fun <A, B, C, D, E> Arb.Companion.tuple5(genA: Arb<A>, genB: Arb<B>, genC: Arb<C>, genD: Arb<D>, genE: Arb<E>): Arb<Tuple5<A, B, C, D, E>> =
  Arb.bind(genA, genB, genC, genD, genE) { a: A, b: B, c: C, d: D, e: E -> Tuple5(a, b, c, d, e) }

fun <A, B, C, D, E, F> Arb.Companion.tuple6(genA: Arb<A>, genB: Arb<B>, genC: Arb<C>, genD: Arb<D>, genE: Arb<E>, genF: Arb<F>): Arb<Tuple6<A, B, C, D, E, F>> =
  Arb.bind(genA, genB, genC, genD, genE, genF) { a: A, b: B, c: C, d: D, e: E, f: F -> Tuple6(a, b, c, d, e, f) }

fun <A, B, C, D, E, F, G> Arb.Companion.tuple7(genA: Arb<A>, genB: Arb<B>, genC: Arb<C>, genD: Arb<D>, genE: Arb<E>, genF: Arb<F>, genG: Arb<G>): Arb<Tuple7<A, B, C, D, E, F, G>> =
  Arb.bind(genA, genB, genC, genD, genE, genF, genG) { a: A, b: B, c: C, d: D, e: E, f: F, g: G -> Tuple7(a, b, c, d, e, f, g) }

fun <A, B, C, D, E, F, G, H> Arb.Companion.tuple8(genA: Arb<A>, genB: Arb<B>, genC: Arb<C>, genD: Arb<D>, genE: Arb<E>, genF: Arb<F>, genG: Arb<G>, genH: Arb<H>): Arb<Tuple8<A, B, C, D, E, F, G, H>> =
  Arb.bind(Arb.tuple7(genA, genB, genC, genD, genE, genF, genG), genH) { tuple: Tuple7<A, B, C, D, E, F, G>, h: H -> Tuple8(tuple.a, tuple.b, tuple.c, tuple.d, tuple.e, tuple.f, tuple.g, h) }

fun <A, B, C, D, E, F, G, H, I> Arb.Companion.tuple9(genA: Arb<A>, genB: Arb<B>, genC: Arb<C>, genD: Arb<D>, genE: Arb<E>, genF: Arb<F>, genG: Arb<G>, genH: Arb<H>, genI: Arb<I>): Arb<Tuple9<A, B, C, D, E, F, G, H, I>> =
  Arb.bind(Arb.tuple8(genA, genB, genC, genD, genE, genF, genG, genH), genI) { tuple: Tuple8<A, B, C, D, E, F, G, H>, i: I -> Tuple9(tuple.a, tuple.b, tuple.c, tuple.d, tuple.e, tuple.f, tuple.g, tuple.h, i) }

fun <A, B, C, D, E, F, G, H, I, J> Arb.Companion.tuple10(genA: Arb<A>, genB: Arb<B>, genC: Arb<C>, genD: Arb<D>, genE: Arb<E>, genF: Arb<F>, genG: Arb<G>, genH: Arb<H>, genI: Arb<I>, genJ: Arb<J>): Arb<Tuple10<A, B, C, D, E, F, G, H, I, J>> =
  Arb.bind(Arb.tuple9(genA, genB, genC, genD, genE, genF, genG, genH, genI), genJ) { tuple: Tuple9<A, B, C, D, E, F, G, H, I>, j: J -> Tuple10(tuple.a, tuple.b, tuple.c, tuple.d, tuple.e, tuple.f, tuple.g, tuple.h, tuple.i, j) }

fun Arb.Companion.nonZeroInt(): Arb<Int> =
  Arb.int().filter { it != 0 }

fun Arb.Companion.intPredicate(): Arb<(Int) -> Boolean> =
  Arb.nonZeroInt().flatMap { num ->
    val absNum = Math.abs(num)
    listOf<(Int) -> Boolean>(
      { it > num },
      { it <= num },
      { it % absNum == 0 },
      { it % absNum == absNum - 1 }
    )
  }

fun <A> Arb.Companion.endo(gen: Arb<A>): Arb<Endo<A>> = gen.map { a: A -> Endo<A> { a } }

fun <B> Arb.Companion.option(gen: Arb<B>): Arb<Option<B>> =
  gen.orNull().map { it.toOption() }

fun <E, A> Arb.Companion.either(genE: Arb<E>, genA: Arb<A>): Arb<Either<E, A>> {
  val genLeft = genE.map { Left(it) }
  val genRight = genA.map { Right(it) }
  return Arb.choice(genLeft, genRight)
}

fun <E, A> Arb<E>.or(genA: Arb<A>): Arb<Either<E, A>> =
  Arb.either(this, genA)

fun <E, A> Arb.Companion.validated(genE: Arb<E>, genA: Arb<A>): Arb<Validated<E, A>> =
  Arb.either(genE, genA).map { Validated.fromEither(it) }

fun <A> Arb.Companion.`try`(genA: Arb<A>, genThrowable: Arb<Throwable> = throwable()): Arb<Try<A>> =
  Arb.either(genThrowable, genA).map { it.fold({ Failure(it) }, { Success(it) }) }

fun <A> Arb.Companion.nonEmptyList(gen: Arb<A>): Arb<NonEmptyList<A>> = object : Arb<NonEmptyList<A>>() {
  override fun edgecases(): List<NonEmptyList<A>> = gen.edgecases().map { NonEmptyList(it) }

  override fun values(rs: RandomSource): Sequence<Sample<NonEmptyList<A>>> =
    gen.values(rs).flatMap { head ->
      Arb.list(gen).values(rs).map {
        NonEmptyList(head.value, it.value)
      }
    }.map { Sample(it) }
}

fun <K : Comparable<K>, V> Arb.Companion.sortedMapK(genK: Arb<K>, genV: Arb<V>): Arb<SortedMapK<K, V>> =
  Arb.bind(genK, genV) { k: K, v: V -> sortedMapOf(k to v) }.map { it.k() }

fun <K, V> Arb.Companion.mapK(genK: Arb<K>, genV: Arb<V>): Arb<MapK<K, V>> =
  Arb.map(genK, genV).map { it.k() }

fun <A> Arb.Companion.listK(genA: Arb<A>): Arb<ListK<A>> =
  Arb.list(genA).map { it.k() }

fun <A> Arb.Companion.sequenceK(genA: Arb<A>): Arb<SequenceK<A>> =
  Arb.list(genA).map { it.asSequence().k() }

fun <A> Arb.Companion.genSetK(genA: Arb<A>): Arb<SetK<A>> =
  Arb.set(genA).map { it.k() }

fun Arb.Companion.unit(): Arb<Unit> =
  Arb.create { Unit }

fun <T> Arb.Companion.id(gen: Arb<T>): Arb<Id<T>> =
  gen.map { Id.just(it) }

fun <A, B> Arb.Companion.ior(genA: Arb<A>, genB: Arb<B>): Arb<Ior<A, B>> =
  arb(
    (genA.orNull().edgecases().asSequence().k() to genB.orNull().edgecases().asSequence().k()).let { (ls, rs) ->
      SequenceK.apply().run { ls.product(rs) }.filterMap {
        Ior.fromOptions(Option.fromNullable(it.a), Option.fromNullable(it.b))
      }.toList()
    }
  ) {
    (Arb.option(genA).values(it) to Arb.option(genB).values(it)).let { (ls, rs) ->
      ls.zip(rs).filterMap {
        Ior.fromOptions(it.first.value, it.second.value)
      }
    }
  }

fun <A, B> Arb.Companion.genConst(gen: Arb<A>): Arb<Const<A, B>> =
  gen.map { Const<A, B>(it) }

fun <A> Arb<A>.eval(): Arb<Eval<A>> =
  map { Eval.just(it) }

fun Arb.Companion.char(): Arb<Char> =
  Arb.of(('A'..'Z') + ('a'..'z') + ('0'..'9') + "!@#$%%^&*()_-~`,<.?/:;}{][±§".toList())
