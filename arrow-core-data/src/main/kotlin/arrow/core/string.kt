package arrow.core

import arrow.typeclasses.Eq
import arrow.typeclasses.Hash
import arrow.typeclasses.Monoid
import arrow.typeclasses.Order
import arrow.typeclasses.Semigroup

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

private object StringEq : Eq<String> {
  override fun String.eqv(b: String): Boolean = this == b
}

fun Eq.Companion.string(): Eq<String> =
  StringEq

fun String.escaped(): String =
  replace("\n", "\\n").replace("\r", "\\r")
    .replace("\"", "\\\"").replace("\'", "\\\'")
    .replace("\t", "\\t").replace("\b", "\\b")

private object StringHash : Hash<String> {
  override fun String.hash(): Int = hashCode()
}

fun Hash.Companion.string(): Hash<String> =
  StringHash
