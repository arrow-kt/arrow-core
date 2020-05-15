package arrow.core

import arrow.higherkind

/**
 *
 */
@higherkind
sealed class Option2<out A, out B> : Option2Of<A, B> {

  companion object {

    operator fun <A, B> invoke(a: A, b: B): Option2<A, B> = Some(a, b)

    fun <A, B> some(a: A, b: B): Option2<A, B> = Some(a, b)

    fun none(): Option2<Nothing, Nothing> = None
  }

  inline fun <D> map(f: (B) -> D): Option2<A, D> =
    fold({ None }, { a, b -> Option2(a, f(b)) })

  inline fun <C, D> bimap(fa: (A) -> C, fb: (B) -> D): Option2<C, D> =
    fold({ None }, { a, b -> Option2(fa(a), fb(b)) })

  inline fun <C> fold(ifNeither: () -> C, ifBoth: (A, B) -> C): C = when (this) {
    is None -> ifNeither()
    is Some -> ifBoth(a, b)
  }

  object None : Option2<Nothing, Nothing>()
  data class Some<out A, out B>(val a: A, val b: B) : Option2<A, B>()
}

fun <A, B> Pair<A, B>.toOption2(): Option2<A, B> = Option2(first, second)

fun <A, B> Tuple2<A, B>.toOption2(): Option2<A, B> = Option2(a, b)

fun <A, B> Option2<A, B>.unwrap(): Option<Tuple2<A, B>> =
  fold({ None }, { a, b -> Some(a toT b) })

fun <A> Option2<A, Nothing>.left(): Option<A> = when (this) {
  is Option2.None -> None
  is Option2.Some -> Some(a)
}

fun <B> Option2<Nothing, B>.right(): Option<B> = when (this) {
  is Option2.None -> None
  is Option2.Some -> Some(b)
}

operator fun <A> Option2<A, Nothing>.component1(): Option<A> = left()

operator fun <B> Option2<Nothing, B>.component2(): Option<B> = right()
