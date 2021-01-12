@file:JvmMultifileClass
@file:JvmName("TupleNKt")

package arrow.core

import arrow.typeclasses.Eq
import arrow.typeclasses.Hash
import arrow.typeclasses.Monoid
import arrow.typeclasses.Order
import arrow.typeclasses.Show

class ForTuple6 private constructor() {
  companion object
}
typealias Tuple6Of<A, B, C, D, E, F> = arrow.Kind6<ForTuple6, A, B, C, D, E, F>
typealias Tuple6PartialOf<A, B, C, D, E> = arrow.Kind5<ForTuple6, A, B, C, D, E>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <A, B, C, D, E, F> Tuple6Of<A, B, C, D, E, F>.fix(): Tuple6<A, B, C, D, E, F> =
  this as Tuple6<A, B, C, D, E, F>

data class Tuple6<out A, out B, out C, out D, out E, out F>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F) : Tuple6Of<A, B, C, D, E, F> {
  fun show(SA: Show<A>, SB: Show<B>, SC: Show<C>, SD: Show<D>, SE: Show<E>, SF: Show<F>): String =
    "(" + listOf(SA.run { a.show() }, SB.run { b.show() }, SC.run { c.show() }, SD.run { d.show() }, SE.run { e.show() }, SF.run { f.show() }).joinToString(", ") + ")"

  override fun toString(): String = show(Show.any(), Show.any(), Show.any(), Show.any(), Show.any(), Show.any())

  companion object
}

fun <A, B, C, D, E, F> Tuple6<A, B, C, D, E, F>.eqv(
  EQA: Eq<A>,
  EQB: Eq<B>,
  EQC: Eq<C>,
  EQD: Eq<D>,
  EQE: Eq<E>,
  EQF: Eq<F>,
  b: Tuple6<A, B, C, D, E, F>
): Boolean =
  EQA.run { a.eqv(b.a) } &&
    EQB.run { this@eqv.b.eqv(b.b) } &&
    EQC.run { c.eqv(b.c) } &&
    EQD.run { d.eqv(b.d) } &&
    EQE.run { e.eqv(b.e) } &&
    EQF.run { f.eqv(b.f) }

fun <A, B, C, D, E, F> Tuple6<A, B, C, D, E, F>.hashWithSalt(
  HA: Hash<A>,
  HB: Hash<B>,
  HC: Hash<C>,
  HD: Hash<D>,
  HE: Hash<E>,
  HF: Hash<F>,
  salt: Int
): Int =
  HA.run {
    HB.run {
      HC.run {
        HD.run {
          HE.run {
            HF.run {
              a.hashWithSalt(
                b.hashWithSalt(
                  c.hashWithSalt(
                    d.hashWithSalt(
                      e.hashWithSalt(
                        f.hashWithSalt(salt)
                      )))))
            }
          }
        }
      }
    }
  }

fun <A, B, C, D, E, F> Tuple6<A, B, C, D, E, F>.compare(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  OF: Order<F>,
  other: Tuple6<A, B, C, D, E, F>
): Ordering = listOf(
  OA.run { a.compare(other.a) },
  OB.run { b.compare(other.b) },
  OC.run { c.compare(other.c) },
  OD.run { d.compare(other.d) },
  OE.run { e.compare(other.e) },
  OF.run { f.compare(other.f) }
).fold(Monoid.ordering())
