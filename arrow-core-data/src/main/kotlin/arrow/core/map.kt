package arrow.core

object MapInstances

object SortedMapInstances

fun <K, A, B> Map<K, A>.align(b: Map<K, B>): Map<K, Ior<A, B>> =
  (keys + b.keys).mapNotNull { key ->
    Ior.fromNullables(this@align[key], b[key])?.let { key to it }
  }.toMap()

fun <K, A, B, C> Map<K, A>.alignWith(b: Map<K, B>, fa: (Ior<A, B>) -> C): Map<K, C> =
  align(b).mapValues { (_, v) -> fa(v) }
