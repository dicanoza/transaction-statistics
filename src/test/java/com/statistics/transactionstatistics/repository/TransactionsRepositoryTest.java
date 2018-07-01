package com.statistics.transactionstatistics.repository;

import static java.lang.Double.MAX_VALUE;
import static java.lang.Double.MIN_VALUE;
import static java.math.BigDecimal.valueOf;

import com.statistics.transactionstatistics.model.Statistics;
import java.math.RoundingMode;
import java.util.Date;
import java.util.stream.IntStream;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;


@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class TransactionsRepositoryTest {

  @Autowired
  TransactionsRepository transactionsRepository;

  @Test
  @DisplayName("executing 100000 calls in parallel")
  public void multipleCallsTest() {
    IntStream.range(0, 10).parallel().forEach(i -> {
      transactionsRepository.store(new Date().getTime(), 5.4);
    });

    Statistics load = transactionsRepository.load(new Date().getTime());
    Assert.assertEquals(54, load.getSum(), 0d);
    Assert.assertEquals(5.4, load.getAvg(), 0d);
    Assert.assertEquals(5.4, load.getMax(), 0d);
    Assert.assertEquals(5.4, load.getMin(), 0d);
    Assert.assertEquals(10, load.getCount(), 0d);
  }

  @Test
  @DisplayName("check if the calculations are correct")
  public void calculationTest() {
    long time = new Date().getTime();
    transactionsRepository.store(time, 1.111111);
    transactionsRepository.store(time, 2.222222);
    transactionsRepository.store(time, 3.333333);
    transactionsRepository.store(time, 4.444444);

    Statistics load = transactionsRepository.load(time);
    Assert.assertEquals(11.11111, load.getSum(), 0d);

    // Rounding operation
    Assert.assertEquals(2.77778, load.getAvg(), 0d);
    Assert.assertEquals(4.444444, load.getMax(), 0d);
    Assert.assertEquals(1.111111, load.getMin(), 0d);
    Assert.assertEquals(4, load.getCount(), 0d);
  }


  @Test
  @DisplayName("check extreme big numbers")
  public void edgeCaseBigNumbersTest() {
    long time = new Date().getTime();
    transactionsRepository.store(time, MAX_VALUE);
    transactionsRepository.store(time, MAX_VALUE);
    transactionsRepository.store(time, MAX_VALUE);
    transactionsRepository.store(time, MAX_VALUE);

    Statistics load = transactionsRepository.load(time);
    Assert.assertEquals(MAX_VALUE, load.getSum(), 0d);
    Assert.assertEquals(valueOf(MAX_VALUE)
        .divide(valueOf(4), RoundingMode.HALF_DOWN)
        .doubleValue(), load.getAvg(), 0d);
    Assert.assertEquals(MAX_VALUE, load.getMax(), 0d);
    Assert.assertEquals(MAX_VALUE, load.getMin(), 0d);
    Assert.assertEquals(4, load.getCount(), 0d);
  }


  @Test
  @DisplayName("check negative numbers")
  public void edgeCaseNegativeNumbersTest() {
    long time = new Date().getTime();
    transactionsRepository.store(time, 1d);
    transactionsRepository.store(time, -1d);

    Statistics load = transactionsRepository.load(time);
    Assert.assertEquals(0, load.getSum(), 0d);
    Assert.assertEquals(0, load.getAvg(), 0d);
    Assert.assertEquals(1, load.getMax(), 0d);
    Assert.assertEquals(-1, load.getMin(), 0d);
    Assert.assertEquals(2, load.getCount(), 0d);
  }


  @Test
  @DisplayName("check extreme negative numbers")
  public void edgeCaseExtremeNegativeNumbersTest() {
    long time = new Date().getTime();
    transactionsRepository.store(time, MIN_VALUE);

    Statistics load = transactionsRepository.load(time);
    Assert.assertEquals(MIN_VALUE, load.getSum(), 0d);
    Assert.assertEquals(MIN_VALUE, load.getAvg(), 0d);
    Assert.assertEquals(MIN_VALUE, load.getMax(), 0d);
    Assert.assertEquals(MIN_VALUE, load.getMin(), 0d);
    Assert.assertEquals(1, load.getCount(), 0d);
  }

}