package arrow.core

import arrow.typeclasses.Eq
import arrow.typeclasses.Hash

private object CharEq : Eq<Char> {
  override fun Char.eqv(b: Char): Boolean = this == b
}

private object CharHash : Hash<Char> {
  override fun Char.hash(): Int = this.hashCode()
}

fun Eq.Companion.char(): Eq<Char> =
  CharEq

fun Hash.Companion.char(): Hash<Char> =
  CharHash
