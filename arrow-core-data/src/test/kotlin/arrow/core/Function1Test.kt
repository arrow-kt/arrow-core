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
import arrow.typeclasses.Conested
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.EqK2
import arrow.typeclasses.conest
import arrow.typeclasses.counnest
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll

class Function1Test : UnitSpec() {

  val conestedEQK = object : EqK<Conested<ForFunction1, Int>> {
    override fun <A> Kind<Conested<ForFunction1, Int>, A>.eqK(other: Kind<Conested<ForFunction1, Int>, A>, EQ: Eq<A>): Boolean {
      return this.counnest().invoke(1) == other.counnest().invoke(1)
    }
  }

  val EQ: Eq<Function1Of<Int, Int>> = Eq { a, b ->
    a(1) == b(1)
  }

  fun <A> eqK(a: A) = object : EqK<Function1PartialOf<A>> {
    override fun <B> Kind<Function1PartialOf<A>, B>.eqK(other: Kind<Function1PartialOf<A>, B>, EQ: Eq<B>): Boolean =
      (this.fix() to other.fix()).let { (ls, rs) ->
        EQ.run {
          ls(a).eqv(rs(a))
        }
      }
  }

  fun eqK2() = object : EqK2<ForFunction1> {
    override fun <A, B> Kind2<ForFunction1, A, B>.eqK(other: Kind2<ForFunction1, A, B>, EQA: Eq<A>, EQB: Eq<B>): Boolean =
      (this.fix() to other.fix()).let { (ls, rs) ->
        EQB.run {
          ls(1).eqv(rs(1))
        }
      }
  }

  fun conestedGENK() = object : GenK<Conested<ForFunction1, Int>> {
    override fun <A> genK(gen: Gen<A>): Gen<Kind<Conested<ForFunction1, Int>, A>> =
      gen.map {
        Function1.just<Int, A>(it).conest()
      } as Gen<Kind<Conested<ForFunction1, Int>, A>>
  }

  fun <A> genK() = object : GenK<Function1PartialOf<A>> {
    override fun <B> genK(gen: Gen<B>): Gen<Kind<Function1PartialOf<A>, B>> = gen.map {
      Function1.just<A, B>(it)
    }
  }

  fun genK2() = object : GenK2<ForFunction1> {
    override fun <A, B> genK(genA: Gen<A>, genB: Gen<B>): Gen<Kind2<ForFunction1, A, B>> = genB.map {
      Function1.just<A, B>(it)
    }
  }

  init {
    testLaws(
      MonoidLaws.laws(Function1.monoid<Int, Int>(Int.monoid()), Gen.constant({ a: Int -> a + 1 }.k()), EQ),
      DivisibleLaws.laws(Function1.divisible(Int.monoid()), conestedGENK(), conestedEQK),
      ProfunctorLaws.laws(Function1.profunctor(), genK2(), eqK2()),
      MonadLaws.laws(Function1.monad(), Function1.functor(), Function1.applicative(), Function1.monad(), genK(), eqK(5150)),
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
