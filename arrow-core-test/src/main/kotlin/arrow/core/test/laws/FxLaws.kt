package arrow.core.test.laws

import arrow.Kind
import arrow.core.EagerBind
import arrow.core.test.concurrency.SideEffect
import arrow.core.test.generators.throwable
import arrow.typeclasses.suspended.BindSyntax
import io.kotlintest.fail
import io.kotlintest.properties.Gen
import io.kotlintest.properties.forAll
import io.kotlintest.shouldBe
import io.kotlintest.shouldThrow
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

typealias EagerFxBlock<F, A> = (suspend EagerBind<F>.() -> A) -> Kind<F, A>
typealias SuspendFxBlock<F, A> = suspend (suspend BindSyntax<F>.() -> A) -> Kind<F, A>

object FxLaws {

  fun <F, A> laws(G: Gen<Kind<F, A>>, fxBlock: EagerFxBlock<F, A>, sfxBlock: SuspendFxBlock<F, A>): List<Law> = listOf(
    Law("non-suspended fx is lazy") { nonSuspendedIsLazy(G, fxBlock) },
    Law("suspended fx is lazy") { suspendedIsLazy(G, sfxBlock) },
    Law("non-suspended fx can bind immediate values") { nonSuspendedCanBindImmediate(G, fxBlock) },
    Law("non-suspended fx can bind immediate exceptions") { nonSuspendedCanBindImmediateException(G, fxBlock) },
    Law("suspended fx can bind immediate values") { suspendedCanBindImmediateValues(G, sfxBlock) },
    Law("suspended fx can bind suspended values") { suspendedCanBindSuspendedValues(G, sfxBlock) },
    Law("suspended fx can bind immediate exceptions") { suspendedCanBindImmediateExceptions(G, sfxBlock) },
    Law("suspended fx can bind suspended exceptions") { suspendedCanBindSuspendedExceptions(G, sfxBlock) }
  )

  private fun <F, A> nonSuspendedIsLazy(G: Gen<Kind<F, A>>, fxBlock: EagerFxBlock<F, A>) {
    forAll(G) { f: Kind<F, A> ->
      val effect = SideEffect()
      fxBlock {
        effect.increment()
        f.bind()
      }

      effect.counter == 0
    }
  }

  private suspend fun <F, A> suspendedIsLazy(G: Gen<Kind<F, A>>, fxBlock: SuspendFxBlock<F, A>) {
    G.random()
      .take(1001)
      .forEach { f ->
        val effect = SideEffect()
        fxBlock {
          effect.increment()
          f.bind()
        }

        effect.counter shouldBe 0
      }
  }

  private fun <F, A> nonSuspendedCanBindImmediate(G: Gen<Kind<F, A>>, fxBlock: EagerFxBlock<F, A>) {
    forAll(G) { f: Kind<F, A> ->
      fxBlock {
        val res = !f
        res
      } == f
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

  private suspend fun <F, A> suspendedCanBindImmediateValues(G: Gen<Kind<F, A>>, fxBlock: SuspendFxBlock<F, A>) {
    G.random()
      .take(1001)
      .forEach { f ->
        fxBlock {
          val res = !f
          res
        } shouldBe f
      }
  }

  private suspend fun <F, A> suspendedCanBindSuspendedValues(G: Gen<Kind<F, A>>, fxBlock: SuspendFxBlock<F, A>) {
    G.random()
      .take(10)
      .forEach { f ->
        fxBlock {
          val res = !(suspend {
            sleep(100)
            f
          }).invoke()

          res
        } shouldBe f
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
            sleep(100)
            throw exception
            res
          }
          fail("It should never reach here. fx should've thrown $exception")
        } shouldBe exception
      }
  }
}

private val scheduler: ScheduledExecutorService by lazy {
  Executors.newScheduledThreadPool(2) { r ->
    Thread(r).apply {
      name = "arrow-effect-scheduler-$id"
      isDaemon = true
    }
  }
}

private suspend fun sleep(duration: Long): Unit =
  if (duration <= 0) Unit
  else suspendCoroutine { cont ->
    scheduler.schedule(
      { cont.resume(Unit) },
      duration,
      TimeUnit.MILLISECONDS
    )
  }
