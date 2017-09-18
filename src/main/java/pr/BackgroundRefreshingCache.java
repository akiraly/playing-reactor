package pr;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.util.concurrent.Uninterruptibles;

import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink.OverflowStrategy;
import reactor.core.scheduler.Schedulers;

public class BackgroundRefreshingCache {
  private static final Logger LOGGER = LoggerFactory.getLogger(BackgroundRefreshingCache.class);

  public static void main(String[] args) {
    Flux<LocalDateTime> flux = Flux.<LocalDateTime>create(e -> {
      Schedulers.newSingle("brc", true)
          .schedulePeriodically(
              () -> {
                LOGGER.info("calculating...");
                e.next(LocalDateTime.now(ZoneOffset.UTC));
              },
              0, 100, TimeUnit.MILLISECONDS);
    }, OverflowStrategy.LATEST).cache(1);

    flux.blockFirst();

    while (true) {
      LOGGER.info("{}", flux.blockFirst(Duration.ofMillis(0)));
      Uninterruptibles.sleepUninterruptibly(50, TimeUnit.MILLISECONDS);
    }
  }

}
