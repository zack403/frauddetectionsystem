package frauddetection;

import model.Transaction;

import java.util.*;

public class FraudDetector {
    private Map<String, Map<String, List<Transaction>>> userTransactions;

    public FraudDetector() {
        userTransactions = new HashMap<>();
    }

    public void processTransaction(Transaction transaction) {
        userTransactions
                .computeIfAbsent(transaction.getUserID(), k -> new HashMap<>())
                .computeIfAbsent(transaction.getServiceID(), k -> new ArrayList<>())
                .add(transaction);

        checkFraudulentPatterns(transaction);
    }

    private void checkFraudulentPatterns(Transaction transaction) {
        String userID = transaction.getUserID();
        String serviceID = transaction.getServiceID();

        List<Transaction> transactions = userTransactions.get(userID).get(serviceID);

        transactions.sort(Comparator.comparing(Transaction::getTimestamp));

        if (getDistinctServicesInWindow(userID, transaction.getTimestamp()) > 3) {
            System.out.println("Alert: User " + userID + " involved in suspicious activity - transactions in more than 3 services within a 5-minute window");
        }

        if (isHighAmountTransaction(userID, transaction.getAmount())) {
            System.out.println("Alert: User " + userID + " involved in suspicious activity - unusually high transaction amount");
        }

        if (isPingPongActivity(transactions)) {
            System.out.println("Alert: User " + userID + " involved in suspicious activity - ping-pong activity detected");
        }
    }

    private int getDistinctServicesInWindow(String userID, long currentTimestamp) {
        Set<String> services = new HashSet<>();
        Map<String, List<Transaction>> userTransactionsByService = userTransactions.get(userID);

        for (List<Transaction> transactions : userTransactionsByService.values()) {
            for (Transaction transaction : transactions) {
                if (currentTimestamp - transaction.getTimestamp() <= 300) {
                    services.add(transaction.getServiceID());
                }
            }
        }

        return services.size();
    }

    private boolean isHighAmountTransaction(String userID, double amount) {
        double sum = 0;
        int count = 0;
        long currentTimestamp = System.currentTimeMillis() / 1000;

        Map<String, List<Transaction>> userTransactionsByService = userTransactions.get(userID);

        for (List<Transaction> transactions : userTransactionsByService.values()) {
            for (Transaction transaction : transactions) {
                if (currentTimestamp - transaction.getTimestamp() <= 86400) {
                    sum += transaction.getAmount();
                    count++;
                }
            }
        }

        double average = count > 0 ? sum / count : 0;
        return amount > 5 * average;
    }

    private boolean isPingPongActivity(List<Transaction> transactions) {
        if (transactions.size() < 2) {
            return false;
        }

        long firstTimestamp = transactions.get(0).getTimestamp();
        long lastTimestamp = transactions.get(transactions.size() - 1).getTimestamp();

        return lastTimestamp - firstTimestamp <= 600;
    }
}
