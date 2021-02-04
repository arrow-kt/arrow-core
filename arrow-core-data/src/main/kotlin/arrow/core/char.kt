package arrow.core

import arrow.typeclasses.Eq
import arrow.typeclasses.Hash
import arrow.typeclasses.Order
import arrow.typeclasses.Show

private object CharShow : Show<Char> {
  override fun Char.show(): String =
    this.toString()
}

private object CharEq : Eq<Char> {
  override fun Char.eqv(b: Char): Boolean = this == b
}

private object CharHash : Hash<Char> {
  override fun Char.hash(): Int = this.hashCode()
}

fun Show.Companion.char(): Show<Char> =
  CharShow

fun Eq.Companion.char(): Eq<Char> =
  CharEq

fun Hash.Companion.char(): Hash<Char> =
  CharHash
