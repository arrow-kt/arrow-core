package arrow.continuations.eveff

// By "Effect Handlers in Haskell, Evidently" by Xie and Leijen
/**
 * Implemented for clarity on how kotlin suspend and the other delimited cont impl work.
 *
 * mark is something we don't have in our implementations for simple reasons:
 *  we can refer to any scope simply by referring to its this-object and thus can jump
 *  straight to it when executing.
 * op is the prompts handle function, what we call shift, it expects a continuation and provides an answer
 *  which is the final result of our prompt. This is equal to shift in function because shift implicitly
 *  multiprompts.
 * cont is a partially applied continuation, which is to say it is our function which we execute in our context.
 *  We don't need this type explicitly because suspend functions are already basically this.
 *
 * All in all this is very close to my current encoding of delimited continuations just with explicit cps and
 *  thus with multishot support. Sadly this cannot easily be added to suspend.
 */

sealed class Ctl<A> {
  fun <B> flatMap(f: (A) -> Ctl<B>): Ctl<B> = when (this) {
    is Pure -> f(res)
    is Yield<*, *, *> ->
      Yield(
        mark as Marker<Any?>,
        op as (((Any?) -> Ctl<Any?>) -> Ctl<Any?>),
        f.kcompose(cont as ((Any?) -> Ctl<A>))
      )
  }
  fun <B> map(f: (A) -> B): Ctl<B> = flatMap { just(f(it)) }

  companion object {
    fun <A> just(a: A): Ctl<A> = Pure(a)
  }
}
data class Pure<A>(val res: A): Ctl<A>()
data class Yield<A, Ans, B>(
  val mark: Marker<Ans>,
  val op: ((B) -> Ctl<Ans>) -> Ctl<Ans>,
  val cont: (B) -> Ctl<A>
): Ctl<A>()

tailrec fun <A> Ctl<A>.runCtl(): A = when (this) {
  is Pure -> res
  is Yield<*, *, *> -> (op(cont as (Any?) -> Nothing) as Ctl<A>).runCtl()
}

class Marker<Ans>

fun <Ans, B> yield(mk: Marker<Ans>, op: ((B) -> Ctl<Ans>) -> Ctl<Ans>): Ctl<B> =
  Yield(mk, op, { Pure(it) })

inline fun <A, B, C> ((B) -> Ctl<C>).kcompose(crossinline f: (A) -> Ctl<B>): (A) -> Ctl<C> = { a ->
  f(a).flatMap(this)
}

inline fun <Ans> prompt(crossinline f: (Marker<Ans>) -> Ctl<Ans>): Ctl<Ans> =
  freshMarker { m: Marker<Ans> -> mPrompt(m, f(m)) }

fun <Ans, A> freshMarker(f: (Marker<Ans>) -> A): A = f(Marker())

fun <Ans> mPrompt(m: Marker<Ans>, c: Ctl<Ans>): Ctl<Ans> = when (c) {
  is Pure -> c
  is Yield<*, *, *> -> {
    val nCont = { x: Any? ->
      mPrompt(
        m,
        (c.cont as (Any?) -> Ctl<Ans>).invoke(x)
      )
    }
    if (m === c.mark) (c.op as ((Any?) -> Ctl<Ans>) -> Ctl<Ans>)(nCont)
    else Yield(
      c.mark as Marker<Any?>,
      c.op as ((Any?) -> Ctl<Any?>) -> Ctl<Any?>,
      nCont
    )
  }
}

// Eff
data class Eff<E, A>(val runEff: (Context<E>) -> Ctl<A>) {
  fun <B> flatMap(f: (A) -> Eff<E, B>): Eff<E, B> =
    Eff { ctx -> runEff(ctx).flatMap { a -> f(a).runEff(ctx) } }
  fun <B> map(f: (A) -> B): Eff<E, B> = flatMap { just<E, B>(f(it)) }

  companion object {
    fun <E, A> just(a: A): Eff<E, A> = Eff { Ctl.just(a) }
  }
}

class Context<E>
