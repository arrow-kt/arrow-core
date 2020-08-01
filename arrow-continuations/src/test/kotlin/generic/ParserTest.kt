package generic

import arrow.continuations.generic.effect.Parser
import arrow.continuations.generic.effect.choice
import arrow.continuations.generic.effect.runParser
import arrow.core.identity
import io.kotlintest.specs.StringSpec

sealed class Language
object German : Language()
object English : Language()

// example
val parser: Parser<Language> = {
  attempt { choice({ string("Wooo") }, { string("Wee") }) }.fold({ string("Error") }, ::identity)
  val lang = optional {
    choice({ string("Hello"); English }, { string("Hallo"); German })
      .also { eof() }
  }
  lang ?: English
}

class ParserTest : StringSpec({
  "can parse" {
    parser
      .runParser("WooHalloWeird")
      .also(::println)
  }
})
