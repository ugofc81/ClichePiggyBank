package com.example.clichepiggybank.controller.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND) // Forces Spring to map this to 404
public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String id) {
        super("User with id '" + id + "' was not found.");
    }
}
