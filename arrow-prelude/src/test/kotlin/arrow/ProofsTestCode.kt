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
    """

  val userRepositoryCode2 =
    """
      package prelude

      import arrow.Extension
      
      interface Repository<A> {
          val entity: A
          fun save(): Unit =
              println("saved")
      }
      
      data class User(val name: String)
      
      class UserRepository(override val entity: User) : Repository<User>
      
      @Extension
      fun User.repository(): Repository<User> =
          UserRepository(this)
      
      fun savedUser() = User("Jane").save()
    """
}
