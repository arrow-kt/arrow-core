package arrow.core

import arrow.Kind
import arrow.typeclasses.Applicative
import arrow.typeclasses.Eq
import arrow.typeclasses.Hash
import arrow.typeclasses.Monoid
import arrow.typeclasses.Order
import arrow.typeclasses.Semigroup
import arrow.typeclasses.Show
import arrow.typeclasses.defaultSalt
import arrow.typeclasses.hashWithSalt

typealias ValidatedNel<E, A> = Validated<Nel<E>, A>
typealias Valid<A> = Validated.Valid<A>
typealias Invalid<E> = Validated.Invalid<E>

@Deprecated("Kind is deprecated, and will be removed in 0.13.0. Please use one of the provided concrete methods instead")
class ForValidated private constructor() {
  companion object
}
@Deprecated("Kind is deprecated, and will be removed in 0.13.0. Please use one of the provided concrete methods instead")
typealias ValidatedOf<E, A> = arrow.Kind2<ForValidated, E, A>
@Deprecated("Kind is deprecated, and will be removed in 0.13.0. Please use one of the provided concrete methods instead")
typealias ValidatedPartialOf<E> = arrow.Kind<ForValidated, E>

@Suppress("UNCHECKED_CAST", "NOTHING_TO_INLINE")
@Deprecated("Kind is deprecated, and will be removed in 0.13.0. Please use one of the provided concrete methods instead")
inline fun <E, A> ValidatedOf<E, A>.fix(): Validated<E, A> =
  this as Validated<E, A>

