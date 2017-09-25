package pr;

import java.util.concurrent.TimeUnit;

import com.google.common.util.concurrent.Uninterruptibles;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

public class Flux2 {

  private static final Scheduler emitter = Schedulers.newElastic("emitter");

  private static final Scheduler transformer = Schedulers.newElastic("transformer");

  private static final Scheduler consumer = Schedulers.newParallel("consumer");

  public static void main(String[] args) {
    Flux<Integer> flux = Flux.<Integer>push(e -> {
      // imagine reading from DB row by row or from file line by line
      for (int fi = 0; fi < 30; fi++) {
        Uninterruptibles.sleepUninterruptibly(1, TimeUnit.SECONDS);
        e.next(fi);
      }
    })
        .log()
        .subscribeOn(emitter)
        .log();

    flux.flatMap(
        // could be some other IO like reading from a second database
        i -> Mono.fromSupplier(() -> i + " - " + i * 2)
            .log()
            .subscribeOn(transformer)
            .log()
            .publishOn(consumer))
        .log()
        .collectList()
        .log().block();
  }

}
