package arrow.core.extensions.semigroup

import arrow.core.AndThen
import arrow.core.semigroup.Endo
import arrow.core.identity
import arrow.extension
import arrow.typeclasses.Monoid
import arrow.typeclasses.Semigroup

@extension
interface EndoSemigroup<A> : Semigroup<Endo<A>> {
  override fun Endo<A>.combine(b: Endo<A>): Endo<A> = Endo(AndThen(this.appEndo).andThen(b.appEndo))
}

@extension
interface EndoMonoid<A> : Monoid<Endo<A>>, EndoSemigroup<A> {
  override fun empty(): Endo<A> = Endo(::identity)
}
