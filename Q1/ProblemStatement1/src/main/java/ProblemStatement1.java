import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import org.json.JSONObject;
import org.json.JSONTokener;

public class ProblemStatement1 {

    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: java -jar DestinationHashGenerator.jar <PRN Number> <path to json file>");
            System.exit(1);
        }

        String prnNumber = args[0];
        String jsonFilePath = args[1];

        // Validate PRN Number
        if (prnNumber.trim().isEmpty()) {
            System.out.println("PRN Number cannot be empty.");
            System.exit(1);
        }

        // Read JSON file and find the value of the first instance of "destination"
        String destinationValue = findDestinationValue(jsonFilePath);
        if (destinationValue == null) {
            System.out.println("Key 'destination' not found in the JSON file.");
            System.exit(1);
        }

        // Generate random 8-character alphanumeric string
        String randomString = generateRandomString(8);

        // Concatenate PRN Number, Destination Value, and Random String
        String concatenatedString = prnNumber + destinationValue + randomString;

        // Generate MD5 hash
        String md5Hash = generateMD5Hash(concatenatedString);

        // Output the result
        System.out.println(md5Hash + ";" + randomString);
    }

    private static String findDestinationValue(String filePath) {
        try (FileInputStream fis = new FileInputStream(filePath)) {
            JSONTokener tokener = new JSONTokener(fis);
            JSONObject jsonObject = new JSONObject(tokener);
            return findDestinationValueRecursively(jsonObject);
        } catch (IOException e) {
            System.out.println("Error reading the JSON file: " + e.getMessage());
            System.exit(1);
        }
        return null;
    }

    private static String findDestinationValueRecursively(JSONObject jsonObject) {
        for (String key : jsonObject.keySet()) {
            if (key.equals("destination")) {
                return jsonObject.getString(key);
            }
            Object value = jsonObject.get(key);
            if (value instanceof JSONObject) {
                String result = findDestinationValueRecursively((JSONObject) value);
                if (result != null) {
                    return result;
                }
            }
        }
        return null;
    }

    private static String generateRandomString(int length) {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            sb.append(characters.charAt(random.nextInt(characters.length())));
        }
        return sb.toString();
    }

    private static String generateMD5Hash(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not found", e);
        }
    }
}
