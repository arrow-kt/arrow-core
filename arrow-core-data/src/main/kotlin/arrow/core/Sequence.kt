package arrow.core

import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup
import kotlin.sequences.plus as _plus

/**
 * Combines two structures by taking the union of their shapes and combining the elements with the given function.
 *
 * ```kotlin:ank:playground
 * import arrow.core.*
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val result =
 *    listOf("A", "B").align(listOf(1, 2, 3)) {
 *      "$it"
 *    }
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 */
fun <A, B, C> Sequence<A>.align(b: Sequence<B>, fa: (Ior<A, B>) -> C): Sequence<C> =
  this.align(b).map(fa)

/**
 * Combines two structures by taking the union of their shapes and using Ior to hold the elements.
 *
 * ```kotlin:ank:playground
 * import arrow.core.*
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val result =
 *     listOf("A", "B").align(listOf(1, 2, 3))
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 */
fun <A, B> Sequence<A>.align(b: Sequence<B>): Sequence<Ior<A, B>> =
  alignRec(this, b)

private fun <X, Y> alignRec(ls: Sequence<X>, rs: Sequence<Y>): Sequence<Ior<X, Y>> {
  val lsIterator = ls.iterator()
  val rsIterator = rs.iterator()

  return sequence {
    while (lsIterator.hasNext() && rsIterator.hasNext()) {
      yield(
        Ior.Both(
          lsIterator.next(),
          rsIterator.next()
        )
      )
    }
    while (lsIterator.hasNext()) yield(lsIterator.next().leftIor())
    while (rsIterator.hasNext()) yield(rsIterator.next().rightIor())
  }
}

fun <A, B> Sequence<A>.ap(ff: Sequence<(A) -> B>): Sequence<B> =
  flatMap { a -> ff.map { f -> f(a) } }

fun <A, B> Sequence<A>.apEval(ff: Eval<Sequence<(A) -> B>>): Eval<Sequence<B>> =
  ff.map { this.ap(it) }

fun <A> Sequence<A>.combineAll(MA: Monoid<A>): A = MA.run {
  this@combineAll.fold(empty()) { acc, a ->
    acc.combine(a)
  }
}

fun <A, B> Sequence<A>.crosswalk(f: (A) -> Sequence<B>): Sequence<Sequence<B>> =
  fold(emptySequence()) { bs, a ->
    f(a).align(bs) { ior ->
      ior.fold(
        { sequenceOf(it) },
        ::identity,
        { l, r -> sequenceOf(l)._plus(r) }
      )
    }
  }

fun <A, K, V> Sequence<A>.crosswalkMap(f: (A) -> Map<K, V>): Map<K, Sequence<V>> =
  fold(emptyMap()) { bs, a ->
    f(a).align(bs) { (_, ior) ->
      ior.fold(
        { sequenceOf(it) },
        ::identity,
        { l, r -> sequenceOf(l)._plus(r) }
      )
    }
  }

fun <A, B> Sequence<A>.crosswalkNull(f: (A) -> B?): Sequence<B>? =
  fold<A, Sequence<B>?>(emptySequence()) { bs, a ->
    Ior.fromNullables(f(a), bs)?.fold(
      { sequenceOf(it) },
      ::identity,
      { l, r -> sequenceOf(l)._plus(r) }
    )
  }

fun <E, A> Sequence<Either<E, Sequence<A>>>.flatSequenceEither(): Either<E, Sequence<A>> =
  flatTraverseEither(::identity)

fun <E, A> Sequence<Validated<E, Sequence<A>>>.flatSequenceValidated(semigroup: Semigroup<E>): Validated<E, Sequence<A>> =
  flatTraverseValidated(semigroup, ::identity)

fun <E, A, B> Sequence<A>.flatTraverseEither(f: (A) -> Either<E, Sequence<B>>): Either<E, Sequence<B>> =
  foldRight<A, Either<E, Sequence<B>>>(emptySequence<B>().right()) { a, acc ->
    f(a).ap(acc.map { bs -> { b: Sequence<B> -> b._plus(bs) } })
  }

