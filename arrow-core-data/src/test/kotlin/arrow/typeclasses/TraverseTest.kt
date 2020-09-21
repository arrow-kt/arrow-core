package arrow.typeclasses

import arrow.core.Either
import arrow.core.extensions.either.applicative.applicative
import arrow.core.extensions.sequence.traverse.sequence
import arrow.core.left
import arrow.core.right
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class TraverseTest : StringSpec({
  "traverse is stacksafe over very long collections and short circuits properly" {
    // This has to traverse 50k elements till it reaches None and terminates
    generateSequence(0) { it + 1 }.map { if (it < 50_000) it.right() else Unit.left() }
      .sequence(Either.applicative()) shouldBe Unit.left()
  }
})
