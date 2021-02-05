package arrow.typeclasses

import arrow.Kind
import arrow.KindDeprecation
import arrow.core.FunctionK

@Deprecated(KindDeprecation)
/**
 * Inject type class as described in "Data types a la carte" (Swierstra 2008).
 *
 * @see [[http://www.staff.science.uu.nl/~swier004/publications/2008-jfp.pdf]]
 */
interface Inject<F, G> {

  fun inj(): FunctionK<F, G>

  fun <A> Kind<F, A>.inject(): Kind<G, A> = inj()(this)
}
