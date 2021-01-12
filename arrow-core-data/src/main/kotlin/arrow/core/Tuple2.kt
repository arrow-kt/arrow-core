@file:JvmMultifileClass
@file:JvmName("TupleNKt")

package arrow.core

import arrow.typeclasses.Eq
import arrow.typeclasses.Hash
import arrow.typeclasses.Monoid
import arrow.typeclasses.Order
import arrow.typeclasses.Show

class ForTuple2 private constructor() {
  companion object
}
typealias Tuple2Of<A, B> = arrow.Kind2<ForTuple2, A, B>
typealias Tuple2PartialOf<A> = arrow.Kind<ForTuple2, A>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <A, B> Tuple2Of<A, B>.fix(): Tuple2<A, B> =
  this as Tuple2<A, B>

data class Tuple2<out A, out B>(val a: A, val b: B) : Tuple2Of<A, B> {

  @Deprecated("Functor hierarchy for Tuple2 is deprecated", ReplaceWith("Tuple2(this.a, f(this.b))", "arrow.core.Tuple2"))
  fun <C> map(f: (B) -> C) =
    a toT f(b)

  @Deprecated("BiFunctor hierarchy for Tuple2 is deprecated", ReplaceWith("Tuple2(fl(this.a), fr(this.b))", "arrow.core.Tuple2"))
  fun <C, D> bimap(fl: (A) -> C, fr: (B) -> D) =
    fl(a) toT fr(b)

  @Deprecated("Apply hierarchy for Tuple2 is deprecated", ReplaceWith("Tuple2(this.a, f.b(this.b))", "arrow.core.Tuple2"))
  fun <C> ap(f: Tuple2Of<*, (B) -> C>) =
    map(f.fix().b)

  @Deprecated("Monad hierarchy for Tuple2 is deprecated", ReplaceWith("f(this.b)"))
  fun <C> flatMap(f: (B) -> Tuple2Of<@UnsafeVariance A, C>) =
    f(b).fix()

  @Deprecated("Comonad hierarchy for Tuple2 is deprecated", ReplaceWith("a toT f(this)", "arrow.core.toT"))
  fun <C> coflatMap(f: (Tuple2Of<A, B>) -> C) =
    a toT f(this)

  @Deprecated("Comonad hierarchy for Tuple2 is deprecated", ReplaceWith("this.b"))
  fun extract() =
    b

  @Deprecated("Foldable hierarchy for Tuple2 is deprecated", ReplaceWith("f(b, this.b)"))
  fun <C> foldL(b: C, f: (C, B) -> C) =
    f(b, this.b)

  @Deprecated("Foldable hierarchy for Tuple2 is deprecated", ReplaceWith("f(this.b, lb)"))
  fun <C> foldR(lb: Eval<C>, f: (B, Eval<C>) -> Eval<C>) =
    f(b, lb)

  fun reverse(): Tuple2<B, A> = Tuple2(b, a)

  fun show(SA: Show<A>, SB: Show<B>): String =
    "(" + listOf(SA.run { a.show() }, SB.run { b.show() }).joinToString(", ") + ")"

  override fun toString(): String = show(Show.any(), Show.any())

  companion object
}

fun <A, B> Tuple2<A, B>.eqv(
  EQA: Eq<A>,
  EQB: Eq<B>,
  b: Tuple2<A, B>
): Boolean =
  EQA.run { a.eqv(b.a) } &&
    EQB.run { this@eqv.b.eqv(b.b) }

fun <A, B> Tuple2<A, B>.hashWithSalt(
  HA: Hash<A>,
  HB: Hash<B>,
  salt: Int
): Int =
  HA.run {
    HB.run {
      a.hashWithSalt(
        b.hashWithSalt(salt))
    }
  }

fun <A, B> Tuple2<A, B>.compare(
  OA: Order<A>,
  OB: Order<B>,
  other: Tuple2<A, B>
): Ordering = listOf(
  OA.run { a.compare(other.a) },
  OB.run { b.compare(other.b) }
).fold(Monoid.ordering())
