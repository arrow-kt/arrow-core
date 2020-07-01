package arrow.continuations.examples

import arrow.continuations.either
import arrow.core.Either
import arrow.core.left
import arrow.core.right

suspend fun main() {

  val fa: Either<String, Int> = 1.right()
  val fb: Either<String, Float> = 2f.right()
  val fc: Either<String, Int> = "not an int".left()

  val success: Either<String, Float> =
    either {
      val a: Int = fa()
      val b: Float = fb()
      a + b
    }

  val error: Either<String, Float> =
    either {
      val a: Int = fa()
      val b: Float = fb()
      val c: Int = fc()
      a + b + c
    }
  println(success) // Right(3.0)
  println(error) // Left(not an int)

}

