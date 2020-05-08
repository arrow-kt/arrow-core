package arrow.core.test

import arrow.core.test.laws.Law
import io.kotlintest.TestCase
import io.kotlintest.TestType
import io.kotlintest.specs.AbstractStringSpec

/**
 * Base class for unit tests
 */
abstract class UnitSpec(
  private val laws: List<Law> = emptyList(),
  tests: AbstractStringSpec.() -> Unit = {}
) : AbstractStringSpec(tests) {

  private val deprecatedLawTestCases = mutableListOf<TestCase>()

  @Deprecated("Use the laws parameter instead of calling this inside init {}")
  fun testLaws(vararg laws: List<Law>): List<TestCase> = laws
    .flatMap { list: List<Law> -> list.asIterable() }
    .distinctBy { law: Law -> law.name }
    .map { law: Law ->
      val lawTestCase = createTestCase(law.name, law.test, defaultTestCaseConfig, TestType.Test)
      deprecatedLawTestCases.add(lawTestCase)
      lawTestCase
    }

  override fun testCases(): List<TestCase> = super.testCases() + deprecatedLawTestCases + lawsTestCases

  private val lawsTestCases: List<TestCase> by lazy {
    laws.distinctBy { law: Law -> law.name }
      .map { law: Law -> createTestCase(law.name, law.test, defaultTestCaseConfig, TestType.Test) }
  }
}

fun listOfLaws(vararg laws: List<Law>): List<Law> = laws.toList().flatten()
