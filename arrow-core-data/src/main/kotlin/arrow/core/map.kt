package arrow.core

object MapInstances

object SortedMapInstances

fun <K, A, B> Map<K, A>.product(fb: Map<K, B>): Map<K, Tuple2<A, B>> =
  MapK.mapN(this, fb) { _, a, b -> Tuple2(a, b) }

@JvmName("product3")
fun <K, A, B, C> Map<K, Tuple2<A, B>>.product(other: Map<K, C>): Map<K, Tuple3<A, B, C>> =
  MapK.mapN(this, other) { _, ab, c -> Tuple3(ab.a, ab.b, c) }

@JvmName("product4")
fun <K, A, B, C, D> Map<K, Tuple3<A, B, C>>.product(other: Map<K, D>): Map<K, Tuple4<A, B, C, D>> =
  MapK.mapN(this, other) { _, abc, d -> Tuple4(abc.a, abc.b, abc.c, d) }

@JvmName("product5")
fun <K, A, B, C, D, EE> Map<K, Tuple4<A, B, C, D>>.product(other: Map<K, EE>): Map<K, Tuple5<A, B, C, D, EE>> =
  MapK.mapN(this, other) { _, abcd, e -> Tuple5(abcd.a, abcd.b, abcd.c, abcd.d, e) }

@JvmName("product6")
fun <K, A, B, C, D, EE, F> Map<K, Tuple5<A, B, C, D, EE>>.product(other: Map<K, F>): Map<K, Tuple6<A, B, C, D, EE, F>> =
  MapK.mapN(this, other) { _, abcde, f -> Tuple6(abcde.a, abcde.b, abcde.c, abcde.d, abcde.e, f) }

@JvmName("product7")
fun <K, A, B, C, D, EE, F, G> Map<K, Tuple6<A, B, C, D, EE, F>>.product(
  other: Map<K, G>
): Map<K, Tuple7<A, B, C, D, EE, F, G>> =
  MapK.mapN(this, other) { _, abcdef, g -> Tuple7(abcdef.a, abcdef.b, abcdef.c, abcdef.d, abcdef.e, abcdef.f, g) }

@JvmName("product8")
fun <K, A, B, C, D, EE, F, G, H> Map<K, Tuple7<A, B, C, D, EE, F, G>>.product(
  other: Map<K, H>
): Map<K, Tuple8<A, B, C, D, EE, F, G, H>> =
  MapK.mapN(this, other) { _, abcdefg, h -> Tuple8(abcdefg.a, abcdefg.b, abcdefg.c, abcdefg.d, abcdefg.e, abcdefg.f, abcdefg.g, h) }

@JvmName("product9")
fun <K, A, B, C, D, EE, F, G, H, I> Map<K, Tuple8<A, B, C, D, EE, F, G, H>>.product(
  other: Map<K, I>
): Map<K, Tuple9<A, B, C, D, EE, F, G, H, I>> =
  MapK.mapN(this, other) { _, abcdefgh, i -> Tuple9(abcdefgh.a, abcdefgh.b, abcdefgh.c, abcdefgh.d, abcdefgh.e, abcdefgh.f, abcdefgh.g, abcdefgh.h, i) }

@JvmName("product10")
fun <K, A, B, C, D, EE, F, G, H, I, J> Map<K, Tuple9<A, B, C, D, EE, F, G, H, I>>.product(
  other: Map<K, J>
): Map<K, Tuple10<A, B, C, D, EE, F, G, H, I, J>> =
  MapK.mapN(this, other) { _, abcdefghi, j -> Tuple10(abcdefghi.a, abcdefghi.b, abcdefghi.c, abcdefghi.d, abcdefghi.e, abcdefghi.f, abcdefghi.g, abcdefghi.h, abcdefghi.i, j) }
