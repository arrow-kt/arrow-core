package effectStack

import arrow.core.Either
import io.kotlintest.properties.Gen
import io.kotlintest.properties.shrinking.Shrinker
import kotlinx.coroutines.Dispatchers
import java.util.concurrent.locks.AbstractQueuedSynchronizer
import kotlin.coroutines.Continuation
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.intrinsics.COROUTINE_SUSPENDED
import kotlin.coroutines.intrinsics.intercepted
import kotlin.coroutines.intrinsics.suspendCoroutineUninterceptedOrReturn
import kotlin.coroutines.startCoroutine
import kotlin.random.Random

internal fun <A> unsafeRunSync(f: suspend () -> A): A {
  val latch = OneShotLatch()
  var ref: Either<Throwable, A>? = null
  f.startCoroutine(Continuation(EmptyCoroutineContext) { a ->
    ref = a.fold({ aa -> Either.Right(aa) }, { t -> Either.Left(t) })
    latch.releaseShared(1)
  })

  latch.acquireSharedInterruptibly(1)

  return when (val either = ref) {
    is Either.Left -> throw either.a
    is Either.Right -> either.b
    null -> throw RuntimeException("Suspend execution should yield a valid result")
  }
}


private class OneShotLatch : AbstractQueuedSynchronizer() {
  override fun tryAcquireShared(ignored: Int): Int =
    if (state != 0) {
      1
    } else {
      -1
    }

  override fun tryReleaseShared(ignore: Int): Boolean {
    state = 1
    return true
  }
}

internal suspend fun Throwable.suspend(): Nothing =
  suspendCoroutineUninterceptedOrReturn { cont ->
    Gen.int().orNull()
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

typealias Suspended<A> = suspend () -> A

@JvmName("suspendedErrors")
fun Gen<Throwable>.suspended(): Gen<Suspended<Nothing>> =
  suspended { e -> suspend { throw e } } as Gen<Suspended<Nothing>>

fun <A> Gen<A>.suspended(): Gen<Suspended<A>> =
  suspended { a -> suspend { a } }

private fun <A> Gen<A>.suspended(liftK: (A) -> (suspend () -> A)): Gen<Suspended<A>> {
  val outer = this
  return object : Gen<Suspended<A>> {
    override fun constants(): Iterable<Suspended<A>> =
      outer.constants().flatMap { value ->
        exhaustiveOptions.map { (a, b, c) -> liftK(value).asSuspended(a, b, c) }
      }

    override fun random(): Sequence<Suspended<A>> =
      outer.random().map {
        liftK(it).asSuspended(Random.nextBoolean(), Random.nextBoolean(), Random.nextBoolean())
      }

    override fun shrinker(): Shrinker<Suspended<A>>? {
      val s = outer.shrinker()
      return if (s == null) null else object : Shrinker<Suspended<A>> {
        override fun shrink(failure: Suspended<A>): List<Suspended<A>> {
          val failed = unsafeRunSync { failure.invoke() }
          return s.shrink(failed).map {
            liftK(it).asSuspended(Random.nextBoolean(), Random.nextBoolean(), Random.nextBoolean())
          }
        }
      }
    }
  }
}

private val exhaustiveOptions =
  listOf(
    Triple(false, false, false),
    Triple(false, false, true),
    Triple(false, true, false),
    Triple(false, true, true),
    Triple(true, false, false),
    Triple(true, false, true),
    Triple(true, true, false),
    Triple(true, true, true)
  )

internal fun <A> (suspend () -> A).asSuspended(
  suspends: Boolean,
  emptyOrNot: Boolean,
  intercepts: Boolean
): Suspended<A> = suspend {
  if (!suspends) this.invoke()
  else suspendCoroutineUninterceptedOrReturn { cont ->
    val ctx = if (emptyOrNot) EmptyCoroutineContext else Dispatchers.Default
    suspend { this.invoke() }.startCoroutine(Continuation(ctx) {
      if (intercepts) cont.resumeWith(it) else cont.intercepted().resumeWith(it)
    })

    COROUTINE_SUSPENDED
  }
}
