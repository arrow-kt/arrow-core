package arrow

import arrow.meta.plugin.testing.CompilerPlugin
import arrow.meta.plugin.testing.CompilerTest
import arrow.meta.plugin.testing.Dependency
import arrow.meta.plugin.testing.assertThis
import org.junit.jupiter.api.Test

class ProofsTest {

  @Test
  fun `extension function`() {
    val arrowMetaVersion = System.getProperty("ARROW_META_VERSION")
    val arrowMetaCompilerPlugin = CompilerPlugin("Arrow Meta", listOf(Dependency("compiler-plugin:$arrowMetaVersion")))
    val prelude = Dependency("arrow-prelude")


    assertThis(CompilerTest(
      config = {
        addCompilerPlugins(arrowMetaCompilerPlugin) + CompilerTest.addDependencies(prelude)
      },
      code = {
        """
        |  import arrow.*
        |  import arrowx.*       
        |  
        | val result = "one-".combine("two")
        """.source
      },
      assert = {
        allOf("result".source.evalsTo("one-two"))
      }
    ))
  }

  @Test
  fun `coercion function`() {
    val arrowMetaVersion = System.getProperty("ARROW_META_VERSION")
    val arrowMetaCompilerPlugin = CompilerPlugin("Arrow Meta", listOf(Dependency("compiler-plugin:$arrowMetaVersion")))
    val prelude = Dependency("arrow-prelude")


    assertThis(CompilerTest(
      config = {
        addCompilerPlugins(arrowMetaCompilerPlugin) + CompilerTest.addDependencies(prelude)
      },
      code = {
        """
        | import arrow.*
        | import arrowx.*      
        |  
        | inline class Id(val value: String)
        |  
        | data class User(val id: Id, val name: String) { // User does not need to directly extend Repository<User>
        |   companion object
        | }
        | 
        | fun interface Repository<A> {
        |     fun load(id: Id): A
        |     interface Syntax<A> {
        |       val value: A
        |       suspend fun save(): Unit
        |    }
        | }
        |  object UserRepository : Repository<User> {
        |     override fun load(id: Id): User = User(id, "Curry")
        | }
        | 
        | @arrow.Coercion
        | fun User.Companion.repository(): Repository<User> =
        |   UserRepository
        |
        | val result = User.load(Id("Curry")).name
        """.source
      },
      assert = {
        allOf("result".source.evalsTo("Curry"))
      }
    ))
  }
}
