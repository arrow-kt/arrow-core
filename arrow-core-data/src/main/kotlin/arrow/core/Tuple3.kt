@file:JvmMultifileClass
@file:JvmName("TupleNKt")

package arrow.core

import arrow.typeclasses.Eq
import arrow.typeclasses.Hash
import arrow.typeclasses.Monoid
import arrow.typeclasses.Order
import arrow.typeclasses.Show

class ForTuple3 private constructor() {
  companion object
}
typealias Tuple3Of<A, B, C> = arrow.Kind3<ForTuple3, A, B, C>
typealias Tuple3PartialOf<A, B> = arrow.Kind2<ForTuple3, A, B>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <A, B, C> Tuple3Of<A, B, C>.fix(): Tuple3<A, B, C> =
  this as Tuple3<A, B, C>

data class Tuple3<out A, out B, out C>(val a: A, val b: B, val c: C) : Tuple3Of<A, B, C> {
  fun show(SA: Show<A>, SB: Show<B>, SC: Show<C>): String =
    "(" + listOf(SA.run { a.show() }, SB.run { b.show() }, SC.run { c.show() }).joinToString(", ") + ")"

  override fun toString(): String = show(Show.any(), Show.any(), Show.any())

  companion object
}

fun <A, B, C> Tuple3<A, B, C>.eqv(
  EQA: Eq<A>,
  EQB: Eq<B>,
  EQC: Eq<C>,
  b: Tuple3<A, B, C>
): Boolean =
  EQA.run { a.eqv(b.a) } &&
    EQB.run { this@eqv.b.eqv(b.b) } &&
    EQC.run { c.eqv(b.c) }

fun <A, B, C> Tuple3<A, B, C>.hashWithSalt(
  HA: Hash<A>,
  HB: Hash<B>,
  HC: Hash<C>,
  salt: Int
): Int =
  HA.run {
    HB.run {
      HC.run {
        a.hashWithSalt(
          b.hashWithSalt(
            c.hashWithSalt(salt)
          ))
      }
    }
  }

fun <A, B, C> Tuple3<A, B, C>.compare(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  other: Tuple3<A, B, C>
): Ordering = listOf(
  OA.run { a.compare(other.a) },
  OB.run { b.compare(other.b) },
  OC.run { c.compare(other.c) }
).fold(Monoid.ordering())
