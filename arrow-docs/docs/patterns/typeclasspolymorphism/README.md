---
layout: docs-core
title: Polymorphic Programs
permalink: /patterns/polymorphic_programs/
---

## How to write polymorphic programs


What if we could write apps without caring about the runtime data types used, but just about **how the data is operated
on**?

Let's say we have an application working with RxJava's `Observable`. We'll end up having a bunch of chained call stacks
based on that given data type. But, at the end of the day, and for the sake of simplicity, wouldn't `Observable` be just
like a "container" with some extra powers?

And same story for other "containers" like `Flow`, `CompletableFuture`, and many more.

Conceptually, all those types represent an operation (already done or pending to be done), which could support things
like mapping over the inner value, flatMapping to chain other operations of the same type, zipping it with other
instances of the same type, and so on.

What if we could write our programs just based on those behaviors in such a declarative style? We could make them be
agnostic from concrete data types like `Observable`. We'd just need to be sure that the data types support a certain
contract, so they are "mappable", "flatMappable", and so on.

This approach could sound a bit weird or look like overengineering, but it has some interesting benefits. Let's put our
eyes on a simple example first, and then we talk about those. Deal?

### A canonical problem

I'm grabbing the following code samples from my mate [Raúl Raja](https://twitter.com/raulraja) who helped polish this post.

Let's say we have a **TODO** app, and we want to fetch some user `Tasks` from a local cache. In case we don't find them, we'll try to fetch them from a network service. We could have a common contract for both of the `DataSources` that
would provide a way to retrieve a `List` of `Tasks` for a given `User`, regardless of the source:

```kotlin
interface DataSource {
  fun allTasksByUser(user: User): Observable<List<Task>>
}
```

We'll return `Observable` here for simplicity, but it could be `Single`, `Maybe`, `Flowable`, or anything else depending
on our needs.

Let's add a couple of mock implementations for it: a **local** data source, and a **remote** data source.

```kotlin
class LocalDataSource : DataSource {
  private val localCache: Map<User, List<Task>> =
    mapOf(User(UserId("user1")) to listOf(Task("LocalTask assigned to user1")))

  override fun allTasksByUser(user: User): Observable<List<Task>> =
    Observable.create { emitter ->
      val cachedUser = localCache[user]
      if (cachedUser != null) {
        emitter.onNext(cachedUser)
      } else {
        emitter.onError(UserNotInLocalStorage(user))
      }
    }
}

class RemoteDataSource : DataSource {
  private val internetStorage: Map<User, List<Task>> =
    mapOf(User(UserId("user2")) to listOf(Task("Remote Task assigned to user2")))

  override fun allTasksByUser(user: User): Observable<List<Task>> =
    Observable.create { emitter ->
      val networkUser = internetStorage[user]
      if (networkUser != null) {
        emitter.onNext(networkUser)
      } else {
        emitter.onError(UserNotInRemoteStorage(user))
      }
    }
}
```

It's clearly rusty, and both implementations are actually the same. It's simply a mocked version of both data sources
that would ideally retrieve the data from a local cache or a network API. We're using an in memory
`Map<User, List<Task>>` for each one to hold the data.

Since we have two `DataSources`, we'll need a way to coordinate both. Let's add the following `Repository`:

```kotlin
class TaskRepository(private val localDS: DataSource,
                     private val remoteDS: RemoteDataSource) {

  fun allTasksByUser(user: User): Observable<List<Task>> =
    localDS.allTasksByUser(user)
      .subscribeOn(Schedulers.io())
      .observeOn(Schedulers.computation())
      .onErrorResumeNext { _: Throwable -> remoteDS.allTasksByUser(user) }
}
```

It basically tries to load the `List<Task>` from the `LocalDataSource`, and if it's not found, it will try to fetch it
from Network using the `RemoteDataSource`, as required by our specs.

Let's add a simple dependency provisioning Module. We'll use it to get all our instances up and running in a nested way.
We'll avoid using any Dependency Injection frameworks:

