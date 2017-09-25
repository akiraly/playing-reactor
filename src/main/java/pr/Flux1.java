package pr;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

public class Flux1 {
  private static final Scheduler emitter = Schedulers.newParallel("emitter");

  private static final Scheduler transformer = Schedulers.newParallel("transformer");

  private static final Scheduler consumer = Schedulers.newParallel("consumer");

  public static void main(String[] args) {
    Flux<Integer> flux = Flux.just(10, 15)
        .log()
        .publishOn(emitter)
        .log()
        .flatMap(
            i -> Mono.fromSupplier(() -> i * 2)
                .log()
                .publishOn(transformer))
        .log()
        .publishOn(consumer)
        .log()
        .map(i -> i - 2)
        .log();

    flux.blockLast();
  }

}
