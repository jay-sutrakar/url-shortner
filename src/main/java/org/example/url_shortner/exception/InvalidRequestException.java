package org.example.url_shortner.exception;

public class InvalidRequestException extends Exception {
    public InvalidRequestException(String emessage) {
        super(emessage);
    }
}
