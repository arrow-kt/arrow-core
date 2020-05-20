package arrow.core

import arrow.higherkind
import arrow.typeclasses.Semigroup
import arrow.typeclasses.Show
import arrow.typeclasses.invoke

/**
 * ank_macro_hierarchy(arrow.core.Option2)
 *
 * You may be already familiar with [Option]. If allows you to work with the uncertainty of a values being present.
 * Similarly to the thought experiment of Schrodinger's cat, it's not possible to know if the value is the until observnig the contents.
 *
 * In some situations you may want to have two values. [Either] let's you have one or other value and [Ior] allows
 * to have either of them but also both at the same time.
 *
 * What if you have a situation where you require either both values or non at all? That's where [Option2] helps.
 *
 * The definition of [Option2] is isomorphic to having [Option]<[Tuple2]<[A], [B]>> with shorthands that simplify operations, avoiding nesting them.
 *
 * One use case for this type class is when working with Location data, latitude and longitude.
 * You either have both values or non if the location is not available yet.
 *
 * ```kotlin:ank
 * inline class Latitude(val latitude: Long)
 * inline class Longitude(val longitude: Long)
 *
 * fun currentLocation(): Option2<Latitude, Longitude> = Option.none()
 * ```
 *
 */
@higherkind
sealed class Option2<out A, out B> : Option2Of<A, B> {

  companion object {

    operator fun <A, B> invoke(a: A, b: B): Option2<A, B> = Some(a, b)

    fun <A, B> just(a: A, b: B): Option2<A, B> = Some(a, b)

    fun none(): Option2<Nothing, Nothing> = None

    fun <L, A, B> tailRecM(a: A, f: (A) -> Option2Of<L, Either<A, B>>, SL: Semigroup<L>): Option2<L, B> =
      f(a).loop(SL, f)
  }

  object None : Option2<Nothing, Nothing>()
  data class Some<out A, out B>(val a: A, val b: B) : Option2<A, B>()
}

private tailrec fun <L, A, B> Option2Of<L, Either<A, B>>.loop(
  SL: Semigroup<L>,
  f: (A) -> Option2Of<L, Either<A, B>>
): Option2<L, B> = when (this) {
  is Option2.Some -> when (b) {
    is Either.Right -> Option2(a, b.b)
    is Either.Left -> when (val fnb = f(b.a)) {
      is Option2.Some -> Option2(SL { a + fnb.a }, fnb.b).loop(SL, f)
      else -> Option2.None
    }
  }
  else -> Option2.None
}

fun <A, B, C> Option2Of<A, B>.map(f: (B) -> C): Option2<A, C> =
  flatMap { a, b -> Option2(a, f(b)) }

fun <A, B, C, D> Option2Of<A, B>.map(f: (A, B) -> Pair<C, D>): Option2<C, D> =
  flatMap { a, b -> f(a, b).toOption2() }

fun <A, B, C, D> Option2Of<A, B>.bimap(fa: (A) -> C, fb: (B) -> D): Option2<C, D> =
  flatMap { a, b -> Option2(fa(a), fb(b)) }

inline fun <A, B, C> Option2Of<A, B>.fold(ifEmpty: () -> C, ifSome: (A, B) -> C): C =
  if (this is Option2.Some) ifSome(a, b) else ifEmpty()

fun <A, B, D> Option2Of<A, B>.flatMap(SG: Semigroup<A>, f: (B) -> Option2Of<A, D>): Option2<A, D> =
  flatMap { l, r -> f(r).fold({ Option2.None }, { a, b -> Option2(SG { l + a }, b) }) }

fun <A, B, C, D> Option2Of<A, B>.flatMap(f: (A, B) -> Option2<C, D>): Option2<C, D> =
  fold({ Option2.None }, f)

fun <A, B, D> Option2Of<A, B>.ap(SG: Semigroup<A>, ff: Option2Of<A, (B) -> D>): Option2<A, D> =
  flatMap(SG) { a -> ff.map { f -> f(a) } }

fun <A, B> Pair<A, B>.toOption2(): Option2<A, B> = Option2(first, second)

fun <A, B> Tuple2<A, B>.toOption2(): Option2<A, B> = Option2(a, b)

fun <A, B> Option2Of<A, B>.unwrap(): Option<Tuple2<A, B>> = fold({ None }, { a, b -> Some(a toT b) })

fun <A> Option2Of<A, Nothing>.left(): Option<A> = fold({ Option.empty() }, { a, _ -> Some(a) })

fun <B> Option2Of<Nothing, B>.right(): Option<B> = fold({ Option.empty() }, { _, b -> Some(b) })

operator fun <A> Option2Of<A, Nothing>.component1(): Option<A> = left()

operator fun <B> Option2Of<Nothing, B>.component2(): Option<B> = right()

fun <A, B> Option2Of<A, B>.show(SA: Show<A>, SB: Show<B>): String =
  fold({ "Option2.None" }, { a, b -> "Option2.Some(a=${SA { a.show() }}, b=${SB { b.show() }})" })
