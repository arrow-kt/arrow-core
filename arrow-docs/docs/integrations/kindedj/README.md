---
layout: docs-core
title: KindedJ
permalink: /integrations/kindedj/
---

## KindedJ

[KindedJ](https://github.com/KindedJ/KindedJ/) is a project to create an interop layer between multiple libraries that emulate [higher kinds]({{ '/patterns/glossary' | relative_url }}) in the JVM
using lightweight higher kind polymorphisms. You can read more about this emulation in [KindedJ's Readme](https://github.com/KindedJ/KindedJ/blob/master/README.md).

### Generation of KindedJ interfaces

Annotating a [datatype]({{ '/patterns/glossary' | relative_url }}) with `@higherkind` will create a typealias `<datatype>KindedJ` that'll be directly available to be used by any other libraries that use [KindedJ](https://github.com/KindedJ/KindedJ/).
Due to the difference in [generics between Kotlin and Java](https://kotlinlang.org/docs/reference/generics.html), this typealias will only be generated if all the generic parameters of the datatype are invariant.

Working with datatypes whose generic parameters are not invariant requires a conversion layer.

### Conversion module

The arrow-kindedj module provides a conversion layer between Arrow and KindedJ where the Kotlin generics `in` and `out` get on the way.

It uses an integration construct called `Convert`, alongside several extension functions.

#### Using KindedJ in Arrow from Kotlin

Using the conversion layer, we're able to use an intermediate representation of any KindedJ datatype generically by converting it into a `Kind2<ForConvert, F, A>`,
where F is the [original representation of the container]({{ '/patterns/glossary' | relative_url }}).

Let's see an example of a Java class defined by a third party:

```java
class IdJ<A> implements io.kindedj.Hk<ForIdJ, A> {
    public A a;
}
```

You can convert it to Arrow using the extension function `fromKindedJ()`:

```kotlin
val idj = IdJ(1)

val id: Kind2<ForConvert, ForIdJ, A> = idj.fromKindedJ()
```

and convert it back using `fromArrow()`:

```kotlin
val idj2: io.kindedj.Hk<ForIdJ, A> = id.fromArrow()
```

#### Using Arrow in KindedJ from Java

Using the conversion layer, we're able to use an intermediate representation of any Arrow datatype generically by converting it into a `io.kindedj.Hk2<ForConvert, F, A>`,
where F is the [original representation of the container]({{ '/patterns/glossary' | relative_url }}). Note that the typealias `io.kindedj.Hk2` is only available in Kotlin.

#### How to work with the conversion layer

It is important to understand that the integration with KindedJ works at a level of genericity where values inside one of the intermediate representations cannot be accessed directly.
It's required to use the constructs inside Arrow or the 3rd party lib implementing KindedJ to be able to use functions like `map` or `flatMap`.

Further integration in KindedJ of constructs such as [typeclasses]({{ '/patterns/glossary' | relative_url }}) is in the KindedJ org's roadmap.
