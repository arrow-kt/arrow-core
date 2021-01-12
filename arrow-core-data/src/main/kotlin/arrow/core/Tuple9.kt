@file:JvmMultifileClass
@file:JvmName("TupleNKt")

package arrow.core

import arrow.typeclasses.Eq
import arrow.typeclasses.Hash
import arrow.typeclasses.Monoid
import arrow.typeclasses.Order
import arrow.typeclasses.Show

class ForTuple9 private constructor() {
  companion object
}
typealias Tuple9Of<A, B, C, D, E, F, G, H, I> = arrow.Kind9<ForTuple9, A, B, C, D, E, F, G, H, I>
typealias Tuple9PartialOf<A, B, C, D, E, F, G, H> = arrow.Kind8<ForTuple9, A, B, C, D, E, F, G, H>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <A, B, C, D, E, F, G, H, I> Tuple9Of<A, B, C, D, E, F, G, H, I>.fix(): Tuple9<A, B, C, D, E, F, G, H, I> =
  this as Tuple9<A, B, C, D, E, F, G, H, I>

data class Tuple9<out A, out B, out C, out D, out E, out F, out G, out H, out I>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H, val i: I) : Tuple9Of<A, B, C, D, E, F, G, H, I> {
  fun show(SA: Show<A>, SB: Show<B>, SC: Show<C>, SD: Show<D>, SE: Show<E>, SF: Show<F>, SG: Show<G>, SH: Show<H>, SI: Show<I>): String =
    "(" + listOf(SA.run { a.show() }, SB.run { b.show() }, SC.run { c.show() }, SD.run { d.show() }, SE.run { e.show() }, SF.run { f.show() }, SG.run { g.show() }, SH.run { h.show() }, SI.run { i.show() }).joinToString(", ") + ")"

  override fun toString(): String = show(Show.any(), Show.any(), Show.any(), Show.any(), Show.any(), Show.any(), Show.any(), Show.any(), Show.any())

  companion object
}

fun <A, B, C, D, E, F, G, H, I> Tuple9<A, B, C, D, E, F, G, H, I>.eqv(
  EQA: Eq<A>,
  EQB: Eq<B>,
  EQC: Eq<C>,
  EQD: Eq<D>,
  EQE: Eq<E>,
  EQF: Eq<F>,
  EQG: Eq<G>,
  EQH: Eq<H>,
  EQI: Eq<I>,
  b: Tuple9<A, B, C, D, E, F, G, H, I>
): Boolean =
  EQA.run { a.eqv(b.a) } &&
    EQB.run { this@eqv.b.eqv(b.b) } &&
    EQC.run { c.eqv(b.c) } &&
    EQD.run { d.eqv(b.d) } &&
    EQE.run { e.eqv(b.e) } &&
    EQF.run { f.eqv(b.f) } &&
    EQG.run { g.eqv(b.g) } &&
    EQH.run { h.eqv(b.h) } &&
    EQI.run { i.eqv(b.i) }

fun <A, B, C, D, E, F, G, H, I> Tuple9<A, B, C, D, E, F, G, H, I>.hashWithSalt(
  HA: Hash<A>,
  HB: Hash<B>,
  HC: Hash<C>,
  HD: Hash<D>,
  HE: Hash<E>,
  HF: Hash<F>,
  HG: Hash<G>,
  HH: Hash<H>,
  HI: Hash<I>,
  salt: Int
): Int =
  HA.run {
    HB.run {
      HC.run {
        HD.run {
          HE.run {
            HF.run {
              HG.run {
                HH.run {
                  HI.run {
                    a.hashWithSalt(
                      b.hashWithSalt(
                        c.hashWithSalt(
                          d.hashWithSalt(
                            e.hashWithSalt(
                              f.hashWithSalt(
                                g.hashWithSalt(
                                  h.hashWithSalt(
                                    i.hashWithSalt(salt)
                                  ))))))))
                  }
                }
              }
            }
          }
        }
      }
    }
  }

fun <A, B, C, D, E, F, G, H, I> Tuple9<A, B, C, D, E, F, G, H, I>.compare(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  OF: Order<F>,
  OG: Order<G>,
  OH: Order<H>,
  OI: Order<I>,
  other: Tuple9<A, B, C, D, E, F, G, H, I>
): Ordering = listOf(
  OA.run { a.compare(other.a) },
  OB.run { b.compare(other.b) },
  OC.run { c.compare(other.c) },
  OD.run { d.compare(other.d) },
  OE.run { e.compare(other.e) },
  OF.run { f.compare(other.f) },
  OG.run { g.compare(other.g) },
  OH.run { h.compare(other.h) },
  OI.run { i.compare(other.i) }
).fold(Monoid.ordering())
