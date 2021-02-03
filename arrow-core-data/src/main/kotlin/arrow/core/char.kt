package arrow.core

import arrow.typeclasses.Hash
import arrow.typeclasses.Order
import arrow.typeclasses.Show

private object CharShow : Show<Char> {
  override fun Char.show(): String =
    this.toString()
}

private object CharOrder : Order<Char> {
  override fun Char.compare(b: Char): Ordering =
    Ordering.fromInt(this.compareTo(b))

  override fun Char.compareTo(b: Char): Int =
    this.compareTo(b)
}

private object CharHash : Hash<Char> {
  override fun Char.hash(): Int = this.hashCode()
}

fun Show.Companion.char(): Show<Char> =
  CharShow

fun Order.Companion.char(): Order<Char> =
  CharOrder

fun Hash.Companion.char(): Hash<Char> =
  CharHash
