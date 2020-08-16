package trans

import arrow.Kind
import arrow.continuations.effectStack.Delimited
import arrow.continuations.generic.DelimContScope
import arrow.continuations.generic.DelimitedScope
import arrow.core.Eval
import arrow.core.ForEval
import arrow.core.ForOption
import arrow.core.None
import arrow.core.Option
import arrow.core.Some
import arrow.core.fix
import arrow.core.identity
import arrow.core.value

abstract class MonadSuspend<F, M>(
  val transScope: TransScope<M>,
) : DelimitedScope<M> by transScope.scope {

  suspend fun exit(): Nothing =
    shift { transScope.exit() }

  suspend operator fun <A> invoke(f: suspend MonadSuspend<F, M>.() -> A): Kind<F, A> =
    f(this).just()

  operator fun <G> div(next: MonadSuspend<G, M>): T<G> =
    T(next)

  abstract suspend operator fun <B> Kind<F, B>.invoke(): B
  abstract suspend fun <B> B.just(): Kind<F, B>
  inner class T<G>(val MG: MonadSuspend<G, M>) : MonadSuspend<G, M>(MG.transScope) {

    @JvmName("divT")
    suspend operator fun <A, Z> div(f: suspend MonadSuspend<F, M>.T<G>.() -> A): Z where Z : Kind<F, Kind<G, A>> =
      f(this).just(unit1 = Unit) as Z

    override suspend operator fun <B> Kind<G, B>.invoke(): B =
      MG.run { this@invoke() }

    suspend operator fun <B> Kind<F, B>.invoke(unit1: Unit = Unit, unit2: Unit = Unit, unit3: Unit = Unit): B =
      this@MonadSuspend.run { this@invoke() }

    override suspend fun <B> B.just(): Kind<G, B> =
      MG.run { this@just.just() }

    suspend operator fun <B> Kind<F, Kind<G, B>>.invoke(unit1: Unit = Unit, unit2: Unit = Unit): B =
      this@MonadSuspend.run { this@invoke() }()

    suspend fun <B> B.just(unit1: Unit = Unit): Kind<F, Kind<G, B>> =
      this@MonadSuspend.run {
        MG.run {
          this@just.just()
        }.just()
      }

    inner class T<H>(val MH: MonadSuspend<H, M>) : MonadSuspend<H, M>(MH.transScope) {

      @JvmName("divT2")
      suspend operator fun <A, Z> div(f: suspend MonadSuspend<F, M>.T<G>.T<H>.() -> A): Z where Z : Kind<F, Kind<G, Kind<H, A>>> =
        f(this).just(unit1 = Unit) as Z

      override suspend operator fun <B> Kind<H, B>.invoke(): B =
        MH.run { this@invoke() }

      suspend operator fun <B> Kind<F, B>.invoke(unit1: Unit = Unit, unit2: Unit = Unit, unit3: Unit = Unit): B =
        this@MonadSuspend.run { this@invoke() }

      override suspend fun <B> B.just(): Kind<H, B> =
        MH.run { this@just.just() }

      suspend operator fun <B> Kind<F, Kind<H, B>>.invoke(unit1: Unit = Unit, unit2: Unit = Unit): B =
        this@MonadSuspend.run { this@invoke() }()

      suspend fun <B> B.just(unit1: Unit = Unit): Kind<F, Kind<G, Kind<H, B>>> =
        this@MonadSuspend.run {
          MG.run {
            MH.run {
              this@just.just()
            }.just()
          }.just()
        }
    }
  }
}

class TransScope<A>(
  val scope: DelimitedScope<A>,
  val exit: () -> A
)

suspend fun <A> DelimitedScope<A>.trans(
  exit: () -> A,
  f: suspend TransScope<A>.() -> A): A =
  f(TransScope(this, exit))

fun <M> TransScope<M>.EvalT(): MonadSuspend<ForEval, M> =
  object : MonadSuspend<ForEval, M>(this) {
    override suspend fun <B> Kind<ForEval, B>.invoke(): B = value()
    override suspend fun <B> B.just(): Kind<ForEval, B> = Eval.now(this)
  }

fun <M> TransScope<M>.OptionT(): MonadSuspend<ForOption, M> =
  object : MonadSuspend<ForOption, M>(this) {
    override suspend fun <B> Kind<ForOption, B>.invoke(): B =
      fix().fold({ exit() }, ::identity)

    override suspend fun <B> B.just(): Kind<ForOption, B> = Some(this)
  }

suspend fun <A> evalOption(f: suspend MonadSuspend<ForEval, *>.T<ForOption>.() -> A): Eval<Option<A>> =
  DelimContScope.reset {
    trans({ Eval.now(None) }) {
      EvalT() / OptionT() / f
    }
  }








