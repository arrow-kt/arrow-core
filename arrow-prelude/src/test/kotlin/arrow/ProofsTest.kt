package arrow

import arrow.meta.plugin.testing.CompilerPlugin
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.Dependency
import arrow.meta.plugin.testing.assertThis
import org.junit.jupiter.api.Test

class ProofsTest {

  private val dependencies = CompilerTest.metaDependencies + CompilerTest.addArguments("-Xallow-jvm-ir-dependencies")

  @Test
  fun `extension function`() {
    assertThis(CompilerTest(
      config = {
        dependencies
      },
      code = {
        ProofsTestCode.semigroupExtensionCode.source
      },
      assert = {
        allOf("result".source.evalsTo("one-two"))
      }
    ))
  }

  @Test
  fun `coercion function`() {
    assertThis(CompilerTest(
      config = {
        dependencies
      },
      code = {
        ProofsTestCode.userRepositoryCode.source
      },
      assert = {
        compiles
      }
    ))
  }
}
