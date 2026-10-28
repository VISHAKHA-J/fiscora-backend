package com.fiscora.exception;

public class AccountNameAlreadyExistsException extends RuntimeException {

    public AccountNameAlreadyExistsException(String message) {
        super(message);
    }
}