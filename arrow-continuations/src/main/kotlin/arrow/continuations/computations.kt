package arrow.continuations // ktlint-disable

import arrow.continuations.generic.DelimitedScope

fun interface Effect<F> {
  fun control(): DelimitedScope<F>
}

suspend inline fun <FA, Eff: Effect<FA>, A> computation(
  crossinline just : (A) -> FA,
  crossinline eff: (DelimitedScope<FA>) -> Eff,
  crossinline block: suspend Eff.() -> A
): FA =
  Reset.single { just(block(eff(this))) }
