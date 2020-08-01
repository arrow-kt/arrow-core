package generic

import arrow.Kind
import arrow.continuations.generic.DelimitedScope
import arrow.continuations.generic.MultiShotDelimContScope
import arrow.continuations.generic.effect.eitherListHandler
import arrow.continuations.generic.effect.listEitherHandler
import arrow.continuations.generic.effect.myFun
import arrow.core.Either
import arrow.core.EitherPartialOf
import arrow.core.ForId
import arrow.core.ForListK
import arrow.core.Id
import arrow.core.ListK
import arrow.core.extensions.id.applicative.applicative
import arrow.core.k
import arrow.core.value
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec

class T : StringSpec({
  "run" {
    MultiShotDelimContScope.reset<Id<ListK<Either<String, Int>>>> {
      val hdl = listEitherHandler(Id.applicative(), this as DelimitedScope<Kind<ForId, Kind<ForListK, Kind<EitherPartialOf<String>, Int>>>>)
      Id(listOf(Either.Right(hdl.myFun())).k())
    }.value() shouldBe listOf(Either.Left("Better luck next time"), Either.Right(42))
  }
  "run2" {
    MultiShotDelimContScope.reset<Id<Either<String, ListK<Int>>>> {
      val hdl = eitherListHandler(Id.applicative(), this as DelimitedScope<Kind<ForId, Kind<EitherPartialOf<String>, Kind<ForListK, Int>>>>)
      Id(Either.Right(listOf(hdl.myFun()).k()))
    }.value() shouldBe Either.Left("Better luck next time")
  }
})
