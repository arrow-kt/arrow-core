@file:JvmMultifileClass
@file:JvmName("TupleNKt")

package arrow.core

import arrow.typeclasses.Show
import arrow.typeclasses.ShowDeprecation

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
