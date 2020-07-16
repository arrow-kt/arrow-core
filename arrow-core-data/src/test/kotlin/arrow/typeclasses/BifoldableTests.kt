package arrow.typeclasses

import arrow.Kind2
import arrow.core.Either
import arrow.core.Eval
import arrow.core.ForEither
import arrow.core.fix
import arrow.mtl.typeclasses.Nested
import arrow.mtl.typeclasses.binest
import arrow.mtl.typeclasses.compose
import arrow.core.test.UnitSpec
import arrow.core.test.generators.GenK2
import arrow.core.test.generators.either
import arrow.core.test.generators.intSmall
import arrow.core.test.laws.BifoldableLaws
import io.kotest.property.Arb
import io.kotest.property.arbitrary.map

class BifoldableTests : UnitSpec() {
  init {
    val eitherBifoldable: Bifoldable<ForEither> = object : Bifoldable<ForEither> {
      override fun <A, B, C> Kind2<ForEither, A, B>.bifoldLeft(c: C, f: (C, A) -> C, g: (C, B) -> C): C =
        this.fix().run {
          when (this) {
            is Either.Left -> f(c, a)
            is Either.Right -> g(c, b)
          }
        }

      override fun <A, B, C> Kind2<ForEither, A, B>.bifoldRight(c: Eval<C>, f: (A, Eval<C>) -> Eval<C>, g: (B, Eval<C>) -> Eval<C>): Eval<C> =
        when (this) {
          is Either.Left -> f(a, c)
          else -> g((this as Either.Right).b, c)
        }
    }

    val eitherComposeEither = eitherBifoldable.compose(eitherBifoldable)

    val eitherGen = Arb.either(Arb.intSmall(), Arb.intSmall())

    val genK2 = object : GenK2<Nested<ForEither, ForEither>> {
      override fun <A, B> genK(genA: Arb<A>, genB: Arb<B>): Arb<Kind2<Nested<ForEither, ForEither>, A, B>> =
        Arb.either(eitherGen, Arb.either(genA, genB)).map {
          it.binest()
        } as Arb<Kind2<Nested<ForEither, ForEither>, A, B>>
    }

    testLaws(BifoldableLaws.laws(eitherComposeEither, genK2))
  }
}
