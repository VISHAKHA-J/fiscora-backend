package com.fiscora.exception;

public class CategoryNameAlreadyExistsException extends RuntimeException {

    public CategoryNameAlreadyExistsException(String message) {
        super(message);
    }
}