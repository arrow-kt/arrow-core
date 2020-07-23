package arrow.core.extensions.semigroup

import arrow.core.extensions.hash
import arrow.core.semigroup.All
import arrow.extension
import arrow.typeclasses.Eq
import arrow.typeclasses.Hash
import arrow.typeclasses.Monoid
import arrow.typeclasses.Order
import arrow.typeclasses.Semigroup
import arrow.typeclasses.Show

@extension
interface AllEq : Eq<All> {
  override fun All.eqv(b: All): Boolean = getAll == b.getAll
}

@extension
interface AllShow : Show<All> {
  override fun All.show(): String = "All($getAll)"
}

@extension
interface AllOrder : Order<All> {
  override fun All.compare(b: All): Int = getAll.compareTo(b.getAll)
}

@extension
interface AllHash : Hash<All>, AllEq {
  override fun All.hash(): Int = Boolean.hash().run { getAll.hash() }
}

@extension
interface AllSemigroup : Semigroup<All> {
  override fun All.combine(b: All): All = All(getAll && b.getAll)
}

@extension
interface AllMonoid : Monoid<All>, AllSemigroup {
  override fun empty(): All = All(true)
}
