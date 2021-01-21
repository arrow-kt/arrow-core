package arrow.core.extensions.either.zip

import arrow.core.Either
import arrow.core.Tuple2

@JvmName("zip")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension projected functions are deprecated",
  ReplaceWith(
    "this.zip(arg1, ::Tuple2)",
    "arrow.core.zip", "arrow.core.Tuple2"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, E> Either<E, A>.zip(arg1: Either<E, B>): Either<E, Tuple2<A, B>> =
  TODO()

@JvmName("zipWith")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension projected functions are deprecated",
  ReplaceWith(
    "this.zip(arg1, arg2)",
    "arrow.core.zip"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C, E> Either<E, A>.zipWith(arg1: Either<E, B>, arg2: Function2<A, B, C>): Either<E, C> =
  TODO()
