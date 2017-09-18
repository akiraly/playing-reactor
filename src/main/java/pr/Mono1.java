package pr;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class Mono1 {

  public static void main(String[] args) {
    Mono<Integer> mono = Mono.just(2)
        .log()
        .publishOn(Schedulers.parallel())
        .log()
        .map(i -> i * 2)
        .log()
        .publishOn(Schedulers.parallel())
        .log()
        .map(i -> i - 2)
        .log();

    mono.block();
  }

}
