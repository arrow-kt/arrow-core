package arrow.core

fun interface Iter<out A> {
  fun iterator(): Iterator<A>
}
