@file:Suppress("NAME_SHADOWING")
package arrow.core

inline fun <A, R> map1(a: A?, fn: (A) -> R): R? =
  map2(a, Unit) { a, _ -> fn(a) }

inline fun <A, B, R> map2(a: A?, b: B?, fn: (A, B) -> R): R? =
  map3(a, b, Unit) { a, b, _ -> fn(a, b) }

inline fun <A, B, C, R> map3(a: A?, b: B?, c: C?, fn: (A, B, C) -> R): R? =
  map4(a, b, c, Unit) { a, b, c, _ -> fn(a, b, c) }

inline fun <A, B, C, D, R> map4(a: A?, b: B?, c: C?, d: D?, fn: (A, B, C, D) -> R): R? =
  map5(a, b, c, d, Unit) { a, b, c, d, _ -> fn(a, b, c, d) }

inline fun <A, B, C, D, E, R> map5(a: A?, b: B?, c: C?, d: D?, e: E?, fn: (A, B, C, D, E) -> R): R? =
  map6(a, b, c, d, e, Unit) { a, b, c, d, e, _ -> fn(a, b, c, d, e) }

inline fun <A, B, C, D, E, F, R> map6(a: A?, b: B?, c: C?, d: D?, e: E?, f: F?, fn: (A, B, C, D, E, F) -> R): R? =
  map7(a, b, c, d, e, f, Unit) { a, b, c, d, e, f, _ -> fn(a, b, c, d, e, f) }

inline fun <A, B, C, D, E, F, G, R> map7(a: A?, b: B?, c: C?, d: D?, e: E?, f: F?, g: G?, fn: (A, B, C, D, E, F, G) -> R): R? =
  map8(a, b, c, d, e, f, g, Unit) { a, b, c, d, e, f, g, _ -> fn(a, b, c, d, e, f, g) }

inline fun <A, B, C, D, E, F, G, H, R> map8(a: A?, b: B?, c: C?, d: D?, e: E?, f: F?, g: G?, h: H?, fn: (A, B, C, D, E, F, G, H) -> R): R? =
  map9(a, b, c, d, e, f, g, h, Unit) { a, b, c, d, e, f, g, h, _ -> fn(a, b, c, d, e, f, g, h) }

inline fun <A, B, C, D, E, F, G, H, I, R> map9(a: A?, b: B?, c: C?, d: D?, e: E?, f: F?, g: G?, h: H?, i: I?, fn: (A, B, C, D, E, F, G, H, I) -> R): R? =
  a?.let { a -> b?.let { b -> c?.let { c -> d?.let { d -> e?.let { e -> f?.let { f -> g?.let { g -> h?.let { h -> i?.let { i ->
    fn(a, b, c, d, e, f, g, h, i)
  } } } } } } } } }
