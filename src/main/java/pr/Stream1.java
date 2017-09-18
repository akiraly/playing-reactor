package pr;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;

public class Stream1 {
  private static final Logger LOGGER = LoggerFactory.getLogger(Stream1.class);

  public static void main(String[] args) {
    ImmutableList.of(10, 15).parallelStream()
        .map(i -> {
          LOGGER.info("calculating for i = {}", i);
          return i * 2;
        })
        .map(i -> {
          LOGGER.info("calculating2 for i = {}", i);
          return i - 2;
        }).toArray();
  }

}
