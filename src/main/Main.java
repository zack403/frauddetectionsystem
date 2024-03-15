package main;

import frauddetection.FraudDetector;
import model.Transaction;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Main {
    public static void main(String[] args) {
        FraudDetector fraudDetector = new FraudDetector();

        try {
            JSONArray jsonArray = readJsonArrayFromFile("test-dataset.json");

            for (Object obj : jsonArray) {
                JSONObject jsonObject = (JSONObject) obj;
                long timestamp = (long) jsonObject.get("timestamp");
                double amount = (double) jsonObject.get("amount");
                String userID = (String) jsonObject.get("userID");
                String serviceID = (String) jsonObject.get("serviceID");

                Transaction transaction = new Transaction(timestamp, amount, userID, serviceID);
                fraudDetector.processTransaction(transaction);
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private static JSONArray readJsonArrayFromFile(String fileName) throws IOException, ParseException {
        try (FileReader reader = new FileReader(fileName)) {
            return (JSONArray) new JSONParser().parse(reader);
        }
    }
}
