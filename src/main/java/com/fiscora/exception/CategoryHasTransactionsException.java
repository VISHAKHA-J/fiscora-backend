package com.fiscora.exception;

public class CategoryHasTransactionsException extends RuntimeException {

    public CategoryHasTransactionsException(String message) {
        super(message);
    }
}