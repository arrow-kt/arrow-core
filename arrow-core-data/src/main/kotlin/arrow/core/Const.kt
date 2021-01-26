package arrow.core

import arrow.Kind
import arrow.typeclasses.Applicative
import arrow.typeclasses.Semigroup
import arrow.typeclasses.Show

@Deprecated("Kind is deprecated, and will be removed in 0.13.0. Please use one of the provided concrete methods instead")
class ForConst private constructor() { companion object }
@Deprecated("Kind is deprecated, and will be removed in 0.13.0. Please use one of the provided concrete methods instead")
typealias ConstOf<A, T> = arrow.Kind2<ForConst, A, T>
@Deprecated("Kind is deprecated, and will be removed in 0.13.0. Please use one of the provided concrete methods instead")
typealias ConstPartialOf<A> = arrow.Kind<ForConst, A>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
@Deprecated("Kind is deprecated, and will be removed in 0.13.0. Please use one of the provided concrete methods instead")
inline fun <A, T> ConstOf<A, T>.fix(): Const<A, T> =
  this as Const<A, T>

fun <A, T> ConstOf<A, T>.value(): A = this.fix().value()

data class Const<A, out T>(private val value: A) : ConstOf<A, T> {

  @Suppress("UNCHECKED_CAST")
  fun <U> retag(): Const<A, U> =
    this as Const<A, U>

  @Suppress("UNUSED_PARAMETER")
  fun <G, U> traverse(GA: Applicative<G>, f: (T) -> Kind<G, U>): Kind<G, Const<A, U>> =
    GA.just(retag())

  @Suppress("UNUSED_PARAMETER")
  fun <G, U> traverseFilter(GA: Applicative<G>, f: (T) -> Kind<G, Option<U>>): Kind<G, Const<A, U>> =
    GA.just(retag())

  companion object {
    fun <A, T> just(a: A): Const<A, T> =
      Const(a)
  }

  fun value(): A =
    value

  fun show(SA: Show<A>): String =
    "$Const(${SA.run { value.show() }})"

  override fun toString(): String =
    show(Show.any())
}

fun <A, T> ConstOf<A, T>.combine(SG: Semigroup<A>, that: ConstOf<A, T>): Const<A, T> =
  Const(SG.run { value().combine(that.value()) })

fun <A, T, U> ConstOf<A, T>.ap(SG: Semigroup<A>, ff: ConstOf<A, (T) -> U>): Const<A, U> =
  fix().retag<U>().combine(SG, ff.fix().retag())

fun <T, A, G> ConstOf<A, Kind<G, T>>.sequence(GA: Applicative<G>): Kind<G, Const<A, T>> =
  fix().traverse(GA, ::identity)

inline fun <A> A.const(): Const<A, Nothing> =
  Const(this)
