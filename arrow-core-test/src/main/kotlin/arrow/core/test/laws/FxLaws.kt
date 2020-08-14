package arrow.core.test.laws

import arrow.Kind
import arrow.core.EagerBind
import arrow.core.test.generators.throwable
import arrow.typeclasses.Eq
import arrow.typeclasses.suspended.BindSyntax
import io.kotest.assertions.fail
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.property.Arb
import io.kotest.property.PropTestConfig
import io.kotest.property.ShrinkingMode
import io.kotest.property.forAll
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.intercepted
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.startCoroutine

private typealias EagerFxBlock<F, A> = (suspend EagerBind<F>.() -> A) -> Kind<F, A>
private typealias SuspendFxBlock<F, A> = suspend (suspend BindSyntax<F>.() -> A) -> Kind<F, A>

object FxLaws {

  fun <F, A> laws(
    pureGen: Arb<Kind<F, A>>, // TODO cannot specify or filter a pure generator, so we need to require an additional one
    G: Arb<Kind<F, A>>,
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

  private suspend fun <F, A> nonSuspendedCanBindImmediateValues(G: Arb<Kind<F, A>>, EQ: Eq<Kind<F, A>>, fxBlock: EagerFxBlock<F, A>) {
    forAll(PropTestConfig(shrinkingMode = ShrinkingMode.Off), G) { f: Kind<F, A> ->
      fxBlock {
        val res = !f
        res
      }.equalUnderTheLaw(f, EQ)
    }
  }

  private suspend fun <F, A> nonSuspendedCanBindImmediateException(G: Arb<Kind<F, A>>, fxBlock: EagerFxBlock<F, A>) {
    forAll(PropTestConfig(shrinkingMode = ShrinkingMode.Off), G, Arb.throwable()) { f, exception ->
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

  private suspend fun <F, A> suspendedCanBindImmediateValues(G: Arb<Kind<F, A>>, EQ: Eq<Kind<F, A>>, fxBlock: SuspendFxBlock<F, A>) {
    forAll(PropTestConfig(shrinkingMode = ShrinkingMode.Off), G) { f ->
      runBlocking {
        fxBlock {
          val res = !f
          res
        }.equalUnderTheLaw(f, EQ)
      }
    }
  }

  private suspend fun <F, A> suspendedCanBindSuspendedValues(G: Arb<Kind<F, A>>, EQ: Eq<Kind<F, A>>, fxBlock: SuspendFxBlock<F, A>) {
    forAll(PropTestConfig(shrinkingMode = ShrinkingMode.Off), G) { f ->
      runBlocking {
        fxBlock {
          val res = !f.suspend()
          res
        }.equalUnderTheLaw(f, EQ)
      }
    }
  }

  private suspend fun <F, A> suspendedCanBindImmediateExceptions(G: Arb<Kind<F, A>>, fxBlock: SuspendFxBlock<F, A>) {
    forAll(PropTestConfig(shrinkingMode = ShrinkingMode.Off), G, Arb.throwable()) { f, exception ->
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

  private suspend fun <F, A> suspendedCanBindSuspendedExceptions(G: Arb<Kind<F, A>>, fxBlock: SuspendFxBlock<F, A>) {
    forAll(PropTestConfig(shrinkingMode = ShrinkingMode.Off), G, Arb.throwable()) { f, exception ->
        shouldThrow<Throwable> {
          fxBlock {
            val res = !f
            exception.suspend()
            res
          }
          fail("It should never reach here. fx should've thrown $exception")
        } == exception
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
