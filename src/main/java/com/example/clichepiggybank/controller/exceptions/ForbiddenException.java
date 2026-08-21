package com.example.clichepiggybank.controller.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN) // Forces Spring to map this to 404
public class ForbiddenException extends RuntimeException {
    public ForbiddenException(String id) {
        super("Inquirer with id '" + id + "' did not have enough rights to perform the request.");
    }
}