/**
 *
 *
 * Imagine you are filling out a web form to sign up for an account. You input your username and
 * password, then submit. A response comes back saying your username can't have dashes in it,
 * so you make some changes, then resubmit. You can't have special characters either. Change, resubmit.
 * Password needs to have at least one capital letter. Change, resubmit. Password needs to have at least one number.
 *
 * Or perhaps you're reading from a configuration file. One could imagine the configuration library
 * you're using returns an `Either`. Your parsing may look something like:
 *
 * ```kotlin:ank
 * import arrow.core.Either
 * import arrow.core.Left
 * import arrow.core.flatMap
 *
 * //sampleStart
 * data class ConnectionParams(val url: String, val port: Int)
 *
 * fun <A> config(key: String): Either<String, A> = Left(key)
 *
 * config<String>("url").flatMap { url ->
 *  config<Int>("port").map { ConnectionParams(url, it) }
 * }
 * //sampleEnd
 * ```
 *
 * You run your program and it says key "url" not found. Turns out the key was "endpoint." So
 * you change your code and re-run. Now it says the "port" key was not a well-formed integer.
 *
 * It would be nice to have all of these errors reported simultaneously. The username's inability to
 * have dashes can be validated separately from it not having special characters, as well as
 * from the password needing to have certain requirements. A misspelled (or missing) field in
 * a config can be validated separately from another field not being well-formed.
 *
 * # Enter `Validated`.
 *
 * ## Parallel Validation
 *
 * Our goal is to report any and all errors across independent bits of data. For instance, when
 * we ask for several pieces of configuration, each configuration field can be validated separately
 * from one another. How then do we ensure that the data we are working with is independent?
 * We ask for both of them up front.
 *
 * As our running example, we will look at config parsing. Our config will be represented by a
 * `Map<String, String>`. Parsing will be handled by a `Read` type class - we provide instances only
 * for `String` and `Int` for brevity.
 *
 * ```kotlin:ank
 * import arrow.core.None
 * import arrow.core.Option
 *
 * //sampleStart
 * abstract class Read<A> {
 *
 * abstract fun read(s: String): Option<A>
 *
 *  companion object {
 *
 *   val stringRead: Read<String> =
 *    object: Read<String>() {
 *     override fun read(s: String): Option<String> = Option(s)
 *    }
 *
 *   val intRead: Read<Int> =
 *    object: Read<Int>() {
 *     override fun read(s: String): Option<Int> =
 *      if (s.matches(Regex("-?[0-9]+"))) Option(s.toInt()) else None
 *    }
 *  }
 * }
 * //sampleEnd
 * ```
 *
 * Then we enumerate our errors. When asking for a config value, one of two things can go wrong:
 * The field is missing, or it is not well-formed with regards to the expected type.
 *
 * ```kotlin:ank
 * sealed class ConfigError {
 *  data class MissingConfig(val field: String): ConfigError()
 *  data class ParseConfig(val field: String): ConfigError()
 * }
 * ```
 *
 * We need a data type that can represent either a successful value (a parsed configuration), or an error.
 * It would look like the following, which Arrow provides in `arrow.Validated`:
 *
 * ```kotlin
 * @higherkind sealed class Validated<out E, out A> : ValidatedOf<E, A> {
 *  data class Valid<out A>(val a: A) : Validated<Nothing, A>()
 *  data class Invalid<out E>(val e: E) : Validated<E, Nothing>()
 * }
 * ```
 *
 * Now we are ready to write our parser.
 *
 * ```kotlin:ank
 * import arrow.core.None
 * import arrow.core.Option
 * import arrow.core.Some
 * import arrow.core.Validated
 * import arrow.core.valid
 * import arrow.core.invalid
 *
 * //sampleStart
 * data class Config(val map: Map<String, String>) {
 *  fun <A> parse(read: Read<A>, key: String): Validated<ConfigError, A> {
 *   val v = Option.fromNullable(map[key])
 *   return when (v) {
 *    is Some ->
 *     when (val s = read.read(v.t)) {
 *      is Some -> s.t.valid()
 *      is None -> ConfigError.ParseConfig(key).invalid()
 *     }
 *    is None -> Validated.Invalid(ConfigError.MissingConfig(key))
 *   }
 *  }
 * }
 * //sampleEnd
 * ```
 *
 * And, as you can see, the parser runs sequentially: it first tries to get the map value and then tries to read it.
 * It's then straightforward to translate this to an effect block. We use here the `either` block which includes syntax
 * to obtain `A` from values of `Validated<*, A>` through the [arrow.core.computations.EitherEffect.invoke]
 *
 * ```kotlin:ank
 * import arrow.core.None
 * import arrow.core.Option
 * import arrow.core.Some
 * import arrow.core.Validated
 * import arrow.core.computations.either
 * import arrow.core.valid
 * import arrow.core.invalid
 *
 * //sampleStart
 * data class Config(val map: Map<String, String>) {
 *   suspend fun <A> parse(read: Read<A>, key: String) = either<ConfigError, A> {
 *     val value = Validated.fromNullable(map[key]) {
 *       ConfigError.MissingConfig(key)
 *     }()
 *     val readVal = Validated.fromOption(read.read(value)) {
 *       ConfigError.ParseConfig(key)
 *     }()
 *     readVal
 *   }
 * }
 * //sampleEnd
 * ```
 *
 * Everything is in place to write the parallel validator. Remember that we can only do parallel
 * validation if each piece is independent. How do we ensure the data is independent? By
 * asking for all of it up front. Let's start with two pieces of data.
 *
 * ```kotlin:ank
 * import arrow.core.Validated
 * //sampleStart
 * fun <E, A, B, C> parallelValidate(v1: Validated<E, A>, v2: Validated<E, B>, f: (A, B) -> C): Validated<E, C> {
 *  return when {
 *   v1 is Validated.Valid && v2 is Validated.Valid -> Validated.Valid(f(v1.a, v2.a))
 *   v1 is Validated.Valid && v2 is Validated.Invalid -> v2
 *   v1 is Validated.Invalid && v2 is Validated.Valid -> v1
 *   v1 is Validated.Invalid && v2 is Validated.Invalid -> TODO()
 *   else -> TODO()
 *  }
 * }
 * //sampleEnd
 * ```
 *
 * We've run into a problem. In the case where both have errors, we want to report both. We
 * don't have a way to combine ConfigErrors. But, as clients, we can change our Validated
 * values where the error can be combined, say, a `List<ConfigError>`. We are going to use a
 * `NonEmptyList<ConfigError>`—the NonEmptyList statically guarantees we have at least one value,
 * which aligns with the fact that, if we have an Invalid, then we most certainly have at least one error.
 * This technique is so common there is a convenient method on `Validated` called `toValidatedNel`
 * that turns any `Validated<E, A>` value to a `Validated<NonEmptyList<E>, A>`. Additionally, the
 * type alias `ValidatedNel<E, A>` is provided.
 *
 * Time to validate:
 *
 * ```kotlin:ank
 * import arrow.core.NonEmptyList
 * import arrow.core.Validated
 * //sampleStart
 * fun <E, A, B, C> parallelValidate
 *   (v1: Validated<E, A>, v2: Validated<E, B>, f: (A, B) -> C): Validated<NonEmptyList<E>, C> =
 *  when {
 *   v1 is Validated.Valid && v2 is Validated.Valid -> Validated.Valid(f(v1.a, v2.a))
 *   v1 is Validated.Valid && v2 is Validated.Invalid -> v2.toValidatedNel()
 *   v1 is Validated.Invalid && v2 is Validated.Valid -> v1.toValidatedNel()
 *   v1 is Validated.Invalid && v2 is Validated.Invalid -> Validated.Invalid(NonEmptyList(v1.e, listOf(v2.e)))
 *   else -> throw IllegalStateException("Not possible value")
 *  }
 * //sampleEnd
 * ```
 *
 * ### Improving the validation
 *
 * Kotlin says that our match is not exhaustive and we have to add `else`. To solve this, we would need to nest our when,
 * but that would complicate the code. To achieve this, Arrow provides an [arrow.typeclasses.Applicative] through the
 * `applicativeNel` function in arrow-core to unlock `tupledN`.
 * This function combines [Validated]s by accumulating errors in a tuple, which we can then map.
 * The above function can be rewritten as follows:
 *
 * ```kotlin:ank:silent
 * import arrow.core.Validated
 * import arrow.core.fix
 * import arrow.reflect.applicativeNel
 *
 * // added manually due to deps
 *
 * val v1: Validated<ConfigError, Int> = Validated.Valid(1)
 * val v2: Validated<ConfigError, Int> = Validated.Valid(2)
 *
 * //sampleStart
 * val parallelValidate = Validated.applicativeNel<ConfigError>()
 *     .tupledN(v1.toValidatedNel(), v2.toValidatedNel()).fix()
 *     .map { (a, b) -> /* combine the result */ }
 * //sampleEnd
 * ```
 *
 * Note that there are multiple `tupledN` functions with more arities, so we could easily add more parameters without worrying about
 * the function blowing up in complexity.
 *
 * ---
 *
 * Coming back to our example, when no errors are present in the configuration, we get a `ConnectionParams` wrapped in a `Valid` instance.
 *
 * ```kotlin:ank:playground
 * import arrow.core.None
 * import arrow.core.Option
 * import arrow.core.Some
 * import arrow.core.Validated
 * import arrow.core.computations.either
 * import arrow.core.valid
 * import arrow.core.invalid
 * import arrow.core.NonEmptyList
 * import arrow.core.fix
 * import arrow.reflect.applicativeNel
 *
 * data class ConnectionParams(val url: String, val port: Int)
 *
 * abstract class Read<A> {
 *  abstract fun read(s: String): Option<A>
 *
 *  companion object {
 *
 *   val stringRead: Read<String> =
 *    object : Read<String>() {
 *     override fun read(s: String): Option<String> = Option(s)
 *    }
 *
 *   val intRead: Read<Int> =
 *    object : Read<Int>() {
 *     override fun read(s: String): Option<Int> =
 *      if (s.matches(Regex("-?[0-9]+"))) Option(s.toInt()) else None
 *    }
 *  }
 * }
 *
 * sealed class ConfigError {
 *  data class MissingConfig(val field: String) : ConfigError()
 *  data class ParseConfig(val field: String) : ConfigError()
 * }
 *
 * data class Config(val map: Map<String, String>) {
 *   suspend fun <A> parse(read: Read<A>, key: String) = either<ConfigError, A> {
 *     val value = Validated.fromNullable(map[key]) {
 *       ConfigError.MissingConfig(key)
 *     }()
 *     val readVal = Validated.fromOption(read.read(value)) {
 *       ConfigError.ParseConfig(key)
 *     }()
 *     readVal
 *   }.toValidatedNel()
 * }
 *
 * val parallelValidate = Validated.applicativeNel<ConfigError>()
 *
 * suspend fun main() {
 * //sampleStart
 *  val config = Config(mapOf("url" to "127.0.0.1", "port" to "1337"))
 *
 *  val valid = parallelValidate.tupledN(
 *  config.parse(Read.stringRead, "url"),
 *  config.parse(Read.intRead, "port")
 *  ).fix().map { (url, port) -> ConnectionParams(url, port) }
 * //sampleEnd
 *  println("valid = $valid")
 * }
 * ```
 *
 * But what happens when we have one or more errors? They are accumulated in a `NonEmptyList` wrapped in
 * an `Invalid` instance.
 *
 * ```kotlin:ank:playground
 * import arrow.core.None
 * import arrow.core.Option
 * import arrow.core.Some
 * import arrow.core.Validated
 * import arrow.core.computations.either
 * import arrow.core.valid
 * import arrow.core.invalid
 * import arrow.core.NonEmptyList
 * import arrow.core.fix
 * import arrow.reflect.applicativeNel
 *
 * data class ConnectionParams(val url: String, val port: Int)
 *
 * abstract class Read<A> {
 *  abstract fun read(s: String): Option<A>
 *
 *  companion object {
 *
 *   val stringRead: Read<String> =
 *    object : Read<String>() {
 *     override fun read(s: String): Option<String> = Option(s)
 *    }
 *
 *   val intRead: Read<Int> =
 *    object : Read<Int>() {
 *     override fun read(s: String): Option<Int> =
 *      if (s.matches(Regex("-?[0-9]+"))) Option(s.toInt()) else None
 *    }
 *  }
 * }
 *
 * sealed class ConfigError {
 *  data class MissingConfig(val field: String) : ConfigError()
 *  data class ParseConfig(val field: String) : ConfigError()
 * }
 *
 * data class Config(val map: Map<String, String>) {
 *   suspend fun <A> parse(read: Read<A>, key: String) = either<ConfigError, A> {
 *     val value = Validated.fromNullable(map[key]) {
 *       ConfigError.MissingConfig(key)
 *     }()
 *     val readVal = Validated.fromOption(read.read(value)) {
 *       ConfigError.ParseConfig(key)
 *     }()
 *     readVal
 *   }.toValidatedNel()
 * }
 *
 * val parallelValidate = Validated.applicativeNel<ConfigError>()
 *
 * suspend fun main() {
 * //sampleStart
 * val config = Config(mapOf("wrong field" to "127.0.0.1", "port" to "not a number"))
 *
 * val valid = parallelValidate.tupledN(
 *  config.parse(Read.stringRead, "url"),
 *  config.parse(Read.intRead, "port")
 *  ).fix().map { (url, port) -> ConnectionParams(url, port) }
 * //sampleEnd
 *  println("valid = $valid")
 * }
 * ```
 *
 * ## Sequential Validation
 *
 * If you do want error accumulation, but occasionally run into places where sequential validation is needed,
 * then Validated provides a `withEither` method to allow you to temporarily turn a Validated
 * instance into an Either instance and apply it to a function.
 *
 * ```kotlin:ank:playground
 * import arrow.core.Either
 * import arrow.core.flatMap
 * import arrow.core.left
 * import arrow.core.None
 * import arrow.core.Option
 * import arrow.core.right
 * import arrow.core.Some
 * import arrow.core.Validated
 * import arrow.core.computations.either
 * import arrow.core.valid
 * import arrow.core.invalid
 *
 * abstract class Read<A> {
 *  abstract fun read(s: String): Option<A>
 *
 *  companion object {
 *
 *   val stringRead: Read<String> =
 *    object : Read<String>() {
 *     override fun read(s: String): Option<String> = Option(s)
 *    }
 *
 *   val intRead: Read<Int> =
 *    object : Read<Int>() {
 *     override fun read(s: String): Option<Int> =
 *      if (s.matches(Regex("-?[0-9]+"))) Option(s.toInt()) else None
 *    }
 *  }
 * }
 *
 * data class Config(val map: Map<String, String>) {
 *   suspend fun <A> parse(read: Read<A>, key: String) = either<ConfigError, A> {
 *     val value = Validated.fromNullable(map[key]) {
 *       ConfigError.MissingConfig(key)
 *     }()
 *     val readVal = Validated.fromOption(read.read(value)) {
 *       ConfigError.ParseConfig(key)
 *     }()
 *     readVal
 *   }.toValidatedNel()
 * }
 *
 * sealed class ConfigError {
 *  data class MissingConfig(val field: String) : ConfigError()
 *  data class ParseConfig(val field: String) : ConfigError()
 * }
 *
 * //sampleStart
 * fun positive(field: String, i: Int): Either<ConfigError, Int> {
 *  return if (i >= 0) i.right()
 *  else ConfigError.ParseConfig(field).left()
 * }
 *
 * val config = Config(mapOf("house_number" to "-42"))
 *
 * suspend fun main() {
 *   val houseNumber = config.parse(Read.intRead, "house_number").withEither { either ->
 *     either.flatMap { positive("house_number", it) }
 *   }
 * //sampleEnd
 *  println(houseNumber)
 * }
 *
 * ```
 *
 * ## Alternative validation strategies to Validated: using `ApplicativeError`
 *
 * We may use `ApplicativeError` instead of `Validated` to abstract away validation strategies and raising errors in the context we are computing in.
 *
 * ```kotlin:ank
 * import arrow.Kind
 * import arrow.core.Either
 * import arrow.core.EitherPartialOf
 * import arrow.core.Nel
 * import arrow.core.NonEmptyList
 * import arrow.core.Validated
 * import arrow.core.ValidatedPartialOf
 * import arrow.core.nel
 * import arrow.typeclasses.ApplicativeError
 * import arrow.core.extensions.validated.applicativeError.applicativeError
 * import arrow.core.extensions.either.applicativeError.applicativeError
 * import arrow.core.extensions.nonemptylist.semigroup.semigroup
 *
 * //sampleStart
 * sealed class ValidationError(val msg: String) {
 *  data class DoesNotContain(val value: String) : ValidationError("Did not contain $value")
 *  data class MaxLength(val value: Int) : ValidationError("Exceeded length of $value")
 *  data class NotAnEmail(val reasons: Nel<ValidationError>) : ValidationError("Not a valid email")
 * }
 *
 * data class FormField(val label: String, val value: String)
 * data class Email(val value: String)
 *
 * sealed class Rules<F>(A: ApplicativeError<F, Nel<ValidationError>>) : ApplicativeError<F, Nel<ValidationError>> by A {
 *
 *  private fun FormField.contains(needle: String): Kind<F, FormField> =
 *   if (value.contains(needle, false)) just(this)
 *   else raiseError(ValidationError.DoesNotContain(needle).nel())
 *
 *  private fun FormField.maxLength(maxLength: Int): Kind<F, FormField> =
 *   if (value.length <= maxLength) just(this)
 *   else raiseError(ValidationError.MaxLength(maxLength).nel())
 *
 *  fun FormField.validateEmail(): Kind<F, Email> =
 *   map(contains("@"), maxLength(250), {
 *    Email(value)
 *   }).handleErrorWith { raiseError(ValidationError.NotAnEmail(it).nel()) }
 *
 *  object ErrorAccumulationStrategy :
 *    Rules<ValidatedPartialOf<Nel<ValidationError>>>(Validated.applicativeError(NonEmptyList.semigroup()))
 *
 *  object FailFastStrategy :
 *   Rules<EitherPartialOf<Nel<ValidationError>>>(Either.applicativeError())
 *
 *  companion object {
 *   infix fun <A> failFast(f: FailFastStrategy.() -> A): A = f(FailFastStrategy)
 *   infix fun <A> accumulateErrors(f: ErrorAccumulationStrategy.() -> A): A = f(ErrorAccumulationStrategy)
 *  }
 * }
 * //sampleEnd
 * ```
 *
 * `Rules` defines abstract behaviors that can be composed and have access to the scope of `ApplicativeError` where we can invoke `just` to lift values into the positive result and `raiseError` into the error context.
 *
 * Once we have such abstract algebra defined, we can simply materialize it to data types that support different error strategies:
 *
 *  *Error accumulation*
 *
 * ```kotlin:ank:playground
 * import arrow.Kind
 * import arrow.core.Either
 * import arrow.core.EitherPartialOf
 * import arrow.core.Nel
 * import arrow.core.NonEmptyList
 * import arrow.core.Validated
 * import arrow.core.ValidatedPartialOf
 * import arrow.core.nel
 * import arrow.typeclasses.ApplicativeError
 * import arrow.core.extensions.validated.applicativeError.applicativeError
 * import arrow.core.extensions.either.applicativeError.applicativeError
 * import arrow.core.extensions.nonemptylist.semigroup.semigroup
 *
 * sealed class ValidationError(val msg: String) {
 *  data class DoesNotContain(val value: String) : ValidationError("Did not contain $value")
 *  data class MaxLength(val value: Int) : ValidationError("Exceeded length of $value")
 *  data class NotAnEmail(val reasons: Nel<ValidationError>) : ValidationError("Not a valid email")
 * }
 *
 * data class FormField(val label: String, val value: String)
 * data class Email(val value: String)
 *
 * sealed class Rules<F>(A: ApplicativeError<F, Nel<ValidationError>>) : ApplicativeError<F, Nel<ValidationError>> by A {
 *
 *  private fun FormField.contains(needle: String): Kind<F, FormField> =
 *   if (value.contains(needle, false)) just(this)
 *   else raiseError(ValidationError.DoesNotContain(needle).nel())
 *
 *  private fun FormField.maxLength(maxLength: Int): Kind<F, FormField> =
 *   if (value.length <= maxLength) just(this)
 *   else raiseError(ValidationError.MaxLength(maxLength).nel())
 *
 *  fun FormField.validateEmail(): Kind<F, Email> =
 *   map(contains("@"), maxLength(250), {
 *    Email(value)
 *   }).handleErrorWith { raiseError(ValidationError.NotAnEmail(it).nel()) }
 *
 *  object ErrorAccumulationStrategy :
 *    Rules<ValidatedPartialOf<Nel<ValidationError>>>(Validated.applicativeError(NonEmptyList.semigroup()))
 *
 *  object FailFastStrategy :
 *   Rules<EitherPartialOf<Nel<ValidationError>>>(Either.applicativeError())
 *
 *  companion object {
 *   infix fun <A> failFast(f: FailFastStrategy.() -> A): A = f(FailFastStrategy)
 *   infix fun <A> accumulateErrors(f: ErrorAccumulationStrategy.() -> A): A = f(ErrorAccumulationStrategy)
 *  }
 * }
 *
 * val value =
 * //sampleStart
 *  Rules accumulateErrors {
 *    listOf(
 *      FormField("Invalid Email Domain Label", "nowhere.com"),
 *      FormField("Too Long Email Label", "nowheretoolong${(0..251).map { "g" }}"), //this accumulates N errors
 *      FormField("Valid Email Label", "getlost@nowhere.com")
 *    ).map { it.validateEmail() }
 *    }
 * //sampleEnd
 * fun main() {
 *  println(value)
 * }
 * ```
 *  *Fail Fast*
 *
 * ```kotlin:ank:playground
 * import arrow.Kind
 * import arrow.core.Either
 * import arrow.core.EitherPartialOf
 * import arrow.core.Nel
 * import arrow.core.NonEmptyList
 * import arrow.core.Validated
 * import arrow.core.ValidatedPartialOf
 * import arrow.core.nel
 * import arrow.typeclasses.ApplicativeError
 * import arrow.core.extensions.validated.applicativeError.applicativeError
 * import arrow.core.extensions.either.applicativeError.applicativeError
 * import arrow.core.extensions.nonemptylist.semigroup.semigroup
 *
 * sealed class ValidationError(val msg: String) {
 *  data class DoesNotContain(val value: String) : ValidationError("Did not contain $value")
 *  data class MaxLength(val value: Int) : ValidationError("Exceeded length of $value")
 *  data class NotAnEmail(val reasons: Nel<ValidationError>) : ValidationError("Not a valid email")
 * }
 *
 * data class FormField(val label: String, val value: String)
 * data class Email(val value: String)
 *
 * sealed class Rules<F>(A: ApplicativeError<F, Nel<ValidationError>>) : ApplicativeError<F, Nel<ValidationError>> by A {
 *
 *  private fun FormField.contains(needle: String): Kind<F, FormField> =
 *   if (value.contains(needle, false)) just(this)
 *   else raiseError(ValidationError.DoesNotContain(needle).nel())
 *
 *  private fun FormField.maxLength(maxLength: Int): Kind<F, FormField> =
 *   if (value.length <= maxLength) just(this)
 *   else raiseError(ValidationError.MaxLength(maxLength).nel())
 *
 *  fun FormField.validateEmail(): Kind<F, Email> =
 *   map(contains("@"), maxLength(250), {
 *    Email(value)
 *   }).handleErrorWith { raiseError(ValidationError.NotAnEmail(it).nel()) }
 *
 *  object ErrorAccumulationStrategy :
 *    Rules<ValidatedPartialOf<Nel<ValidationError>>>(Validated.applicativeError(NonEmptyList.semigroup()))
 *
 *  object FailFastStrategy :
 *   Rules<EitherPartialOf<Nel<ValidationError>>>(Either.applicativeError())
 *
 *  companion object {
 *   infix fun <A> failFast(f: FailFastStrategy.() -> A): A = f(FailFastStrategy)
 *   infix fun <A> accumulateErrors(f: ErrorAccumulationStrategy.() -> A): A = f(ErrorAccumulationStrategy)
 *  }
 * }
 *
 * val value =
 * //sampleStart
 *  Rules failFast {
 *    listOf(
 *      FormField("Invalid Email Domain Label", "nowhere.com"),
 *      FormField("Too Long Email Label", "nowheretoolong${(0..251).map { "g" }}"), //this fails fast
 *      FormField("Valid Email Label", "getlost@nowhere.com")
 *    ).map { it.validateEmail() }
 *  }
 * //sampleEnd
 * fun main() {
 *  println(value)
 * }
 * ```
 *
 * ### Supported type classes
 *
 * ```kotlin:ank:replace
 * import arrow.reflect.DataType
 * import arrow.reflect.tcMarkdownList
 * import arrow.core.Validated
 *
 * DataType(Validated::class).tcMarkdownList()
 * ```
 */
sealed class Validated<out E, out A> : ValidatedOf<E, A> {

  companion object {

    fun <E, A> invalidNel(e: E): ValidatedNel<E, A> = Invalid(NonEmptyList(e, listOf()))

    fun <E, A> validNel(a: A): ValidatedNel<E, A> = Valid(a)

    /**
     * Converts an `Either<E, A>` to a `Validated<E, A>`.
     */
    fun <E, A> fromEither(e: Either<E, A>): Validated<E, A> = e.fold({ Invalid(it) }, { Valid(it) })

    /**
     * Converts an `Option<A>` to a `Validated<E, A>`, where the provided `ifNone` output value is returned as [Invalid]
     * when the specified `Option` is `None`.
     */
    inline fun <E, A> fromOption(o: Option<A>, ifNone: () -> E): Validated<E, A> =
      o.fold(
        { Invalid(ifNone()) },
        { Valid(it) }
      )

    /**
     * Converts a nullable `A?` to a `Validated<E, A>`, where the provided `ifNull` output value is returned as [Invalid]
     * when the specified value is null.
     */
    inline fun <E, A> fromNullable(value: A?, ifNull: () -> E): Validated<E, A> =
      value?.let(::Valid) ?: Invalid(ifNull())

    inline fun <A> catch(f: () -> A): Validated<Throwable, A> =
      try {
        f().valid()
      } catch (e: Throwable) {
        e.nonFatalOrThrow().invalid()
      }

    inline fun <E, A> catch(recover: (Throwable) -> E, f: () -> A): Validated<E, A> =
      catch(f).mapLeft(recover)

    inline fun <A> catchNel(f: () -> A): ValidatedNel<Throwable, A> =
      try {
        f().validNel()
      } catch (e: Throwable) {
        e.nonFatalOrThrow().invalidNel()
      }

    /** Construct an [Eq] instance which use [EQL] and [EQR] to compare the [Invalid] and [Valid] cases **/
    fun <L, R> eq(EQL: Eq<L>, EQR: Eq<R>): Eq<Validated<L, R>> =
      ValidatedEq(EQL, EQR)

    fun <L, R> hash(HL: Hash<L>, HR: Hash<R>): Hash<Validated<L, R>> =
      ValidatedHash(HL, HR)

    fun <L, R> show(SL: Show<L>, SR: Show<R>): Show<Validated<L, R>> =
      ValidatedShow(SL, SR)

    fun <L, R> order(OL: Order<L>, OR: Order<R>): Order<Validated<L, R>> =
      ValidatedOrder(OL, OR)

    /**
     * Lifts a function `A -> B` to the [Validated] structure.
     *
     * `A -> B -> Validated<E, A> -> Validated<E, B>`
     *
     * ```kotlin:ank:playground:extension
     * import arrow.core.*
     *
     * fun main(args: Array<String>) {
     *   val result =
     *   //sampleStart
     *   Validated.lift { s: CharSequence -> "$s World" }("Hello".valid())
     *   //sampleEnd
     *   println(result)
     * }
     * ```
     */
    fun <E, A, B> lift(f: (A) -> B): (Validated<E, A>) -> Validated<E, B> =
      { fa -> fa.map(f) }

    /**
     * Lifts two functions to the Bifunctor type.
     *
     * ```kotlin:ank
     * import arrow.core.*
     *
     * fun main(args: Array<String>) {
     *   //sampleStart
     *   val f = Validated.lift(String::toUpperCase, Int::inc)
     *   val res1 = f("test".invalid())
     *   val res2 = f(1.valid())
     *   //sampleEnd
     *   println("res1: $res1")
     *   println("res2: $res2")
     * }
     * ```
     */
    fun <A, B, C, D> lift(fl: (A) -> C, fr: (B) -> D): (Validated<A, B>) -> Validated<C, D> =
      { fa -> fa.bimap(fl, fr) }

    val s = 1.inc()

    val unit: Validated<Nothing, Unit> = Unit.valid()

    fun <E> unit(): Validated<E, Unit> = unit

    fun <EE, A, B, Z> mapN(
      SE: Semigroup<EE>,
      a: Validated<EE, A>,
      b: Validated<EE, B>,
      lbd: (Tuple2<A, B>) -> Z
    ): Validated<EE, Z> =
      tupledN(SE, a, b).map(lbd)

    fun <EE, A, B, C, Z> mapN(
      SE: Semigroup<EE>,
      a: Validated<EE, A>,
      b: Validated<EE, B>,
      c: Validated<EE, C>,
      lbd: (Tuple3<A, B, C>) -> Z
    ): Validated<EE, Z> =
      tupledN(SE, a, b, c).map(lbd)

    fun <EE, A, B, C, D, Z> mapN(
      SE: Semigroup<EE>,
      a: Validated<EE, A>,
      b: Validated<EE, B>,
      c: Validated<EE, C>,
      d: Validated<EE, D>,
      lbd: (Tuple4<A, B, C, D>) -> Z
    ): Validated<EE, Z> =
      tupledN(SE, a, b, c, d).map(lbd)

    fun <EE, A, B, C, D, E, Z> mapN(
      SE: Semigroup<EE>,
      a: Validated<EE, A>,
      b: Validated<EE, B>,
      c: Validated<EE, C>,
      d: Validated<EE, D>,
      e: Validated<EE, E>,
      lbd: (Tuple5<A, B, C, D, E>) -> Z
    ): Validated<EE, Z> =
      tupledN(SE, a, b, c, d, e).map(lbd)

    fun <EE, A, B, C, D, E, FF, Z> mapN(
      SE: Semigroup<EE>,
      a: Validated<EE, A>,
      b: Validated<EE, B>,
      c: Validated<EE, C>,
      d: Validated<EE, D>,
      e: Validated<EE, E>,
      f: Validated<EE, FF>,
      lbd: (Tuple6<A, B, C, D, E, FF>) -> Z
    ): Validated<EE, Z> =
      tupledN(SE, a, b, c, d, e, f).map(lbd)

    fun <EE, A, B, C, D, E, FF, G, Z> mapN(
      SE: Semigroup<EE>,
      a: Validated<EE, A>,
      b: Validated<EE, B>,
      c: Validated<EE, C>,
      d: Validated<EE, D>,
      e: Validated<EE, E>,
      f: Validated<EE, FF>,
      g: Validated<EE, G>,
      lbd: (Tuple7<A, B, C, D, E, FF, G>) -> Z
    ): Validated<EE, Z> =
      tupledN(SE, a, b, c, d, e, f, g).map(lbd)

    fun <EE, A, B, C, D, E, FF, G, H, Z> mapN(
      SE: Semigroup<EE>,
      a: Validated<EE, A>,
      b: Validated<EE, B>,
      c: Validated<EE, C>,
      d: Validated<EE, D>,
      e: Validated<EE, E>,
      f: Validated<EE, FF>,
      g: Validated<EE, G>,
      h: Validated<EE, H>,
      lbd: (Tuple8<A, B, C, D, E, FF, G, H>) -> Z
    ): Validated<EE, Z> =
      tupledN(SE, a, b, c, d, e, f, g, h).map(lbd)

    fun <EE, A, B, C, D, E, FF, G, H, I, Z> mapN(
      SE: Semigroup<EE>,
      a: Validated<EE, A>,
      b: Validated<EE, B>,
      c: Validated<EE, C>,
      d: Validated<EE, D>,
      e: Validated<EE, E>,
      f: Validated<EE, FF>,
      g: Validated<EE, G>,
      h: Validated<EE, H>,
      i: Validated<EE, I>,
      lbd: (Tuple9<A, B, C, D, E, FF, G, H, I>) -> Z
    ): Validated<EE, Z> =
      tupledN(SE, a, b, c, d, e, f, g, h, i).map(lbd)

    fun <EE, A, B, C, D, E, FF, G, H, I, J, Z> mapN(
      SE: Semigroup<EE>,
      a: Validated<EE, A>,
      b: Validated<EE, B>,
      c: Validated<EE, C>,
      d: Validated<EE, D>,
      e: Validated<EE, E>,
      f: Validated<EE, FF>,
      g: Validated<EE, G>,
      h: Validated<EE, H>,
      i: Validated<EE, I>,
      j: Validated<EE, J>,
      lbd: (Tuple10<A, B, C, D, E, FF, G, H, I, J>) -> Z
    ): Validated<EE, Z> =
      tupledN(SE, a, b, c, d, e, f, g, h, i, j).map(lbd)

    fun <EE, A, B> tupledN(
      SE: Semigroup<EE>,
      a: Validated<EE, A>,
      b: Validated<EE, B>
    ): Validated<EE, Tuple2<A, B>> =
      a.product(SE, b)

    fun <EE, A, B, C> tupledN(
      SE: Semigroup<EE>,
      a: Validated<EE, A>,
      b: Validated<EE, B>,
      c: Validated<EE, C>
    ): Validated<EE, Tuple3<A, B, C>> =
      a.product(SE, b).product(SE, c)

    fun <EE, A, B, C, D> tupledN(
      SE: Semigroup<EE>,
      a: Validated<EE, A>,
      b: Validated<EE, B>,
      c: Validated<EE, C>,
      d: Validated<EE, D>
    ): Validated<EE, Tuple4<A, B, C, D>> =
      a.product(SE, b).product(SE, c).product(SE, d)

    fun <EE, A, B, C, D, E> tupledN(
      SE: Semigroup<EE>,
      a: Validated<EE, A>,
      b: Validated<EE, B>,
      c: Validated<EE, C>,
      d: Validated<EE, D>,
      e: Validated<EE, E>
    ): Validated<EE, Tuple5<A, B, C, D, E>> =
      a.product(SE, b).product(SE, c).product(SE, d).product(SE, e)

    fun <EE, A, B, C, D, E, FF> tupledN(
      SE: Semigroup<EE>,
      a: Validated<EE, A>,
      b: Validated<EE, B>,
      c: Validated<EE, C>,
      d: Validated<EE, D>,
      e: Validated<EE, E>,
      f: Validated<EE, FF>
    ): Validated<EE, Tuple6<A, B, C, D, E, FF>> =
      a.product(SE, b).product(SE, c).product(SE, d).product(SE, e).product(SE, f)

    fun <EE, A, B, C, D, E, FF, G> tupledN(
      SE: Semigroup<EE>,
      a: Validated<EE, A>,
      b: Validated<EE, B>,
      c: Validated<EE, C>,
      d: Validated<EE, D>,
      e: Validated<EE, E>,
      f: Validated<EE, FF>,
      g: Validated<EE, G>
    ): Validated<EE, Tuple7<A, B, C, D, E, FF, G>> =
      a.product(SE, b).product(SE, c).product(SE, d).product(SE, e).product(SE, f).product(SE, g)

    fun <EE, A, B, C, D, E, FF, G, H> tupledN(
      SE: Semigroup<EE>,
      a: Validated<EE, A>,
      b: Validated<EE, B>,
      c: Validated<EE, C>,
      d: Validated<EE, D>,
      e: Validated<EE, E>,
      f: Validated<EE, FF>,
      g: Validated<EE, G>,
      h: Validated<EE, H>
    ): Validated<EE, Tuple8<A, B, C, D, E, FF, G, H>> =
      a.product(SE, b).product(SE, c).product(SE, d).product(SE, e).product(SE, f).product(SE, g).product(SE, h)

    fun <EE, A, B, C, D, E, FF, G, H, I> tupledN(
      SE: Semigroup<EE>,
      a: Validated<EE, A>,
      b: Validated<EE, B>,
      c: Validated<EE, C>,
      d: Validated<EE, D>,
      e: Validated<EE, E>,
      f: Validated<EE, FF>,
      g: Validated<EE, G>,
      h: Validated<EE, H>,
      i: Validated<EE, I>
    ): Validated<EE, Tuple9<A, B, C, D, E, FF, G, H, I>> =
      a.product(SE, b).product(SE, c).product(SE, d).product(SE, e).product(SE, f).product(SE, g).product(SE, h).product(SE, i)

    fun <EE, A, B, C, D, E, FF, G, H, I, J> tupledN(
      SE: Semigroup<EE>,
      a: Validated<EE, A>,
      b: Validated<EE, B>,
      c: Validated<EE, C>,
      d: Validated<EE, D>,
      e: Validated<EE, E>,
      f: Validated<EE, FF>,
      g: Validated<EE, G>,
      h: Validated<EE, H>,
      i: Validated<EE, I>,
      j: Validated<EE, J>
    ): Validated<EE, Tuple10<A, B, C, D, E, FF, G, H, I, J>> =
      a.product(SE, b).product(SE, c).product(SE, d).product(SE, e).product(SE, f).product(SE, g)
        .product(SE, h).product(SE, i).product(SE, j)
  }

  /**
   * Discards the [A] value inside [Validated] signaling this container may be pointing to a noop
   * or an effect whose return value is deliberately ignored. The singleton value [Unit] serves as signal.
   *
   * ```kotlin:ank:playground
   * import arrow.core.*
   *
   * fun main(args: Array<String>) {
   *   val result =
   *   //sampleStart
   *   "Hello World".valid().void()
   *   //sampleEnd
   *   println(result)
   * }
   * ```
   */
  fun void(): Validated<E, Unit> =
    map { Unit }

  /**
   * Applies [f] to an [A] inside [Validated] and returns the [Validated] structure with a tuple of the [A] value and the
   * computed [B] value as result of applying [f]
   *
   *
   * ```kotlin:ank
   * import arrow.core.*
   *
   * fun main(args: Array<String>) {
   *   val result =
   *   //sampleStart
   *   "Hello".valid().fproduct { "$it World" }
   *   //sampleEnd
   *   println(result)
   * }
   * ```
   */
  fun <B> fproduct(f: (A) -> B): Validated<E, Tuple2<A, B>> =
    map { a -> Tuple2(a, f(a)) }

  /**
   * Replaces [A] inside [Validated] with [B] resulting in a Kind<F, B>
   *
   * ```kotlin:ank
   * import arrow.core.*
   *
   * fun main(args: Array<String>) {
   *   val result =
   *   //sampleStart
   *   "Hello World".valid().mapConst("...")
   *   //sampleEnd
   *   println(result)
   * }
   * ```
   */
  fun <B> mapConst(b: B): Validated<E, B> =
    map { b }

  /**
   * Pairs [B] with [A] returning a Validated<E, Tuple2<B, A>>
   *
   * ```kotlin:ank
   * import arrow.core.*
   *
   * fun main(args: Array<String>) {
   *   val result =
   *   //sampleStart
   *   "Hello".valid().tupleLeft("World")
   *   //sampleEnd
   *   println(result)
   * }
   * ```
   */
  fun <B> tupleLeft(b: B): Validated<E, Tuple2<B, A>> =
    map { a -> Tuple2(b, a) }

  /**
   * Pairs [A] with [B] returning a Validated<E, Tuple2<A, B>>
   *
   * ```kotlin:ank:playground:extension
   * import arrow.core.*
   *
   * fun main(args: Array<String>) {
   *   val result =
   *   //sampleStart
   *   "Hello".valid().tupleRight("World")
   *   //sampleEnd
   *   println(result)
   * }
   * ```
   */
  fun <B> tupleRight(b: B): Validated<E, Tuple2<A, B>> =
    map { a -> Tuple2(a, b) }

  inline fun <D> traverse(fa: (A) -> Iterable<D>): List<Validated<E, D>> =
    fold({ emptyList() }, { fa(it).map { Valid(it) } })

  inline fun <D> traverse_(fa: (A) -> Iterable<D>): List<Unit> =
    fold({ emptyList() }, { fa(it).void() })

  inline fun <EE, D> traverseEither(fa: (A) -> Either<EE, D>): Either<EE, Validated<E, D>> =
    when (this) {
      is Valid -> fa(this.a).map { Valid(it) }
      is Invalid -> this.right()
    }

  inline fun <EE, D> traverseEither_(fa: (A) -> Either<EE, D>): Either<EE, Unit> =
    fold({ Either.right(Unit) }, { fa(it).void() })

  inline fun <C> bifoldLeft(
    c: C,
    fa: (C, A) -> C,
    fe: (C, E) -> C
  ): C = fold({ fe(c, it) }, { fa(c, it) })

  inline fun <C> bifoldRight(
    c: Eval<C>,
    fa: (A, Eval<C>) -> Eval<C>,
    fe: (E, Eval<C>) -> Eval<C>
  ): Eval<C> =
    fold({ fe(it, c) }, { fa(it, c) })

  inline fun <C> bifoldMap(MN: Monoid<C>, f: (A) -> C, g: (E) -> C) = MN.run {
    bifoldLeft(MN.empty(), { c, a -> c.combine(f(a)) }, { c, b -> c.combine(g(b)) })
  }

  fun <C, D> bitraverse(fa: (A) -> Iterable<D>, fe: (E) -> Iterable<C>): List<Validated<C, D>> =
    fold({ fe(it).map { Invalid(it) } }, { fa(it).map { Valid(it) } })

  fun <C, D, EE> bitraverseEither(
    fa: (A) -> Either<EE, C>,
    fe: (E) -> Either<EE, D>
  ): Either<EE, Validated<D, C>> =
    fold({ fe(it).map { Invalid(it) } }, { fa(it).map { Valid(it) } })

  fun <B> foldMap(MB: Monoid<B>, f: (A) -> B): B =
    fold({ MB.empty() }, f)

  fun show(SE: Show<E>, SA: Show<A>): String = fold(
    {
      "Invalid(${SE.run { it.show() }})"
    },
    {
      "Valid(${SA.run { it.show() }})"
    }
  )

  fun hash(HL: Hash<E>, HR: Hash<A>): Int =
    hashWithSalt(HL, HR, defaultSalt)

  fun hashWithSalt(HL: Hash<E>, HR: Hash<A>, salt: Int): Int = fold(
    { e -> HL.run { e.hashWithSalt(salt.hashWithSalt(0)) } },
    { a -> HR.run { a.hashWithSalt(salt.hashWithSalt(1)) } }
  )

  data class Valid<out A>(val a: A) : Validated<Nothing, A>() {
    override fun toString(): String = show(Show.any(), Show.any())
  }

  data class Invalid<out E>(val e: E) : Validated<E, Nothing>() {
    override fun toString(): String = show(Show.any(), Show.any())
  }

  inline fun <B> fold(fe: (E) -> B, fa: (A) -> B): B =
    when (this) {
      is Valid -> fa(a)
      is Invalid -> (fe(e))
    }

  val isValid =
    fold({ false }, { true })
  val isInvalid =
    fold({ true }, { false })

  /**
   * Is this Valid and matching the given predicate
   */
  inline fun exist(predicate: (A) -> Boolean): Boolean =
    fold({ false }, predicate)

  inline fun findOrNull(predicate: (A) -> Boolean): A? =
    when (this) {
      is Valid -> if (predicate(this.a)) this.a else null
      is Invalid -> null
    }

  inline fun all(predicate: (A) -> Boolean): Boolean =
    fold({ true }, predicate)

  fun isEmpty(): Boolean = isInvalid

  fun isNotEmpty(): Boolean = isValid

  /**
   * Converts the value to an Either<E, A>
   */
  fun toEither(): Either<E, A> =
    fold(::Left, ::Right)

  /**
   * Returns Valid values wrapped in Some, and None for Invalid values
   */
  fun toOption(): Option<A> =
    fold({ None }, ::Some)

  /**
   * Convert this value to a single element List if it is Valid,
   * otherwise return an empty List
   */
  fun toList(): List<A> =
    fold({ listOf() }, ::listOf)

  /** Lift the Invalid value into a NonEmptyList. */
  fun toValidatedNel(): ValidatedNel<E, A> =
    fold({ invalidNel(it) }, ::Valid)

  /**
   * Convert to an Either, apply a function, convert back. This is handy
   * when you want to use the Monadic properties of the Either type.
   */
  inline fun <EE, B> withEither(f: (Either<E, A>) -> Either<EE, B>): Validated<EE, B> =
    fromEither(f(toEither()))

  /**
   * From [arrow.typeclasses.Bifunctor], maps both types of this Validated.
   *
   * Apply a function to an Invalid or Valid value, returning a new Invalid or Valid value respectively.
   */
  inline fun <EE, AA> bimap(fe: (E) -> EE, fa: (A) -> AA): Validated<EE, AA> =
    fold({ Invalid(fe(it)) }, { Valid(fa(it)) })

  /**
   * Apply a function to a Valid value, returning a new Valid value
   */
  inline fun <B> map(f: (A) -> B): Validated<E, B> =
    bimap(::identity, f)

  @Deprecated("Use mapLeft for consistency", ReplaceWith("mapLeft(f)"))
  inline fun <EE> leftMap(f: (E) -> EE): Validated<EE, A> =
    mapLeft(f)

  /**
   * Apply a function to an Invalid value, returning a new Invalid value.
   * Or, if the original valid was Valid, return it.
   */
  inline fun <EE> mapLeft(f: (E) -> EE): Validated<EE, A> =
    bimap(f, ::identity)

  /**
   * apply the given function to the value with the given B when
   * valid, otherwise return the given B
   */
  inline fun <B> foldLeft(b: B, f: (B, A) -> B): B =
    fold({ b }, { f(b, it) })

  fun <B> foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    when (this) {
      is Valid -> Eval.defer { f(this.a, lb) }
      is Invalid -> lb
    }

  fun swap(): Validated<A, E> =
    fold(::Valid, ::Invalid)
}

/**
 * Compares two instances of [Validated] and returns true if they're considered not equal for this instance.
 *
 * @receiver object to compare with [other]
 * @param other object to compare with [this@neqv]
 * @returns false if [this@neqv] and [other] are equivalent, true otherwise.
 */
fun <E, B> Validated<E, B>.neqv(
  EQL: Eq<E>,
  EQR: Eq<B>,
  other: Validated<E, B>
): Boolean = Validated.eq(EQL, EQR).run {
  this@neqv.neqv(other)
}

/**
 * Compares two instances of [Validated] and returns true if they're considered not equal for this instance.
 *
 * @receiver object to compare with [other]
 * @param other object to compare with [this@neqv]
 * @returns false if [this@neqv] and [other] are equivalent, true otherwise.
 */
fun <E, B> Validated<E, B>.eqv(
  EQL: Eq<E>,
  EQR: Eq<B>,
  other: Validated<E, B>
): Boolean = Validated.eq(EQL, EQR).run {
  this@eqv.neqv(other)
}

/**
 * Replaces the [B] value inside [F] with [A] resulting in a Kind<F, A>
 */
fun <E, A, B> A.mapConst(fb: Validated<E, B>): Validated<E, A> =
  fb.mapConst(this)

/**
 * Given [A] is a sub type of [B], re-type this value from Validated<E, A> to Validated<E, B>
 *
 * ```kotlin:ank:playground:extension
 * import arrow.core.*
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val string: Validated<Int, String> = "Hello".invalid()
 *   val chars: Validated<Int, CharSequence> =
 *     string.widen<Int, CharSequence, String>()
 *   //sampleEnd
 *   println(chars)
 * }
 * ```
 */
fun <E, B, A : B> Validated<E, A>.widen(): Validated<E, B> =
  this

fun <EE, E : EE, A> Validated<E, A>.leftWiden(): Validated<EE, A> =
  this

fun <E, A> Validated<E, A>.replicate(SE: Semigroup<E>, n: Int): Validated<E, List<A>> =
  if (n <= 0) emptyList<A>().valid()
  else Validated.mapN(SE, this, replicate(SE, n - 1)) { (a, xs) -> listOf(a) + xs }

fun <E, A> Validated<E, A>.replicate(SE: Semigroup<E>, n: Int, MA: Monoid<A>): Validated<E, A> =
  if (n <= 0) MA.empty().valid()
  else Validated.mapN(SE, this@replicate, replicate(SE, n - 1, MA)) { (a, xs) -> MA.run { a + xs } }

fun <E, A, B> Validated<E, A>.product(SE: Semigroup<E>, fb: Validated<E, B>): Validated<E, Tuple2<A, B>> =
  ap(SE, fb.map { b: B -> { a: A -> Tuple2(a, b) } })

fun <E, A, B, Z> Validated<E, A>.map2(SE: Semigroup<E>, fb: Validated<E, B>, f: (Tuple2<A, B>) -> Z): Validated<E, Z> =
  product(SE, fb).map(f)

fun <EE, A, B, Z> Validated<EE, Tuple2<A, B>>.product(
  SE: Semigroup<EE>,
  other: Validated<EE, Z>,
  dummyImplicit: Unit = Unit
): Validated<EE, Tuple3<A, B, Z>> =
  map2(SE, other) { (ab, c) -> Tuple3(ab.a, ab.b, c) }

fun <EE, A, B, C, Z> Validated<EE, Tuple3<A, B, C>>.product(
  SE: Semigroup<EE>,
  other: Validated<EE, Z>,
  dummyImplicit: Unit = Unit,
  dummyImplicit2: Unit = Unit
): Validated<EE, Tuple4<A, B, C, Z>> =
  map2(SE, other) { (abc, d) -> Tuple4(abc.a, abc.b, abc.c, d) }

fun <EE, A, B, C, D, Z> Validated<EE, Tuple4<A, B, C, D>>.product(
  SE: Semigroup<EE>,
  other: Validated<EE, Z>,
  dummyImplicit: Unit = Unit,
  dummyImplicit2: Unit = Unit,
  dummyImplicit3: Unit = Unit
): Validated<EE, Tuple5<A, B, C, D, Z>> =
  map2(SE, other) { (abcd, e) -> Tuple5(abcd.a, abcd.b, abcd.c, abcd.d, e) }

fun <EE, A, B, C, D, E, Z> Validated<EE, Tuple5<A, B, C, D, E>>.product(
  SE: Semigroup<EE>,
  other: Validated<EE, Z>,
  dummyImplicit: Unit = Unit,
  dummyImplicit2: Unit = Unit,
  dummyImplicit3: Unit = Unit,
  dummyImplicit4: Unit = Unit
): Validated<EE, Tuple6<A, B, C, D, E, Z>> =
  map2(SE, other) { (abcde, f) -> Tuple6(abcde.a, abcde.b, abcde.c, abcde.d, abcde.e, f) }

fun <EE, A, B, C, D, E, FF, Z> Validated<EE, Tuple6<A, B, C, D, E, FF>>.product(
  SE: Semigroup<EE>,
  other: Validated<EE, Z>,
  dummyImplicit: Unit = Unit,
  dummyImplicit2: Unit = Unit,
  dummyImplicit3: Unit = Unit,
  dummyImplicit4: Unit = Unit,
  dummyImplicit5: Unit = Unit
): Validated<EE, Tuple7<A, B, C, D, E, FF, Z>> =
  map2(SE, other) { (abcdef, g) -> Tuple7(abcdef.a, abcdef.b, abcdef.c, abcdef.d, abcdef.e, abcdef.f, g) }

fun <EE, A, B, C, D, E, FF, G, Z> Validated<EE, Tuple7<A, B, C, D, E, FF, G>>.product(
  SE: Semigroup<EE>,
  other: Validated<EE, Z>,
  dummyImplicit: Unit = Unit,
  dummyImplicit2: Unit = Unit,
  dummyImplicit3: Unit = Unit,
  dummyImplicit4: Unit = Unit,
  dummyImplicit5: Unit = Unit,
  dummyImplicit6: Unit = Unit
): Validated<EE, Tuple8<A, B, C, D, E, FF, G, Z>> =
  map2(SE, other) { (abcdefg, h) -> Tuple8(abcdefg.a, abcdefg.b, abcdefg.c, abcdefg.d, abcdefg.e, abcdefg.f, abcdefg.g, h) }

fun <EE, A, B, C, D, E, FF, G, H, Z> Validated<EE, Tuple8<A, B, C, D, E, FF, G, H>>.product(
  SE: Semigroup<EE>,
  other: Validated<EE, Z>,
  dummyImplicit: Unit = Unit,
  dummyImplicit2: Unit = Unit,
  dummyImplicit3: Unit = Unit,
  dummyImplicit4: Unit = Unit,
  dummyImplicit5: Unit = Unit,
  dummyImplicit6: Unit = Unit,
  dummyImplicit7: Unit = Unit
): Validated<EE, Tuple9<A, B, C, D, E, FF, G, H, Z>> =
  map2(SE, other) { (abcdefgh, i) -> Tuple9(abcdefgh.a, abcdefgh.b, abcdefgh.c, abcdefgh.d, abcdefgh.e, abcdefgh.f, abcdefgh.g, abcdefgh.h, i) }

fun <EE, A, B, C, D, E, FF, G, H, I, Z> Validated<EE, Tuple9<A, B, C, D, E, FF, G, H, I>>.product(
  SE: Semigroup<EE>,
  other: Validated<EE, Z>,
  dummyImplicit: Unit = Unit,
  dummyImplicit2: Unit = Unit,
  dummyImplicit3: Unit = Unit,
  dummyImplicit4: Unit = Unit,
  dummyImplicit5: Unit = Unit,
  dummyImplicit6: Unit = Unit,
  dummyImplicit7: Unit = Unit,
  dummyImplicit9: Unit = Unit
): Validated<EE, Tuple10<A, B, C, D, E, FF, G, H, I, Z>> =
  map2(SE, other) { (abcdefghi, j) -> Tuple10(abcdefghi.a, abcdefghi.b, abcdefghi.c, abcdefghi.d, abcdefghi.e, abcdefghi.f, abcdefghi.g, abcdefghi.h, abcdefghi.i, j) }

fun <A, B> Validated<Iterable<A>, Iterable<B>>.bisequence(): List<Validated<A, B>> =
  bitraverse(::identity, ::identity)

fun <A, B, E> Validated<Either<E, A>, Either<E, B>>.bisequenceEither(): Either<E, Validated<A, B>> =
  bitraverseEither(::identity, ::identity)

fun <E, A> Validated<E, A>.fold(MA: Monoid<A>): A = MA.run {
  foldLeft(empty()) { acc, a -> acc.combine(a) }
}

fun <E, A> Validated<E, A>.combineAll(MA: Monoid<A>): A =
  fold(MA)

fun <A, B> Validated<A, Iterable<B>>.sequence(): List<Validated<A, B>> =
  traverse(::identity)

fun <A, B> Validated<A, Iterable<B>>.sequence_(): List<Unit> =
  traverse_(::identity)

fun <E, A, B> Validated<A, Either<E, B>>.sequenceEither(): Either<E, Validated<A, B>> =
  traverseEither(::identity)

fun <E, A, B> Validated<A, Either<E, B>>.traverseEither_(): Either<E, Unit> =
  traverseEither_(::identity)

fun <L, R> Validated<L, R>.compare(OL: Order<L>, OR: Order<R>, b: Validated<L, R>): Ordering = fold(
  { l1 -> b.fold({ l2 -> OL.run { l1.compare(l2) } }, { LT }) },
  { r1 -> b.fold({ GT }, { r2 -> OR.run { r1.compare(r2) } }) }
)

fun <L, R> Validated<L, R>.compareTo(OL: Order<L>, OR: Order<R>, b: Validated<L, R>): Int =
  compare(OL, OR, b).toInt()

fun <L, R> Validated<L, R>.lt(OL: Order<L>, OR: Order<R>, b: Validated<L, R>): Boolean =
  compare(OL, OR, b) == LT

fun <L, R> Validated<L, R>.lte(OL: Order<L>, OR: Order<R>, b: Validated<L, R>): Boolean =
  compare(OL, OR, b) != GT

fun <L, R> Validated<L, R>.gt(OL: Order<L>, OR: Order<R>, b: Validated<L, R>): Boolean =
  compare(OL, OR, b) == GT

fun <L, R> Validated<L, R>.gte(OL: Order<L>, OR: Order<R>, b: Validated<L, R>): Boolean =
  compare(OL, OR, b) != LT

fun <L, R> Validated<L, R>.max(OL: Order<L>, OR: Order<R>, b: Validated<L, R>): Validated<L, R> =
  if (gt(OL, OR, b)) this else b

fun <L, R> Validated<L, R>.min(OL: Order<L>, OR: Order<R>, b: Validated<L, R>): Validated<L, R> =
  if (lt(OL, OR, b)) this else b

fun <L, R> Validated<L, R>.sort(OL: Order<L>, OR: Order<R>, b: Validated<L, R>): Tuple2<Validated<L, R>, Validated<L, R>> =
  if (gte(OL, OR, b)) Tuple2(this, b) else Tuple2(b, this)

fun <E, A, B> Validated<E, Either<A, B>>.select(f: Validated<E, (A) -> B>): Validated<E, B> =
  fold({ Invalid(it) }, { it.fold({ l -> f.map { ff -> ff(l) } }, { r -> r.valid() }) })

fun <E, A, B, C> Validated<E, Either<A, B>>.branch(fl: Validated<E, (A) -> C>, fr: Validated<E, (B) -> C>): Validated<E, C> {
  val nested: Validated<E, Either<A, Either<B, Nothing>>> = map { it.map(::Left) }
  val ffl: Validated<E, (A) -> Either<Nothing, C>> = fl.map { it.andThen(::Right) }
  return nested.select(ffl).select(fr)
}

private fun <E> Validated<E, Boolean>.selector(): Validated<E, Either<Unit, Unit>> =
  map { bool -> if (bool) Either.leftUnit else Either.unit }

fun <E> Validated<E, Boolean>.whenS(x: Validated<E, () -> Unit>): Validated<E, Unit> =
  selector().select(x.map { f -> { f() } })

fun <E, A> Validated<E, Boolean>.ifS(fl: Validated<E, A>, fr: Validated<E, A>): Validated<E, A> =
  selector().branch(fl.map { { _: Unit -> it } }, fr.map { { _: Unit -> it } })

fun <E> Validated<E, Boolean>.orS(f: Validated<E, Boolean>): Validated<E, Boolean> =
  ifS(Valid(true), f)

fun <E> Validated<E, Boolean>.andS(f: Validated<E, Boolean>): Validated<E, Boolean> =
  ifS(f, Valid(false))

/**
 * Return the Valid value, or the default if Invalid
 */
inline fun <E, B> ValidatedOf<E, B>.getOrElse(default: () -> B): B =
  fix().fold({ default() }, ::identity)

/**
 * Return the Valid value, or null if Invalid
 */
fun <E, B> ValidatedOf<E, B>.orNull(): B? =
  getOrElse { null }

/**
 * Return the Valid value, or the result of f if Invalid
 */
inline fun <E, B> ValidatedOf<E, B>.valueOr(f: (E) -> B): B =
  fix().fold({ f(it) }, ::identity)

/**
 * If `this` is valid return `this`, otherwise if `that` is valid return `that`, otherwise combine the failures.
 * This is similar to [orElse] except that here failures are accumulated.
 */
inline fun <E, A> ValidatedOf<E, A>.findValid(SE: Semigroup<E>, that: () -> Validated<E, A>): Validated<E, A> =
  fix().fold(
    { e ->
      that().fold(
        { ee -> Invalid(SE.run { e.combine(ee) }) },
        { Valid(it) }
      )
    },
    { Valid(it) }
  )

/**
 * Return this if it is Valid, or else fall back to the given default.
 * The functionality is similar to that of [findValid] except for failure accumulation,
 * where here only the error on the right is preserved and the error on the left is ignored.
 */
inline fun <E, A> ValidatedOf<E, A>.orElse(default: () -> Validated<E, A>): Validated<E, A> =
  fix().fold(
    { default() },
    { Valid(it) }
  )

/**
 * From Apply:
 * if both the function and this value are Valid, apply the function
 */
inline fun <E, A, B> ValidatedOf<E, A>.ap(SE: Semigroup<E>, f: Validated<E, (A) -> B>): Validated<E, B> =
  when (val value = fix()) {
    is Validated.Valid -> when (f) {
      is Validated.Valid -> Valid(f.a(value.a))
      is Validated.Invalid -> f
    }
    is Validated.Invalid -> when (f) {
      is Validated.Valid -> value
      is Validated.Invalid -> Invalid(SE.run { value.e.combine(f.e) })
    }
  }

@Deprecated(
  "To keep API consistent with Either and Option please use `handleErrorWith` instead",
  ReplaceWith("handleErrorWith(f)")
)
inline fun <E, A> ValidatedOf<E, A>.handleLeftWith(f: (E) -> ValidatedOf<E, A>): Validated<E, A> =
  handleErrorWith(f)

inline fun <E, A> ValidatedOf<E, A>.handleErrorWith(f: (E) -> ValidatedOf<E, A>): Validated<E, A> =
  when (val value = fix()) {
    is Validated.Valid -> value
    is Validated.Invalid -> f(value.e).fix()
  }

inline fun <E, A> ValidatedOf<E, A>.handleError(f: (E) -> A): Validated<Nothing, A> =
  when (val value = fix()) {
    is Validated.Valid -> value
    is Validated.Invalid -> Valid(f(value.e))
  }

inline fun <E, A, B> Validated<E, A>.redeem(fe: (E) -> B, fa: (A) -> B): Validated<E, B> =
  when (this) {
    is Validated.Valid -> map(fa)
    is Validated.Invalid -> Valid(fe(this.e))
  }

fun <E, A> Validated<E, A>.attempt(): Validated<Nothing, Either<E, A>> =
  map { Right(it) }.handleError { Left(it) }

fun <G, E, A, B> ValidatedOf<E, A>.traverse(GA: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, Validated<E, B>> = GA.run {
  fix().fold({ e -> just(Invalid(e)) }, { a -> f(a).map(::Valid) })
}

fun <G, E, A> ValidatedOf<E, Kind<G, A>>.sequence(GA: Applicative<G>): Kind<G, Validated<E, A>> =
  fix().traverse(GA, ::identity)

fun <E, A> ValidatedOf<E, A>.combine(
  SE: Semigroup<E>,
  SA: Semigroup<A>,
  y: ValidatedOf<E, A>
): Validated<E, A> =
  y.fix().let { that ->
    when {
      this is Valid && that is Valid -> Valid(SA.run { a.combine(that.a) })
      this is Invalid && that is Invalid -> Invalid(SE.run { e.combine(that.e) })
      this is Invalid -> this
      else -> that
    }
  }

fun <E, A> ValidatedOf<E, A>.combineK(SE: Semigroup<E>, y: ValidatedOf<E, A>): Validated<E, A> {
  val xev = fix()
  val yev = y.fix()
  return when (xev) {
    is Valid -> xev
    is Invalid -> when (yev) {
      is Invalid -> Invalid(SE.run { xev.e.combine(yev.e) })
      is Valid -> yev
    }
  }
}

/**
 * Converts the value to an Ior<E, A>
 */
fun <E, A> ValidatedOf<E, A>.toIor(): Ior<E, A> =
  fix().fold({ Ior.Left(it) }, { Ior.Right(it) })

inline fun <A> A.valid(): Validated<Nothing, A> =
  Valid(this)

inline fun <E> E.invalid(): Validated<E, Nothing> =
  Invalid(this)

inline fun <A> A.validNel(): ValidatedNel<Nothing, A> =
  Validated.validNel(this)

inline fun <E> E.invalidNel(): ValidatedNel<E, Nothing> =
  Validated.invalidNel(this)

private class ValidatedEq<L, R>(
  private val EQL: Eq<L>,
  private val EQR: Eq<R>
) : Eq<Validated<L, R>> {

  override fun Validated<L, R>.eqv(b: Validated<L, R>): Boolean = when (this) {
    is Valid -> when (b) {
      is Invalid -> false
      is Valid -> EQR.run { a.eqv(b.a) }
    }
    is Invalid -> when (b) {
      is Invalid -> EQL.run { e.eqv(b.e) }
      is Valid -> false
    }
  }
}

private class ValidatedShow<L, R>(
  private val SL: Show<L>,
  private val SR: Show<R>,
) : Show<Validated<L, R>> {
  override fun Validated<L, R>.show(): String =
    show(SL, SR)
}

private class ValidatedHash<L, R>(
  private val HL: Hash<L>,
  private val HR: Hash<R>
) : Hash<Validated<L, R>> {
  override fun Validated<L, R>.hashWithSalt(salt: Int): Int =
    hashWithSalt(HL, HR, salt)
}

private class ValidatedOrder<L, R>(
  private val OL: Order<L>,
  private val OR: Order<R>
) : Order<Validated<L, R>> {
  override fun Validated<L, R>.compare(b: Validated<L, R>): Ordering =
    compare(OL, OR, b)
}
