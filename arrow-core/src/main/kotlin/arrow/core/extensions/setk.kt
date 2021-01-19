package arrow.core.extensions

import arrow.Kind
import arrow.core.Eval
import arrow.core.ForSetK
import arrow.core.SetK
import arrow.core.Tuple2
import arrow.core.extensions.setk.eq.eq
import arrow.core.fix
import arrow.core.k
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.Foldable
import arrow.typeclasses.Hash
import arrow.typeclasses.Monoid
import arrow.typeclasses.MonoidK
import arrow.typeclasses.Monoidal
import arrow.typeclasses.Semigroup
import arrow.typeclasses.SemigroupK
import arrow.typeclasses.Semigroupal
import arrow.typeclasses.Show
import arrow.typeclasses.hashWithSalt
import arrow.core.combineK as setCombineK

@Deprecated(
  "Typeclass instance have been moved to the companion object of the typeclass.",
  ReplaceWith("Semigroup.setK<A>()", "arrow.core.extensions", "arrow.core.extensions"),
  DeprecationLevel.WARNING)
interface SetKSemigroup<A> : Semigroup<SetK<A>> {
  override fun SetK<A>.combine(b: SetK<A>): SetK<A> =
    this.fix().setCombineK(b)
}

@Deprecated(
  "Typeclass instance have been moved to the companion object of the typeclass.",
  ReplaceWith("Monoid.setK<A>()", "arrow.core.extensions", "arrow.core.extensions"),
  DeprecationLevel.WARNING)
interface SetKMonoid<A> : Monoid<SetK<A>>, SetKSemigroup<A> {
  override fun empty(): SetK<A> = emptySet<A>().k()
}

@Deprecated(
  "Typeclass instance have been moved to the companion object of the typeclass.",
  ReplaceWith("Eq.setK<A>()", "arrow.core.extensions", "arrow.core.extensions"),
  DeprecationLevel.WARNING)
interface SetKEq<A> : Eq<SetK<A>> {

  fun EQ(): Eq<A>

  override fun SetK<A>.eqv(b: SetK<A>): Boolean =
    if (size == b.size) map { aa ->
      b.find { bb -> EQ().run { aa.eqv(bb) } } != null
    }.fold(true) { acc, bool ->
      acc && bool
    }
    else false
}

@Deprecated(
  "Typeclass instance have been moved to the companion object of the typeclass.",
  ReplaceWith("Show.setK<A>()", "arrow.core.extensions", "arrow.core.extensions"),
  DeprecationLevel.WARNING)
interface SetKShow<A> : Show<SetK<A>> {
  fun SA(): Show<A>
  override fun SetK<A>.show(): String = show(SA())
}

@Deprecated(
  "Typeclass instance have been moved to the companion object of the typeclass.",
  ReplaceWith("Foldable.setK()", "arrow.core.extensions", "arrow.core.extensions"),
  DeprecationLevel.WARNING)
interface SetKFoldable : Foldable<ForSetK> {
  override fun <A, B> Kind<ForSetK, A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> Kind<ForSetK, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().foldRight(lb, f)

  override fun <A> Kind<ForSetK, A>.isEmpty(): kotlin.Boolean =
    fix().isEmpty()
}

@Deprecated(
  "Typeclass instance have been moved to the companion object of the typeclass.",
  ReplaceWith("SemigroupK.setK()", "arrow.core.extensions", "arrow.core.extensions"),
  DeprecationLevel.WARNING)
interface SetKSemigroupK : SemigroupK<ForSetK> {
  override fun <A> Kind<ForSetK, A>.combineK(y: Kind<ForSetK, A>): SetK<A> =
    fix().setCombineK(y)
}

@Deprecated(
  "Typeclass instance have been moved to the companion object of the typeclass.",
  ReplaceWith("Semigroupal.setK()", "arrow.core.extensions", "arrow.core.extensions"),
  DeprecationLevel.WARNING)
interface SetKSemigroupal : Semigroupal<ForSetK> {
  override fun <A, B> Kind<ForSetK, A>.product(fb: Kind<ForSetK, B>): Kind<ForSetK, Tuple2<A, B>> =
    fb.fix().flatMap { b -> this.fix().map { a -> Tuple2(a, b) } }.toSet().k()
}

@Deprecated(
  "Typeclass instance have been moved to the companion object of the typeclass.",
  ReplaceWith("Monoidal.setK()", "arrow.core.extensions", "arrow.core.extensions"),
  DeprecationLevel.WARNING)
interface SetKMonoidal : Monoidal<ForSetK>, SetKSemigroupal {
  override fun <A> identity(): Kind<ForSetK, A> = SetK.empty()
}

@Deprecated(
  "Typeclass instance have been moved to the companion object of the typeclass.",
  ReplaceWith("MonoidK.setK()", "arrow.core.extensions", "arrow.core.extensions"),
  DeprecationLevel.WARNING)
interface SetKMonoidK : MonoidK<ForSetK> {
  override fun <A> empty(): SetK<A> =
    SetK.empty()

  override fun <A> Kind<ForSetK, A>.combineK(y: Kind<ForSetK, A>): SetK<A> =
    fix().setCombineK(y)
}

