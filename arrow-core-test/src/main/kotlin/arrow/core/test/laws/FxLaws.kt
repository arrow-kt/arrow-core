package arrow.core.test.laws

import arrow.Kind
import arrow.core.EagerBind
import arrow.core.EagerInvoke
import arrow.core.test.generators.throwable
import arrow.typeclasses.Eq
import arrow.typeclasses.suspended.BindSyntax
import arrow.typeclasses.suspended.Invoke
import io.kotlintest.fail
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.intercepted
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.startCoroutine

private typealias EagerFxBlock<F, A> = (suspend EagerBind<F>.() -> A) -> Kind<F, A>
private typealias SuspendFxBlock<F, A> = suspend (suspend BindSyntax<F>.() -> A) -> Kind<F, A>

private typealias EagerFxBlockInvoke<F, A> = (suspend EagerInvoke<F>.() -> A) -> Kind<F, A>
private typealias SuspendFxBlockInvoke<F, A> = suspend (suspend Invoke<F>.() -> A) -> Kind<F, A>

object FxLaws {

  fun <F, A> laws(
    pureGen: Gen<Kind<F, A>>, // TODO cannot specify or filter a pure generator, so we need to require an additional one
    G: Gen<Kind<F, A>>,
    EQ: Eq<Kind<F, A>>,
    fxEager: EagerFxBlock<F, A>,
    fxSuspend: SuspendFxBlock<F, A>
  ): List<Law> = listOf(
    Law("non-suspended fx can bind immediate values") { nonSuspendedCanBindImmediateValues(G, EQ, fxEager) },
    Law("non-suspended fx can bind immediate exceptions") { nonSuspendedCanBindImmediateException(pureGen, fxEager) },
    Law("suspended fx can bind immediate values") { suspendedCanBindImmediateValues(G, EQ, fxSuspend) },
    Law("suspended fx can bind suspended values") { suspendedCanBindSuspendedValues(G, EQ, fxSuspend) },
    Law("suspended fx can bind immediate exceptions") { suspendedCanBindImmediateExceptions(pureGen, fxSuspend) },
    Law("suspended fx can bind suspended exceptions") { suspendedCanBindSuspendedExceptions(pureGen, fxSuspend) }
  )

  fun <F, A> lawsContinuation(
    pureGen: Gen<Kind<F, A>>, // TODO cannot specify or filter a pure generator, so we need to require an additional one
    G: Gen<Kind<F, A>>,
    EQ: Eq<Kind<F, A>>,
    fxEager: EagerFxBlockInvoke<F, A>,
    fxSuspend: SuspendFxBlockInvoke<F, A>
  ): List<Law> = listOf(
    Law("non-suspended fx can bind immediate values") { nonSuspendedCanBindImmediateValuesInvoke(G, EQ, fxEager) },
    Law("non-suspended fx can bind immediate exceptions") { nonSuspendedCanBindImmediateExceptionInvoke(pureGen, fxEager) },
    Law("suspended fx can bind immediate values") { suspendedCanBindImmediateValuesInvoke(G, EQ, fxSuspend) },
    Law("suspended fx can bind suspended values") { suspendedCanBindSuspendedValuesInvoke(G, EQ, fxSuspend) },
    Law("suspended fx can bind immediate exceptions") { suspendedCanBindImmediateExceptionsInvoke(pureGen, fxSuspend) },
    Law("suspended fx can bind suspended exceptions") { suspendedCanBindSuspendedExceptionsInvoke(pureGen, fxSuspend) }
  )

  private suspend fun <F, A> nonSuspendedCanBindImmediateValues(G: Gen<Kind<F, A>>, EQ: Eq<Kind<F, A>>, fxBlock: EagerFxBlock<F, A>) {
    forAll(G) { f: Kind<F, A> ->
      fxBlock {
        val res = !f
        res
      }.equalUnderTheLaw(f, EQ)
    }
  }

  private fun <F, A> nonSuspendedCanBindImmediateException(G: Gen<Kind<F, A>>, fxBlock: EagerFxBlock<F, A>) {
    forAll(G, Gen.throwable()) { f, exception ->
      shouldThrow<Throwable> {
        fxBlock {
          val res = !f
          throw exception
          res
        }

        fail("It should never reach here. fx should've thrown $exception")
      } == exception
    }
  }

  private suspend fun <F, A> suspendedCanBindImmediateValues(G: Gen<Kind<F, A>>, EQ: Eq<Kind<F, A>>, fxBlock: SuspendFxBlock<F, A>) {
    G.random()
      .take(1001)
      .forEach { f ->
        fxBlock {
          val res = !f
          res
        }.equalUnderTheLaw(f, EQ)
      }
  }

