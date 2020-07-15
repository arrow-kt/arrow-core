package arrow.core.test.generators

import arrow.Kind2
import arrow.core.Either
import arrow.core.ForEither
import arrow.core.ForIor
import arrow.core.ForValidated
import arrow.core.Ior
import arrow.core.Validated
import io.kotest.property.Arb

interface GenK2<F> {
  /**
   * lifts Arb<A> and Arb<B> to the context F. the resulting Arb can be used to create types Kind2<F, A, B>
   */
  fun <A, B> genK(genA: Arb<A>, genB: Arb<B>): Arb<Kind2<F, A, B>>
}

fun Either.Companion.genK2() =
  object : GenK2<ForEither> {
    override fun <A, B> genK(genA: Arb<A>, genB: Arb<B>): Arb<Kind2<ForEither, A, B>> =
      Arb.either(genA, genB) as Arb<Kind2<ForEither, A, B>>
  }

fun Ior.Companion.genK2() =
  object : GenK2<ForIor> {
    override fun <A, B> genK(genA: Arb<A>, genB: Arb<B>): Arb<Kind2<ForIor, A, B>> =
      Arb.ior(genA, genB) as Arb<Kind2<ForIor, A, B>>
  }

fun Validated.Companion.genK2() =
  object : GenK2<ForValidated> {
    override fun <A, B> genK(genA: Arb<A>, genB: Arb<B>): Arb<Kind2<ForValidated, A, B>> =
      Arb.validated(genA, genB) as Arb<Kind2<ForValidated, A, B>>
  }
