package arrow.core

import arrow.typeclasses.Eq
import arrow.typeclasses.Hash
import arrow.typeclasses.Monoid
import arrow.typeclasses.Order
import arrow.typeclasses.Semigroup
import arrow.typeclasses.Show
import arrow.typeclasses.hashWithSalt

sealed class Ordering {
  override fun equals(other: Any?): Boolean = this === other // ref equality is fine because objects should be singletons

  override fun toString(): String = show()

  override fun hashCode(): Int = toInt()

  fun toInt(): Int = when (this) {
    LT -> -1
    GT -> 1
    EQ -> 0
  }

  operator fun plus(b: Ordering): Ordering = when (this) {
    LT -> LT
    EQ -> b
    GT -> GT
  }

  fun hash(): Int = hashWithSalt(hashCode())

  fun hashWithSalt(salt: Int): Int =
    salt.hashWithSalt(hashCode())

  fun show(): String = when (this) {
    LT -> "LT"
    GT -> "GT"
    EQ -> "EQ"
  }

  companion object {
    fun fromInt(i: Int): Ordering = when (i) {
      0 -> EQ
      else -> if (i < 0) LT else GT
    }

    fun eq(): Eq<Ordering> = OrderingEq

    fun hash(): Hash<Ordering> = OrderingHash

    fun monoid(): Monoid<Ordering> = OrderingMonoid

    fun order(): Order<Ordering> = OrderingOrder

    fun semigroup(): Semigroup<Ordering> = OrderingMonoid

    fun show(): Show<Ordering> = OrderingShow
  }
}

object LT : Ordering()
object GT : Ordering()
object EQ : Ordering()

fun Ordering.compareTo(b: Ordering): Int =
  OrderingOrder.run { compare(b).toInt() }

fun Ordering.eqv(other: Ordering): Boolean =
  OrderingOrder.run { this@eqv.compare(other) == EQ }

fun Ordering.lt(b: Ordering): Boolean =
  OrderingOrder.run { compare(b) == LT }

fun Ordering.lte(b: Ordering): Boolean =
  OrderingOrder.run { compare(b) != GT }

fun Ordering.gt(b: Ordering): Boolean =
  OrderingOrder.run { compare(b) == GT }

fun Ordering.gte(b: Ordering): Boolean =
  OrderingOrder.run { compare(b) != LT }

fun Ordering.max(b: Ordering): Ordering =
  if (gt(b)) this else b

fun Ordering.min(b: Ordering): Ordering =
  if (lt(b)) this else b

fun Ordering.sort(b: Ordering): Tuple2<Ordering, Ordering> =
  if (gte(b)) Tuple2(this, b) else Tuple2(b, this)

private object OrderingEq : Eq<Ordering> {
  override fun Ordering.eqv(b: Ordering): Boolean = when (this) {
    is LT -> when (b) {
      is LT -> true
      else -> false
    }
    is GT -> when (b) {
      is GT -> true
      else -> false
    }
    is EQ -> when (b) {
      is EQ -> true
      else -> false
    }
  }
}

@Suppress("EXTENSION_SHADOWED_BY_MEMBER")
private object OrderingHash : Hash<Ordering> {
  override fun Ordering.hash(): Int = hash()

  override fun Ordering.hashWithSalt(salt: Int): Int =
    hashWithSalt(salt)
}

private object OrderingMonoid : Monoid<Ordering> {
  override fun empty(): Ordering = EQ

  override fun Ordering.combine(b: Ordering): Ordering =
    this + b
}

private object OrderingOrder : Order<Ordering> {
  override fun Ordering.compare(b: Ordering): Ordering = when (this) {
    is LT -> when (b) {
      is LT -> EQ
      else -> GT
    }
    is GT -> when (b) {
      is GT -> EQ
      else -> LT
    }
    is EQ -> b
  }
}

private object OrderingShow : Show<Ordering> {
  override fun Ordering.show(): String = this.show()
}
