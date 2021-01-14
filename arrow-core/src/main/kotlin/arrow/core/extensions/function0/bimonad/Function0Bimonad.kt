package arrow.core.extensions.function0.bimonad

import arrow.core.Function0.Companion
import arrow.core.extensions.Function0Bimonad
import kotlin.PublishedApi
import kotlin.Suppress

/**
 * cached extension
 */
@PublishedApi()
internal val bimonad_singleton: Function0Bimonad = object : arrow.core.extensions.Function0Bimonad
    {}

@Suppress(
  "UNCHECKED_CAST",
  "NOTHING_TO_INLINE"
)
inline fun Companion.bimonad(): Function0Bimonad = bimonad_singleton
