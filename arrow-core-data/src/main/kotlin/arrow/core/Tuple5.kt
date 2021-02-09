@file:JvmMultifileClass
@file:JvmName("TupleNKt")

package arrow.core

import arrow.typeclasses.Show
import arrow.typeclasses.ShowDeprecation

class ForTuple5 private constructor() {
  companion object
}
typealias Tuple5Of<A, B, C, D, E> = arrow.Kind5<ForTuple5, A, B, C, D, E>
typealias Tuple5PartialOf<A, B, C, D> = arrow.Kind4<ForTuple5, A, B, C, D>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
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
