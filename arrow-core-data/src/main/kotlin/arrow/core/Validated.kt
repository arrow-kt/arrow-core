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
 * //sampleStart
 * abstract class Read<A> {
 *
 * abstract fun read(s: String): A?
 *
 *  companion object {
 *
 *   val stringRead: Read<String> =
 *    object: Read<String>() {
 *     override fun read(s: String): String? = s
 *    }
 *
 *   val intRead: Read<Int> =
 *    object: Read<Int>() {
 *     override fun read(s: String): Int? =
 *      if (s.matches(Regex("-?[0-9]+"))) s.toInt() else null
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
 * sealed class Validated<out E, out A> {
 *  data class Valid<out A>(val a: A) : Validated<Nothing, A>()
 *  data class Invalid<out E>(val e: E) : Validated<E, Nothing>()
 * }
 * ```
 *
 * Now we are ready to write our parser.
 *
 * ```kotlin:ank
 * import arrow.core.Validated
 * import arrow.core.valid
 * import arrow.core.invalid
 *
 * //sampleStart
 * data class Config(val map: Map<String, String>) {
 *  fun <A> parse(read: Read<A>, key: String): Validated<ConfigError, A> =
 *   when (val v = map[key]) {
 *    null -> Validated.Invalid(ConfigError.MissingConfig(key))
 *    else -> when (val s = read.read(v)) {
 *        null -> ConfigError.ParseConfig(key).invalid()
 *        else -> s.valid()
 *      }
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
 *     val readVal = Validated.fromNullable(read.read(value)) {
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
 * but that would complicate the code. To achieve this, Arrow provides [mapN] & [tupledN].
 * This function combines [Validated]s by accumulating errors in a tuple, which we can then map.
 * The above function can be rewritten as follows:
 *
 * ```kotlin:ank:silent
 * import arrow.core.Validated
 * import arrow.core.validNel
 * import arrow.core.extensions.nonemptylist.semigroup.semigroup
 *
 * //sampleStart
 * val parallelValidate = Validated
 *   .mapN(NonEmptyList.semigroup<ConfigError>(), 1.validNel(), 2.validNel())
 *     { a, b -> /* combine the result */ }
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
 * import arrow.core.Validated
 * import arrow.core.computations.either
 * import arrow.core.valid
 * import arrow.core.invalid
 * import arrow.core.NonEmptyList
 * import arrow.core.extensions.nonemptylist.semigroup.semigroup
 *
 * data class ConnectionParams(val url: String, val port: Int)
 *
 * abstract class Read<A> {
 *  abstract fun read(s: String): A?
 *
 *  companion object {
 *
 *   val stringRead: Read<String> =
 *    object : Read<String>() {
 *     override fun read(s: String): String? = s
 *    }
 *
 *   val intRead: Read<Int> =
 *    object : Read<Int>() {
 *     override fun read(s: String): Int? =
 *      if (s.matches(Regex("-?[0-9]+"))) s.toInt() else null
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
 *     val readVal = Validated.fromNullable(read.read(value)) {
 *       ConfigError.ParseConfig(key)
 *     }()
 *     readVal
 *   }.toValidatedNel()
 * }
 *
 *
 * suspend fun main() {
 * //sampleStart
 *  val config = Config(mapOf("url" to "127.0.0.1", "port" to "1337"))
 *
 *  val valid = Validated.mapN(
 *    NonEmptyList.semigroup<ConfigError>(),
 *    config.parse(Read.stringRead, "url"),
 *    config.parse(Read.intRead, "port")
 *  ) { url, port -> ConnectionParams(url, port) }
 * //sampleEnd
 *  println("valid = $valid")
 * }
 * ```
 *
 * But what happens when we have one or more errors? They are accumulated in a `NonEmptyList` wrapped in
 * an `Invalid` instance.
 *
 * ```kotlin:ank:playground
 * import arrow.core.Validated
 * import arrow.core.computations.either
 * import arrow.core.valid
 * import arrow.core.invalid
 * import arrow.core.NonEmptyList
 * import arrow.core.extensions.nonemptylist.semigroup.semigroup
 *
 * data class ConnectionParams(val url: String, val port: Int)
 *
 * abstract class Read<A> {
 *  abstract fun read(s: String): A?
 *
 *  companion object {
 *
 *   val stringRead: Read<String> =
 *    object : Read<String>() {
 *     override fun read(s: String): String? = s
 *    }
 *
 *   val intRead: Read<Int> =
 *    object : Read<Int>() {
 *     override fun read(s: String): Int? =
 *      if (s.matches(Regex("-?[0-9]+"))) s.toInt() else null
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
 *     val readVal = Validated.fromNullable(read.read(value)) {
 *       ConfigError.ParseConfig(key)
 *     }()
 *     readVal
 *   }.toValidatedNel()
 * }
 *
 * suspend fun main() {
 * //sampleStart
 * val config = Config(mapOf("wrong field" to "127.0.0.1", "port" to "not a number"))
 *
 * val valid = Validated.mapN(
 *  NonEmptyList.semigroup<ConfigError>(),
 *  config.parse(Read.stringRead, "url"),
 *  config.parse(Read.intRead, "port")
 *  ) { url, port -> ConnectionParams(url, port) }
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
 * import arrow.core.right
 * import arrow.core.Validated
 * import arrow.core.computations.either
 * import arrow.core.valid
 * import arrow.core.invalid
 *
 * abstract class Read<A> {
 *  abstract fun read(s: String): A?
 *
 *  companion object {
 *
 *   val stringRead: Read<String> =
 *    object : Read<String>() {
 *     override fun read(s: String): String? = s
 *    }
 *
 *   val intRead: Read<Int> =
 *    object : Read<Int>() {
 *     override fun read(s: String): Int? =
 *      if (s.matches(Regex("-?[0-9]+"))) s.toInt() else null
 *    }
 *  }
 * }
 *
 * data class Config(val map: Map<String, String>) {
 *   suspend fun <A> parse(read: Read<A>, key: String) = either<ConfigError, A> {
 *     val value = Validated.fromNullable(map[key]) {
 *       ConfigError.MissingConfig(key)
 *     }()
 *     val readVal = Validated.fromNullable(read.read(value)) {
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
 * fun positive(field: String, i: Int): Either<ConfigError, Int> =
 *  if (i >= 0) i.right()
 *  else ConfigError.ParseConfig(field).left()
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

    @Deprecated("Use the inline version. Hidden for binary compat", level = DeprecationLevel.HIDDEN)
    suspend fun <A> catch(f: suspend () -> A): Validated<Throwable, A> =
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

    @Deprecated("Use the inline version. Hidden for binary compat", level = DeprecationLevel.HIDDEN)
    suspend fun <A> catchNel(f: suspend () -> A): ValidatedNel<Throwable, A> =
      try {
        f().validNel()
      } catch (e: Throwable) {
        e.nonFatalOrThrow().invalidNel()
      }

    /** Construct an [Eq] instance which use [EQE] and [EQA] to compare the [Invalid] and [Valid] cases **/
    fun <E, A> eq(EQE: Eq<E>, EQA: Eq<A>): Eq<Validated<E, A>> =
      ValidatedEq(EQE, EQA)

    fun <E, A> hash(HE: Hash<E>, HA: Hash<A>): Hash<Validated<E, A>> =
      ValidatedHash(HE, HA)

    fun <E, A> show(SE: Show<E>, SA: Show<A>): Show<Validated<E, A>> =
      ValidatedShow(SE, SA)

    fun <E, A> order(OE: Order<E>, OA: Order<A>): Order<Validated<E, A>> =
      ValidatedOrder(OE, OA)

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

    fun <E, A, B, Z> mapN(
      SE: Semigroup<E>,
      a: Validated<E, A>,
      b: Validated<E, B>,
      f: (A, B) -> Z
    ): Validated<E, Z> =
      tupledN(SE, a, b)
        .map { (a, b) -> f(a, b) }

    fun <E, A, B, C, Z> mapN(
      SE: Semigroup<E>,
      a: Validated<E, A>,
      b: Validated<E, B>,
      c: Validated<E, C>,
      f: (A, B, C) -> Z
    ): Validated<E, Z> =
      tupledN(SE, a, b, c)
        .map { (a, b, c) -> f(a, b, c) }

    fun <E, A, B, C, D, Z> mapN(
      SE: Semigroup<E>,
      a: Validated<E, A>,
      b: Validated<E, B>,
      c: Validated<E, C>,
      d: Validated<E, D>,
      f: (A, B, C, D) -> Z
    ): Validated<E, Z> =
      tupledN(SE, a, b, c, d)
        .map { (a, b, c, d) -> f(a, b, c, d) }

    fun <E, A, B, C, D, EE, Z> mapN(
      SE: Semigroup<E>,
      a: Validated<E, A>,
      b: Validated<E, B>,
      c: Validated<E, C>,
      d: Validated<E, D>,
      e: Validated<E, EE>,
      f: (A, B, C, D, EE) -> Z
    ): Validated<E, Z> =
      tupledN(SE, a, b, c, d, e)
        .map { (a, b, c, d, e) -> f(a, b, c, d, e) }

    fun <E, A, B, C, D, EE, FF, Z> mapN(
      SE: Semigroup<E>,
      a: Validated<E, A>,
      b: Validated<E, B>,
      c: Validated<E, C>,
      d: Validated<E, D>,
      e: Validated<E, EE>,
      ff: Validated<E, FF>,
      f: (A, B, C, D, EE, FF) -> Z
    ): Validated<E, Z> =
      tupledN(SE, a, b, c, d, e, ff)
        .map { (a, b, c, d, e, ff) -> f(a, b, c, d, e, ff) }

    fun <E, A, B, C, D, EE, F, G, Z> mapN(
      SE: Semigroup<E>,
      a: Validated<E, A>,
      b: Validated<E, B>,
      c: Validated<E, C>,
      d: Validated<E, D>,
      e: Validated<E, EE>,
      ff: Validated<E, F>,
      g: Validated<E, G>,
      f: (A, B, C, D, EE, F, G) -> Z
    ): Validated<E, Z> =
      tupledN(SE, a, b, c, d, e, ff, g)
        .map { (a, b, c, d, e, ff, g) -> f(a, b, c, d, e, ff, g) }

    fun <E, A, B, C, D, EE, F, G, H, Z> mapN(
      SE: Semigroup<E>,
      a: Validated<E, A>,
      b: Validated<E, B>,
      c: Validated<E, C>,
      d: Validated<E, D>,
      e: Validated<E, EE>,
      ff: Validated<E, F>,
      g: Validated<E, G>,
      h: Validated<E, H>,
      f: (A, B, C, D, EE, F, G, H) -> Z
    ): Validated<E, Z> =
      tupledN(SE, a, b, c, d, e, ff, g, h)
        .map { (a, b, c, d, e, ff, g, h) -> f(a, b, c, d, e, ff, g, h) }

    fun <E, A, B, C, D, EE, F, G, H, I, Z> mapN(
      SE: Semigroup<E>,
      a: Validated<E, A>,
      b: Validated<E, B>,
      c: Validated<E, C>,
      d: Validated<E, D>,
      e: Validated<E, EE>,
      ff: Validated<E, F>,
      g: Validated<E, G>,
      h: Validated<E, H>,
      i: Validated<E, I>,
      f: (A, B, C, D, EE, F, G, H, I) -> Z
    ): Validated<E, Z> =
      tupledN(SE, a, b, c, d, e, ff, g, h, i)
        .map { (a, b, c, d, e, ff, g, h, i) -> f(a, b, c, d, e, ff, g, h, i) }

    fun <E, A, B, C, D, EE, F, G, H, I, J, Z> mapN(
      SE: Semigroup<E>,
      a: Validated<E, A>,
      b: Validated<E, B>,
      c: Validated<E, C>,
      d: Validated<E, D>,
      e: Validated<E, EE>,
      ff: Validated<E, F>,
      g: Validated<E, G>,
      h: Validated<E, H>,
      i: Validated<E, I>,
      j: Validated<E, J>,
      f: (A, B, C, D, EE, F, G, H, I, J) -> Z
    ): Validated<E, Z> =
      tupledN(SE, a, b, c, d, e, ff, g, h, i, j)
        .map { (a, b, c, d, e, ff, g, h, i, j) -> f(a, b, c, d, e, ff, g, h, i, j) }

    fun <E, A, B> tupledN(
      SE: Semigroup<E>,
      a: Validated<E, A>,
      b: Validated<E, B>
    ): Validated<E, Tuple2<A, B>> =
      a.product(SE, b)

    fun <E, A, B, C> tupledN(
      SE: Semigroup<E>,
      a: Validated<E, A>,
      b: Validated<E, B>,
      c: Validated<E, C>
    ): Validated<E, Tuple3<A, B, C>> =
      a.product(SE, b).product(SE, c)

    fun <E, A, B, C, D> tupledN(
      SE: Semigroup<E>,
      a: Validated<E, A>,
      b: Validated<E, B>,
      c: Validated<E, C>,
      d: Validated<E, D>
    ): Validated<E, Tuple4<A, B, C, D>> =
      a.product(SE, b).product(SE, c).product(SE, d)

    fun <E, A, B, C, D, EE> tupledN(
      SE: Semigroup<E>,
      a: Validated<E, A>,
      b: Validated<E, B>,
      c: Validated<E, C>,
      d: Validated<E, D>,
      e: Validated<E, EE>
    ): Validated<E, Tuple5<A, B, C, D, EE>> =
      a.product(SE, b).product(SE, c).product(SE, d).product(SE, e)

    fun <E, A, B, C, D, EE, F> tupledN(
      SE: Semigroup<E>,
      a: Validated<E, A>,
      b: Validated<E, B>,
      c: Validated<E, C>,
      d: Validated<E, D>,
      e: Validated<E, EE>,
      f: Validated<E, F>
    ): Validated<E, Tuple6<A, B, C, D, EE, F>> =
      a.product(SE, b).product(SE, c).product(SE, d).product(SE, e).product(SE, f)

    fun <E, A, B, C, D, EE, F, G> tupledN(
      SE: Semigroup<E>,
      a: Validated<E, A>,
      b: Validated<E, B>,
      c: Validated<E, C>,
      d: Validated<E, D>,
      e: Validated<E, EE>,
      f: Validated<E, F>,
      g: Validated<E, G>
    ): Validated<E, Tuple7<A, B, C, D, EE, F, G>> =
      a.product(SE, b).product(SE, c).product(SE, d).product(SE, e).product(SE, f).product(SE, g)

    fun <E, A, B, C, D, EE, F, G, H> tupledN(
      SE: Semigroup<E>,
      a: Validated<E, A>,
      b: Validated<E, B>,
      c: Validated<E, C>,
      d: Validated<E, D>,
      e: Validated<E, EE>,
      f: Validated<E, F>,
      g: Validated<E, G>,
      h: Validated<E, H>
    ): Validated<E, Tuple8<A, B, C, D, EE, F, G, H>> =
      a.product(SE, b).product(SE, c).product(SE, d).product(SE, e).product(SE, f).product(SE, g).product(SE, h)

    fun <E, A, B, C, D, EE, F, G, H, I> tupledN(
      SE: Semigroup<E>,
      a: Validated<E, A>,
      b: Validated<E, B>,
      c: Validated<E, C>,
      d: Validated<E, D>,
      e: Validated<E, EE>,
      f: Validated<E, F>,
      g: Validated<E, G>,
      h: Validated<E, H>,
      i: Validated<E, I>
    ): Validated<E, Tuple9<A, B, C, D, EE, F, G, H, I>> =
      a.product(SE, b).product(SE, c).product(SE, d).product(SE, e).product(SE, f).product(SE, g).product(SE, h).product(SE, i)

    fun <E, A, B, C, D, EE, F, G, H, I, J> tupledN(
      SE: Semigroup<E>,
      a: Validated<E, A>,
      b: Validated<E, B>,
      c: Validated<E, C>,
      d: Validated<E, D>,
      e: Validated<E, EE>,
      f: Validated<E, F>,
      g: Validated<E, G>,
      h: Validated<E, H>,
      i: Validated<E, I>,
      j: Validated<E, J>
    ): Validated<E, Tuple10<A, B, C, D, EE, F, G, H, I, J>> =
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

  inline fun <B> traverse(fa: (A) -> Iterable<B>): List<Validated<E, B>> =
    fold({ emptyList() }, { fa(it).map { Valid(it) } })

  inline fun <B> traverse_(fa: (A) -> Iterable<B>): List<Unit> =
    fold({ emptyList() }, { fa(it).void() })

  inline fun <EE, B> traverseEither(fa: (A) -> Either<EE, B>): Either<EE, Validated<E, B>> =
    when (this) {
      is Valid -> fa(this.a).map { Valid(it) }
      is Invalid -> this.right()
    }

  inline fun <EE, B> traverseEither_(fa: (A) -> Either<EE, B>): Either<EE, Unit> =
    fold({ Either.right(Unit) }, { fa(it).void() })

  inline fun <B> bifoldLeft(
    c: B,
    fe: (B, E) -> B,
    fa: (B, A) -> B
  ): B =
    fold({ fe(c, it) }, { fa(c, it) })

  inline fun <B> bifoldRight(
    c: Eval<B>,
    fe: (E, Eval<B>) -> Eval<B>,
    fa: (A, Eval<B>) -> Eval<B>
  ): Eval<B> =
    fold({ fe(it, c) }, { fa(it, c) })

  inline fun <B> bifoldMap(MN: Monoid<B>, g: (E) -> B, f: (A) -> B) = MN.run {
    bifoldLeft(MN.empty(), { c, b -> c.combine(g(b)) }) { c, a -> c.combine(f(a)) }
  }

  fun <EE, B> bitraverse(fe: (E) -> Iterable<EE>, fa: (A) -> Iterable<B>): List<Validated<EE, B>> =
    fold({ fe(it).map { Invalid(it) } }, { fa(it).map { Valid(it) } })

  fun <EE, B, C> bitraverseEither(
    fe: (E) -> Either<EE, B>,
    fa: (A) -> Either<EE, C>
  ): Either<EE, Validated<B, C>> =
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
  inline fun <EE, B> bimap(fe: (E) -> EE, fa: (A) -> B): Validated<EE, B> =
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
 * Replaces the [B] value inside [Validated] with [A] resulting in Validated<E, A>
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
  else Validated.mapN(SE, this, replicate(SE, n - 1)) { a, xs -> listOf(a) + xs }

fun <E, A> Validated<E, A>.replicate(SE: Semigroup<E>, n: Int, MA: Monoid<A>): Validated<E, A> =
  if (n <= 0) MA.empty().valid()
  else Validated.mapN(SE, this@replicate, replicate(SE, n - 1, MA)) { a, xs -> MA.run { a + xs } }

fun <E, A, B> Validated<E, A>.product(SE: Semigroup<E>, fb: Validated<E, B>): Validated<E, Tuple2<A, B>> =
  ap(SE, fb.map { b: B -> { a: A -> Tuple2(a, b) } })

fun <E, A, B, Z> Validated<E, A>.map2(SE: Semigroup<E>, fb: Validated<E, B>, f: (Tuple2<A, B>) -> Z): Validated<E, Z> =
  product(SE, fb).map(f)

@JvmName("product3")
fun <E, A, B, C> Validated<E, Tuple2<A, B>>.product(
  SE: Semigroup<E>,
  other: Validated<E, C>,
): Validated<E, Tuple3<A, B, C>> =
  map2(SE, other) { (ab, c) -> Tuple3(ab.a, ab.b, c) }

@JvmName("product4")
fun <E, A, B, C, D> Validated<E, Tuple3<A, B, C>>.product(
  SE: Semigroup<E>,
  other: Validated<E, D>,
): Validated<E, Tuple4<A, B, C, D>> =
  map2(SE, other) { (abc, d) -> Tuple4(abc.a, abc.b, abc.c, d) }

@JvmName("product5")
fun <E, A, B, C, D, EE> Validated<E, Tuple4<A, B, C, D>>.product(
  SE: Semigroup<E>,
  other: Validated<E, EE>,
): Validated<E, Tuple5<A, B, C, D, EE>> =
  map2(SE, other) { (abcd, e) -> Tuple5(abcd.a, abcd.b, abcd.c, abcd.d, e) }

@JvmName("product6")
fun <E, A, B, C, D, EE, F> Validated<E, Tuple5<A, B, C, D, EE>>.product(
  SE: Semigroup<E>,
  other: Validated<E, F>,
): Validated<E, Tuple6<A, B, C, D, EE, F>> =
  map2(SE, other) { (abcde, f) -> Tuple6(abcde.a, abcde.b, abcde.c, abcde.d, abcde.e, f) }

@JvmName("product7")
fun <E, A, B, C, D, EE, F, G> Validated<E, Tuple6<A, B, C, D, EE, F>>.product(
  SE: Semigroup<E>,
  other: Validated<E, G>,
): Validated<E, Tuple7<A, B, C, D, EE, F, G>> =
  map2(SE, other) { (abcdef, g) -> Tuple7(abcdef.a, abcdef.b, abcdef.c, abcdef.d, abcdef.e, abcdef.f, g) }

@JvmName("product8")
fun <E, A, B, C, D, EE, F, G, H> Validated<E, Tuple7<A, B, C, D, EE, F, G>>.product(
  SE: Semigroup<E>,
  other: Validated<E, H>,
): Validated<E, Tuple8<A, B, C, D, EE, F, G, H>> =
  map2(SE, other) { (abcdefg, h) -> Tuple8(abcdefg.a, abcdefg.b, abcdefg.c, abcdefg.d, abcdefg.e, abcdefg.f, abcdefg.g, h) }

@JvmName("product9")
fun <E, A, B, C, D, EE, F, G, H, I> Validated<E, Tuple8<A, B, C, D, EE, F, G, H>>.product(
  SE: Semigroup<E>,
  other: Validated<E, I>,
): Validated<E, Tuple9<A, B, C, D, EE, F, G, H, I>> =
  map2(SE, other) { (abcdefgh, i) -> Tuple9(abcdefgh.a, abcdefgh.b, abcdefgh.c, abcdefgh.d, abcdefgh.e, abcdefgh.f, abcdefgh.g, abcdefgh.h, i) }

@JvmName("product10")
fun <E, A, B, C, D, EE, F, G, H, I, J> Validated<E, Tuple9<A, B, C, D, EE, F, G, H, I>>.product(
  SE: Semigroup<E>,
  other: Validated<E, J>,
): Validated<E, Tuple10<A, B, C, D, EE, F, G, H, I, J>> =
  map2(SE, other) { (abcdefghi, j) -> Tuple10(abcdefghi.a, abcdefghi.b, abcdefghi.c, abcdefghi.d, abcdefghi.e, abcdefghi.f, abcdefghi.g, abcdefghi.h, abcdefghi.i, j) }

fun <E, A> Validated<Iterable<E>, Iterable<A>>.bisequence(): List<Validated<E, A>> =
  bitraverse(::identity, ::identity)

fun <E, A, B> Validated<Either<E, A>, Either<E, B>>.bisequenceEither(): Either<E, Validated<A, B>> =
  bitraverseEither(::identity, ::identity)

fun <E, A> Validated<E, A>.fold(MA: Monoid<A>): A = MA.run {
  foldLeft(empty()) { acc, a -> acc.combine(a) }
}

fun <E, A> Validated<E, A>.combineAll(MA: Monoid<A>): A =
  fold(MA)

fun <E, A> Validated<E, Iterable<A>>.sequence(): List<Validated<E, A>> =
  traverse(::identity)

fun <E, A> Validated<E, Iterable<A>>.sequence_(): List<Unit> =
  traverse_(::identity)

fun <E, A, B> Validated<A, Either<E, B>>.sequenceEither(): Either<E, Validated<A, B>> =
  traverseEither(::identity)

fun <E, A, B> Validated<A, Either<E, B>>.traverseEither_(): Either<E, Unit> =
  traverseEither_(::identity)

fun <E, A> Validated<E, A>.compare(OE: Order<E>, OA: Order<A>, b: Validated<E, A>): Ordering = fold(
  { l1 -> b.fold({ l2 -> OE.run { l1.compare(l2) } }, { LT }) },
  { r1 -> b.fold({ GT }, { r2 -> OA.run { r1.compare(r2) } }) }
)

fun <E, A> Validated<E, A>.compareTo(OE: Order<E>, OA: Order<A>, b: Validated<E, A>): Int =
  compare(OE, OA, b).toInt()

fun <E, A> Validated<E, A>.lt(OE: Order<E>, OA: Order<A>, b: Validated<E, A>): Boolean =
  compare(OE, OA, b) == LT

fun <E, A> Validated<E, A>.lte(OE: Order<E>, OA: Order<A>, b: Validated<E, A>): Boolean =
  compare(OE, OA, b) != GT

fun <E, A> Validated<E, A>.gt(OE: Order<E>, OA: Order<A>, b: Validated<E, A>): Boolean =
  compare(OE, OA, b) == GT

fun <E, A> Validated<E, A>.gte(OE: Order<E>, OA: Order<A>, b: Validated<E, A>): Boolean =
  compare(OE, OA, b) != LT

fun <E, A> Validated<E, A>.max(OE: Order<E>, OA: Order<A>, b: Validated<E, A>): Validated<E, A> =
  if (gt(OE, OA, b)) this else b

fun <E, A> Validated<E, A>.min(OE: Order<E>, OA: Order<A>, b: Validated<E, A>): Validated<E, A> =
  if (lt(OE, OA, b)) this else b

fun <E, A> Validated<E, A>.sort(OE: Order<E>, OA: Order<A>, b: Validated<E, A>): Tuple2<Validated<E, A>, Validated<E, A>> =
  if (gte(OE, OA, b)) Tuple2(this, b) else Tuple2(b, this)

fun <E, A, B> Validated<E, Either<A, B>>.select(f: Validated<E, (A) -> B>): Validated<E, B> =
  fold({ Invalid(it) }, { it.fold({ l -> f.map { ff -> ff(l) } }, { r -> r.valid() }) })

fun <E, A, B, C> Validated<E, Either<A, B>>.branch(fl: Validated<E, (A) -> C>, fr: Validated<E, (B) -> C>): Validated<E, C> =
  when (this) {
    is Validated.Valid -> when (val either = this.a) {
      is Either.Left -> fl.map { f -> f(either.a) }
      is Either.Right -> fr.map { f -> f(either.b) }
    }
    is Validated.Invalid -> this
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
inline fun <E, A> ValidatedOf<E, A>.getOrElse(default: () -> A): A =
  fix().fold({ default() }, ::identity)

/**
 * Return the Valid value, or null if Invalid
 */
fun <E, A> ValidatedOf<E, A>.orNull(): A? =
  getOrElse { null }

/**
 * Return the Valid value, or the result of f if Invalid
 */
inline fun <E, A> ValidatedOf<E, A>.valueOr(f: (E) -> A): A =
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

@Deprecated("@extension kinded projected functions are deprecated. Replace with traverse or traverseEither from arrow.core.*")
fun <G, E, A, B> ValidatedOf<E, A>.traverse(GA: Applicative<G>, f: (A) -> Kind<G, B>): Kind<G, Validated<E, B>> = GA.run {
  fix().fold({ e -> just(Invalid(e)) }, { a -> f(a).map(::Valid) })
}

@Deprecated("@extension kinded projected functions are deprecated. Replace with sequence or sequenceEither from arrow.core.*")
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
