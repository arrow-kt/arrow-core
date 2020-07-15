package arrow.core.test.laws

import arrow.typeclasses.Eq
import arrow.typeclasses.Show
import io.kotest.property.Arb
import io.kotest.property.forAll

object ShowLaws {

  fun <F> laws(S: Show<F>, EQ: Eq<F>, GEN: Arb<F>): List<Law> =
    EqLaws.laws(EQ, GEN) + listOf(
      Law("Show Laws: equality") { S.equalShow(EQ, GEN) }
    )

  private suspend fun <F> Show<F>.equalShow(EQ: Eq<F>, GEN: Arb<F>): Unit =
    forAll(GEN, GEN) { a, b ->
      if (EQ.run { a.eqv(b) })
        a.show() == b.show()
      else
        true
    }
}
