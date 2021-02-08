package arrow.typeclasses

import arrow.KindDeprecation

/**
 * ank_macro_hierarchy(arrow.typeclasses.Bimonad)
 */
@Deprecated(KindDeprecation)
interface Bimonad<F> : Monad<F>, Comonad<F> {

  override val fx: BiMonadFx<F>
    get() = object : BiMonadFx<F> {
      override val M: Monad<F> = this@Bimonad
      override val CM: Comonad<F> = this@Bimonad
    }
}

interface BiMonadFx<F> : MonadFx<F>, ComonadFx<F>
