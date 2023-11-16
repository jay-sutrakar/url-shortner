package org.example.url_shortner.util;

import java.time.LocalDate;
import java.util.Base64;
import java.util.UUID;

public class Utility {
    // Need to implement a different hashcode method if we need
    // fixed length hash value
    public static String getEncodedString(String originalString) {
//        byte[] bytes = originalString.getBytes();
        //TODO need to figure out the logic to generate hash
//        String encodeString = Base64.getEncoder().encodeToString(bytes);
        // temp workaround
        return UUID.randomUUID().toString().replace("-", "").substring(0, 7);
    }

    public static String getShortUrlFromHashCode(String hashCode) {
        //TODO this url will be generated based on the service url
        return "http://localhost:8080/api/v1/" + hashCode;
    }


    public static String getSelectQueryForUrlSearch(String hashCode, String table) {
        return String.format("SELECT url FROM %s WHERE hash_code='%s';", table, hashCode);
    }

    public static String getInsertQueryForUrlEntry(String url, String hashCode, String table) {
        return String.format("INSERT INTO %s (hash_code, url, c_ts) VALUES ('%s', '%s', '%s')", table, hashCode, url, LocalDate.now());
    }
}
