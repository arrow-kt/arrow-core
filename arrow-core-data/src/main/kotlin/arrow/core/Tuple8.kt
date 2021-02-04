@file:JvmMultifileClass
@file:JvmName("TupleNKt")

package arrow.core

import arrow.typeclasses.Eq
import arrow.typeclasses.Hash
import arrow.typeclasses.Monoid
import arrow.typeclasses.Order
import arrow.typeclasses.Show
import arrow.typeclasses.ShowDeprecation
import arrow.typeclasses.defaultSalt

class ForTuple8 private constructor() {
  companion object
}
typealias Tuple8Of<A, B, C, D, E, F, G, H> = arrow.Kind8<ForTuple8, A, B, C, D, E, F, G, H>
typealias Tuple8PartialOf<A, B, C, D, E, F, G> = arrow.Kind7<ForTuple8, A, B, C, D, E, F, G>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <A, B, C, D, E, F, G, H> Tuple8Of<A, B, C, D, E, F, G, H>.fix(): Tuple8<A, B, C, D, E, F, G, H> =
  this as Tuple8<A, B, C, D, E, F, G, H>

data class Tuple8<out A, out B, out C, out D, out E, out F, out G, out H>(val a: A, val b: B, val c: C, val d: D, val e: E, val f: F, val g: G, val h: H) : Tuple8Of<A, B, C, D, E, F, G, H> {
  @Deprecated(ShowDeprecation)
  fun show(SA: Show<A>, SB: Show<B>, SC: Show<C>, SD: Show<D>, SE: Show<E>, SF: Show<F>, SG: Show<G>, SH: Show<H>): String =
    "(" + listOf(SA.run { a.show() }, SB.run { b.show() }, SC.run { c.show() }, SD.run { d.show() }, SE.run { e.show() }, SF.run { f.show() }, SG.run { g.show() }, SH.run { h.show() }).joinToString(", ") + ")"

  override fun toString(): String =
    "($a, $b, $c, $d, $e, $f, $g, $h)"

  companion object
}

fun <A, B, C, D, E, F, G, H> Tuple8<A, B, C, D, E, F, G, H>.eqv(
  EQA: Eq<A>,
  EQB: Eq<B>,
  EQC: Eq<C>,
  EQD: Eq<D>,
  EQE: Eq<E>,
  EQF: Eq<F>,
  EQG: Eq<G>,
  EQH: Eq<H>,
  other: Tuple8<A, B, C, D, E, F, G, H>
): Boolean =
  EQA.run { a.eqv(other.a) } &&
    EQB.run { this@eqv.b.eqv(other.b) } &&
    EQC.run { c.eqv(other.c) } &&
    EQD.run { d.eqv(other.d) } &&
    EQE.run { e.eqv(other.e) } &&
    EQF.run { f.eqv(other.f) } &&
    EQG.run { g.eqv(other.g) } &&
    EQH.run { h.eqv(other.h) }

fun <A, B, C, D, E, F, G, H> Tuple8<A, B, C, D, E, F, G, H>.neqv(
  EQA: Eq<A>,
  EQB: Eq<B>,
  EQC: Eq<C>,
  EQD: Eq<D>,
  EQE: Eq<E>,
  EQF: Eq<F>,
  EQG: Eq<G>,
  EQH: Eq<H>,
  other: Tuple8<A, B, C, D, E, F, G, H>
): Boolean = !eqv(EQA, EQB, EQC, EQD, EQE, EQF, EQG, EQH, other)

private class Tuple8Eq<A, B, C, D, E, F, G, H>(
  private val EQA: Eq<A>,
  private val EQB: Eq<B>,
  private val EQC: Eq<C>,
  private val EQD: Eq<D>,
  private val EQE: Eq<E>,
  private val EQF: Eq<F>,
  private val EQG: Eq<G>,
  private val EQH: Eq<H>
) : Eq<Tuple8<A, B, C, D, E, F, G, H>> {
  override fun Tuple8<A, B, C, D, E, F, G, H>.eqv(other: Tuple8<A, B, C, D, E, F, G, H>): Boolean =
    eqv(EQA, EQB, EQC, EQD, EQE, EQF, EQG, EQH, other)
}

