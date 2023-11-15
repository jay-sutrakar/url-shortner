package org.example.url_shortner.util;

import java.util.Base64;

public class Utility {
    // Need to implement a different hashcode method if we need
    // fixed length hash value
    public static String getEncodedString(String originalString) {
        byte[] bytes = originalString.getBytes();
        String encodeString = Base64.getEncoder().encodeToString(bytes);
        return encodeString;
    }
}
