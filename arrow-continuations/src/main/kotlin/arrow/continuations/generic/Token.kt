package arrow.continuations.generic

/** Represents a unique identifier using object equality. */
internal class Token {
  override fun toString(): String = "Token(${Integer.toHexString(hashCode())})"
}
