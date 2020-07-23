package arrow.typeclasses

interface Num<A> {
  operator fun A.plus(b: A): A
  operator fun A.times(b: A): A

  fun Long.fromLong(): A
}
