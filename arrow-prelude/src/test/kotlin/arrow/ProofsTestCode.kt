package arrow

object ProofsTestCode {

  val userRepositoryCode =
    """
      package arrow
      
      import arrow.*
      import arrowx.*
      
      inline class Id(val value: String)
      
      data class User(val id: Id, val name: String) { // User does not need to directly extend Repository<User>
        companion object
      }
      
      fun interface Repository<A> {
        fun load(id: Id): A
        interface Syntax<A> {
          val value: A
          suspend fun save(): Unit
        }
      }
      
      object UserRepository : Repository<User> {
        override fun load(id: Id): User = User(id, "Curry")
      }
      
      @arrow.Extension
      fun User.Companion.repository(): Repository<User> =
        UserRepository
        
      val result = User.load(Id("Curry")).name
    """.trimIndent()
}
