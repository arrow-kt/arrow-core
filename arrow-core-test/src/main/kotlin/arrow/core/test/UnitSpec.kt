package arrow.core.test

import arrow.core.test.laws.Law
import io.kotlintest.TestCase
import io.kotlintest.TestContext
import io.kotlintest.TestType
import io.kotlintest.specs.AbstractStringSpec

/**
 * Base class for unit tests
 */
abstract class UnitSpec(
  private val laws: List<Law> = emptyList(),
  private val tests: UnitSpecContext.() -> Unit = {}
) : AbstractStringSpec() {

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

  override fun testCases(): List<TestCase> =
    (super.testCases() + deprecatedLawTestCases + lawsTestCases + testCases).distinctBy { it.name }

  private val lawsTestCases: List<TestCase> by lazy {
    laws.map { law: Law -> createTestCase(law.name, law.test) }
  }

  private val testCases: List<TestCase> by lazy {
    UnitSpecContext().apply(tests).tests.map { (name, test) -> createTestCase(name, test) }
  }

  private fun createTestCase(name: String, test: suspend TestContext.() -> Unit) =
    createTestCase(name, test, defaultTestCaseConfig, TestType.Test)
}

fun listOfLaws(vararg laws: List<Law>): List<Law> = laws.toList().flatten()

class UnitSpecContext {

  private val _tests = mutableMapOf<String, suspend TestContext.() -> Unit>()
  internal val tests: Map<String, suspend TestContext.() -> Unit> = _tests

  operator fun String.invoke(test: suspend TestContext.() -> Unit) {
    _tests[this] = test
  }
}
