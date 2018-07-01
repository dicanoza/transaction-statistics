# transaction-statistics

This service collects transactions and retrieve statistics uppon then for the past 60 seconds.

## Getting Started

These instructions will demonstrate how to build and run.

### Prerequisites

Before we start you must have these installed.

```
Java 1.8 or higher
Maven 3.3.9 or higher
```

### Installing and running


To run it, open a command window at the main directory of the project and run the command below.

```
mvn spring-boot:run
```

## Testing

### Get Statistics

To get statistics, it's possible to run a simple get call, like this below:

```
curl localhost:8080/statistics
```
Sample response:
```
{"sum":0.0,"avg":0.0,"max":0.0,"min":0.0,"count":0}
```
 ### Post transactions

To add transactions, call a POST method /transactions informing the the amount and timestamp.
 
```
curl --header "Content-Type: application/json" \
  --request POST \
  --data '{"amount": 12.3,"timestamp": 1478192204000}' \
  http://localhost:8080/transactions
  ```

