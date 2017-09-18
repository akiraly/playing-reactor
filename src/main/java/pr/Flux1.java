package pr;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class Flux1 {

  public static void main(String[] args) {
    Flux<Integer> flux = Flux.just(10, 15)
        .log()
        .publishOn(Schedulers.parallel())
        .log()
        .flatMap(i -> Mono.fromSupplier(() -> i * 2).publishOn(Schedulers.parallel()))
        .log()
        .publishOn(Schedulers.parallel())
        .log()
        .map(i -> i - 2)
        .log();

    flux.blockLast();
  }

}
