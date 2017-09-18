# Intro to Reactive Programming on the JVM with Reactor
Links, materials and notes about reactive programming with reactor

# What is Reactive Programming?

> Reactive programming [...] is a subset of asynchronous programming and a paradigm where the availability of new information drives the logic forward rather than having control flow driven by a thread-of-execution. [[ref](https://www.oreilly.com/ideas/reactive-programming-vs-reactive-systems)]

# Reactive Programming vs Reactive System

They are not the same thing!

> A reactive system is an architectural style that allows multiple individual applications to coalesce as a single unit, reacting to its surroundings, while remaining aware of each other—this could manifest as being able to scale up/down, load balancing, and even taking some of these steps proactively. [[ref](https://www.oreilly.com/ideas/reactive-programming-vs-reactive-systems)]

We are focusing on Reactive Programming (the coding part) here not on the System.

# Reactive Programming on the JVM

1. [Reactive Streams](http://www.reactive-streams.org): The API + TCK
1. [RxJava 2](https://github.com/ReactiveX/RxJava): Java 6 (and Android) compatible implementation of Reactive Streams
1. *[Reactor](http://projectreactor.io)*: Java 8 based implementation of Reactive Streams. Developed by the Spring Team, to be used in Spring 5 and onwards.
1. Java 9 Flow API: The Reactive Streams API copy pasted into [java.util.concurrent.Flow](http://download.java.net/java/jdk9/docs/api/index.html?java/util/concurrent/Flow.html) class. More details [here](https://community.oracle.com/docs/DOC-1006738)

We are focusing on Reactor here.

# The Reactive Streams API

```java
public interface Publisher<T> {
    void subscribe(Subscriber<? super T> s);
}

public interface Subscriber<T> {
    void onSubscribe(Subscription s);
    void onNext(T t);
    void onError(Throwable t);
    void onComplete();
}

public interface Subscription {
    void request(long n);
    void cancel();
}

public interface Processor<T, R> extends Subscriber<T>, Publisher<R> {}
```

![Publisher, Subscriber and Subscription](https://cdn.infoq.com/statics_s2_20170905-0254/resource/presentations/rxjava-reactor/en/slides/sl22.jpg)[[ref](https://www.infoq.com/presentations/rxjava-reactor)]

# Marble diagrams?

Example:
![example marble diagram](https://raw.githubusercontent.com/wiki/ReactiveX/RxJava/images/rx-operators/legend.png)[[ref](http://www.java-allandsundry.com/2016/02/marble-diagrams-rxjava-operators.html)]

# Backpressure?

# Flux

![Flux marble](http://ordina-jworks.github.io/img/reactive/flux.png)[[ref](http://ordina-jworks.github.io/reactive/2016/12/12/Reactive-Programming-Spring-Reactor.html)]

Represents stream of 0 to many elements. Sends 0..N signals + an error or completion signal.

It is similar to `java.util.Stream` but even more similar to `java.util.Iterable` as a `Flux` is reusable.

Example:
```java
Flux<String> flux = Flux.just("Hello,", " world!"); // creates Flux with a static method

flux.subscribe(); // need to subscribe to start the data flow
flux.block(); // subscribes + waits... this operation exits the reactor world.
```

# Mono

![Mono marble](http://ordina-jworks.github.io/img/reactive/mono.png)[[ref](http://ordina-jworks.github.io/reactive/2016/12/12/Reactive-Programming-Spring-Reactor.html)]

Represents 0 to 1 elements. Sends 0..1 signals + an error or completion signal.

It is similar to `java.util.Optional` and `java.util.concurrent.CompletableFuture`. `Mono` is reusable as well.

Example:
```java
Mono<String> mono = Mono.just("Hello, world!");

mono.subscribe(); // need to subscribe to start the data flow
mono.block(); // subscribes + waits... this operation exits the reactor world.
```

# Schedulers

Just think about them as thread pools. You use different kind of thread pools for different kind of operations.

`Schedulers.parallel()` -> CPU intensive operations
`Schedulers.elastic()` -> Everything that involves waiting, for example db/network/file/ldap write/read.

Example:
```java
Flux<Integer> flux = Flux.just(1, 2)
	.log() // built in logging, uses slf4j if on classpath
	.publishOn(Schedulers.parallel()) // or subscribeOn()
	.log()
	.flatMap(i -> Mono.fromSupplier(() -> i * 2).publishOn(Schedulers.parallel()))
	.log()
	.publishOn(Schedulers.parallel())
	.log()
	.map(i -> i - 2)
	.log();

flux.blockLast();
```

Output:
```
[main] INFO reactor.Flux.Array.1 - | onSubscribe([Synchronous Fuseable] FluxArray.ArraySubscription)
[main] INFO reactor.Flux.PublishOn.2 - | onSubscribe([Fuseable] FluxPublishOn.PublishOnSubscriber)
[main] INFO reactor.Flux.FlatMap.3 - onSubscribe(FluxFlatMap.FlatMapMain)
[main] INFO reactor.Flux.PublishOn.4 - | onSubscribe([Fuseable] FluxPublishOn.PublishOnSubscriber)
[main] INFO reactor.Flux.MapFuseable.5 - | onSubscribe([Fuseable] FluxMapFuseable.MapFuseableSubscriber)
[main] INFO reactor.Flux.MapFuseable.5 - | request(unbounded)
[main] INFO reactor.Flux.PublishOn.4 - | request(unbounded)
[main] INFO reactor.Flux.FlatMap.3 - request(256)
[main] INFO reactor.Flux.PublishOn.2 - | request(256)
[main] INFO reactor.Flux.Array.1 - | request(256)
[main] INFO reactor.Flux.Array.1 - | onNext(1)
[main] INFO reactor.Flux.Array.1 - | onNext(2)
[parallel-2] INFO reactor.Flux.PublishOn.2 - | onNext(1)
[main] INFO reactor.Flux.Array.1 - | onComplete()
[parallel-2] INFO reactor.Flux.PublishOn.2 - | onNext(2)
[parallel-3] INFO reactor.Flux.FlatMap.3 - onNext(2)
[parallel-2] INFO reactor.Flux.PublishOn.2 - | onComplete()
[parallel-1] INFO reactor.Flux.PublishOn.4 - | onNext(2)
[parallel-3] INFO reactor.Flux.PublishOn.2 - | request(1)
[parallel-1] INFO reactor.Flux.MapFuseable.5 - | onNext(0)
[parallel-4] INFO reactor.Flux.FlatMap.3 - onNext(4)
[parallel-1] INFO reactor.Flux.PublishOn.4 - | onNext(4)
[parallel-1] INFO reactor.Flux.MapFuseable.5 - | onNext(2)
[parallel-4] INFO reactor.Flux.FlatMap.3 - onComplete()
[parallel-1] INFO reactor.Flux.PublishOn.4 - | onComplete()
[parallel-1] INFO reactor.Flux.MapFuseable.5 - | onComplete()
```

Another example (what is the use case?):
```java
Flux<LocalDateTime> flux = Flux.<LocalDateTime>create(e -> {
  Schedulers.newSingle("brc", true)
	  .schedulePeriodically(
		  () -> {
			LOGGER.info("calculating...");
			e.next(LocalDateTime.now(ZoneOffset.UTC));
		  },
		  0, 100, TimeUnit.MILLISECONDS);
}, OverflowStrategy.LATEST).cache(1);

// ...

flux.blockFirst();
```

# Articles
1. [Reactive programming vs. Reactive systems](https://www.oreilly.com/ideas/reactive-programming-vs-reactive-systems)
1. [Best resource for learning RxJava](https://www.reddit.com/r/java/comments/6vkw50/best_resource_for_learning_rxjava/)
1. [5 Things to Know About Reactive Programming](https://www.reddit.com/r/java/comments/6x5ikr/5_things_to_know_about_reactive_programming/)
1. [Loading files with backpressure - RxJava FAQ](https://www.reddit.com/r/java/comments/6xp3hm/loading_files_with_backpressure_rxjava_faq/)
1. [Introduction to Reactive Streams for Java Developers](https://www.reddit.com/r/java/comments/6v20yj/introduction_to_reactive_streams_for_java/?ref=share&ref_source=link)
1. [Servlet vs Reactive Stacks in Five Use Cases - presentation](https://www.reddit.com/r/java/comments/6u0d0i/servlet_vs_reactive_stacks_in_five_use_cases/)
1. [An update on Reactive Streams and what's coming in Java 9 by Viktor Klang](https://www.reddit.com/r/java/comments/6t88wn/an_update_on_reactive_streams_and_whats_coming_in/?ref=share&ref_source=link)
1. [Introduction to Reactive Programming by a core developer of Vert.x (interview + 2 playgrounds)](https://www.reddit.com/r/java/comments/6q4moe/xpost_from_rprogramming_introduction_to_reactive/)
1. [Marble diagrams examples](https://github.com/politrons/reactive)
1. [Marble Diagrams - Rxjava operators](http://www.java-allandsundry.com/2016/02/marble-diagrams-rxjava-operators.html)
1. [Introduction to Reactive Programming](https://www.infoq.com/presentations/rxjava-reactor)
1. [Reactive Programming With Spring Reactor](http://ordina-jworks.github.io/reactive/2016/12/12/Reactive-Programming-Spring-Reactor.html)
1. [The Reactive Scrabble benchmarks by akarnokd blog](http://akarnokd.blogspot.hu/2016/12/the-reactive-scrabble-benchmarks.html)
1. [Reactor – Simple Ways to create Flux/Mono](http://javasampleapproach.com/reactive-programming/reactor/reactor-create-flux-and-mono-simple-ways-to-create-publishers-reactive-programming)

# Other Links
1. [Reactive Programming wikipedia](https://en.wikipedia.org/wiki/Reactive_programming)
1. [The Reactive Manifesto](http://www.reactivemanifesto.org)
1. [Reactive Streams](http://www.reactive-streams.org)
1. [Project Reactor](http://projectreactor.io)
1. [Reactor github](https://github.com/reactor/reactor-core)
1. [RxMarbles: Interactive diagrams of Rx Observables](http://rxmarbles.com)
1. [Lite Rx API Hands-On with Reactor Core 3](https://github.com/reactor/lite-rx-api-hands-on)
