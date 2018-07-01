package com.statistics.transactionstatistics.model;

import static java.lang.Double.valueOf;

import java.util.Objects;

public class Statistics {

  private Double sum;
  private Double avg;
  private Double max;
  private Double min;
  private Long count;

  /**
   * Default constructor setting values to 0.
   */
  public Statistics() {
    this.sum = valueOf(0);
    this.avg = valueOf(0);
    this.max = valueOf(0);
    this.min = valueOf(0);
    this.count = Long.valueOf(0);
  }

  /**
   * Parametric constructor.
   *
   * @param sum of the amount of the transactions
   * @param avg of the amount of the transactions
   * @param max of the amount of the transactions
   * @param min of the amount of the transactions
   * @param count of the transactions
   */
  public Statistics(Double sum, Double avg, Double max, Double min, Long count) {
    this.sum = sum;
    this.avg = avg;
    this.max = max;
    this.min = min;
    this.count = count;
  }

  public Double getSum() {
    return sum;
  }

  public void setSum(Double sum) {
    this.sum = sum;
  }

  public Double getAvg() {
    return avg;
  }

  public void setAvg(Double avg) {
    this.avg = avg;
  }

  public Double getMax() {
    return max;
  }

  public void setMax(Double max) {
    this.max = max;
  }

  public Double getMin() {
    return min;
  }

  public void setMin(Double min) {
    this.min = min;
  }

  public Long getCount() {
    return count;
  }

  public void setCount(Long count) {
    this.count = count;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Statistics)) {
      return false;
    }
    Statistics that = (Statistics) o;
    return Objects.equals(sum, that.sum)
        && Objects.equals(avg, that.avg)
        && Objects.equals(max, that.max)
        && Objects.equals(min, that.min)
        && Objects.equals(count, that.count);
  }

  @Override
  public int hashCode() {

    return Objects.hash(sum, avg, max, min, count);
  }

  @Override
  public String toString() {
    return "Statistics{"
        + "sum=" + sum
        + ", avg=" + avg
        + ", max=" + max
        + ", min=" + min
        + ", count=" + count
        + '}';
  }
}
