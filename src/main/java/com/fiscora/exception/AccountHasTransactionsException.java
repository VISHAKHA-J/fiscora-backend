package com.fiscora.exception;

public class AccountHasTransactionsException extends RuntimeException{

    public AccountHasTransactionsException(String message){
        super(message);
    }

}
