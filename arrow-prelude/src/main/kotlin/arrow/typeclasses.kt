package arrowx

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

fun interface Semigroup<A> {
  fun A.combine(other: A): A
  interface Syntax<A> {
    val value: A
    fun combine(other: A): A
  }
}
operator fun String.getValue(nothing: Nothing?, property: KProperty<*>): Semigroup.Syntax<String> =
  StringSyntax(this)

inline class StringSyntax(override val value: String) : Semigroup.Syntax<String>, ReadOnlyProperty<String, Semigroup.Syntax<String>> {
  override fun combine(other: String): String =
    value + other

  override fun getValue(thisRef: String, property: KProperty<*>): Semigroup.Syntax<String> = this
}

@arrow.Extension
fun String.semigroupSyntax(): Semigroup.Syntax<String> =
  StringSyntax(this)
