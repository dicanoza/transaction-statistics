package com.statistics.transactionstatistics.model;

import java.util.Objects;

public class Transaction {

  private Long timestamp;
  private Double amount;

  /**
   * Default constructor.
   */
  public Transaction() {
  }

  /**
   * Parametric constructor.
   *
   * @param timestamp of the transaction.
   * @param amount of the transaction.
   */
  public Transaction(Long timestamp, Double amount) {
    this.timestamp = timestamp;
    this.amount = amount;
  }

  public Long getTimestamp() {

    return timestamp;
  }

  public void setTimestamp(Long timestamp) {
    this.timestamp = timestamp;
  }

  public Double getAmount() {
    return amount;
  }

  public void setAmount(Double amount) {
    this.amount = amount;
  }


  @Override
  public String toString() {
    return "Transaction{"
        + "timestamp=" + timestamp
        + ", amount=" + amount
        + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Transaction)) {
      return false;
    }
    Transaction that = (Transaction) o;
    return Objects.equals(timestamp, that.timestamp)
        && Objects.equals(amount, that.amount);
  }

  @Override
  public int hashCode() {

    return Objects.hash(timestamp, amount);
  }

}
