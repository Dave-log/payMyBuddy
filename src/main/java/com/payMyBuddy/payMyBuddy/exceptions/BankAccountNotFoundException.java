package com.payMyBuddy.payMyBuddy.exceptions;

public class BankAccountNotFoundException extends RuntimeException {
    public BankAccountNotFoundException(String message) { super(message); }
}
