package arrow.continuations.reflect2

import arrow.Kind
import arrow.core.*
import arrow.core.extensions.either.applicative.applicative
import arrow.core.extensions.either.applicative.map
import arrow.core.extensions.either.apply.apEval
import arrow.core.extensions.list.traverse.traverse
import arrow.core.extensions.listk.monad.flatMap
import arrow.typeclasses.Applicative

sealed class Reflect {
  abstract suspend operator fun List<Int>.invoke(): Int
}

class ReflectM(val prompt: Prompt<List<Int>, Any?>) : Reflect() {
  override suspend fun List<Int>.invoke(): Int {
//        println("invokeSuspend: $prompt")
    val result = prompt.suspend(this)
    println("Result: $result")
    return result as Int
  }
  // since we know the receiver of this suspend is the
  // call to flatMap, the casts are safe
}

@PublishedApi
internal fun reify(prog: suspend (Reflect) -> List<Int>): List<Int> {

//    var currentPrompt: Prompt? = null

  // The coroutine keeps sending monadic values until it completes
  // with a monadic value
  val coroutine = Coroutine<List<Int>, Any?, List<Int>> { prompt ->
//        currentPrompt = prompt
    // capability to reflect M
    val reflect = ReflectM(prompt)
    prog(reflect)
  }

  fun step(x: Int): Either<List<Int>, List<Int>> {
    println("Step : $x")
    coroutine.resume(x)
    return if (coroutine.isDone()) Right(coroutine.result())
    else Left(coroutine.value())
  }

  fun run(): List<Int> =
    if (coroutine.isDone()) coroutine.result()
    else tailRecM(coroutine.value()) { values ->
      val r = values.map(::step)
      println("run#tailRecM: $r")
      r
    }.flatten()

  return run()
}

@Suppress("UNCHECKED_CAST")
private tailrec fun <A, B> go(
  buf: ArrayList<B>,
  f: (A) -> List<Either<A, B>>,
  v: List<Either<A, B>>
) {
  if (v.isNotEmpty()) {
    val head: Either<A, B> = v.first()
    println("head: $head")
    when (head) {
      is Either.Right -> {
        println("Right?")
        buf += head.b
        go(buf, f, v.drop(1).k())
      }
      is Either.Left -> {
        val head: A = head.a
        val newHead = f(head)
        println("Left: $head, newHead: $newHead")
        val newRes = (newHead + v.drop(1))
        println("Head: $head, newHead: $newHead, newRes: $newRes")
        go(buf, f, newRes)
      }
    }
  }
}

fun <A, B> tailRecM(a: A, f: (A) -> List<Either<A, B>>): List<B> {
  val buf = ArrayList<B>()
  go(buf, f, f(a))
  return ListK(buf)
}
