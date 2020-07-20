package arrow.core

import io.kotlintest.matchers.types.shouldBeNull
import io.kotlintest.matchers.types.shouldNotBeNull
import io.kotlintest.specs.StringSpec

fun <A> List<A>.forkPath(choice1: A, choice2: A): Pair<List<A>, List<A>> =
  Pair(this + choice1, this + choice2)

fun <A> List<List<A>>.forkPaths(choice1: A, choice2: A): List<List<A>> =
  this.fold(emptyList()) { acc: List<List<A>>, path: List<A> ->
    val paths: Pair<List<A>, List<A>> = path.forkPath(choice1, choice2)
    acc.plusElement(paths.first).plusElement(paths.second)
  }

fun <A> generateAllPathsForNForks(choice1: A, choice2: A, n: Int): List<List<A>> =
  IntRange(1, n).fold(emptyList()) { acc: List<List<A>>, _ ->
    acc.forkPaths(choice1, choice2)
  }

class NullableTest : StringSpec({
  "map1 short circuits if any arg is null" {
    map1(null) { _ -> Unit }.shouldBeNull()
  }

  "map1 performs action when arg is not null" {
    map1(1) { _ -> Unit }.shouldNotBeNull()
  }

  "map2 only performs action when all arguments are not null" {
    generateAllPathsForNForks("a", null, 2)
      .forEach { (a: String?, b: String?) ->
        if (listOf(a, b).all { it != null }) {
          map2(a, b, { _, _ -> Unit }).shouldNotBeNull()
        } else {
          map2(a, b, { _, _ -> Unit }).shouldBeNull()
        }
      }
  }
})
