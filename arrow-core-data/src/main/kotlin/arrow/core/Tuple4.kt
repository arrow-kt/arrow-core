@file:JvmMultifileClass
@file:JvmName("TupleNKt")

package arrow.core

import arrow.typeclasses.Eq
import arrow.typeclasses.Hash
import arrow.typeclasses.Monoid
import arrow.typeclasses.Order
import arrow.typeclasses.Show
import arrow.typeclasses.defaultSalt

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

private class Tuple4Show<A, B, C, D>(
  private val SA: Show<A>,
  private val SB: Show<B>,
  private val SC: Show<C>,
  private val SD: Show<D>
) : Show<Tuple4<A, B, C, D>> {
  override fun Tuple4<A, B, C, D>.show(): String =
    show(SA, SB, SC, SD)
}

fun <A, B, C, D> Show.Companion.tuple4(
  SA: Show<A>,
  SB: Show<B>,
  SC: Show<C>,
  SD: Show<D>
): Show<Tuple4<A, B, C, D>> =
  Tuple4Show(SA, SB, SC, SD)

fun <A, B, C, D> Tuple4<A, B, C, D>.eqv(
  EQA: Eq<A>,
  EQB: Eq<B>,
  EQC: Eq<C>,
  EQD: Eq<D>,
  other: Tuple4<A, B, C, D>
): Boolean =
  EQA.run { a.eqv(other.a) } &&
    EQB.run { this@eqv.b.eqv(other.b) } &&
    EQC.run { c.eqv(other.c) } &&
    EQD.run { d.eqv(other.d) }

fun <A, B, C, D> Tuple4<A, B, C, D>.neqv(
  EQA: Eq<A>,
  EQB: Eq<B>,
  EQC: Eq<C>,
  EQD: Eq<D>,
  other: Tuple4<A, B, C, D>
): Boolean = !eqv(EQA, EQB, EQC, EQD, other)

private class Tuple4Eq<A, B, C, D>(
  private val EQA: Eq<A>,
  private val EQB: Eq<B>,
  private val EQC: Eq<C>,
  private val EQD: Eq<D>
) : Eq<Tuple4<A, B, C, D>> {
  override fun Tuple4<A, B, C, D>.eqv(other: Tuple4<A, B, C, D>): Boolean =
    eqv(EQA, EQB, EQC, EQD, other)
}

fun <A, B, C, D> Eq.Companion.tuple4(
  EQA: Eq<A>,
  EQB: Eq<B>,
  EQC: Eq<C>,
  EQD: Eq<D>
): Eq<Tuple4<A, B, C, D>> =
  Tuple4Eq(EQA, EQB, EQC, EQD)

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

fun <A, B, C, D> Tuple4<A, B, C, D>.hash(
  HA: Hash<A>,
  HB: Hash<B>,
  HC: Hash<C>,
  HD: Hash<D>
): Int = hashWithSalt(HA, HB, HC, HD, defaultSalt)

private class Tuple4Hash<A, B, C, D>(
  private val HA: Hash<A>,
  private val HB: Hash<B>,
  private val HC: Hash<C>,
  private val HD: Hash<D>
) : Hash<Tuple4<A, B, C, D>> {
  override fun Tuple4<A, B, C, D>.hashWithSalt(salt: Int): Int =
    hashWithSalt(HA, HB, HC, HD, salt)
}

fun <A, B, C, D> Hash.Companion.tuple4(
  HA: Hash<A>,
  HB: Hash<B>,
  HC: Hash<C>,
  HD: Hash<D>
): Hash<Tuple4<A, B, C, D>> =
  Tuple4Hash(HA, HB, HC, HD)

operator fun <A : Comparable<A>, B : Comparable<B>, C : Comparable<C>, D : Comparable<D>> Tuple4<A, B, C, D>.compareTo(other: Tuple4<A, B, C, D>): Int {
  val first = a.compareTo(other.a)
  return if (first == 0) {
    val second = b.compareTo(other.b)
    if (second == 0) {
      val third = c.compareTo(other.c)
      if (third == 0) d.compareTo(other.d)
      else third
    } else second
  } else first
}
