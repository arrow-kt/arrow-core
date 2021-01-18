---
layout: docs-core
title: Divisible
permalink: /arrow/typeclasses/divisible/
---

## Divisible




`Divisible` extends upon `Divide` by providing an empty method called `conquer`.
`conquer` is useful for proving identity laws when working with `Divisible` instances.

Extending the serializer example from `Divide`, `conquer` would simply serialize data to an empty string.

```kotlin:ank:playground
import arrow.Kind
import arrow.core.Tuple2
import arrow.core.toT
import com.example.domain.*

fun main(args: Array<String>) {
  //sampleStart
   val emptySerializer: SerializerOf<Int> = Serializer.divisible().conquer()

  val result = emptySerializer.fix().func(1)
  //sampleEnd
  println(result)
}
```

### Main Combinators

#### conquer

Constructs an empty value for any `Kind<F, A>`.

### Laws

Arrow provides `DivisibleLaws` in the form of test cases for internal verification of lawful instances and third party apps creating their own `Divisible` instances.

See [Deriving and creating custom typeclass]({{ '/patterns/glossary' | relative_url }}) to provide your own `Divisible` instances for custom datatypes.
