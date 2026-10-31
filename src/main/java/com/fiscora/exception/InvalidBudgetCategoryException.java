package com.fiscora.exception;

public class InvalidBudgetCategoryException extends RuntimeException {

    public InvalidBudgetCategoryException(String message) {
        super(message);
    }
}