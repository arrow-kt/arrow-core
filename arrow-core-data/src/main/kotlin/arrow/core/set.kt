package arrow.core

import arrow.typeclasses.Eq

object SetExtensions

object SortedSetInstances

fun <A> Set<A>.eqv(EQA: Eq<A>, b: Set<A>): Boolean =
  if (size == b.size) EQA.run {
    fold(true) { acc, aa ->
      val found = (b.find { bb -> aa.eqv(bb) } != null)
      acc && found
    }
  } else false
