package arrow

inline class Id(val value: String)

fun interface Repository<A> {
  fun load(id: Id): A
  interface Syntax<A> {
    val value: A
    suspend fun save(): Unit
  }
}

data class User(val id: Id, val name: String) { // User does not need to directly extend Repository<User>
  companion object
}

object UserRepository : Repository<User> {
  override fun load(id: Id): User = User(id, "Curry")
}

@arrow.Coercion
fun User.Companion.repository(): Repository<User> =
  UserRepository
