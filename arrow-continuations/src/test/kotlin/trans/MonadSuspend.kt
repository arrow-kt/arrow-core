package trans

import arrow.Kind
import arrow.continuations.generic.DelimContScope
import arrow.continuations.generic.DelimitedScope
import arrow.core.Eval
import arrow.core.ForEval
import arrow.core.ForOption
import arrow.core.None
import arrow.core.Option
import arrow.core.ShortCircuit
import arrow.core.Some
import arrow.core.fix
import arrow.core.identity
import arrow.core.value


abstract class MonadSuspend<F, S>(scope: DelimitedScope<S>): DelimitedScope<S> by scope {

  abstract suspend operator fun <B> Kind<F, B>.invoke(): B

  abstract suspend fun <B> B.just(): Kind<F, B>

  operator fun <G> div(other: MonadSuspend<G, S>): Trans2<F, G, S> =
    Trans2(this@MonadSuspend, other)

  suspend operator fun <Z : Kind<F, A>, A> div(f: suspend Trans<F, S>.() -> A): Z =
    Trans(this) / f
}

open class Trans<F, S>(open val MF: MonadSuspend<F, S>): DelimitedScope<S> by MF {

  suspend operator fun <B> Kind<F, B>.invoke(): B =
    MF.run { this@invoke() }

  suspend fun <B> B.just(): Kind<F, B> =
    MF.run { this@just.just() }

  open suspend fun <B> exit(fb: Kind<F, B>): S =
    shift { it(fb as S) }

  suspend operator fun <Z : Kind<F, A>, A> div(f: suspend Trans<F, S>.() -> A): Z =
    try {
      val result = f(this)
      result
    } catch (e: ShortCircuit) {
      e.value
    }.just() as Z

}

open class Trans2<F, G, S>(
  override val MF: MonadSuspend<F, S>,
  val MG: MonadSuspend<G, S>
) : Trans<F, S>(MF) {

  @JvmName("exit2")
  suspend fun <B> exit(fb: Kind<G, B>): S =
    shift { it(fb.just(unit = Unit) as S) }

  @JvmName("invokeG")
  suspend operator fun <B> Kind<G, B>.invoke(): B =
    MG.run { this@invoke() }

  @JvmName("invokeFG")
  suspend operator fun <B> Kind<F, Kind<G, B>>.invoke(): B =
    MF.run { MG.run { this@invoke() }() }

  @JvmName("justFG")
  suspend fun <B> B.just(unit: Unit = Unit): Kind<F, Kind<G, B>> =
    MF.run { MG.run { this@just.just() }.just() }

  operator fun <H> div(other: MonadSuspend<H, S>): Trans3<F, G, H, S> =
    Trans3(this, other)

  @JvmName("run2")
  suspend operator fun <Z : Kind<F, Kind<G, A>>, A> div(f: suspend Trans2<F, G, S>.() -> A): Z =
    try {
      f(this)
    } catch (e: ShortCircuit) {
      e.value
    }.just(unit = Unit) as Z
}

class Trans3<F, G, H, S>(
  val trans2: Trans2<F, G, S>,
  val MH: MonadSuspend<H, S>
) : Trans2<F, G, S>(trans2.MF, trans2.MG) {

  @JvmName("exit3")
  suspend fun <B> exit(fb: Kind<H, B>): S =
    shift { it(fb.just(unit = Unit, unit2 = Unit) as S) }

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

  @JvmName("run3")
  suspend operator fun <Z : Kind<F, Kind<G, Kind<H, A>>>, A> div(f: suspend Trans3<F, G, H, S>.() -> A): Z =
    try {
      f(this)
    } catch (e: ShortCircuit) {
      e.value
    }.just(unit = Unit, unit2 = Unit) as Z
}


fun <A> DelimitedScope<A>.EvalT(): MonadSuspend<ForEval, A> =
  object : MonadSuspend<ForEval, A>(this) {
    override suspend fun <B> Kind<ForEval, B>.invoke(): B = value()
    override suspend fun <B> B.just(): Kind<ForEval, B> = Eval.now(this)
  }

fun <A> DelimitedScope<A>.OptionT(exit: (None) -> A): MonadSuspend<ForOption, A> =
  object : MonadSuspend<ForOption, A>(this) {
    override suspend fun <B> Kind<ForOption, B>.invoke(): B =
      fix().fold({ shift { exit(None) } }, ::identity)

    override suspend fun <B> B.just(): Kind<ForOption, B> = Some(this)
  }

suspend fun <A> evalOption(f: suspend Trans2<ForEval, ForOption, Eval<Option<A>>>.() -> A): Eval<Option<A>> =
  DelimContScope.reset {
    EvalT() / OptionT { Eval.now(it) } / f
  }








