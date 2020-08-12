package arrow.core

class ShortCircuit(val value: Any?) : RuntimeException(null, null) {
  override fun fillInStackTrace(): Throwable = this
  override fun toString(): String = "ShortCircuit($value)"
  inline fun <E> resolve(): E = value as E
}
