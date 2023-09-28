package com.example.translator.Exceptions;

public class NoResponseException extends Exception {
    public NoResponseException(String responseId) {
        super("Could not get Response for Id: " + responseId);
    }
}
