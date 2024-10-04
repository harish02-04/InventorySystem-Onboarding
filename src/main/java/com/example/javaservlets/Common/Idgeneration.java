package com.example.javaservlets.Common;

import java.security.SecureRandom;
import java.util.UUID;

public class Idgeneration {

    public static String generateOrgId() {
        final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        final int ORG_ID_LENGTH = 6;
        final SecureRandom random = new SecureRandom();
        StringBuilder orgId = new StringBuilder(ORG_ID_LENGTH);
        for (int i = 0; i < ORG_ID_LENGTH; i++) {
            int index = random.nextInt(ALPHANUMERIC.length());
            orgId.append(ALPHANUMERIC.charAt(index));
        }
        return orgId.toString();
    }
    public static String generateStaffId() {
        final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        final int STAFF_ID_LENGTH = 6;
        final SecureRandom random = new SecureRandom();
        StringBuilder staffId = new StringBuilder(STAFF_ID_LENGTH);
        for (int i = 0; i < STAFF_ID_LENGTH; i++) {
            int index = random.nextInt(ALPHANUMERIC.length());
            staffId.append(ALPHANUMERIC.charAt(index));
        }
        return staffId.toString();
    }

    public static String generateItemId() {
       final String ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
       final int ITEM_ID_LENGTH = 8;
        SecureRandom random = new SecureRandom();
        StringBuilder itemId = new StringBuilder("IT"); // Start with prefix "IT"

        for (int i = 0; i < ITEM_ID_LENGTH; i++) {
            int index = random.nextInt(ALPHANUMERIC.length());
            itemId.append(ALPHANUMERIC.charAt(index)); // Append a random character
        }

        return itemId.toString(); // Return the generated item ID
    }

    public static String generateTransactionId() {
        // Generate a UUID and take only a portion of it for better readability
        String uniqueId = UUID.randomUUID().toString().replace("-", "").substring(0, 10);

        // Prefix the ID with "TRAN_"
        return "TRAN_" + uniqueId;
    }
}