fun <E, A, B> Sequence<A>.flatTraverseValidated(semigroup: Semigroup<E>, f: (A) -> Validated<E, Sequence<B>>): Validated<E, Sequence<B>> =
  foldRight<A, Validated<E, Sequence<B>>>(emptySequence<B>().valid()) { a, acc ->
    f(a).ap(semigroup, acc.map { bs -> { b: Sequence<B> -> b._plus(bs) } })
  }

fun <A> Sequence<Sequence<A>>.flatten(): Sequence<A> =
  flatMap(::identity)

fun <A> Sequence<A>.fold(MA: Monoid<A>): A = MA.run {
  this@fold.fold(empty()) { acc, a ->
    acc.combine(a)
  }
}

fun <A, B> Sequence<A>.foldMap(MB: Monoid<B>, f: (A) -> B): B = MB.run {
  this@foldMap.fold(empty()) { acc, a ->
    acc.combine(f(a))
  }
}

inline fun <A, B> Sequence<A>.foldRight(initial: B, operation: (A, B) -> B): B =
  toList().foldRight(initial, operation)

fun <A, B> Sequence<A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> {
  fun Iterator<A>.loop(): Eval<B> =
    if (hasNext()) f(next(), Eval.defer { loop() }) else lb
  return Eval.defer { this.iterator().loop() }
}

/**
 *  Applies [f] to an [A] inside [Iterable] and returns the [List] structure with a tuple of the [A] value and the
 *  computed [B] value as result of applying [f]
 *
 *  ```kotlin:ank:playground
 * import arrow.core.*
 *
 *  fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   listOf("Hello").fproduct { "$it World" }
 *   //sampleEnd
 *   println(result)
 *  }
 *  ```
 */
fun <A, B> Sequence<A>.fproduct(f: (A) -> B): Sequence<Pair<A, B>> =
  map { a -> a to f(a) }

fun <B> Sequence<Boolean>.ifM(ifFalse: () -> Sequence<B>, ifTrue: () -> Sequence<B>): Sequence<B> =
  flatMap { bool ->
    if (bool) ifTrue() else ifFalse()
  }

/**
 * Logical conditional. The equivalent of Prolog's soft-cut.
 * If its first argument succeeds at all, then the results will be
 * fed into the success branch. Otherwise, the failure branch is taken.
 *
 * ```kotlin:ank:playground
 * import arrow.core.*
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val result =
 *    listOf(1,2,3).ifThen(listOf("empty")) { i ->
 *      listOf("$i, ${i + 1}")
 *    }
 *   //sampleEnd
 *   println(result)
 * }
 */
fun <A, B> Sequence<A>.ifThen(fb: Sequence<B>, ffa: (A) -> Sequence<B>): Sequence<B> =
  split()?.let { (fa, a) ->
    ffa(a)._plus(fa.flatMap(ffa))
  } ?: fb

/**
 * interleave both computations in a fair way.
 *
 * ```kotlin:ank:playground
 * import arrow.core.*
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val tags = List(10) { "#" }
 *   val result =
 *    tags.interleave(listOf("A", "B", "C"))
 *   //sampleEnd
 *   println(result)
 * }
 */
fun <A> Sequence<A>.interleave(other: Sequence<A>): Sequence<A> =
  sequence {
    val lsIterator = this@interleave.iterator()
    val rsIterator = other.iterator()

    while (lsIterator.hasNext() && rsIterator.hasNext()) {
      yield(lsIterator.next())
      yield(rsIterator.next())
    }
    yieldAll(lsIterator)
    yieldAll(rsIterator)
  }

/**
 * Returns a [List<C>] containing the result of applying some transformation `(A?, B) -> C`
 * on a zip, excluding all cases where the right value is null.
 *
 * Example:
 * ```kotlin:ank:playground
 * import arrow.core.*
 *
 * //sampleStart
 * val left = listOf(1, 2).leftPadZip(listOf("a")) { l, r -> l toT r }      // Result: [Tuple2(1, "a")]
 * val right = listOf(1).leftPadZip(listOf("a", "b")) { l, r -> l toT r }   // Result: [Tuple2(1, "a"), Tuple2(null, "b")]
 * val both = listOf(1, 2).leftPadZip(listOf("a", "b")) { l, r -> l toT r } // Result: [Tuple2(1, "a"), Tuple2(2, "b")]
 * //sampleEnd
 *
 * fun main() {
 *   println("left = $left")
 *   println("right = $right")
 *   println("both = $both")
 * }
 * ```
 */
