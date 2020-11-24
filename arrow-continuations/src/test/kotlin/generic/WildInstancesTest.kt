package generic

import arrow.continuations.Effect
import arrow.continuations.Reset
import io.kotlintest.shouldBe
import io.kotlintest.specs.StringSpec
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList


@FlowPreview
fun interface FlowEffect<A> : Effect<Flow<A>> {
  suspend operator fun <B> Flow<B>.invoke(): B =
    control().shift { cb -> flatMapConcat { cb(it) } }
}

@FlowPreview
suspend fun <A> flowing(block: suspend FlowEffect<*>.() -> A): Flow<A> =
  Reset.multi { flowOf(block(FlowEffect { this })) }

fun interface IterableEffect<A> : Effect<List<A>> {
  suspend operator fun <B> Iterable<B>.invoke(): B =
    control().shift { cb -> flatMap { cb(it) } }
}

suspend fun <A> list(block: suspend IterableEffect<*>.() -> A): List<A> =
  Reset.multi { listOf(block(IterableEffect { this })) }

@FlowPreview
class WildInstancesTest : StringSpec({

//  "list" {
//    list { 1 } shouldBe listOf(1)
//  }
//
//  "list multi-shot" {
//    list {
//      val a = listOf(1, 2, 3)()
//      val b = setOf("a", "b", "c")()
//      "$a$b"
//    } shouldBe listOf("1a", "1b", "1c", "2a", "2b", "2c", "3a", "3b", "3c")
//  }
//
//  "flow" {
//    flowing { 1 }.toList() shouldBe flowOf(1).toList()
//  }

  "flow multi-shot" {
    flowing {
      val a = flowOf(1, 2, 3)()
      val b = flowOf("a", "b", "c")()
      "$a$b"
    }.toList() shouldBe listOf("1a", "1b", "1c", "2a", "2b", "2c", "3a", "3b", "3c")
  }

})