fun <A, B, C, D, E, F, G, H> Eq.Companion.tuple8(
  EQA: Eq<A>,
  EQB: Eq<B>,
  EQC: Eq<C>,
  EQD: Eq<D>,
  EQE: Eq<E>,
  EQF: Eq<F>,
  EQG: Eq<G>,
  EQH: Eq<H>
): Eq<Tuple8<A, B, C, D, E, F, G, H>> =
  Tuple8Eq(EQA, EQB, EQC, EQD, EQE, EQF, EQG, EQH)

fun <A, B, C, D, E, F, G, H> Tuple8<A, B, C, D, E, F, G, H>.hashWithSalt(
  HA: Hash<A>,
  HB: Hash<B>,
  HC: Hash<C>,
  HD: Hash<D>,
  HE: Hash<E>,
  HF: Hash<F>,
  HG: Hash<G>,
  HH: Hash<H>,
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
                  a.hashWithSalt(
                    b.hashWithSalt(
                      c.hashWithSalt(
                        d.hashWithSalt(
                          e.hashWithSalt(
                            f.hashWithSalt(
                              g.hashWithSalt(
                                h.hashWithSalt(salt)
                              )))))))
                }
              }
            }
          }
        }
      }
    }
  }

fun <A, B, C, D, E, F, G, H> Tuple8<A, B, C, D, E, F, G, H>.hash(
  HA: Hash<A>,
  HB: Hash<B>,
  HC: Hash<C>,
  HD: Hash<D>,
  HE: Hash<E>,
  HF: Hash<F>,
  HG: Hash<G>,
  HH: Hash<H>
): Int = hashWithSalt(HA, HB, HC, HD, HE, HF, HG, HH, defaultSalt)

private class Tuple8Hash<A, B, C, D, E, F, G, H>(
  private val HA: Hash<A>,
  private val HB: Hash<B>,
  private val HC: Hash<C>,
  private val HD: Hash<D>,
  private val HE: Hash<E>,
  private val HF: Hash<F>,
  private val HG: Hash<G>,
  private val HH: Hash<H>
) : Hash<Tuple8<A, B, C, D, E, F, G, H>> {
  override fun Tuple8<A, B, C, D, E, F, G, H>.hashWithSalt(salt: Int): Int =
    hashWithSalt(HA, HB, HC, HD, HE, HF, HG, HH, salt)
}

fun <A, B, C, D, E, F, G, H> Hash.Companion.tuple8(
  HA: Hash<A>,
  HB: Hash<B>,
  HC: Hash<C>,
  HD: Hash<D>,
  HE: Hash<E>,
  HF: Hash<F>,
  HG: Hash<G>,
  HH: Hash<H>
): Hash<Tuple8<A, B, C, D, E, F, G, H>> =
  Tuple8Hash(HA, HB, HC, HD, HE, HF, HG, HH)

operator fun <A : Comparable<A>, B : Comparable<B>, C : Comparable<C>, D : Comparable<D>, E : Comparable<E>, F : Comparable<F>, G : Comparable<G>, H : Comparable<H>>
  Tuple8<A, B, C, D, E, F, G, H>.compareTo(other: Tuple8<A, B, C, D, E, F, G, H>): Int {
  val first = a.compareTo(other.a)
  return if (first == 0) {
    val second = b.compareTo(other.b)
    if (second == 0) {
      val third = c.compareTo(other.c)
      if (third == 0) {
        val fourth = d.compareTo(other.d)
        if (fourth == 0) {
          val fifth = e.compareTo(other.e)
          if (fifth == 0) {
            val sixth = f.compareTo(other.f)
            if (sixth == 0) {
              val seventh = g.compareTo(other.g)
              if (seventh == 0) h.compareTo(other.h)
              else seventh
            } else sixth
          } else fifth
        } else fourth
      } else third
    } else second
  } else first
}
