package arrow.core

import arrow.higherkind
import arrow.typeclasses.Semigroup
import arrow.typeclasses.Show

@higherkind
sealed class Can<out A, out B> : CanOf<A, B> {

  object None : Can<Nothing, Nothing>()
  data class Left<A>(val a: A) : Can<A, Nothing>()
  data class Right<B>(val b: B) : Can<Nothing, B>()
  data class Both<A, B>(val a: A, val b: B) : Can<A, B>()

  companion object {

    fun none(): Can<Nothing, Nothing> = None
    fun <A> left(left: A): Can<A, Nothing> = Left(left)
    fun <B> right(right: B): Can<Nothing, B> = Right(right)
    fun <A, B> both(left: A, right: B): Can<A, B> = Both(left, right)

    operator fun <A, B> invoke(ior: Ior<A, B>): Can<A, B> = ior.fold(::Left, ::Right, ::Both)

    private tailrec fun <A2, A, B> Semigroup<A2>.loop(
      v: Can<A2, Either<A, B>>,
      f: (A) -> CanOf<A2, Either<A, B>>
    ): Can<A2, B> = when (v) {
      is None -> None
      is Left -> Left(v.a)
      is Right -> when (val either = v.b) {
        is Either.Right -> Right(either.b)
        is Either.Left -> loop(f(either.a).fix(), f)
      }
      is Both -> when (val either = v.b) {
        is Either.Right -> Both(v.a, either.b)
        is Either.Left -> when (val fnb = f(either.a).fix()) {
          is None -> None
          is Left -> Left(v.a.combine(fnb.a))
          is Right -> loop(Both(v.a, fnb.b), f)
          is Both -> loop(Both(v.a.combine(fnb.a), fnb.b), f)
        }
      }
    }

    fun <A2, A, B> tailRecM(a: A, f: (A) -> CanOf<A2, Either<A, B>>, SL: Semigroup<A2>): Can<A2, B> =
      SL.run { loop(f(a).fix(), f) }

  }

}

fun <A, B, C> Can<A, B>.fold(
  ifNone: () -> C,
  ifLeft: (A) -> C,
  ifRight: (B) -> C,
  ifBoth: (A, B) -> C
): C = when (this) {
  is Can.None -> ifNone()
  is Can.Left -> ifLeft(a)
  is Can.Right -> ifRight(b)
  is Can.Both -> ifBoth(a, b)
}

/**
 * Binds the given function across [Can.Right].
 *
 * @param f The function to bind across [Can.Right].
 */
fun <A, B, B2> Can<A, B>.flatMap(SG: Semigroup<A>, f: (B) -> Can<A, B2>): Can<A, B2> =
  fold(
    ifNone = Can.Companion::none,
    ifLeft = { Can.Left(it) },
    ifRight = f,
    ifBoth = { l, r ->
      with(SG) {
        f(r).fold(
          ifNone = Can.Companion::none,
          ifLeft = { Can.Left(l.combine(it)) },
          ifRight = { Can.Both(l, it) },
          ifBoth = { ll, rr -> Can.Both(l.combine(ll), rr) }
        )
      }
    }
  )

fun <A, B, B2> CanOf<A, B>.flatMap(f: (B) -> Can<A, B2>): Can<A, B2> =
  fix().run {
    when (this) {
      is Can.None -> Can.None
      is Can.Left -> Can.Left(a)
      is Can.Right -> f(b)
      is Can.Both -> f(b)
    }
  }

fun <A, B, C> CanOf<A, B>.map(f: (B) -> C): Can<A, C> =
  flatMap { Can.Right(f(it)) }

fun <L, R, L2, R2> CanOf<L, R>.bimap(leftOperation: (L) -> L2, rightOperation: (R) -> R2): Can<L2, R2> =
  fix().fold(
    ifNone = { Can.None },
    ifLeft = { Can.Left(leftOperation(it)) },
    ifRight = { Can.Right(rightOperation(it)) },
    ifBoth = { l, r -> Can.Both(leftOperation(l), rightOperation(r)) }
  )

fun <A, B, B2> CanOf<A, B>.ap(SG: Semigroup<A>, ff: CanOf<A, (B) -> B2>): Can<A, B2> =
  fix().flatMap(SG) { a -> ff.fix().map { f -> f(a) } }

fun <A, B> Pair<A, B>.bothCan(): Can<A, B> = Can.Both(first, second)

fun <A, B> Tuple2<A, B>.bothCan(): Can<A, B> = Can.Both(a, b)

fun <A> A.leftCan(): Can<A, Nothing> = Can.Left(this)

fun <B> B.rightCan(): Can<Nothing, B> = Can.Right(this)

fun <A, B> CanOf<A, B>.left(): Option<A> =
  fix().fold(
    ifNone = { None },
    ifLeft = { Option.just(it) },
    ifRight = { None },
    ifBoth = { l, _ -> Option.just(l) }
  )

operator fun <A, B> CanOf<A, B>.component1(): Option<A> = left()

fun <A, B> CanOf<A, B>.right(): Option<B> =
  fix().fold(
    ifNone = { None },
    ifLeft = { None },
    ifRight = { Option.just(it) },
    ifBoth = { _, r -> Option.just(r) }
  )

operator fun <A, B> CanOf<A, B>.component2(): Option<B> = right()

fun <A, B> CanOf<A, B>.show(SL: Show<A>, SR: Show<B>): String =
  fix().fold(
    ifNone = { "None" },
    ifLeft = { "Left(${SL.run { it.show() }})" },
    ifRight = { "Right(${SR.run { it.show() }})" },
    ifBoth = { a, b -> "Both(left=${SL.run { a.show() }}, right=${SR.run { b.show() }})" }
  )
