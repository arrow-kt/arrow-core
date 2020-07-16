package arrow.typeclasses

import arrow.Kind
import arrow.core.extensions.monoid
import arrow.core.extensions.monoid.invariant.invariant
import arrow.core.test.UnitSpec
import arrow.core.test.generators.GenK
import arrow.core.test.laws.InvariantLaws
import arrow.core.test.laws.equalUnderTheLaw
import io.kotest.property.Arb
import io.kotest.property.arbitrary.constant

class MonoidTest : UnitSpec() {

  fun EQK() = object : EqK<ForMonoid> {
    override fun <A> Kind<ForMonoid, A>.eqK(other: Kind<ForMonoid, A>, EQ: Eq<A>): Boolean =
      (this.fix() to other.fix()).let { (ls, rs) ->

        val l = ls.run {
          ls.empty().combine(ls.empty())
        }
        val r = rs.fix().run { rs.empty().combine(rs.empty()) }

        l.equalUnderTheLaw(r, EQ)
      }
  }

  fun <A> genk(M: Monoid<A>) = object : GenK<ForMonoid> {
    override fun <A> genK(gen: Arb<A>): Arb<Kind<ForMonoid, A>> =
      Arb.constant(M) as Arb<Kind<ForMonoid, A>>
  }

  init {
    testLaws(
      InvariantLaws.laws(Monoid.invariant<Int>(), genk(Int.monoid()), EQK())
    )
  }
}
