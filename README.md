# Real-Time Fraud Detection System

## Project Structure/Setup

The project is structured into multiple packages to maintain separation of concerns:

- **src/main:** Contains the `Main.java` class responsible for initializing and running the fraud detection system.
- **src/model:** Contains the `Transaction.java` class representing transaction objects.
- **src/frauddetection:** Contains the `FraudDetector.java` class responsible for detecting fraudulent patterns.

## Algorithm Description:

- The FraudDetector class maintains a map of user transactions for each service.
- It checks for three fraudulent patterns:
  1. Conducting transactions in more than 3 distinct services within a 5-minute window.
  2. Transactions that are 5x above the user's average transaction amount in the last 24 hours.
  3. "Ping-pong" activity (transactions bouncing back and forth between two services) within 10 minutes.
- The solution efficiently processes and analyzes the transaction stream without relying on external libraries designed specifically for anomaly detection.

## Out-of-Order Event Processing:
The solution efficiently handles a large volume of transactions and incorporates mechanisms for handling out-of-order events, considering network latencies in a distributed system. Here's how it addresses out-of-order event processing:

- Event Buffering: The system maintains a priority queue to store incoming events temporarily before processing them. Events are ordered based on their timestamps.

- Handling Out-of-Order Events: When an event is received, its timestamp is compared with the timestamp of the last processed event. If the event is older than the last processed event, it is ignored to handle out-of-order events.

- Time Window Consideration: Events within the last 5 minutes are considered part of the current window. Buffered events that fall outside of this window are not processed immediately.

- Reordering Mechanism: Buffered events are processed based on their timestamps. Events that are within the current time window are processed, ensuring that late-arriving events are processed correctly.

- Maximum Delay Threshold: The system defines a maximum delay threshold for out-of-order events. Events that arrive later than this threshold are considered out-of-order and are handled accordingly.

## Fraud Detection Logic
- The FraudDetector.java class implements logic to detect fraudulent patterns in the transaction data. It checks for patterns such as conducting transactions in multiple services within a short time frame, unusually high transaction amounts, and "ping-pong" activities between services. The system flags users involved in suspicious activities and generates alerts accordingly.

## Assumptions
 - The test dataset provided represents a real-world scenario with various types of transactions and users.
- The fraud detection logic implemented covers the specified patterns and can be further extended as needed.
- The system handles out-of-order events by processing transactions sequentially and maintaining user transaction histories.

## How to Run

1. Compile the Java files:

    ```bash
      javac -cp lib/json-simple-1.1.1.jar src/main/*.java src/model/*.java src/frauddetection/*.java

    ```

2. Run the Main class:

    ```bash
    java -cp "src;lib/json-simple-1.1.1.jar" main.Main

    ```

## Test Dataset

The test dataset is stored in a JSON file named `test_dataset.json`. Each transaction object in the dataset represents a single transaction with fields such as `timestamp`, `amount`, `userID`, and `serviceID`. The `Main.java` class reads transactions from this file and processes them using the fraud detection system.

