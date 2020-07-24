package arrow.core.extensions

import arrow.typeclasses.EQ
import arrow.typeclasses.Eq
import arrow.typeclasses.GT
import arrow.typeclasses.Hash
import arrow.typeclasses.LT
import arrow.typeclasses.Order
import arrow.typeclasses.Ordering
import arrow.typeclasses.Show

interface CharShow : Show<Char> {
  override fun Char.show(): String =
    this.toString()
}

interface CharEq : Eq<Char> {
  override fun Char.eqv(b: Char): Boolean = this == b
}

interface CharOrder : Order<Char> {
  override fun Char.eqv(b: Char): Boolean = this == b

  override fun Char.compare(b: Char): Ordering =
    if (this < b) LT else if (this > b) GT else EQ

  override fun Char.lt(b: Char): Boolean = this < b

  override fun Char.lte(b: Char): Boolean = this <= b

  override fun Char.gt(b: Char): Boolean = this > b

  override fun Char.gte(b: Char): Boolean = this >= b

  override fun Char.neqv(b: Char): Boolean = this != b
}

interface CharHash : Hash<Char>, CharEq {
  override fun Char.hash(): Int = this.hashCode()
}

fun Char.Companion.show(): Show<Char> =
  object : CharShow {}

fun Char.Companion.eq(): Eq<Char> =
  object : CharEq {}

fun Char.Companion.order(): Order<Char> =
  object : CharOrder {}

fun Char.Companion.hash(): Hash<Char> =
  object : CharHash {}
