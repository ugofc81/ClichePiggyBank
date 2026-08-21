package com.example.clichepiggybank.controller.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND) // Forces Spring to map this to 404
public class SanctionNotFoundException extends RuntimeException {
    public SanctionNotFoundException(String id) {
        super("Sanction with id '" + id + "' was not found.");
    }
}
