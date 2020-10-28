package arrow.typeclasses

import arrow.typeclasses.suspended.BindSyntax
import kotlin.coroutines.RestrictsSuspension

@RestrictsSuspension
interface EagerBind<F> : BindSyntax<F>
