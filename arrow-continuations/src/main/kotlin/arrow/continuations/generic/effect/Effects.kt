package arrow.continuations.generic.effect

interface Error<E> {
  suspend fun raise(e: E): Nothing
  suspend fun <A> catch(f: suspend () -> A, hdl: suspend (E) -> A): A
}

interface Empty {
  suspend fun empty(): Nothing
}

interface Choose {
  suspend fun choose(): Boolean
}

suspend inline fun <A> Choose.choice(f: () -> A, g: () -> A): A = if (choose()) f() else g()

interface NonDet : Choose, Empty
