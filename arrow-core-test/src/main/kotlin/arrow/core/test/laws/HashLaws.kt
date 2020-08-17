package arrow.core.test.laws

import arrow.typeclasses.Eq
import arrow.typeclasses.Hash
import io.kotest.property.Arb
import io.kotest.property.arbitrary.int
import io.kotest.property.forAll

object HashLaws {

  fun <F> laws(HF: Hash<F>, G: Arb<F>, EQ: Eq<F>): List<Law> =
    listOf(
      Law("Hash Laws: Equality implies equal hash") { equalHash(HF, EQ, G) },
      Law("Hash Laws: Multiple calls to hash should result in the same hash") { equalHashM(HF, G) },
        Law("Hash Laws: Multiple calls to hashWithSalt with the same salt should result in the same hash") { equalHashWithSaltM(HF, G) }
    )

  private suspend fun <F> equalHash(HF: Hash<F>, EQ: Eq<F>, G: Arb<F>) {
    forAll(G, G, Arb.int()) { a, b, salt ->
      if (EQ.run { a.eqv(b) })
        HF.run { a.hash() == b.hash() } && HF.run { a.hashWithSalt(salt) == b.hashWithSalt(salt) }
      else
        true
    }
  }

  private suspend fun <F> equalHashM(HF: Hash<F>, G: Arb<F>) {
    forAll(G) { a ->
      HF.run { a.hash() == a.hash() }
    }
  }

  private suspend fun <F> equalHashWithSaltM(HF: Hash<F>, G: Arb<F>) {
    forAll(G, Arb.int()) { a, salt ->
      HF.run { a.hashWithSalt(salt) == a.hashWithSalt(salt) }
    }
  }
}