fun <A, B, C> Sequence<A>.leftPadZip(other: Sequence<B>, fab: (A?, B) -> C): Sequence<C> =
  padZip(other) { a: A?, b: B? -> b?.let { fab(a, it) } }.mapNotNull(::identity)

fun <A> Sequence<A>.many(): Sequence<Sequence<A>> =
  if (none()) sequenceOf(emptySequence())
  else map { generateSequence { it } }

/**
 * Returns a [List<Tuple2<A?, B>>] containing the zipped values of the two listKs
 * with null for padding on the left.
 *
 * Example:
 * ```kotlin:ank:playground
 * import arrow.core.*
 *
 * //sampleStart
 * val padRight = listOf(1, 2).leftPadZip(listOf("a"))        // Result: [Tuple2(1, "a")]
 * val padLeft = listOf(1).leftPadZip(listOf("a", "b"))       // Result: [Tuple2(1, "a"), Tuple2(null, "b")]
 * val noPadding = listOf(1, 2).leftPadZip(listOf("a", "b"))  // Result: [Tuple2(1, "a"), Tuple2(2, "b")]
 * //sampleEnd
 *
 * fun main() {
 *   println("left = $left")
 *   println("right = $right")
 *   println("both = $both")
 * }
 * ```
 */
fun <A, B> Sequence<A>.leftPadZip(other: Sequence<B>): Sequence<Pair<A?, B>> =
  this.leftPadZip(other) { a, b -> a to b }

fun <A, B> Sequence<A>.mapConst(b: B): Sequence<B> =
  map { b }

fun <A> Sequence<A>.once(): Sequence<A> =
  firstOrNull()?.let { sequenceOf(it) } ?: emptySequence()

/**
 * Returns a [List<Tuple2<A?, B?>>] containing the zipped values of the two lists with null for padding.
 *
 * Example:
 * ```kotlin:ank:playground
 * import arrow.core.*
 *
 * //sampleStart
 * val padRight = listOf(1, 2).padZip(listOf("a"))        // Result: [Tuple2(1, "a"), Tuple2(2, null)]
 * val padLeft = listOf(1).padZip(listOf("a", "b"))       // Result: [Tuple2(1, "a"), Tuple2(null, "b")]
 * val noPadding = listOf(1, 2).padZip(listOf("a", "b"))  // Result: [Tuple2(1, "a"), Tuple2(2, "b")]
 * //sampleEnd
 *
 * fun main() {
 *   println("padRight = $padRight")
 *   println("padLeft = $padLeft")
 *   println("noPadding = $noPadding")
 * }
 * ```
 */
fun <A, B> Sequence<A>.padZip(other: Sequence<B>): Sequence<Pair<A?, B?>> =
  align(other) { ior ->
    ior.fold(
      { it to null },
      { null to it },
      { a, b -> a to b }
    )
  }

/**
 * Returns a [ListK<C>] containing the result of applying some transformation `(A?, B?) -> C`
 * on a zip.
 *
 * Example:
 * ```kotlin:ank:playground
 * import arrow.core.*
 *
 * //sampleStart
 * val padZipRight = listOf(1, 2).padZip(listOf("a")) { l, r -> l toT r }     // Result: [Tuple2(1, "a"), Tuple2(2, null)]
 * val padZipLeft = listOf(1).padZip(listOf("a", "b")) { l, r -> l toT r }    // Result: [Tuple2(1, "a"), Tuple2(null, "b")]
 * val noPadding = listOf(1, 2).padZip(listOf("a", "b")) { l, r -> l toT r }  // Result: [Tuple2(1, "a"), Tuple2(2, "b")]
 * //sampleEnd
 *
 * fun main() {
 *   println("padZipRight = $padZipRight")
 *   println("padZipLeft = $padZipLeft")
 *   println("noPadding = $noPadding")
 * }
 * ```
 */
fun <A, B, C> Sequence<A>.padZip(other: Sequence<B>, fa: (A?, B?) -> C): Sequence<C> =
  padZip(other).map { fa(it.first, it.second) }

fun <A, B> Sequence<A>.reduceRightEvalOrNull(
  initial: (A) -> B,
  operation: (A, acc: Eval<B>) -> Eval<B>
): Eval<B?> =
  toList().reduceRightEvalOrNull(initial, operation)

