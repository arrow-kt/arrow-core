@file:JvmMultifileClass
@file:JvmName("TupleNKt")

package arrow.core

import arrow.typeclasses.Eq
import arrow.typeclasses.Hash
import arrow.typeclasses.Monoid
import arrow.typeclasses.Order
import arrow.typeclasses.Show

class ForTuple4 private constructor() {
  companion object
}
typealias Tuple4Of<A, B, C, D> = arrow.Kind4<ForTuple4, A, B, C, D>
typealias Tuple4PartialOf<A, B, C> = arrow.Kind3<ForTuple4, A, B, C>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <A, B, C, D> Tuple4Of<A, B, C, D>.fix(): Tuple4<A, B, C, D> =
  this as Tuple4<A, B, C, D>

data class Tuple4<out A, out B, out C, out D>(val a: A, val b: B, val c: C, val d: D) : Tuple4Of<A, B, C, D> {
  fun show(SA: Show<A>, SB: Show<B>, SC: Show<C>, SD: Show<D>): String =
    "(" + listOf(SA.run { a.show() }, SB.run { b.show() }, SC.run { c.show() }, SD.run { d.show() }).joinToString(", ") + ")"

  override fun toString(): String = show(Show.any(), Show.any(), Show.any(), Show.any())

  companion object
}

fun <A, B, C, D> Tuple4<A, B, C, D>.eqv(
  EQA: Eq<A>,
  EQB: Eq<B>,
  EQC: Eq<C>,
  EQD: Eq<D>,
  b: Tuple4<A, B, C, D>
): Boolean =
  EQA.run { a.eqv(b.a) } &&
    EQB.run { this@eqv.b.eqv(b.b) } &&
    EQC.run { c.eqv(b.c) } &&
    EQD.run { d.eqv(b.d) }

fun <A, B, C, D> Tuple4<A, B, C, D>.hashWithSalt(
  HA: Hash<A>,
  HB: Hash<B>,
  HC: Hash<C>,
  HD: Hash<D>,
  salt: Int
): Int =
  HA.run {
    HB.run {
      HC.run {
        HD.run {
          a.hashWithSalt(
            b.hashWithSalt(
              c.hashWithSalt(
                d.hashWithSalt(salt)
              )))
        }
      }
    }
  }

fun <A, B, C, D> Tuple4<A, B, C, D>.compare(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  other: Tuple4<A, B, C, D>
): Ordering = listOf(
  OA.run { a.compare(other.a) },
  OB.run { b.compare(other.b) },
  OC.run { c.compare(other.c) },
  OD.run { d.compare(other.d) }
).fold(Monoid.ordering())
