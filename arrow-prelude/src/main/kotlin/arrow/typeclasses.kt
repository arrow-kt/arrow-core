package arrowx

import arrow.Extension
import arrow.Given
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun interface Semigroup<A> {
  fun A.combine(other: A): A
  interface Syntax<A> {
    val value: A
    fun combine(other: A): A
  }
}

operator fun String.Companion.getValue(nothing: Nothing?, property: KProperty<*>): Monoid<String> =
  StringMonoid

operator fun String.getValue(nothing: Nothing?, property: KProperty<*>): Semigroup.Syntax<String> =
  StringSyntax(this)

interface Monoid<A> : Semigroup<A> {
  fun empty(): A
}

@Given
object StringMonoid : Monoid<String>, ReadOnlyProperty<String, Monoid<String>> {
  override fun String.combine(other: String): String = this + other
  override fun empty(): String = ""
  override fun getValue(thisRef: String, property: KProperty<*>): Monoid<String> = this
}

inline class StringSyntax(override val value: String) : Semigroup.Syntax<String>, ReadOnlyProperty<String, Semigroup.Syntax<String>> {
  override fun combine(other: String): String =
    value + other

  override fun getValue(thisRef: String, property: KProperty<*>): Semigroup.Syntax<String> = this
}

@Extension
fun String.Companion.monoid(): Monoid<String> =
  StringMonoid

@Extension
fun String.semigroupSyntax(): Semigroup.Syntax<String> =
  StringSyntax(this)
