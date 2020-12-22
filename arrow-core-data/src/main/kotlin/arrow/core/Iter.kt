package arrow.core

fun interface Iter<out A> {
  fun iterator(): Iterator<A>

  fun iterable(): Iterable<A> = Iterable { iterator() }
}
