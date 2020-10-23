package arrow.core.computations

import arrow.core.Either
import arrow.core.test.UnitSpec

class EitherContinuationTest : UnitSpec() {
  init {
    "either from nullable should lift value as a Right if it is not null" {
      eitherNew {
        Either.fromNullable(1).invoke()
      } shouldBe Either.Right(1)
    }

    "either from nullable should lift value as a Left if it is null" {
      eitherNew {
        Either.fromNullable(null).invoke()
      } shouldBe Either.Left(Unit)
    }
  }
}
