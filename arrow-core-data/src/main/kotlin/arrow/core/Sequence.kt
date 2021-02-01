package arrow.core

import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup
import kotlin.sequences.plus as _plus

fun <A> Semigroup.Companion.sequence(): Semigroup<Sequence<A>> =
  Monoid.sequence()

fun <A> Monoid.Companion.sequence(): Monoid<Sequence<A>> =
  SequenceMonoid as Monoid<Sequence<A>>

object SequenceMonoid : Monoid<Sequence<Any?>> {
  override fun empty(): Sequence<Any?> = emptySequence()
  override fun Sequence<Any?>.combine(b: Sequence<Any?>): Sequence<Any?> = this._plus(b)
}
