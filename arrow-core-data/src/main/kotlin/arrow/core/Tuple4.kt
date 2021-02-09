@file:JvmMultifileClass
@file:JvmName("TupleNKt")

package arrow.core

import arrow.typeclasses.Show
import arrow.typeclasses.ShowDeprecation

class ForTuple4 private constructor() {
  companion object
}
typealias Tuple4Of<A, B, C, D> = arrow.Kind4<ForTuple4, A, B, C, D>
typealias Tuple4PartialOf<A, B, C> = arrow.Kind3<ForTuple4, A, B, C>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
inline fun <A, B, C, D> Tuple4Of<A, B, C, D>.fix(): Tuple4<A, B, C, D> =
  this as Tuple4<A, B, C, D>

data class Tuple4<out A, out B, out C, out D>(val a: A, val b: B, val c: C, val d: D) : Tuple4Of<A, B, C, D> {

  @Deprecated(ShowDeprecation)
  fun show(SA: Show<A>, SB: Show<B>, SC: Show<C>, SD: Show<D>): String =
    "(" + listOf(SA.run { a.show() }, SB.run { b.show() }, SC.run { c.show() }, SD.run { d.show() }).joinToString(", ") + ")"

  override fun toString(): String =
    "($a, $b, $c, $d)"

  companion object
}

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
