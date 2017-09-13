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

# Mono

![Mono marble](http://ordina-jworks.github.io/img/reactive/mono.png)[[ref](http://ordina-jworks.github.io/reactive/2016/12/12/Reactive-Programming-Spring-Reactor.html)]

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

# Other Links
1. [Reactive Programming wikipedia](https://en.wikipedia.org/wiki/Reactive_programming)
1. [The Reactive Manifesto](http://www.reactivemanifesto.org)
1. [Reactive Streams](http://www.reactive-streams.org)
1. [Project Reactor](http://projectreactor.io)
1. [Reactor github](https://github.com/reactor/reactor-core)
1. [RxMarbles: Interactive diagrams of Rx Observables](http://rxmarbles.com)
1. [Lite Rx API Hands-On with Reactor Core 3](https://github.com/reactor/lite-rx-api-hands-on)