fun <A> Sequence<A>.replicate(n: Int): Sequence<Sequence<A>> =
  if (n <= 0) emptySequence()
  else this.let { l -> Sequence { List(n) { l }.iterator() } }

fun <A> Sequence<A>.replicate(n: Int, MA: Monoid<A>): Sequence<A> =
  if (n <= 0) sequenceOf(MA.empty())
  else SequenceK.mapN(this@replicate, replicate(n - 1, MA)) { a, xs -> MA.run { a + xs } }

/**
 * Returns a [List<C>] containing the result of applying some transformation `(A, B?) -> C`
 * on a zip, excluding all cases where the left value is null.
 *
 * Example:
 * ```kotlin:ank:playground
 * import arrow.core.*
 *
 * //sampleStart
 * val left = listOf(1, 2).rightPadZip(listOf("a")) { l, r -> l toT r }      // Result: [Tuple2(1, "a"), Tuple2(null, "b")]
 * val right = listOf(1).rightPadZip(listOf("a", "b")) { l, r -> l toT r }   // Result: [Tuple2(1, "a")]
 * val both = listOf(1, 2).rightPadZip(listOf("a", "b")) { l, r -> l toT r } // Result: [Tuple2(1, "a"), Tuple2(2, "b")]
 * //sampleEnd
 *
 * fun main() {
 *   println("left = $left")
 *   println("right = $right")
 *   println("both = $both")
 * }
 * ```
 */
fun <A, B, C> Sequence<A>.rightPadZip(other: Sequence<B>, fa: (A, B?) -> C): Sequence<C> =
  other.leftPadZip(this) { a, b -> fa(b, a) }

/**
 * Returns a [List<Tuple2<A, B?>>] containing the zipped values of the two listKs
 * with null for padding on the right.
 *
 * Example:
 * ```kotlin:ank:playground
 * import arrow.core.*
 *
 * //sampleStart
 * val padRight = listOf(1, 2).rightPadZip(listOf("a"))        // Result: [Tuple2(1, "a"), Tuple2(2, null)]
 * val padLeft = listOf(1).rightPadZip(listOf("a", "b"))       // Result: [Tuple2(1, "a")]
 * val noPadding = listOf(1, 2).rightPadZip(listOf("a", "b"))  // Result: [Tuple2(1, "a"), Tuple2(2, "b")]
 * //sampleEnd
 *
 * fun main() {
 *   println("left = $left")
 *   println("right = $right")
 *   println("both = $both")
 * }
 * ```
 */
fun <A, B> Sequence<A>.rightPadZip(other: Sequence<B>): Sequence<Pair<A, B?>> =
  this.rightPadZip(other) { a, b -> a to b }

/**
 * aligns two structures and combine them with the given [Semigroup.combine]
 */
fun <A> Sequence<A>.salign(
  SG: Semigroup<A>,
  other: Sequence<A>
): Sequence<A> = SG.run {
  align(other) {
    it.fold(::identity, ::identity) { a, b ->
      a.combine(b)
    }
  }
}

fun <A, B> Sequence<Either<A, B>>.selectM(f: Sequence<(A) -> B>): Sequence<B> =
  flatMap { it.fold({ a -> f.map { ff -> ff(a) } }, { b -> sequenceOf(b) }) }

/**
 * Separate the inner [Either] values into the [Either.Left] and [Either.Right].
 *
 * @receiver Iterable of Validated
 * @return a tuple containing List with [Either.Left] and another List with its [Either.Right] values.
 */
fun <A, B> Sequence<Either<A, B>>.separateEither(): Pair<Sequence<A>, Sequence<B>> {
  val asep = flatMap { gab -> gab.fold({ sequenceOf(it) }, { emptySequence() }) }
  val bsep = flatMap { gab -> gab.fold({ emptySequence() }, { sequenceOf(it) }) }
  return asep to bsep
}

/**
 * Separate the inner [Validated] values into the [Validated.Invalid] and [Validated.Valid].
 *
 * @receiver Iterable of Validated
 * @return a tuple containing List with [Validated.Invalid] and another List with its [Validated.Valid] values.
 */
