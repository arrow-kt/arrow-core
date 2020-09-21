package arrow.typeclasses

import arrow.Kind
import arrow.core.ForId
import arrow.core.Id
import arrow.core.IdOf
import arrow.core.Option
import arrow.core.fix
import arrow.core.value

/**
 * ank_macro_hierarchy(arrow.typeclasses.TraverseFilter)
 */
interface TraverseFilter<F> : Traverse<F>, FunctorFilter<F> {

  private object IdApplicative : Applicative<ForId> {
    override fun <A, B> IdOf<A>.ap(ff: IdOf<(A) -> B>): Id<B> =
      fix().ap(ff)

    override fun <A, B> IdOf<A>.map(f: (A) -> B): Id<B> =
      fix().map(f)

    override fun <A> just(a: A): Id<A> =
      Id.just(a)
  }

  /**
   * Returns [F]<[B]> in [G] context by applying [AP] on a selector function [f], which returns [Option] of [B]
   * in [G] context.
   */
  @Deprecated("Please use traverseFilterNullable")
  fun <G, A, B> Kind<F, A>.traverseFilter(AP: Applicative<G>, f: (A) -> Kind<G, Option<B>>): Kind<G, Kind<F, B>>

  fun <G, A, B> Kind<F, A>.traverseFilterNullable(AP: Applicative<G>, f: (A) -> Kind<G, B?>): Kind<G, Kind<F, B>> = AP.run {
    traverseFilter(AP) { a -> f(a).map { Option.fromNullable(it) } }
  }

  @Deprecated("Please use mapNotNull")
  override fun <A, B> Kind<F, A>.filterMap(f: (A) -> Option<B>): Kind<F, B> =
    traverseFilter(IdApplicative) { Id(f(it)) }.value()

  override fun <A, B> Kind<F, A>.mapNotNull(f: (A) -> B?): Kind<F, B> =
    traverseFilterNullable(IdApplicative) { Id(f(it)) }.value()

  /**
   * Returns [F]<[A]> in [G] context by applying [GA] on a selector function [f] in [G] context.
   */
  fun <G, A> Kind<F, A>.filterA(f: (A) -> Kind<G, Boolean>, GA: Applicative<G>): Kind<G, Kind<F, A>> = GA.run {
    traverseFilterNullable(this) { a -> f(a).map { b -> if (b) a else null } }
  }

  override fun <A> Kind<F, A>.filter(f: (A) -> Boolean): Kind<F, A> =
    filterA({ Id(f(it)) }, IdApplicative).value()

  /**
   * Filter out instances of [B] type and traverse the [G] context.
   */
  fun <G, A, B> Kind<F, A>.traverseFilterIsInstance(AP: Applicative<G>, klass: Class<B>): Kind<G, Kind<F, B>> = AP.run {
    filterA({ a -> just(klass.isInstance(a)) }, AP)
      .map { fa -> fa.map { a -> klass.cast(a) } }
  }
}
