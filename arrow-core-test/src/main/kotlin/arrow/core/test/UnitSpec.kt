package arrow.core.test

import arrow.core.test.laws.Law
import io.kotest.core.spec.createTestCase
import io.kotest.core.spec.style.StringSpec
import io.kotest.core.test.TestCase
import io.kotest.core.test.TestName
import io.kotest.core.test.TestType

/**
 * Base class for unit tests
 */
abstract class UnitSpec : StringSpec() {

  private val lawTestCases = mutableListOf<TestCase>()

  fun testLaws(vararg laws: List<Law>): List<TestCase> = laws
    .flatMap { list: List<Law> -> list.asIterable() }
    .distinctBy { law: Law -> law.name }
    .map { law: Law ->
      val lawTestCase = createTestCase(TestName(name = law.name), law.test, defaultTestCaseConfig()!!, TestType.Test)
      lawTestCases.add(lawTestCase)
      lawTestCase
    }

  override fun materializeRootTests(): List<TestCase> = super.materializeRootTests() + lawTestCases
}
