package arrow.core.test.laws

import arrow.Kind
import arrow.core.Ior
import arrow.core.Tuple2
import arrow.core.extensions.eq
import arrow.core.extensions.ior.eq.eq
import arrow.core.extensions.tuple2.eq.eq
import arrow.core.toT
import arrow.core.test.generators.GenK
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Foldable
import arrow.typeclasses.Semialign
import arrow.typeclasses.Unalign
import io.kotest.property.Arb
import io.kotest.property.RandomSource
import io.kotest.property.arbitrary.int
import io.kotest.property.arbitrary.arb
import io.kotest.property.forAll

object UnalignLaws {
  fun <F> laws(
    UA: Unalign<F>,
    GENK: GenK<F>,
    EQK: EqK<F>
  ): List<Law> = SemialignLaws.laws(UA, GENK, EQK) + unalignLaws(UA, GENK, EQK)

  fun <F> laws(
    UA: Unalign<F>,
    GENK: GenK<F>,
    EQK: EqK<F>,
    FOLD: Foldable<F>
  ): List<Law> = SemialignLaws.laws(UA, GENK, EQK, FOLD) + unalignLaws(UA, GENK, EQK)

  private fun <F> unalignLaws(
    UA: Unalign<F>,
    GENK: GenK<F>,
    EQK: EqK<F>
  ): List<Law> {
    val iorIntEq = EQK.liftEq(Ior.eq(Int.eq(), Int.eq()))
    val intEq = EQK.liftEq(Int.eq())
    val tuple2Eq = Tuple2.eq(intEq, intEq)
    val intGen = GENK.genK(Arb.int())

    return listOf(
      Law("Unalign Laws: unalign inverts align") { UA.unalignInvertsAlign(intGen, tuple2Eq) },
      Law("Unalign Laws: align inverts unalign") {
        UA.alignInvertsUnalign(iorGen(UA, intGen, intGen), iorIntEq)
      }
    )
  }

  private suspend fun <F, A, B> Unalign<F>.alignInvertsUnalign(G: Arb<Kind<F, Ior<A, B>>>, EQ: Eq<Kind<F, Ior<A, B>>>) =
    forAll(G) { xs ->
      val alignTuple: (Tuple2<Kind<F, A>, Kind<F, B>>) -> Kind<F, Ior<A, B>> =
        { (a, b) -> align(a, b) }
      alignTuple(unalign(xs)).equalUnderTheLaw(xs, EQ)
    }

  private suspend fun <F, A> Unalign<F>.unalignInvertsAlign(G: Arb<Kind<F, A>>, EQ: Eq<Tuple2<Kind<F, A>, Kind<F, A>>>) =
    forAll(G, G) { a, b ->
      unalign(align(a, b)).equalUnderTheLaw(a toT b, EQ)
    }
}

private fun <F, A, B> iorGen(
  SA: Semialign<F>,
  genA: Arb<Kind<F, A>>,
  genB: Arb<Kind<F, B>>
): Arb<Kind<F, Ior<A, B>>> = arb<Kind<F, Ior<A, B>>>(
    genA.edgecases().zip(genB.edgecases()).map { SA.align(it.first, it.second) }
) {
  sequence {
    val vsA = genA.values(RandomSource.Default).iterator()
    val vsB = genB.values(RandomSource.Default).iterator()

    while (vsA.hasNext()) {
      val a = vsA.next()

      if (vsB.hasNext()) {
        val b = vsB.next()

        yield(SA.align(a, b))
      }
    }
  }
}
