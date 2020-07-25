package arrow.core.extensions

import arrow.core.Ordering
import arrow.typeclasses.Eq
import arrow.typeclasses.Hash
import arrow.typeclasses.Monoid
import arrow.typeclasses.Order
import arrow.typeclasses.Semigroup
import arrow.typeclasses.Show

interface StringSemigroup : Semigroup<String> {
  override fun String.combine(b: String): String = "${this}$b"
}

fun String.Companion.semigroup(): Semigroup<String> =
  object : StringSemigroup {}

interface StringMonoid : Monoid<String>, StringSemigroup {
  override fun empty(): String = ""
}

fun String.Companion.monoid(): Monoid<String> =
  object : StringMonoid {}

interface StringEq : Eq<String> {
  override fun String.eqv(b: String): Boolean = this == b
}

fun String.Companion.eq(): Eq<String> =
  object : StringEq {}

interface StringShow : Show<String> {
  override fun String.show(): String = "\"${this.escape()}\""

  private fun String.escape(): String =
    replace("\n", "\\n").replace("\r", "\\r")
      .replace("\"", "\\\"").replace("\'", "\\\'")
      .replace("\t", "\\t").replace("\b", "\\b")
}

fun String.Companion.show(): Show<String> =
  object : StringShow {}

interface StringOrder : Order<String> {
  override fun String.compare(b: String): Ordering =
    Ordering.fromInt(this.compareTo(b))

  override fun String.compareTo(b: String): Int = this.compareTo(b)
}

fun String.Companion.order(): Order<String> =
  object : StringOrder {}

interface StringHash : Hash<String>, StringEq {
  override fun String.hash(): Int = hashCode()
}

fun String.Companion.hash(): Hash<String> =
  object : StringHash {}

object StringContext : StringShow, StringOrder, StringMonoid

object ForString {
  infix fun <L> extensions(f: StringContext.() -> L): L =
    f(StringContext)
}
