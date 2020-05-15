package arrow.core.extensions

import arrow.Kind
import arrow.Kind2
import arrow.core.ForOption2
import arrow.core.Option2
import arrow.core.Option2PartialOf
import arrow.core.extensions.option2.eq.eq
import arrow.core.fix
import arrow.extension
import arrow.typeclasses.Bifunctor
import arrow.typeclasses.Eq
import arrow.typeclasses.EqK
import arrow.typeclasses.EqK2
import arrow.typeclasses.Functor
import arrow.undocumented
import me.eugeniomarletti.kotlin.metadata.shadow.utils.addToStdlib.safeAs

@extension
@undocumented
interface Option2Functor<L> : Functor<Option2PartialOf<L>> {
  override fun <A, B> Kind<Option2PartialOf<L>, A>.map(f: (A) -> B): Option2<L, B> = fix().map(f)
}

@extension
@undocumented
interface Option2Bifunctor : Bifunctor<ForOption2> {

  override fun <A, B, C, D> Kind2<ForOption2, A, B>.bimap(fl: (A) -> C, fr: (B) -> D): Kind2<ForOption2, C, D> = fix().bimap(fl, fr)
}

@extension
interface Option2Eq<L, R> : Eq<Option2<L, R>> {

  fun EQL(): Eq<L>

  fun EQR(): Eq<R>

  override fun Option2<L, R>.eqv(other: Option2<L, R>): Boolean = when (this) {
    is Option2.None -> other is Option2.None
    is Option2.Some -> other.safeAs<Option2.Some<L, R>>()?.let { (a2, b2) ->
      EQL().run { a.eqv(a2) } && EQR().run { b.eqv(b2) }
    } ?: false
  }
}

@extension
interface Option2EqK<A> : EqK<Option2PartialOf<A>> {
  fun EQA(): Eq<A>

  override fun <B> Kind<Option2PartialOf<A>, B>.eqK(other: Kind<Option2PartialOf<A>, B>, EQ: Eq<B>): Boolean =
    Option2.eq(EQA(), EQ).run {
      this@eqK.fix().eqv(other.fix())
    }
}

@extension
interface Option2EqK2 : EqK2<ForOption2> {
  override fun <A, B> Kind2<ForOption2, A, B>.eqK(other: Kind2<ForOption2, A, B>, EQA: Eq<A>, EQB: Eq<B>): Boolean =
    (this.fix() to other.fix()).let {
      Option2.eq(EQA, EQB).run {
        it.first.eqv(it.second)
      }
    }
}
