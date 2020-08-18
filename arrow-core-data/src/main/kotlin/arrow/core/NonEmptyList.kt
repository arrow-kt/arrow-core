package arrow.core

import arrow.Kind
import arrow.higherkind
import arrow.typeclasses.Applicative
import arrow.typeclasses.Show

typealias Nel<A> = NonEmptyList<A>

/**
 * ank_macro_hierarchy(arrow.core.NonEmptyList)
 *
 *
 * `NonEmptyList` is a data type used in __Λrrow__ to model ordered lists that guarantee to have at least one value.
 * `NonEmptyList` is available in the `arrow-core-data` module under the `import arrow.core.NonEmptyList`
 *
 * ## of
 *
 * A `NonEmptyList` guarantees the list always has at least 1 element.
 *
 * ```kotlin:ank:playground
 * import arrow.core.NonEmptyList
 *
 * val value =
 * //sampleStart
 *  // NonEmptyList.of() // does not compile
 *  NonEmptyList.of(1, 2, 3, 4, 5) // NonEmptyList<Int>
 * //sampleEnd
 * fun main() {
 *  println(value)
 * }
 * ```
 *
 * ## head
 *
 * Unlike `List[0]`, `NonEmptyList.head` it's a safe operation that guarantees no exception throwing.
 *
 * ```kotlin:ank:playground
 * import arrow.core.NonEmptyList
 *
 * val value =
 * //sampleStart
 *  NonEmptyList.of(1, 2, 3, 4, 5).head
 * //sampleEnd
 * fun main() {
 *  println(value)
 * }
 * ```
 *
 * ## foldLeft
 *
 * When we fold over a `NonEmptyList`, we turn a `NonEmptyList< A >` into `B` by providing a __seed__ value and a __function__ that carries the state on each iteration over the elements of the list.
 * The first argument is a function that addresses the __seed value__, this can be any object of any type which will then become the resulting typed value.
 * The second argument is a function that takes the current state and element in the iteration and returns the new state after transformations have been applied.
 *
 * ```kotlin:ank:playground
 * import arrow.core.NonEmptyList
 *
 * //sampleStart
 * fun sumNel(nel: NonEmptyList<Int>): Int =
 *  nel.foldLeft(0) { acc, n -> acc + n }
 * val value = sumNel(NonEmptyList.of(1, 1, 1, 1))
 * //sampleEnd
 * fun main() {
 *  println("value = $value")
 * }
 * ```
 *
 * ## map
 *
 * `map` allows us to transform `A` into `B` in `NonEmptyList< A >`
 *
 * ```kotlin:ank:playground
 * import arrow.core.NonEmptyList
 *
 * val value =
 * //sampleStart
 *  NonEmptyList.of(1, 1, 1, 1).map { it + 1 }
 * //sampleEnd
 * fun main() {
 *  println(value)
 * }
 * ```
 *
 * ## flatMap
 *
 * `flatMap` allows us to compute over the contents of multiple `NonEmptyList< * >` values
 *
 * ```kotlin:ank:playground
 * import arrow.core.NonEmptyList
 *
 * //sampleStart
 * val nelOne: NonEmptyList<Int> = NonEmptyList.of(1)
 * val nelTwo: NonEmptyList<Int> = NonEmptyList.of(2)
 *
 * val value = nelOne.flatMap { one ->
 *  nelTwo.map { two ->
 *    one + two
 *  }
 * }
 * //sampleEnd
 * fun main() {
 *  println("value = $value")
 * }
 * ```
 *
 * ## Monad binding
 *
 * Λrrow allows imperative style comprehensions to make computing over `NonEmptyList` values easy.
 *
 * ```kotlin:ank:playground
 * import arrow.core.NonEmptyList
 * import arrow.core.extensions.fx
 *
 * //sampleStart
 * val nelOne: NonEmptyList<Int> = NonEmptyList.of(1)
 * val nelTwo: NonEmptyList<Int> = NonEmptyList.of(2)
 * val nelThree: NonEmptyList<Int> = NonEmptyList.of(3)
 *
 * val value = NonEmptyList.fx {
 *  val (one) = nelOne
 *  val (two) = nelTwo
 *  val (three) = nelThree
 *  one + two + three
 * }
 * //sampleEnd
 * fun main() {
 *  println("value = $value")
 * }
 * ```
 *
 * Monad binding in `NonEmptyList` and other collection related data type can be used as generators
 *
 * ```kotlin:ank:playground
 * import arrow.core.NonEmptyList
 * import arrow.core.extensions.fx
 *
 * val value =
 * //sampleStart
 *  NonEmptyList.fx {
 *    val (x) = NonEmptyList.of(1, 2, 3)
 *    val (y) = NonEmptyList.of(1, 2, 3)
 *   x + y
 *  }
 * //sampleEnd
 * fun main() {
 *  println(value)
 * }
 * ```
 *
 * ## Applicative Builder
 *
 * Λrrow contains methods that allow you to preserve type information when computing over different `NonEmptyList` typed values.
 *
 * ```kotlin:ank:playground
 * import arrow.core.NonEmptyList
 * import java.util.UUID
 * import arrow.core.extensions.nonemptylist.apply.map
 *
 * //sampleStart
 * data class Person(val id: UUID, val name: String, val year: Int)
 *
 * // Note each NonEmptyList is of a different type
 * val nelId: NonEmptyList<UUID> = NonEmptyList.of(UUID.randomUUID(), UUID.randomUUID())
 * val nelName: NonEmptyList<String> = NonEmptyList.of("William Alvin Howard", "Haskell Curry")
 * val nelYear: NonEmptyList<Int> = NonEmptyList.of(1926, 1900)
 *
 * val value = map(nelId, nelName, nelYear) { (id, name, year) ->
 *  Person(id, name, year)
 * }
 * //sampleEnd
 * fun main() {
 *  println("value = $value")
 * }
 * ```
 *
 * ### Summary
 *
 * - `NonEmptyList` is __used to model lists that guarantee at least one element__
 * - We can easily construct values of `NonEmptyList` with `NonEmptyList.of`
 * - `foldLeft`, `map`, `flatMap` and others are used to compute over the internal contents of a `NonEmptyList` value.
 * - `fx { ... } comprehensions` can be __used to imperatively compute__ over multiple `NonEmptyList` values in sequence.
 * - `NonEmptyList.applicative().map { ... }` can be used to compute over multiple `NonEmptyList` values preserving type information and __abstracting over arity__ with `map`
 *
 * ### Supported type classes
 *
 * ```kotlin:ank:replace
 * import arrow.reflect.DataType
 * import arrow.reflect.tcMarkdownList
 * import arrow.core.NonEmptyList
 *
 * DataType(NonEmptyList::class).tcMarkdownList()
 * ```
 *
 */
