package arrow

/**
 * `Kind<F, A>` represents a generic `F<A>` in a way that's allowed by Kotlin.
 * To revert it back to its original form use the extension function `fix()`.
 *
 * ```kotlin:ank:playground
 * import arrow.Kind
 * import arrow.core.*
 *
 * fun main(args: Array<String>) {
 *   //sampleStart
 *   val a: Kind<ForOption, Int> = Option(1)
 *   val fixedA: Option<Int> = a.fix()
 *   //sampleEnd
 *   println(fixedA)
 * }
 * ```
 */

Kind<F, A> = Kind2<F, Nothing, A>

Kind2<F, E, A> -> Kind<F, A> .map, fla

@documented
interface Kind<out F, out A> : Kind2<F, Nothing, A> {
  fun <B> nest(): Kind2<F, B, A> = this
}
interface Kind2<out F, out A, out B> : Kind3<F, A, Nothing, B> {
  fun <B> unnest(): Kind<F, B> = this as Kind<F, B>
}
interface Kind3<out F, out A, out B, out C> : Kind4<F, A, B, Nothing, C>
interface Kind4<out F, out A, out B, out C, out D> : Kind5<F, A, B, C, Nothing, D>
interface Kind5<out F, out A, out B, out C, out D, out E> : Kind6<F, A, B, C, D, Nothing, E>
interface Kind6<out F, out A, out B, out C, out D, out E, out G> : Kind7<F, A, B, C, D, E, Nothing, G>
interface Kind7<out F, out A, out B, out C, out D, out E, out G, out H> : Kind8<F, A, B, C, D, E, G, Nothing, H>
interface Kind8<out F, out A, out B, out C, out D, out E, out G, out H, out I> : Kind9<F, A, B, C, D, E, G, H, Nothing, I>
interface Kind9<out F, out A, out B, out C, out D, out E, out G, out H, out I, out J> : Kind10<F, A, B, C, D, E, G, H, I, Nothing, J>
interface Kind10<out F, out A, out B, out C, out D, out E, out G, out H, out I, out J, out K> : Kind11<F, A, B, C, D, E, G, H, I, J, Nothing, K>
interface Kind11<out F, out A, out B, out C, out D, out E, out G, out H, out I, out J, out K, out L> : Kind12<F, A, B, C, D, E, G, H, I, J, K, Nothing, L>
interface Kind12<out F, out A, out B, out C, out D, out E, out G, out H, out I, out J, out K, out L, out M> : Kind13<F, A, B, C, D, E, G, H, I, J, K, L, Nothing, M>
interface Kind13<out F, out A, out B, out C, out D, out E, out G, out H, out I, out J, out K, out L, out M, out N> : Kind14<F, A, B, C, D, E, G, H, I, J, K, L, M, Nothing, N>
interface Kind14<out F, out A, out B, out C, out D, out E, out G, out H, out I, out J, out K, out L, out M, out N, out O> : Kind15<F, A, B, C, D, E, G, H, I, J, K, L, M, N, Nothing, O>
interface Kind15<out F, out A, out B, out C, out D, out E, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P> : Kind16<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, Nothing, P>
interface Kind16<out F, out A, out B, out C, out D, out E, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q> : Kind17<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Nothing, Q>
interface Kind17<out F, out A, out B, out C, out D, out E, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R> : Kind18<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Q, Nothing, R>
interface Kind18<out F, out A, out B, out C, out D, out E, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R, out S> : Kind19<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Q, R, Nothing, S>
interface Kind19<out F, out A, out B, out C, out D, out E, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R, out S, out T> : Kind20<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Q, R, S, Nothing, T>
interface Kind20<out F, out A, out B, out C, out D, out E, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R, out S, out T, out U> : Kind21<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, Nothing, U>
interface Kind21<out F, out A, out B, out C, out D, out E, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R, out S, out T, out U, out V> : Kind22<F, A, B, C, D, E, G, H, I, J, K, L, M, N, O, P, Q, R, S, T, U, Nothing, V>
interface Kind22<out F, out A, out B, out C, out D, out E, out G, out H, out I, out J, out K, out L, out M, out N, out O, out P, out Q, out R, out S, out T, out U, out V, out W>