  private suspend fun <F, A> suspendedCanBindSuspendedValues(G: Gen<Kind<F, A>>, EQ: Eq<Kind<F, A>>, fxBlock: SuspendFxBlock<F, A>) {
    G.random()
      .take(10)
      .forEach { f ->
        fxBlock {
          val res = f.suspend().bind()
          res
        }.equalUnderTheLaw(f, EQ)
      }
  }

  private suspend fun <F, A> suspendedCanBindImmediateExceptions(G: Gen<Kind<F, A>>, fxBlock: SuspendFxBlock<F, A>) {
    Gen.bind(G, Gen.throwable(), ::Pair)
      .random()
      .take(1001)
      .forEach { (f, exception) ->
        shouldThrow<Throwable> {
          fxBlock {
            val res = !f
            throw exception
            res
          }
          fail("It should never reach here. fx should've thrown $exception")
        } shouldBe exception
      }
  }

  private suspend fun <F, A> suspendedCanBindSuspendedExceptions(G: Gen<Kind<F, A>>, fxBlock: SuspendFxBlock<F, A>) {
    Gen.bind(G, Gen.throwable(), ::Pair)
      .random()
      .take(10)
      .forEach { (f, exception) ->
        shouldThrow<Throwable> {
          fxBlock {
            val res = !f
            exception.suspend()
            res
          }
          fail("It should never reach here. fx should've thrown $exception")
        } shouldBe exception
      }
  }

  private suspend fun <F, A> nonSuspendedCanBindImmediateValuesInvoke(G: Gen<Kind<F, A>>, EQ: Eq<Kind<F, A>>, fxBlock: EagerFxBlockInvoke<F, A>) {
    forAll(G) { f: Kind<F, A> ->
      fxBlock {
        val res = f()
        res
      }.equalUnderTheLaw(f, EQ)
    }
  }

  private fun <F, A> nonSuspendedCanBindImmediateExceptionInvoke(G: Gen<Kind<F, A>>, fxBlock: EagerFxBlockInvoke<F, A>) {
    forAll(G, Gen.throwable()) { f, exception ->
      shouldThrow<Throwable> {
        fxBlock {
          val res = f()
          throw exception
          res
        }

        fail("It should never reach here. fx should've thrown $exception")
      } == exception
    }
  }

  private suspend fun <F, A> suspendedCanBindImmediateValuesInvoke(G: Gen<Kind<F, A>>, EQ: Eq<Kind<F, A>>, fxBlock: SuspendFxBlockInvoke<F, A>) {
    G.random()
      .take(1001)
      .forEach { f ->
        fxBlock {
          val res = f()
          res
        }.equalUnderTheLaw(f, EQ)
      }
  }

  private suspend fun <F, A> suspendedCanBindSuspendedValuesInvoke(G: Gen<Kind<F, A>>, EQ: Eq<Kind<F, A>>, fxBlock: SuspendFxBlockInvoke<F, A>) {
    G.random()
      .take(10)
      .forEach { f ->
        fxBlock {
          val res = f.suspend()()
          res
        }.equalUnderTheLaw(f, EQ)
      }
  }

  private suspend fun <F, A> suspendedCanBindImmediateExceptionsInvoke(G: Gen<Kind<F, A>>, fxBlock: SuspendFxBlockInvoke<F, A>) {
    Gen.bind(G, Gen.throwable(), ::Pair)
      .random()
      .take(1001)
      .forEach { (f, exception) ->
        shouldThrow<Throwable> {
          fxBlock {
            val res = f()
            throw exception
            res
          }
          fail("It should never reach here. fx should've thrown $exception")
        } shouldBe exception
      }
  }

  private suspend fun <F, A> suspendedCanBindSuspendedExceptionsInvoke(G: Gen<Kind<F, A>>, fxBlock: SuspendFxBlockInvoke<F, A>) {
    Gen.bind(G, Gen.throwable(), ::Pair)
      .random()
      .take(10)
      .forEach { (f, exception) ->
        shouldThrow<Throwable> {
          fxBlock {
            val res = f()
            exception.suspend()
            res
          }
          fail("It should never reach here. fx should've thrown $exception")
        } shouldBe exception
      }
  }
}

// TODO expose for tests
internal suspend fun Throwable.suspend(): Nothing =
  suspendCoroutineUninterceptedOrReturn { cont ->
    suspend { throw this }.startCoroutine(Continuation(EmptyCoroutineContext) {
      cont.intercepted().resumeWith(it)
    })

    COROUTINE_SUSPENDED
  }

internal suspend fun <A> A.suspend(): A =
  suspendCoroutineUninterceptedOrReturn { cont ->
    suspend { this }.startCoroutine(Continuation(EmptyCoroutineContext) {
      cont.intercepted().resumeWith(it)
    })

    COROUTINE_SUSPENDED
  }
