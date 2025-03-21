package com.example.LoanMicro;

import jakarta.persistence.EntityNotFoundException;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandler {

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @org.springframework.web.bind.annotation.ExceptionHandler
    public String CustomEntityNotFoundException(EntityNotFoundException e) {
        return e.getMessage();
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @org.springframework.web.bind.annotation.ExceptionHandler
    public String CustomBadRequestException(BadRequestException e) {
        return e.getMessage();
    }

}
