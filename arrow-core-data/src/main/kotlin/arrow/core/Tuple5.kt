@file:JvmMultifileClass
@file:JvmName("TupleNKt")

package arrow.core

import arrow.typeclasses.Eq
import arrow.typeclasses.Hash
import arrow.typeclasses.Monoid
import arrow.typeclasses.Order
import arrow.typeclasses.Show

class ForTuple5 private constructor() {
  companion object
}
typealias Tuple5Of<A, B, C, D, E> = arrow.Kind5<ForTuple5, A, B, C, D, E>
typealias Tuple5PartialOf<A, B, C, D> = arrow.Kind4<ForTuple5, A, B, C, D>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <A, B, C, D, E> Tuple5Of<A, B, C, D, E>.fix(): Tuple5<A, B, C, D, E> =
  this as Tuple5<A, B, C, D, E>

data class Tuple5<out A, out B, out C, out D, out E>(val a: A, val b: B, val c: C, val d: D, val e: E) : Tuple5Of<A, B, C, D, E> {
  fun show(SA: Show<A>, SB: Show<B>, SC: Show<C>, SD: Show<D>, SE: Show<E>): String =
    "(" + listOf(SA.run { a.show() }, SB.run { b.show() }, SC.run { c.show() }, SD.run { d.show() }, SE.run { e.show() }).joinToString(", ") + ")"

  override fun toString(): String = show(Show.any(), Show.any(), Show.any(), Show.any(), Show.any())

  companion object
}

fun <A, B, C, D, E> Tuple5<A, B, C, D, E>.eqv(
  EQA: Eq<A>,
  EQB: Eq<B>,
  EQC: Eq<C>,
  EQD: Eq<D>,
  EQE: Eq<E>,
  b: Tuple5<A, B, C, D, E>
): Boolean =
  EQA.run { a.eqv(b.a) } &&
    EQB.run { this@eqv.b.eqv(b.b) } &&
    EQC.run { c.eqv(b.c) } &&
    EQD.run { d.eqv(b.d) } &&
    EQE.run { e.eqv(b.e) }

fun <A, B, C, D, E> Tuple5<A, B, C, D, E>.hashWithSalt(
  HA: Hash<A>,
  HB: Hash<B>,
  HC: Hash<C>,
  HD: Hash<D>,
  HE: Hash<E>,
  salt: Int
): Int =
  HA.run {
    HB.run {
      HC.run {
        HD.run {
          HE.run {
            a.hashWithSalt(
              b.hashWithSalt(
                c.hashWithSalt(
                  d.hashWithSalt(
                    e.hashWithSalt(salt)
                  ))))
          }
        }
      }
    }
  }

fun <A, B, C, D, E> Tuple5<A, B, C, D, E>.compare(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  other: Tuple5<A, B, C, D, E>
): Ordering = listOf(
  OA.run { a.compare(other.a) },
  OB.run { b.compare(other.b) },
  OC.run { c.compare(other.c) },
  OD.run { d.compare(other.d) },
  OE.run { e.compare(other.e) }
).fold(Monoid.ordering())
