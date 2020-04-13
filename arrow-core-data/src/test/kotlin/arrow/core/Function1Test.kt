package arrow.core

import arrow.Kind
import arrow.Kind2
import arrow.core.extensions.function1.applicative.applicative
import arrow.core.extensions.function1.category.category
import arrow.core.extensions.function1.divisible.divisible
import arrow.core.extensions.function1.functor.functor
import arrow.core.extensions.function1.monad.monad
import arrow.core.extensions.function1.monoid.monoid
import arrow.core.extensions.function1.profunctor.profunctor
import arrow.core.extensions.function1.semigroup.semigroup
import arrow.core.extensions.monoid
import arrow.core.extensions.semigroup
import arrow.core.test.UnitSpec
import arrow.core.test.generators.GenK
import arrow.core.test.generators.GenK2
import arrow.core.test.laws.CategoryLaws
import arrow.core.test.laws.DivisibleLaws
import arrow.core.test.laws.MonadLaws
import arrow.core.test.laws.MonoidLaws
import arrow.core.test.laws.ProfunctorLaws
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.EqK2
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

class Function1Test : UnitSpec() {

  val EQ: Eq<Function1Of<Int, Int>> = Eq { a, b ->
    a(1) == b(1)
  }

  fun eqK() = object : EqK<ForFunction1> {
    override fun <A> Function1PartialOf<A>.eqK(other: Kind<ForFunction1, A>, EQ: Eq<A>): Boolean =
      (this.fix() to other.fix()).let { (ls, rs) ->
        EQ.run {
          ls(Unit).eqv(rs(Unit))
        }
      }
  }

  fun eqK2() = object : EqK2<ForFunction1> {
    override fun <A, B> Kind2<ForFunction1, A, B>.eqK(other: Kind2<ForFunction1, A, B>, EQA: Eq<A>, EQB: Eq<B>): Boolean =
      (this.fix() to other.fix()).let { (ls, rs) ->
        eqK().run {
          ls.unnest<B>().eqK(rs.unnest(), EQB)
        }
      }
  }

  fun genK() = object : GenK<ForFunction1> {
    override fun <B> genK(gen: Gen<B>): Gen<Kind<ForFunction1, B>> = gen.map {
      Function1.just<Unit, B>(it).unnest()
    }
  }

  fun genK2() = object : GenK2<ForFunction1> {
    override fun <A, B> genK(genA: Gen<A>, genB: Gen<B>): Gen<Kind2<ForFunction1, A, B>> =
      genK().genK(genB).map { it.nest() }
  }

  init {
    testLaws(
      MonoidLaws.laws(Function1.monoid<Int, Int>(Int.monoid()), Gen.constant({ a: Int -> a + 1 }.k()), EQ),
      DivisibleLaws.laws(Function1.divisible(Int.monoid()), genK(), eqK()),
      ProfunctorLaws.laws(Function1.profunctor(), genK2(), eqK2()),
      MonadLaws.laws(Function1.monad(), Function1.functor(), Function1.applicative(), Function1.monad(), genK(), eqK()),
      CategoryLaws.laws(Function1.category(), genK2(), eqK2())
    )

    "Semigroup of Function1<A> is Function1<Semigroup<A>>" {
      forAll { a: Int ->
        val left = Function1.semigroup<Int, Int>(Int.semigroup()).run {
          Function1<Int, Int> { it }.combine(Function1 { it })
        }

        val right = Function1<Int, Int> { Int.monoid().run { it.combine(it) } }

        left.invoke(a) == right.invoke(a)
      }
    }

    "Function1<A>.empty() is Function1{A.empty()}" {
      forAll { a: Int, b: Int ->
        val left = Function1.monoid<Int, Int>(Int.monoid()).run { empty() }
        val right = Function1<Int, Int> { Int.monoid().run { empty() } }
        left.invoke(a) == right.invoke(b)
      }
    }
  }
}
