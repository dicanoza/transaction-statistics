package com.statistics.transactionstatistics.repository;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.math.BigDecimal.valueOf;

import com.statistics.transactionstatistics.model.Statistics;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.LongStream;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Repository;

@Repository
public class TransactionsRepository {

  public static final int ONE_SECOND = 1000;
  private final ConcurrentHashMap<Long, Statistics> cache = new ConcurrentHashMap<>();


  /**
   * Store the transaction and calculates statistics for the next 60 seconds. Note that the
   * calculations are threshold to a second to improve performance. This method is executed in
   * constant time for every call.
   *
   * @param timestamp of the transaction.
   * @param value the amount of the transaction.
   */
  public void store(Long timestamp, Double value) {
    final long threshold = getThreshold(timestamp);
    LongStream.range(0, 60)
        .parallel()
        .map(key -> threshold + (key * ONE_SECOND))
        .forEach(futureTimestamp -> {
          synchronized (cache) {
            cache.computeIfPresent(futureTimestamp, (innerKey, statisticsEntity) -> {
              statisticsEntity
                  .setSum(min(Double.MAX_VALUE,
                      valueOf(statisticsEntity.getSum())
                      .add(valueOf(value))
                      .doubleValue()));
              statisticsEntity.setCount(statisticsEntity.getCount() + 1);
              statisticsEntity.setMax(max(value, statisticsEntity.getMax()));
              statisticsEntity.setMin(min(value, statisticsEntity.getMin()));
              statisticsEntity.setAvg(valueOf(statisticsEntity.getSum())
                  .divide(valueOf(statisticsEntity.getCount()),RoundingMode.HALF_DOWN)
                  .doubleValue());
              return statisticsEntity;
            });
            cache.putIfAbsent(futureTimestamp,
                new Statistics(value, value, value, value, 1L)
            );
          }
        });

  }

  /**
   * Loads the statistics for the current second. Threshold to a second to improve performance.
   *
   * @param timestamp to be considered
   * @return Statistics
   */
  public Statistics load(Long timestamp) {
    return cache.getOrDefault(getThreshold(timestamp), new Statistics());
  }

  /**
   * Cleanup the cache that is older than one second. This one second rule is just to be safe and
   * not compete with a statistics request that might be still being processed.
   */
  @Scheduled(cron = "* * * * * *")
  public void cleanCache() {
    final long time = getThreshold(new Date().getTime()) - ONE_SECOND;
    cache
        .entrySet()
        .stream()
        .map(Entry::getKey)
        .filter(k -> k < time)
        .forEach(cache::remove);
  }

  private long getThreshold(Long timestamp) {
    return (timestamp / ONE_SECOND) * ONE_SECOND;
  }

}
