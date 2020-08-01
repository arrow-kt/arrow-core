package arrow.continuations.generic.effect

interface Error<E> {
  suspend fun raise(e: E): Nothing
  suspend fun <Eff : Error<E>, A> Eff.catch(f: suspend Eff.() -> A, hdl: suspend Eff.(E) -> A): A
}

interface Empty {
  suspend fun empty(): Nothing
}

interface Choose {
  suspend fun choose(): Boolean
}

interface NonDet : Choose, Empty