@Deprecated(
  "Typeclass instance have been moved to the companion object of the typeclass.",
  ReplaceWith("Hash.setK<A>()", "arrow.core.extensions", "arrow.core.extensions"),
  DeprecationLevel.WARNING)
interface SetKHash<A> : Hash<SetK<A>> {
  fun HA(): Hash<A>

  override fun SetK<A>.hashWithSalt(salt: Int): Int =
    HA().run { foldLeft(salt) { hash, v -> v.hashWithSalt(hash) } }.hashWithSalt(size)
}

@Deprecated(
  "Typeclass instance have been moved to the companion object of the typeclass.",
  ReplaceWith("EqK.setK()", "arrow.core.extensions", "arrow.core.extensions"),
  DeprecationLevel.WARNING)
interface SetKEqK : EqK<ForSetK> {
  override fun <A> Kind<ForSetK, A>.eqK(other: Kind<ForSetK, A>, EQ: Eq<A>) =
    (this.fix() to other.fix()).let {
      SetK.eq(EQ).run {
        it.first.eqv(it.second)
      }
    }
}

/**
 * [Semigroup] instance definition for [SetK]
 */
fun <A> Semigroup.Companion.setK(): Semigroup<SetK<A>> = Semigroup<SetK<A>> { b ->
  this.fix().setCombineK(b)
}

/**
 * [Monoid] instance definition for [SetK]
 */
fun <A> Monoid.Companion.setK(): Monoid<SetK<A>> = object : Monoid<SetK<A>>, SetKSemigroup<A> {
  override fun empty(): SetK<A> =
    emptySet<A>().k()
}

/**
 * [Eq] instance definition for [SetK]
 */
fun <A> Eq.Companion.setK(EQ: () -> Eq<A>): Eq<SetK<A>> = object : Eq<SetK<A>> {
  override fun SetK<A>.eqv(b: SetK<A>): Boolean =
    if (size == b.size) map { aa ->
      b.find { bb -> EQ().run { aa.eqv(bb) } } != null
    }.fold(true) { acc, bool ->
      acc && bool
    }
    else false
}

/**
 * [Show] instance definition for [SetK]
 */
fun <A> Show.Companion.setK(SA: () -> Show<A>): Show<SetK<A>> = object : Show<SetK<A>> {
  override fun SetK<A>.show(): String =
    show(SA())
}

/**
 * [Foldable] instance definition for [SetK]
 */
fun Foldable.Companion.setK(): Foldable<ForSetK> = object : Foldable<ForSetK> {
  override fun <A, B> Kind<ForSetK, A>.foldLeft(b: B, f: (B, A) -> B): B =
    fix().foldLeft(b, f)

  override fun <A, B> Kind<ForSetK, A>.foldRight(lb: Eval<B>, f: (A, Eval<B>) -> Eval<B>): Eval<B> =
    fix().foldRight(lb, f)

  override fun <A> Kind<ForSetK, A>.isEmpty(): kotlin.Boolean =
    fix().isEmpty()
}

/**
 * [SemigroupK] instance definition for [SetK]
 */
fun SemigroupK.Companion.setK(): SemigroupK<ForSetK> = object : SemigroupK<ForSetK> {
  override fun <A> Kind<ForSetK, A>.combineK(y: Kind<ForSetK, A>): Kind<ForSetK, A> =
    fix().setCombineK(y)
}

/**
 * [Semigroupal] instance definition for [SetK]
 */
fun Semigroupal.Companion.setK(): Semigroupal<ForSetK> = object : Semigroupal<ForSetK> {
  override fun <A, B> Kind<ForSetK, A>.product(fb: Kind<ForSetK, B>): Kind<ForSetK, Tuple2<A, B>> =
    fb.fix().flatMap { b -> this.fix().map { a -> Tuple2(a, b) } }.toSet().k()
}

/**
 * [Monoidal] instance definition for [SetK]
 */
fun Monoidal.Companion.setK(): Monoidal<ForSetK> = object : Monoidal<ForSetK>, SetKSemigroupal {
  override fun <A> identity(): Kind<ForSetK, A> =
    SetK.empty()
}

/**
 * [MonoidK] instance definition for [SetK]
 */
fun MonoidK.Companion.setK(): MonoidK<ForSetK> = object : MonoidK<ForSetK> {
  override fun <A> empty(): SetK<A> =
    SetK.empty()

  override fun <A> Kind<ForSetK, A>.combineK(y: Kind<ForSetK, A>): SetK<A> =
    fix().setCombineK(y)
}

/**
 * [Hash] instance definition for [SetK]
 */
fun <A> Hash.Companion.setK(HA: () -> Hash<A>): Hash<SetK<A>> = object : Hash<SetK<A>> {
  override fun SetK<A>.hashWithSalt(salt: Int): Int =
    HA().run { foldLeft(salt) { hash, v -> v.hashWithSalt(hash) } }.hashWithSalt(size)
}

/**
 * [EqK] instance definition for [SetK]
 */
fun EqK.Companion.setK(): EqK<ForSetK> = object : EqK<ForSetK> {
  override fun <A> Kind<ForSetK, A>.eqK(other: Kind<ForSetK, A>, EQ: Eq<A>): Boolean =
    (this.fix() to other.fix()).let {
      SetK.eq(EQ).run {
        it.first.eqv(it.second)
      }
    }
}
