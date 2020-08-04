package eveff

import arrow.continuations.eveff.Pure
import arrow.continuations.eveff.prompt
import arrow.continuations.eveff.runCtl
import arrow.continuations.eveff.yield
import io.kotlintest.specs.StringSpec

class CtlTest : StringSpec({
  "test" {
    prompt<Int> {
      yield<Int, Int>(it) { k -> k(10).flatMap(k) }
        .map { i -> i * 2 }
    }.runCtl().also { println(it) }
  }
})
