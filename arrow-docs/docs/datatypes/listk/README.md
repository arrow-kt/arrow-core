---
layout: docs-core
title: ListK
permalink: /arrow/core/listk/
---

## ListK




ListK wraps over the platform `List` type to make it a [type constructor]({{'/patterns/glossary/#type-constructors' | relative_url }}).

It can be created from Kotlin List type with a convenient `k()` function.

```kotlin:ank
import arrow.*
import arrow.core.*

listOf(1, 2, 3).k()
```

For most use cases, you will never use `ListK` directly, but rather `List` directly with the extension functions that Arrow projects over it.

ListK implements operators from many useful typeclasses.

The @extension type class processor expands all type class combinators that `ListK` provides automatically over `List`

For instance, it has `combineK` from the [`SemigroupK`]({{'/arrow/typeclasses/semigroupk/' | relative_url }}) typeclass.

It can be used to cheaply combine two lists:

```kotlin:ank
import arrow.core.extensions.list.semigroupK.*

val hello = listOf('h', 'e', 'l', 'l', 'o')
val commaSpace = listOf(',', ' ')
val world = listOf('w', 'o', 'r', 'l', 'd')

hello.combineK(commaSpace).combineK(world)
```

The functions `traverse` and `sequence` come from [`Traverse`]({{'/apidocs/arrow-core-data/arrow.typeclasses/-traverse/' | relative_url }}).

Traversing a list creates a new container [`Kind<F, A>`]({{'/patterns/glossary/#type-constructors' | relative_url }}) by combining the result of a function applied to each element:

```kotlin:ank
import arrow.core.*
import arrow.core.extensions.option.applicative.*
import arrow.core.extensions.list.traverse.*

val numbers = listOf(Math.random(), Math.random(), Math.random())
numbers.traverse(Option.applicative(), { if (it > 0.5) Some(it) else None })
```

and complements the convenient function `sequence()` that converts a list of `ListK<Kind<F, A>>` into a `Kind<F, ListK<A>>`:

```kotlin:ank
fun andAnother() = Some(Math.random())

val requests = listOf(Some(Math.random()), andAnother(), andAnother())
requests.sequence(Option.applicative())
```

If you want to aggregate the elements of a list into any other value, you can use `foldLeft` and `foldRight` from [`Foldable`]({{'/arrow/typeclasses/foldable' | relative_url }}).

Folding a list into a new value, `String` in this case, starting with an initial value and a combine function:

```kotlin:ank
listOf('a', 'b', 'c', 'd', 'e').k().foldLeft("-> ") { x, y -> x + y }
```

Or you can apply a list of transformations using `ap` from [`Applicative`]({{'/arrow/typeclasses/applicative/' | relative_url }}).

```kotlin:ank
import arrow.core.extensions.*
import arrow.core.extensions.list.apply.*

listOf(1, 2, 3).ap(listOf({ x: Int -> x + 10}, { x: Int -> x * 2}))
```