```kotlin
class Module {
  private val localDataSource: LocalDataSource = LocalDataSource()
  private val remoteDataSource: RemoteDataSource = RemoteDataSource()
  val repository: TaskRepository = TaskRepository(localDataSource, remoteDataSource)
}
```

And finally, we'll need a simple test to run the whole stack of operations:

```kotlin
object test {

  @JvmStatic
  fun main(args: Array<String>): Unit {
    val user1 = User(UserId("user1"))
    val user2 = User(UserId("user2"))
    val user3 = User(UserId("unknown user"))

    val dependenciesModule = Module()
    dependenciesModule.run {
      repository.allTasksByUser(user1).subscribe({ println(it) }, { println(it) })
      repository.allTasksByUser(user2).subscribe({ println(it) }, { println(it) })
      repository.allTasksByUser(user3).subscribe({ println(it) }, { println(it) })
    }
  }
}
```

[Here you have all the pieces together](https://gist.github.com/JorgeCastilloPrz/05793f11497e0e31f207d2a3e6522bdb), just
in case you want to copy/paste the complete program.

This program composes the execution chain for three different users, and then subscribes to the resulting [`Observable`]({{ '/integrations/rx2' | relative_url }}).

Lucky for us, the first two `Users` are available. `User1` is available on the local data source, and `User2` is available
on the remote data source.

We're not so lucky for `User3`, since it's not found on the local data source. The program will try to load it from the remote
service, where it's not found either. The search will fail, and we'll print the corresponding error on the subscription.

This is what we get printed for the three cases:

```
> [Task(value=LocalTask assigned to user1)]
> [Task(value=Remote Task assigned to user2)]
> UserNotInRemoteStorage(user=User(userId=UserId(value=unknown user)))
```

This is all we get from our canonical example. Let's try to encode it now using a **Functional Programming
polymorphic style**.

### Abstracting out the data types

Our `DataSource` contract interface will look like this from now on:

```kotlin
interface DataSource<F> {
  fun allTasksByUser(user: User): Kind<F, List<Task>>
}
```

It's fairly similar, but it has two important differences:

 * It depends on an `F` generic type.
 * The return type is `Kind<F, List<Task>>`.


`Kind` is basically [the way Arrow encodes something called **Higher Kinded Types**]({{ '/patterns/glossary/#type-constructors' | relative_url }}).
Let's learn the concept pretty rapidly with a very basic example.

On `Observable<A>`, we have 2 parts:

* `Observable`: The "witness" type, the container. A fixed type.
* `A`: The generic type argument. An abstraction, and we can pass any types for it.

We're used to abstract over generic types, like `A`. We are familiar with it. The truth is, we can also abstract over
type containers like `Observable`. That's why "**Higher Kinds**" (Higher Kinded Types) exist.

The overall idea is that we can have constructs as `F<A>` , where both `F` and `A` can be generics. That syntax is not
supported by the Kotlin compiler ([yet?](https://github.com/Kotlin/KEEP/pull/87)), so we need to mimic it by using a
different approach.

Arrow adds support for this by [using an intermediate meta interface called `Kind<F, A>`]({{ '/patterns/glossary/#type-constructors' | relative_url }})
that holds references to both types, and also generates converters at compile time on both directions, so we can go from
`Kind<Observable, List<Task>>` to `Observable<List<Task>>` and vice versa. It's not ideal, but it works for what it's worth.

So, if we take a look at our snippet again:

```kotlin
interface DataSource<F> {
  fun allTasksByUser(user: User): Kind<F, List<Task>>
}
```

The `DataSource` function **returns a higher kind**: `Kind<F, List<Task>>`. It translates to `F<List<Task>>`, where `F`
remains generic.

We're just fixing the `List<Task>` type here, which is already concrete. In other words, we don't care about what the
container type (`F`) is, as long as it keeps a `List<Task>` inside. That is, we leave the slot open **for passing in
different containers**. Clear enough? Let's keep moving.

Let's take a look at the `DataSource` implementations, but, this time, separately for a more gradual learning approach. The local
one first:

```kotlin
class LocalDataSource<F>(A: ApplicativeError<F, Throwable>) : DataSource<F>, ApplicativeError<F, Throwable> by A {

    private val localCache: Map<User, List<Task>> =
      mapOf(User(UserId("user1")) to listOf(Task("LocalTask assigned to user1")))

    override fun allTasksByUser(user: User): Kind<F, List<Task>> =
      Option.fromNullable(localCache[user]).fold(
        { raiseError(UserNotInLocalStorage(user)) },
        { just(it) }
      )
}
```

See a bunch of new things? Don't be scared. Let's go **step by step**.

This `DataSource` keeps the generic `F` since it implements `DataSource<F>`. We want to be able to pass that type from
the outside, all the way down.

Now, forget about that probably unfamiliar [`ApplicativeError`]({{ '/arrow/typeclasses/applicativeerror' | relative_url }})
thing on the constructor, and focus on the `allTasksByUser()` function, just for now. We'll get back to that.

```kotlin
override fun allTasksByUser(user: User): Kind<F, List<Task>> =
    Option.fromNullable(localCache[user]).fold(
      { raiseError(UserNotInLocalStorage(user)) },
      { just(it) }
    )
```

As you see, it returns (one more time) `Kind<F, List<Task>>`. So we still don't care about what the container `F` is, as
long as it contains a `List<Task>`.

But we have a problem. Depending on whether we can find the `Tasks` for the given user on the local cache or not, we
might want to **notify an error** (`Tasks` not found), or **return the Tasks already wrapped into `F`** (`Tasks` found).

And for both things, we must return: `Kind<F, List<Task>>`.

In other words, there's a type **we still don't know anything about: `F`**. And we need a way to return an error wrapped
into it, and also a way to construct an instance of it wrapping a successful value. Sounds impossible? 

Let's go back to the class declaration and find that [`ApplicativeError`]({{ '/arrow/typeclasses/applicativeerror' | relative_url }})
being passed on construction, and then used as a delegate for the class (`by A`).

```kotlin
class LocalDataSource<F>(A: ApplicativeError<F, Throwable>) : DataSource<F>, ApplicativeError<F, Throwable> by A {
    //...
}
```

[`ApplicativeError`]({{ '/arrow/typeclasses/applicativeerror' | relative_url }}) extends from [`Applicative`]({{ '/arrow/typeclasses/applicative' | relative_url }}),
and both are [**Typeclasses**]({{ '/typeclasses/intro' | relative_url }}).

**Typeclasses define behaviors (contracts)**. They're basically encoded as interfaces that work over a generic argument,
as in [`Monad<F>`]({{ '/arrow/typeclasses/monad' | relative_url }}) , [`Functor<F>`]({{ '/arrow/typeclasses/functor' | relative_url }})
and many more. That `F` is the data type. So we will be able to pass types like [`Either`]({{ '/apidocs/arrow-core-data/arrow.core/-either/' | relative_url }})
, [`Option`]({{ '/apidocs/arrow-core-data/arrow.core/-option/' | relative_url }}), [`Observable`]({{ '/integrations/rx2' | relative_url }}),
[`Flowable`]({{ '/integrations/rx2' | relative_url }}), and many more for it.

Don't worry if you don't know about some of them yet. Data types like `Either` or `Option` are particular from
Functional Programming, and you probably don't need to know more about them yet.

So, back to our two problems:

* **Wrapping a successful value into an instance of `Kind<F, List<Task>>`**

We can rely on a typeclass for this: [`Applicative`]({{ '/arrow/typeclasses/applicative' | relative_url }}). Since [`ApplicativeError`]({{ '/arrow/typeclasses/applicativeerror' | relative_url }})
extends from it, we can delegate to it. We're delegating our class on it, so we can use its features out of the box.

[`Applicative`]({{ '/arrow/typeclasses/applicative' | relative_url }}) provides the `just(a)` function. `just(a)` **wraps
a value into the context of any Higher Kind**. So, if we have an `Applicative<F>`, it could call `just(a)` to wrap the
value into the container `F`, regardless of which one is it. Let's say it's `Observable`. We'll have an
`Applicative<Observable>`, which will know how to wrap `a` into an `Observable` like `Observable.just(a)`.

* **Wrapping an error into an instance of `Kind<F, List<Task>>`**

We can use [`ApplicativeError`]({{ '/arrow/typeclasses/applicativeerror' | relative_url }}) for that. It brings the
function `raiseError(e)` into scope. `raiseError(e)` basically wraps an error into the `F` container. For the
`Observable` example, raising the error would end up doing something like `Observable.error<A>(t)`, where `t` is a
`Throwable`, since we're declaring our error type when we declare the typeclass as `ApplicativeError<F, Throwable>`.

Let's get back to our abstracted `LocalDataSource<F>` implementation.

```kotlin
class LocalDataSource<F>(A: ApplicativeError<F, Throwable>) :
    DataSource<F>, ApplicativeError<F, Throwable> by A {

    private val localCache: Map<User, List<Task>> =
      mapOf(User(UserId("user1")) to listOf(Task("LocalTask assigned to user1")))

    override fun allTasksByUser(user: User): Kind<F, List<Task>> =
      Option.fromNullable(localCache[user]).fold(
        { raiseError(UserNotInLocalStorage(user)) },
        { just(it) }
      )
}
```

The in memory map remains the same, but the function does a couple things probably new to you:

* It tries to load the `Tasks` from the local cache, and since that returns a nullable (because the `Tasks` could not be
found), we are going to model that using an [`Option`]({{ '/apidocs/arrow-core-data/arrow.core/-option/' | relative_url }}). In case you're not
aware of how [`Option`]({{ '/apidocs/arrow-core-data/arrow.core/-option/' | relative_url }}) works, it models presence vs. absence of a value. A
value that is wrapped inside of it.

* After getting our optional value, we `fold` over it. Folding is just equivalent to a when statement over the optional
value. When it's **absent**, it wraps an error into the `F` data type (first lambda passed in). And when it's
**present**, it constructs an instance of the `F` data type wrapping it (second lambda). For both cases, it uses the
[`ApplicativeError`]({{ '/arrow/typeclasses/applicativeerror' | relative_url }}) features mentioned before:
`raiseError()` and `just()`.

With this, we've basically abstracted away our data source implementation, so it doesn't know about which container it's
using for the type `F`. And we have been able to do it thanks to the abstractions defined by the typeclasses.

The network `DataSource` implementation would look similar:

```kotlin
class RemoteDataSource<F>(A: Async<F>) : DataSource<F>, Async<F> by A {
  private val internetStorage: Map<User, List<Task>> =
    mapOf(User(UserId("user2")) to listOf(Task("Remote Task assigned to user2")))

  override fun allTasksByUser(user: User): Kind<F, List<Task>> =
    async { callback: (Either<Throwable, List<Task>>) -> Unit ->
      Option.fromNullable(internetStorage[user]).fold(
        { callback(UserNotInRemoteStorage(user).left()) },
        { callback(it.right()) }
      )
    }
}
```

This time, there's a subtle difference: Instead of delegating into an instance of [`ApplicativeError`]({{ '/arrow/typeclasses/applicativeerror' | relative_url }})
as before, we'll use a different typeclass: [`Async`]({{ '/effects/async/' | relative_url }}).

That's because of the asynchronous nature of a network call. We want to code it in an asynchronous way, so we'll
delegate its async requirements into a typeclass that is thought for it.

[`Async`]({{ '/effects/async/' | relative_url }}) models asynchronous operations. So it's able to model any
operations that are based on callbacks. Note that we still don't know about the concrete data types to use, but about
the problem, which **is asynchronous by nature**. Therefore, we use a Typeclass that encodes that problematic to solve
it.

So, zooming in a bit into the function:

```kotlin
override fun allTasksByUser(user: User): Kind<F, List<Task>> =
    async { callback: (Either<Throwable, List<Task>>) -> Unit ->
      Option.fromNullable(internetStorage[user]).fold(
        { callback(UserNotInRemoteStorage(user).left()) },
        { callback(it.right()) }
      )
    }
```

We can use the `async {}` function provided by the [`Async`]({{ '/effects/async/' | relative_url }}) typeclass to
model our operation and create an instance of the type `Kind<F, List<Task>>` that will be resolved asynchronously.

If we were using a fixed data type like `Observable`, `Async.async {}` would be equivalent to `Observable.create()`, in
terms of building up an operation that can be bridged from synchronous or asynchronous code, i.e. `Thread` or
`AsyncTask`.

The `callback` parameter is used to wire back the real resulting callbacks into the `F` container (higher kind type)
context.

With this, we have our `RemoteDataSource` also abstracted away and depending on a still unknown container type `F`.

Let's go up one level to take a look at the repo. If you can remember, we need to search for the user `Tasks` on the
`LocalDataSource` first, and, if they're not available, try to fetch them from the `RemoteLocalDataSource`.

```kotlin
class TaskRepository<F>(
  private val localDS: DataSource<F>,
  private val remoteDS: RemoteDataSource<F>,
  AE: ApplicativeError<F, Throwable>) : ApplicativeError<F, Throwable> by AE {

  fun allTasksByUser(user: User): Kind<F, List<Task>> =
    localDS.allTasksByUser(user).handleErrorWith {
      when (it) {
        is UserNotInLocalStorage -> remoteDS.allTasksByUser(user)
        else -> raiseError(UnknownError(it))
      }
    }
}
```

[`ApplicativeError<F, Throwable>`]({{ '/arrow/typeclasses/applicativeerror/' | relative_url }}) is back! It also brings
an extension function into scope called `handleErrorWith()` that works over any Higher Kind receiver.

It's encoded like:

```kotlin
fun <A> Kind<F, A>.handleErrorWith(f: (E) -> Kind<F, A>): Kind<F, A>
```

Since the call `localDS.allTasksByUser(user)` returns `Kind<F, List<Task>>`, which would stand for `F<List<Task>>` where
`F` remains generic, we can call the `handleErrorWith()` function on top of it.

`handleErrorWith()` allows you to react to errors using the passed in lambda. Let's zoom in to the function:

```kotlin
fun allTasksByUser(user: User): Kind<F, List<Task>> =
    localDS.allTasksByUser(user).handleErrorWith {
      when (it) {
        is UserNotInLocalStorage -> remoteDS.allTasksByUser(user)
        else -> raiseError(UnknownError(it))
      }
    }
```

So we basically get back the result of the first operation unless it throws an exception, which will be handled by the
lambda block. In case the error has the type `UserNotInLocalStorage`, we try to search for the `Tasks` on the remote
`DataSource`. In any other cases, we raise (wrap) an unknown error into the `F` container type.

The dependency provisioning module remains very similar:

```kotlin
class Module<F>(A: Async<F>) {
  private val localDataSource: LocalDataSource<F> = LocalDataSource(A)
  private val remoteDataSource: RemoteDataSource<F> = RemoteDataSource(A)
  val repository: TaskRepository<F> =
      TaskRepository(localDataSource, remoteDataSource, A)
}
```

The only difference is that now it's abstracted away and depends on `F`, which stays polymorphic. I've intentionally
obviated this before to avoid noise, but [`Async`]({{ '/effects/async/' | relative_url }}) ultimately extends
from [`ApplicativeError`]({{ '/arrow/typeclasses/applicativeerror/' | relative_url }}), so we can use an instance of it
to solve the concerns we have at all the nested levels, and pass it all the way down as you can see on the module.

### Testing polymorphism

We're finally at the point where **our complete app is abstracted away from any concrete data type containers**
(`F`) and we can focus on testing its polymorphism using the runtime. We'll test the same code passing in different data
types for the type `F`. The scenario is the same we had when we were plainly using `Observable`.

We're now in the program's edge, where we are already out of any abstraction boundaries, and where we are in charge to
pass in the implementation details.

Let's use RxJava `Single` as the container `F` to start with.

```kotlin
object test {

  @JvmStatic
  fun main(args: Array<String>): Unit {
    val user1 = User(UserId("user1"))
    val user2 = User(UserId("user2"))
    val user3 = User(UserId("unknown user"))

    val singleModule = Module(SingleK.async())
    singleModule.run {
      repository.allTasksByUser(user1).fix().single.subscribe(::println, ::println)
      repository.allTasksByUser(user2).fix().single.subscribe(::println, ::println)
      repository.allTasksByUser(user3).fix().single.subscribe(::println, ::println)
    }
  }
}
```

Arrow provides wrappers over some well-known library data types for compatibility, so there's a handy [`SingleK`]({{ '/integrations/rx2/' | relative_url }})
wrapper available for it. These wrappers **enable the data types as Higher Kinds** to be able to use them with
typeclasses.

This test will print:

```
[Task(value=LocalTask assigned to user1)]
[Task(value=Remote Task assigned to user2)]
UserNotInRemoteStorage(user=User(userId=UserId(value=unknown user)))
```

These are the same results as the one using a fixed `Observable`. 🎉

Let's move on to `Maybe`, for which we also have a [`MaybeK`]({{ '/integrations/rx2/' | relative_url }}) wrapper:

```kotlin
@JvmStatic
fun main(args: Array<String>): Unit {
    val user1 = User(UserId("user1"))
    val user2 = User(UserId("user2"))
    val user3 = User(UserId("unknown user"))

    val maybeModule = Module(MaybeK.async())
    maybeModule.run {
      repository.allTasksByUser(user1).fix().maybe.subscribe(::println, ::println)
      repository.allTasksByUser(user2).fix().maybe.subscribe(::println, ::println)
      repository.allTasksByUser(user3).fix().maybe.subscribe(::println, ::println)
    }
}
```

The same result is printed, but, this time, running using a different data type:

```kotlin
[Task(value=LocalTask assigned to user1)]
[Task(value=Remote Task assigned to user2)]
UserNotInRemoteStorage(user=User(userId=UserId(value=unknown user)))
```

What about [`ObservableK`]({{ '/integrations/rx2/' | relative_url }}) / [`FlowableK`]({{ '/integrations/rx2/' | relative_url }})
? Let's try:

```kotlin
object test {

  @JvmStatic
  fun main(args: Array<String>): Unit {
    val user1 = User(UserId("user1"))
    val user2 = User(UserId("user2"))
    val user3 = User(UserId("unknown user"))

    val observableModule = Module(ObservableK.async())
    observableModule.run {
      repository.allTasksByUser(user1).fix().observable.subscribe(::println, ::println)
      repository.allTasksByUser(user2).fix().observable.subscribe(::println, ::println)
      repository.allTasksByUser(user3).fix().observable.subscribe(::println, ::println)
    }

    val flowableModule = Module(FlowableK.async())
    flowableModule.run {
      repository.allTasksByUser(user1).fix().flowable.subscribe(::println)
      repository.allTasksByUser(user2).fix().flowable.subscribe(::println)
      repository.allTasksByUser(user3).fix().flowable.subscribe(::println, ::println)
    }
  }
}
```

Printing:

```
[Task(value=LocalTask assigned to user1)]
[Task(value=Remote Task assigned to user2)]
UserNotInRemoteStorage(user=User(userId=UserId(value=unknown user)))

[Task(value=LocalTask assigned to user1)]
[Task(value=Remote Task assigned to user2)]
UserNotInRemoteStorage(user=User(userId=UserId(value=unknown user)))
```

Everything is working as expected. 💪

And with this, we are done with all the testing. As you can see, we are able to run the same code stack using different
data types, so our program has become completely agnostic of those.

[Here you have the polymorphic app all together](https://gist.github.com/JorgeCastilloPrz/c0a4604b9a5dedc89be82b13cfcc1315)
for copy / paste purposes.

### This is cool but . . . why should I?

That's always your choice, but there are some important benefits that FP brings to the table that you'll need to know
when the time comes.

* You have a really clear separation of concerns: How the data is operated and composed (your actual program) vs. the
runtime. This means **better testability**.

* Your programs would ideally target abstractions, so you have the choice on how to implement them depending on your
needs / environments (i.e., testing). That's ultimately a DI related concept, so, for sure, you can do that without the
need for FP. That said, the FP approach leads you to program in such a way that is less prone to break this "principle."
That's because there's a clearly defined boundary between the declarative algebras (operations) and the runtime (types
used to run it), where the details are.

* Composing your program algebras (operations) based on abstractions allows you to keep your codebase deterministic and free
of unexpected effects (pure). If you want to know more about purity and why pure code is less prone to errors or
unexpected behaviors, [take a look at this post](https://medium.com/@JorgeCastilloPr/kotlin-functional-programming-does-it-make-sense-36ad07e6bacf).

* Following the previous point, your program side effects are controlled at the edge. Effects are caused by
implementation details passed from a single point on the system. (Anything beyond the edge boundaries remains pure).

* If you choose to work with only [Typeclasses]({{ '/typeclasses/intro' | relative_url }}), you'll get a unified
API for all the data types. Repeatability helps you to get familiarized with the concepts (Repeatability, as in just using
operations like `map`, `flatMap`, `fold`, all the way, regardless of the problem you're solving.). Of course, that's
constrained to using some libraries that enable pure FP over Kotlin and provide those operations and constructs, like
Arrow.

* These patterns **remove the need for specific DI frameworks**, since they're based on the same concepts of Dependency
Injection out of the box. You're always able to provide implementation details later on and switch them transparently,
and, before that moment, your program is not tied to any "side-effecty" details. This approach could be considered DI by
itself actually, since it's based on targeting abstractions leaving details to be passed from a single point in the
edge.

* As a final conclusion, I'd suggest that you use the approach that better fits your needs. Functional Programming is not
going to solve all your problems. There are no silver bullets, but it's a totally legit choice that brings a
bunch of key benefits.

### Related links

If you want to learn more about **typeclasses**, you can [read the docs section about them]({{ '/typeclasses/intro' | relative_url }}).
Still, I'll be happy if you now understand that **they're used as contracts to compose our polymorphic programs based
on abstraction** thanks to this post.

If you still have any doubts, please don't hesitate to contact me. The fastest way to do it is my Twitter handle: [@JorgeCastilloPR](https://www.twitter.com/JorgeCastilloPR).

Some of the mentioned concepts like purity are described in the following blogposts:

* [Kotlin Functional Programming: Does it make sense?](https://medium.com/@JorgeCastilloPr/kotlin-functional-programming-does-it-make-sense-36ad07e6bacf) by [Jorge Castillo](https://www.twitter.com/JorgeCastilloPR))
* [Kotlin purity and Function Memoization](https://medium.com/@JorgeCastilloPr/kotlin-purity-and-function-memoization-b12ab35d70a5) by [Jorge Castillo](https://www.twitter.com/JorgeCastilloPR))

Also, consider watching [FP to the max](https://youtu.be/sxudIMiOo68) by [John De Goes](https://twitter.com/jdegoes) and
the related `FpToTheMax.kt` example located in the `arrow-examples` module. This technique may seem to be overkill
on such a small example, but it is meant to be used at scale.
