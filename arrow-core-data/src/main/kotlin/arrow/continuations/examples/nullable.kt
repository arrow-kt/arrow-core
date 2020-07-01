package arrow.continuations.examples

import arrow.continuations.nullable

suspend fun main() {

  val fa: Int? = 1
  val fb: Float? = 2f
  val fc: Int? = null

  val success: Float? =
    nullable {
      val a: Int = fa()
      val b: Float = fb()
      a + b
    }

  val error: Float? =
    nullable {
      val a: Int = fa()
      val b: Float = fb()
      val c: Int = fc()
      a + b + c
    }
  println(success) // 3.0
  println(error) // null

}

