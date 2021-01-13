package arrow.core.extensions.function0.selective

import arrow.Kind
import arrow.core.Either
import arrow.core.ForFunction0
import arrow.core.Function0
import arrow.core.Function0.Companion
import arrow.core.extensions.Function0Selective
import kotlin.Boolean
import kotlin.Deprecated
import kotlin.Function1
import kotlin.PublishedApi
import kotlin.Suppress
import kotlin.Unit
import kotlin.jvm.JvmName

/**
 * cached extension
 */
@PublishedApi()
internal val selective_singleton: Function0Selective = object :
    arrow.core.extensions.Function0Selective {}

@JvmName("select")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "select(arg1)",
  "arrow.core.select"
  ),
  DeprecationLevel.WARNING
)
fun <A, B> Kind<ForFunction0, Either<A, B>>.select(arg1: Kind<ForFunction0, Function1<A, B>>):
    Function0<B> = arrow.core.Function0.selective().run {
  this@select.select<A, B>(arg1) as arrow.core.Function0<B>
}

@JvmName("branch")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "branch(arg1, arg2)",
  "arrow.core.branch"
  ),
  DeprecationLevel.WARNING
)
fun <A, B, C> Kind<ForFunction0, Either<A, B>>.branch(
  arg1: Kind<ForFunction0, Function1<A, C>>,
  arg2: Kind<ForFunction0, Function1<B, C>>
): Function0<C> =
  arrow.core.Function0.selective().run {
    this@branch.branch<A, B, C>(arg1, arg2) as arrow.core.Function0<C>
  }

@JvmName("whenS")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "whenS(arg1)",
  "arrow.core.whenS"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForFunction0, Boolean>.whenS(arg1: Kind<ForFunction0, kotlin.Function0<Unit>>):
    Function0<Unit> = arrow.core.Function0.selective().run {
  this@whenS.whenS<A>(arg1) as arrow.core.Function0<kotlin.Unit>
}

@JvmName("ifS")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "ifS(arg1, arg2)",
  "arrow.core.ifS"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForFunction0, Boolean>.ifS(arg1: Kind<ForFunction0, A>, arg2: Kind<ForFunction0, A>):
    Function0<A> = arrow.core.Function0.selective().run {
  this@ifS.ifS<A>(arg1, arg2) as arrow.core.Function0<A>
}

@JvmName("orS")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "orS(arg1)",
  "arrow.core.orS"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForFunction0, Boolean>.orS(arg1: Kind<ForFunction0, Boolean>): Function0<Boolean> =
    arrow.core.Function0.selective().run {
  this@orS.orS<A>(arg1) as arrow.core.Function0<kotlin.Boolean>
}

@JvmName("andS")
@Suppress(
  "UNCHECKED_CAST",
  "USELESS_CAST",
  "EXTENSION_SHADOWED_BY_MEMBER",
  "UNUSED_PARAMETER"
)
@Deprecated(
  "@extension kinded projected functions are deprecated",
  ReplaceWith(
  "andS(arg1)",
  "arrow.core.andS"
  ),
  DeprecationLevel.WARNING
)
fun <A> Kind<ForFunction0, Boolean>.andS(arg1: Kind<ForFunction0, Boolean>): Function0<Boolean> =
    arrow.core.Function0.selective().run {
  this@andS.andS<A>(arg1) as arrow.core.Function0<kotlin.Boolean>
}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun Companion.selective(): Function0Selective = selective_singleton
