package main;

import frauddetection.FraudDetector;
import model.Transaction;

import java.io.FileReader;
import java.io.IOException;
import java.util.PriorityQueue;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Main {
    private static final long WINDOW_SIZE_MILLISECONDS = 300000;
    private static PriorityQueue<Transaction> eventBuffer = new PriorityQueue<>((t1, t2) -> Long.compare(t1.getTimestamp(), t2.getTimestamp()));
    private static long lastProcessedTimestamp = Long.MIN_VALUE;

    public static void main(String[] args) {
        FraudDetector fraudDetector = new FraudDetector();

        try {
            JSONArray jsonArray;
            try (FileReader reader = new FileReader("test-dataset.json")) {
                jsonArray = (JSONArray) new JSONParser().parse(reader);
            }

            for (Object obj : jsonArray) {
                JSONObject jsonObject = (JSONObject) obj;
                long timestamp = (long) jsonObject.get("timestamp");
                double amount = (double) jsonObject.get("amount");
                String userID = (String) jsonObject.get("userID");
                String serviceID = (String) jsonObject.get("serviceID");

                if (timestamp < lastProcessedTimestamp) {
                    continue;
                }

                eventBuffer.add(new Transaction(timestamp, amount, userID, serviceID));

                while (!eventBuffer.isEmpty() && eventBuffer.peek().getTimestamp() <= timestamp - WINDOW_SIZE_MILLISECONDS) {
                    Transaction event = eventBuffer.poll();
                    processEvent(event, fraudDetector);
                }

                lastProcessedTimestamp = timestamp;
            }

            while (!eventBuffer.isEmpty()) {
                Transaction event = eventBuffer.poll();
                processEvent(event, fraudDetector);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private static void processEvent(Transaction event, FraudDetector fraudDetector) {
        fraudDetector.processTransaction(event);
    }
}