fun <A, B> Sequence<Validated<A, B>>.separateValidated(): Pair<Sequence<A>, Sequence<B>> {
  val asep = flatMap { gab -> gab.fold({ sequenceOf(it) }, { emptySequence() }) }
  val bsep = flatMap { gab -> gab.fold({ emptySequence() }, { sequenceOf(it) }) }
  return asep to bsep
}

fun <E, A> Sequence<Either<E, A>>.sequenceEither(): Either<E, Sequence<A>> =
  traverseEither(::identity)

fun <E> Sequence<Either<E, *>>.sequenceEither_(): Either<E, Unit> =
  traverseEither_(::identity)

fun <E, A> Sequence<Validated<E, A>>.sequenceValidated(semigroup: Semigroup<E>): Validated<E, Sequence<A>> =
  traverseValidated(semigroup, ::identity)

fun <E> Sequence<Validated<E, *>>.sequenceValidated_(semigroup: Semigroup<E>): Validated<E, Unit> =
  traverseValidated_(semigroup, ::identity)

fun <A> Sequence<A>.some(): Sequence<Sequence<A>> =
  if (none()) emptySequence()
  else map { generateSequence { it } }

/**
 * attempt to split the computation, giving access to the first result.
 *
 * ```kotlin:ank:playground
 * import arrow.core.*
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val result =
 *    listOf("A", "B", "C").split()
 *   //sampleEnd
 *   println(result)
 * }
 */
fun <A> Sequence<A>.split(): Pair<Sequence<A>, A>? =
  firstOrNull()?.let { first ->
    Pair(tail(), first)
  }

fun <A> Sequence<A>.tail(): Sequence<A> =
  drop(1)

fun <E, A, B> Sequence<A>.traverseEither(f: (A) -> Either<E, B>): Either<E, Sequence<B>> =
  foldRight<A, Either<E, Sequence<B>>>(emptySequence<B>().right()) { a, acc ->
    f(a).ap(acc.map { bs -> { b: B -> sequenceOf(b)._plus(bs) } })
  }

fun <E, A> Sequence<A>.traverseEither_(f: (A) -> Either<E, *>): Either<E, Unit> {
  val void = { _: Unit -> { _: Any? -> Unit } }
  return foldRight<A, Either<E, Unit>>(Unit.right()) { a, acc ->
    f(a).ap(acc.map(void))
  }
}

fun <E, A, B> Sequence<A>.traverseValidated(semigroup: Semigroup<E>, f: (A) -> Validated<E, B>): Validated<E, Sequence<B>> =
  foldRight<A, Validated<E, Sequence<B>>>(emptySequence<B>().valid()) { a, acc ->
    f(a).ap(semigroup, acc.map { bs -> { b: B -> sequenceOf(b)._plus(bs) } })
  }

fun <E, A> Sequence<A>.traverseValidated_(semigroup: Semigroup<E>, f: (A) -> Validated<E, *>): Validated<E, Unit> =
  foldRight<A, Validated<E, Unit>>(Unit.valid()) { a, acc ->
    f(a).ap(semigroup, acc.map { { Unit } })
  }

/**
 *  Pairs [B] with [A] returning a List<Tuple2<B, A>>
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 *
 *  fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   listOf("Hello", "Hello2").tupleLeft("World")
 *   //sampleEnd
 *   println(result)
 *  }
 *  ```
 */
fun <A, B> Sequence<A>.tupleLeft(b: B): Sequence<Pair<B, A>> =
  map { a -> b to a }

/**
 *  Pairs [A] with [B] returning a List<Tuple2<A, B>>
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 *
 *  fun main(args: Array<String>) {
 *   val result =
 *   //sampleStart
 *   listOf("Hello").tupleRight("World")
 *   //sampleEnd
 *   println(result)
 *  }
 *  ```
 */
fun <A, B> Sequence<A>.tupleRight(b: B): Sequence<Pair<A, B>> =
  map { a -> a to b }

/**
 * splits a union into its component parts.
 *
 * ```kotlin:ank:playground
 * import arrow.core.*
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val result =
 *    listOf(("A" toT 1).bothIor(), ("B" toT 2).bothIor(), "C".leftIor())
 *      .unalign()
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 */
fun <A, B> Sequence<Ior<A, B>>.unalign(): Pair<Sequence<A>, Sequence<B>> =
  fold(emptySequence<A>() to emptySequence()) { (l, r), x ->
    x.fold(
      { l._plus(it) to r },
      { l to r._plus(it) },
      { a, b -> l._plus(a) to r._plus(b) }
    )
  }