@higherkind
class NonEmptyList<out A>(
  val head: A,
  val tail: List<A>
) : NonEmptyListOf<A>, AbstractList<A>() {

  private constructor(list: List<A>) : this(list[0], list.drop(1))

  override val size: Int =
    1 + tail.size

  val all: List<A>
    get() = toList()

  override operator fun get(index: Int): A {
    if (index < 0 || index >= size) throw IndexOutOfBoundsException("$index is not in 1..${size - 1}")
    return if (index == 0) head else tail[index - 1]
  }

  override fun isEmpty(): Boolean = false

  fun toList(): List<A> = listOf(head) + tail

  inline fun <B> map(f: (A) -> B): NonEmptyList<B> =
    NonEmptyList(f(head), tail.map(f))

  inline fun <B> flatMap(f: (A) -> NonEmptyListOf<B>): NonEmptyList<B> =
    f(head).fix() + tail.flatMap { f(it).fix().all }

  fun <B> ap(ff: NonEmptyListOf<(A) -> B>): NonEmptyList<B> =
    fix().flatMap { a -> ff.fix().map { f -> f(a) } }.fix()

  operator fun plus(l: NonEmptyList<@UnsafeVariance A>): NonEmptyList<A> =
    NonEmptyList(all + l.all)

  operator fun plus(l: List<@UnsafeVariance A>): NonEmptyList<A> =
    NonEmptyList(all + l)

  operator fun plus(a: @UnsafeVariance A): NonEmptyList<A> =
    NonEmptyList(all + a)

  inline fun <B> foldLeft(b: B, f: (B, A) -> B): B =
    this.fix().tail.fold(f(b, this.fix().head), f)

  fun <B> foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    all.k().foldRight(lb, f)

  fun <G, B> traverse(AG: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, NonEmptyList<B>> =
    AG.run { all.k().traverse(AG, f).map { Nel.fromListUnsafe(it) } }

  fun <B> coflatMap(f: (NonEmptyListOf<A>) -> B): NonEmptyList<B> {
    val buf = mutableListOf<B>()
    tailrec fun consume(list: List<A>): List<B> =
      if (list.isEmpty()) {
        buf
      } else {
        val tail = list.subList(1, list.size)
        buf += f(NonEmptyList(list[0], tail))
        consume(tail)
      }
    return NonEmptyList(f(this), consume(this.fix().tail))
  }

  fun extract(): A =
    this.fix().head

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (other?.javaClass != javaClass) return false

    other as NonEmptyList<*>

    if (all != other.all) return false

    return true
  }

  override fun hashCode(): Int =
    all.hashCode()

  fun show(SA: Show<A>): String =
    "NonEmptyList(${all.k().show(SA)})"

  override fun toString(): String =
    show(Show.any())

  companion object {
    operator fun <A> invoke(head: A, vararg t: A): NonEmptyList<A> =
      NonEmptyList(head, t.asList())

    fun <A> of(head: A, vararg t: A): NonEmptyList<A> =
      NonEmptyList(head, t.asList())

    fun <A> fromList(l: List<A>): Option<NonEmptyList<A>> =
      if (l.isEmpty()) None else Some(NonEmptyList(l))

    fun <A> fromListUnsafe(l: List<A>): NonEmptyList<A> =
      NonEmptyList(l)

    fun <A> just(a: A): NonEmptyList<A> =
      of(a)

    @Suppress("UNCHECKED_CAST")
    private tailrec fun <A, B> go(
      buf: ArrayList<B>,
      f: (A) -> Kind<ForNonEmptyList, Either<A, B>>,
      v: NonEmptyList<Either<A, B>>
    ) {
      val head: Either<A, B> = v.head
      when (head) {
        is Either.Right -> {
          buf += head.b
          val x = fromList(v.tail)
          when (x) {
            is Some<NonEmptyList<Either<A, B>>> -> go(buf, f, x.t)
            is None -> Unit
          }
        }
        is Either.Left -> go(buf, f, f(head.a).fix() + v.tail)
      }
    }

    fun <A, B> tailRecM(a: A, f: (A) -> Kind<ForNonEmptyList, Either<A, B>>): NonEmptyList<B> {
      val buf = ArrayList<B>()
      go(buf, f, f(a).fix())
      return fromListUnsafe(buf)
    }
  }
}

inline fun <A> A.nel(): NonEmptyList<A> =
  NonEmptyList.of(this)

fun <A, G> NonEmptyListOf<Kind<G, A>>.sequence(GA: Applicative<G>): Kind<G, NonEmptyList<A>> =
  fix().traverse(GA, ::identity)

fun <A> NonEmptyListOf<A>.combineK(y: NonEmptyListOf<A>): NonEmptyList<A> =
  fix().plus(y.fix())
