@file:JvmMultifileClass
@file:JvmName("TupleNKt")

package arrow.core

import arrow.typeclasses.Eq
import arrow.typeclasses.Hash
import arrow.typeclasses.Monoid
import arrow.typeclasses.Order
import arrow.typeclasses.Show

class ForTuple7 private constructor() {
  companion object
}
typealias Tuple7Of<A, B, C, D, E, F, G> = arrow.Kind7<ForTuple7, A, B, C, D, E, F, G>
typealias Tuple7PartialOf<A, B, C, D, E, F> = arrow.Kind6<ForTuple7, A, B, C, D, E, F>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <A, B, C, D, E, F, G> Tuple7Of<A, B, C, D, E, F, G>.fix(): Tuple7<A, B, C, D, E, F, G> =
  this as Tuple7<A, B, C, D, E, F, G>

data class Tuple7<out A, out B, out C, out D, out E, out F, out G>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G) : Tuple7Of<A, B, C, D, E, F, G> {
  fun show(SA: Show<A>, SB: Show<B>, SC: Show<C>, SD: Show<D>, SE: Show<E>, SF: Show<F>, SG: Show<G>): String =
    "(" + listOf(SA.run { a.show() }, SB.run { b.show() }, SC.run { c.show() }, SD.run { d.show() }, SE.run { e.show() }, SF.run { f.show() }, SG.run { g.show() }).joinToString(", ") + ")"

  override fun toString(): String = show(Show.any(), Show.any(), Show.any(), Show.any(), Show.any(), Show.any(), Show.any())

  companion object
}

fun <A, B, C, D, E, F, G> Tuple7<A, B, C, D, E, F, G>.eqv(
  EQA: Eq<A>,
  EQB: Eq<B>,
  EQC: Eq<C>,
  EQD: Eq<D>,
  EQE: Eq<E>,
  EQF: Eq<F>,
  EQG: Eq<G>,
  b: Tuple7<A, B, C, D, E, F, G>
): Boolean =
  EQA.run { a.eqv(b.a) } &&
    EQB.run { this@eqv.b.eqv(b.b) } &&
    EQC.run { c.eqv(b.c) } &&
    EQD.run { d.eqv(b.d) } &&
    EQE.run { e.eqv(b.e) } &&
    EQF.run { f.eqv(b.f) } &&
    EQG.run { g.eqv(b.g) }

fun <A, B, C, D, E, F, G> Tuple7<A, B, C, D, E, F, G>.hashWithSalt(
  HA: Hash<A>,
  HB: Hash<B>,
  HC: Hash<C>,
  HD: Hash<D>,
  HE: Hash<E>,
  HF: Hash<F>,
  HG: Hash<G>,
  salt: Int
): Int =
  HA.run {
    HB.run {
      HC.run {
        HD.run {
          HE.run {
            HF.run {
              HG.run {
                a.hashWithSalt(
                  b.hashWithSalt(
                    c.hashWithSalt(
                      d.hashWithSalt(
                        e.hashWithSalt(
                          f.hashWithSalt(
                            g.hashWithSalt(salt)
                          ))))))
              }
            }
          }
        }
      }
    }
  }

fun <A, B, C, D, E, F, G> Tuple7<A, B, C, D, E, F, G>.compare(
  OA: Order<A>,
  OB: Order<B>,
  OC: Order<C>,
  OD: Order<D>,
  OE: Order<E>,
  OF: Order<F>,
  OG: Order<G>,
  other: Tuple7<A, B, C, D, E, F, G>
): Ordering = listOf(
  OA.run { a.compare(other.a) },
  OB.run { b.compare(other.b) },
  OC.run { c.compare(other.c) },
  OD.run { d.compare(other.d) },
  OE.run { e.compare(other.e) },
  OF.run { f.compare(other.f) },
  OG.run { g.compare(other.g) }
).fold(Monoid.ordering())
