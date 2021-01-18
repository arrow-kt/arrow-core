---
layout: docs-core
title: Selective
permalink: /arrow/typeclasses/selective/
---

## Selective




`Selective` is a typeclass to represent a composition of two independent effectful computations.

### Main Combinators

`Selective` includes all combinators present in [`Applicative`]({{ '/arrow/typeclasses/applicative/' | relative_url }}).

#### Kind<F, Either<A, B>>#select

Select applies an effectful computation wrapped in a `Kind<F, (A) -> B>` that will be applied to the datatype for one of its branches.

```kotlin:ank
import arrow.core.*
import arrow.core.extensions.*
import arrow.fx.*

Some<Either<Int, String>>(Left(1))
  .select(Some({ a: Int -> a.toString() }))
```

```kotlin:ank
Some<Either<Int, String>>(Right("2"))
  .select(Some({ a: Int -> a.toString() }))
```

#### Kind<F, Either<A, B>>#branch

Applies an effectful computation to either side of the branch.

```kotlin:ank
import arrow.core.extensions.option.selective.branch

Some<Either<Int, String>>(Left(1))
  .branch(Some({ a: Int ->
    listOf(a.toString())
  }), Some({ b: String ->
    listOf(b)
  }))
```

```kotlin:ank
Some<Either<Int, String>>(Right("0"))
  .branch(Some({ a: Int ->
    listOf(a.toString())
  }), Some({ b: String ->
    listOf(b)
  }))
```

### Laws

Arrow provides [`SelectiveLaws`][selective_law_source]{:target="_blank"} in the form of test cases for internal verification of lawful instances and third party apps creating their own Selective instances.

#### Creating your own `Selective` instances

Arrow already provides `Selective` instances for most common datatypes both in Arrow and the Kotlin stdlib.

See [Deriving and creating custom typeclass]({{ '/patterns/glossary' | relative_url }}) to provide your own `Selective` instances for custom datatypes.

[selective_law_source]: https://github.com/arrow-kt/arrow-core/blob/master/arrow-core-test/src/main/kotlin/arrow/core/test/laws/SelectiveLaws.kt
