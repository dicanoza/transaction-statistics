package com.statistics.transactionstatistics.service;

import com.statistics.transactionstatistics.model.Statistics;
import com.statistics.transactionstatistics.repository.TransactionsRepository;
import org.springframework.stereotype.Service;

@Service
public class StatisticsService {

  private TransactionsRepository transactionsRepository;

  public StatisticsService(
      TransactionsRepository transactionsRepository) {
    this.transactionsRepository = transactionsRepository;
  }

  public Statistics load(Long time) {
    return transactionsRepository.load(time);
  }

  public void store(Long timestamp, Double value) {
    transactionsRepository.store(timestamp,value);
  }
}