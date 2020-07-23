package arrow.core.extensions.semigroup

import arrow.core.extensions.hash
import arrow.core.semigroup.Any
import arrow.extension
import arrow.typeclasses.Eq
import arrow.typeclasses.Hash
import arrow.typeclasses.Monoid
import arrow.typeclasses.Order
import arrow.typeclasses.Semigroup
import arrow.typeclasses.Show

@extension
interface AnyEq : Eq<Any> {
  override fun Any.eqv(b: Any): Boolean = getAny == b.getAny
}

@extension
interface AnyShow : Show<Any> {
  override fun Any.show(): String = "Any($getAny)"
}

@extension
interface AnyOrder : Order<Any> {
  override fun Any.compare(b: Any): Int = getAny.compareTo(b.getAny)
}

@extension
interface AnyHash : Hash<Any>, AnyEq {
  override fun Any.hash(): Int = Boolean.hash().run { getAny.hash() }
}

@extension
interface AnySemigroup : Semigroup<Any> {
  override fun Any.combine(b: Any): Any = Any(getAny || b.getAny)
}

@extension
interface AnyMonoid : Monoid<Any>, AnySemigroup {
  override fun empty(): Any = Any(false)
}
