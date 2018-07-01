package com.statistics.transactionstatistics.constroller;

import static org.springframework.http.ResponseEntity.ok;
import static org.springframework.web.servlet.support.ServletUriComponentsBuilder.fromCurrentRequest;

import com.statistics.transactionstatistics.model.Statistics;
import com.statistics.transactionstatistics.model.Transaction;
import com.statistics.transactionstatistics.service.StatisticsService;
import java.util.Date;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class StatisticsController {

  public static final int SIXTY_SECONDS = 60000;
  private StatisticsService statisticsService;

  public StatisticsController(
      StatisticsService statisticsService) {
    this.statisticsService = statisticsService;
  }

  /**
   * Return statistics from the past 60 seconds. Statistics returned are referenced in the class
   * {@link Statistics}. Including, Sum of the value of the transactions, Avg, Max and Min values,
   * and also the count of transactions.
   */
  @GetMapping("/statistics")
  public ResponseEntity<Statistics> statistics() {
    return ok(statisticsService.load(new Date().getTime()));
  }

  /**
   * Post method to store a new transaction. Transactions are referenced by the class {@link
   * Transaction}. It includes the timestamp of the transaction and amount. If the timestamp refer
   * to a time prior to 60 seconds from the time of the received call, a NO_CONTENT message will be
   * returned.
   */
  @PostMapping("/transactions")
  public ResponseEntity<Void> transactions(
      @RequestBody Transaction transaction) {
    if (transaction.getTimestamp() < (new Date().getTime() - SIXTY_SECONDS)) {
      return ResponseEntity.noContent().build();
    }

    statisticsService.store(transaction.getTimestamp(), transaction.getAmount());
    return ResponseEntity.created(fromCurrentRequest().path("/statistics").build().toUri()).build();
  }


}
