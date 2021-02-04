package arrow.core

import arrow.typeclasses.Eq
import arrow.typeclasses.Hash
import arrow.typeclasses.Monoid
import arrow.typeclasses.Order

private object BooleanEq : Eq<Boolean> {
  override fun Boolean.eqv(b: Boolean): Boolean = this == b
}

private object BooleanHash : Hash<Boolean> {
  override fun Boolean.hash(): Int = this.hashCode()
}

fun Eq.Companion.boolean(): Eq<Boolean> =
  BooleanEq

fun Hash.Companion.boolean(): Hash<Boolean> =
  BooleanHash

private object AndMonoid : Monoid<Boolean> {
  override fun Boolean.combine(b: Boolean): Boolean = this && b
  override fun empty(): Boolean = true
}

fun Monoid.Companion.boolean(): Monoid<Boolean> =
  AndMonoid
