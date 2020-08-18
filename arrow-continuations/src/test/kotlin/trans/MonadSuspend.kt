package trans

import arrow.Kind
import arrow.continuations.generic.DelimContScope
import arrow.continuations.generic.DelimitedScope
import arrow.core.Eval
import arrow.core.ForEval
import arrow.core.ForOption
import arrow.core.Option
import arrow.core.Some
import arrow.core.fix
import arrow.core.identity
import arrow.core.value

abstract class MonadSuspend<F> {
  abstract suspend operator fun <B> Kind<F, B>.invoke(): B
  abstract suspend fun <B> B.just(): Kind<F, B>
  operator fun <G> div(other: MonadSuspend<G>): Trans2<F, G> =
    Trans2(this, other)
  suspend operator fun <Z : Kind<F, A>, A> div(f: suspend Trans<F>.() -> A): Z =
    f(Trans(this)).just() as Z
}

open class Trans<F>(open val MF: MonadSuspend<F>) {

  suspend operator fun <B> Kind<F, B>.invoke(): B =
    MF.run { this@invoke() }

  suspend fun <B> B.just(): Kind<F, B> =
    MF.run { this@just.just() }

  suspend operator fun <Z : Kind<F, A>, A> div(f: suspend Trans<F>.() -> A): Z =
    f(this).just() as Z

}

open class Trans2<F, G>(
  override val MF: MonadSuspend<F>,
  val MG: MonadSuspend<G>
) : Trans<F>(MF) {

  @JvmName("invokeG")
  suspend operator fun <B> Kind<G, B>.invoke(): B =
    MG.run { this@invoke() }

  @JvmName("invokeFG")
  suspend operator fun <B> Kind<F, Kind<G, B>>.invoke(): B =
    MF.run { MG.run { this@invoke() }() }

  @JvmName("justFG")
  suspend fun <B> B.just(unit: Unit = Unit): Kind<F, Kind<G, B>> =
    MF.run { MG.run { this@just.just() }.just() }

  operator fun <H> div(other: MonadSuspend<H>): Trans3<F, G, H> =
    Trans3(this, other)

  @JvmName("run2")
  suspend operator fun <Z : Kind<F, Kind<G, A>>, A> div(f: suspend Trans2<F, G>.() -> A): Z =
    f(this).just() as Z
}


class Trans3<F, G, H>(
  val trans2: Trans2<F, G>,
  val MH: MonadSuspend<H>
) : Trans2<F, G>(trans2.MF, trans2.MG) {

  @JvmName("invokeH")
  suspend operator fun <B> Kind<H, B>.invoke(): B =
    MH.run { this@invoke() }

  @JvmName("invokeFGH")
  suspend operator fun <B> Kind<F, Kind<G, Kind<H, B>>>.invoke(): B =
    trans2.run { MH.run { this@invoke() }() }

  @JvmName("justFGH")
  suspend fun <B> B.just(unit: Unit = Unit, unit2: Unit = Unit): Kind<F, Kind<G, Kind<H, B>>> =
    MF.run {
      MG.run { MH.run { this@just.just() }.just() }.just()
    }
}

fun <A> DelimitedScope<A>.EvalT(): MonadSuspend<ForEval> =
  object : MonadSuspend<ForEval>() {
    override suspend fun <B> Kind<ForEval, B>.invoke(): B = value()
    override suspend fun <B> B.just(): Kind<ForEval, B> = Eval.now(this)
  }

fun <A> DelimitedScope<A>.OptionT(): MonadSuspend<ForOption> =
  object : MonadSuspend<ForOption>() {
    override suspend fun <B> Kind<ForOption, B>.invoke(): B =
      fix().fold({ shift { TODO() } }, ::identity)

    override suspend fun <B> B.just(): Kind<ForOption, B> = Some(this)
  }

suspend fun <A> eval(f: suspend Trans<ForEval>.() -> A): Eval<A> =
  DelimContScope.reset {
    EvalT() / f
  }

suspend fun <A> evalOption(f: suspend Trans2<ForEval, ForOption>.() -> A): Eval<Option<A>> =
  DelimContScope.reset {
    EvalT() / OptionT() / f
  }








