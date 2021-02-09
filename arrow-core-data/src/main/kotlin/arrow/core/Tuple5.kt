@file:JvmMultifileClass
@file:JvmName("TupleNKt")

package arrow.core

import arrow.KindDeprecation
import arrow.typeclasses.Hash
import arrow.typeclasses.Show
import arrow.typeclasses.ShowDeprecation
import arrow.typeclasses.defaultSalt

@Deprecated(
  message = KindDeprecation,
  level = DeprecationLevel.WARNING
)
class ForTuple5 private constructor() {
  companion object
}

@Deprecated(
  message = KindDeprecation,
  level = DeprecationLevel.WARNING
)
typealias Tuple5Of<A, B, C, D, E> = arrow.Kind5<ForTuple5, A, B, C, D, E>

@Deprecated(
  message = KindDeprecation,
  level = DeprecationLevel.WARNING
)
typealias Tuple5PartialOf<A, B, C, D> = arrow.Kind4<ForTuple5, A, B, C, D>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
@Deprecated(
  message = KindDeprecation,
  level = DeprecationLevel.WARNING
)
inline fun <A, B, C, D, E> Tuple5Of<A, B, C, D, E>.fix(): Tuple5<A, B, C, D, E> =
  this as Tuple5<A, B, C, D, E>

data class Tuple5<out A, out B, out C, out D, out E>(val a: A, val b: B, val c: C, val d: D, val e: E) : Tuple5Of<A, B, C, D, E> {

  @Deprecated(ShowDeprecation)
  fun show(SA: Show<A>, SB: Show<B>, SC: Show<C>, SD: Show<D>, SE: Show<E>): String =
    "(" + listOf(SA.run { a.show() }, SB.run { b.show() }, SC.run { c.show() }, SD.run { d.show() }, SE.run { e.show() }).joinToString(", ") + ")"

  override fun toString(): String =
    "($a, $b, $c, $d, $e)"

  companion object
}

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

fun <A, B, C, D, E> Tuple5<A, B, C, D, E>.hash(
  HA: Hash<A>,
  HB: Hash<B>,
  HC: Hash<C>,
  HD: Hash<D>,
  HE: Hash<E>
): Int = hashWithSalt(HA, HB, HC, HD, HE, defaultSalt)

private class Tuple5Hash<A, B, C, D, E>(
  private val HA: Hash<A>,
  private val HB: Hash<B>,
  private val HC: Hash<C>,
  private val HD: Hash<D>,
  private val HE: Hash<E>
) : Hash<Tuple5<A, B, C, D, E>> {
  override fun Tuple5<A, B, C, D, E>.hashWithSalt(salt: Int): Int =
    hashWithSalt(HA, HB, HC, HD, HE, salt)
}

fun <A, B, C, D, E> Hash.Companion.tuple5(
  HA: Hash<A>,
  HB: Hash<B>,
  HC: Hash<C>,
  HD: Hash<D>,
  HE: Hash<E>
): Hash<Tuple5<A, B, C, D, E>> =
  Tuple5Hash(HA, HB, HC, HD, HE)

operator fun <A : Comparable<A>, B : Comparable<B>, C : Comparable<C>, D : Comparable<D>, E : Comparable<E>>
  Tuple5<A, B, C, D, E>.compareTo(other: Tuple5<A, B, C, D, E>): Int {
  val first = a.compareTo(other.a)
  return if (first == 0) {
    val second = b.compareTo(other.b)
    if (second == 0) {
      val third = c.compareTo(other.c)
      if (third == 0) {
        val fourth = d.compareTo(other.d)
        if (fourth == 0) e.compareTo(other.e)
        else fourth
      } else third
    } else second
  } else first
}
