package arrow.core.extensions

import arrow.core.Endo
import arrow.core.compose
import arrow.core.identity
import arrow.typeclasses.Monoid

interface EndoMonoid<A> : Monoid<Endo<A>> {
  override fun empty(): Endo<A> = Endo(::identity)
  override fun Endo<A>.combine(g: Endo<A>): Endo<A> = Endo(f.compose(g.f))
}