/**
 * after applying the given function, splits the resulting union shaped structure into its components parts
 *
 * ```kotlin:ank:playground
 * import arrow.core.*
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val result =
 *      listOf(1, 2, 3).unalign {
 *        it.leftIor()
 *      }
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 */
fun <A, B, C> Sequence<C>.unalign(fa: (C) -> Ior<A, B>): Pair<Sequence<A>, Sequence<B>> =
  map(fa).unalign()

fun <A, B> Sequence<Either<A, B>>.uniteEither(): Sequence<B> =
  flatMap { either ->
    either.fold({ emptySequence() }, { b -> sequenceOf(b) })
  }

fun <A, B> Sequence<Validated<A, B>>.uniteValidated(): Sequence<B> =
  flatMap { validated ->
    validated.fold({ emptySequence() }, { b -> sequenceOf(b) })
  }

/**
 * Fair conjunction. Similarly to interleave
 *
 * ```kotlin:ank:playground
 * import arrow.core.*
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val result =
 *    listOf(1,2,3).unweave { i -> listOf("$i, ${i + 1}") }
 *   //sampleEnd
 *   println(result)
 * }
 */
fun <A, B> Sequence<A>.unweave(ffa: (A) -> Sequence<B>): Sequence<B> =
  split()?.let { (fa, a) ->
    ffa(a).interleave(fa.unweave(ffa))
  } ?: emptySequence()

/**
 * unzips the structure holding the resulting elements in an `Tuple2`
 *
 * ```kotlin:ank:playground
 * import arrow.core.*
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val result =
 *      listOf("A" toT 1, "B" toT 2).k().unzip()
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 */
fun <A, B> Sequence<Pair<A, B>>.unzip(): Pair<Sequence<A>, Sequence<B>> =
  fold(emptySequence<A>() to emptySequence()) { (l, r), x ->
    l._plus(x.first) to r._plus(x.second)
  }

/**
 * after applying the given function unzip the resulting structure into its elements.
 *
 * ```kotlin:ank:playground
 * import arrow.core.*
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val result =
 *    listOf("A:1", "B:2", "C:3").k().unzip { e ->
 *      e.split(":").let {
 *        it.first() toT it.last()
 *      }
 *    }
 *   //sampleEnd
 *   println(result)
 * }
 * ```
 */
fun <A, B, C> Sequence<C>.unzip(fc: (C) -> Pair<A, B>): Pair<Sequence<A>, Sequence<B>> =
  map(fc).unzip()

fun <A> Sequence<A>.void(): Sequence<Unit> =
  mapConst(Unit)

/**
 *  Given [A] is a sub type of [B], re-type this value from Iterable<A> to Iterable<B>
 *
 *  Kind<F, A> -> Kind<F, B>
 *
 *  ```kotlin:ank:playground
 *  import arrow.core.*
 *
 *  fun main(args: Array<String>) {
 *   //sampleStart
 *   val result: Iterable<CharSequence> =
 *     listOf("Hello World").widen()
 *   //sampleEnd
 *   println(result)
 *  }
 *  ```
 */
fun <B, A : B> Sequence<A>.widen(): Sequence<B> =
  this

fun <A, B, Z> Sequence<A>.zipEval(other: Eval<Sequence<B>>): Eval<Sequence<Pair<A, B>>> =
  other.map { this.zip(it) }

fun <A, B, Z> Sequence<A>.zipEval(other: Eval<Sequence<B>>, f: (Pair<A, B>) -> Z): Eval<Sequence<Z>> =
  other.map { this.zip(it).map(f) }

fun <A> Semigroup.Companion.sequence(): Semigroup<Sequence<A>> =
  Monoid.sequence()

fun <A> Monoid.Companion.sequence(): Monoid<Sequence<A>> =
  SequenceMonoid as Monoid<Sequence<A>>

object SequenceMonoid : Monoid<Sequence<Any?>> {
  override fun empty(): Sequence<Any?> = emptySequence()
  override fun Sequence<Any?>.combine(b: Sequence<Any?>): Sequence<Any?> = this._plus(b)
}
