package arrow.core

import arrow.typeclasses.Hash
import arrow.typeclasses.Monoid
import arrow.typeclasses.Order
import arrow.typeclasses.Semigroup
import arrow.typeclasses.Show

private object StringSemigroup : Semigroup<String> {
  override fun String.combine(b: String): String = "${this}$b"
}

fun Semigroup.Companion.string(): Semigroup<String> =
  StringSemigroup

private object StringMonoid : Monoid<String> {
  override fun String.combine(b: String): String = "${this}$b"
  override fun empty(): String = ""
}

fun Monoid.Companion.string(): Monoid<String> =
  StringMonoid

private object StringShow : Show<String> {
  override fun String.show(): String = "\"${this.escape()}\""

  private fun String.escape(): String =
    replace("\n", "\\n").replace("\r", "\\r")
      .replace("\"", "\\\"").replace("\'", "\\\'")
      .replace("\t", "\\t").replace("\b", "\\b")
}

fun Show.Companion.string(): Show<String> =
  StringShow

private object StringOrder : Order<String> {
  override fun String.compare(b: String): Ordering =
    Ordering.fromInt(this.compareTo(b))

  override fun String.compareTo(b: String): Int = this.compareTo(b)
}

fun Order.Companion.string(): Order<String> =
  StringOrder

private object StringHash : Hash<String> {
  override fun String.hash(): Int = hashCode()
}

fun Hash.Companion.string(): Hash<String> =
  StringHash
