package com.hperperidis.beerbase.controller;

import java.time.Clock;

import com.hperperidis.beerbase.Exceptions.RemoteServiceException;
import com.hperperidis.beerbase.Exceptions.response.ExceptionResponse;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Controller Advice class handling RuntimeExceptions thrown by our controller.
 * Makes sure exception messages are communicated in a REST-Full manner to our API consumers.
 *
 * @author C. Perperidis(ta6hbe@hotmail.com)
 */
@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(value = { RemoteServiceException.class })
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public ExceptionResponse RemoteServiceException(RemoteServiceException ex) {
        ExceptionResponse response = new ExceptionResponse(
                HttpStatus.NOT_FOUND.value(),
                Clock.systemUTC().millis(),
                ex.getMessage(),
                String.format("An External Service Exception occurred while attempting to retrieve data from remote URL: %s, with error: ",
                              ex.getRemotePath(), ex.getMessage()),
                ex.getCause());
        return response;
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ExceptionResponse remoteServiceException(Exception ex) {
        ExceptionResponse response = new ExceptionResponse(
                HttpStatus.BAD_REQUEST.value(),
                Clock.systemUTC().millis(),
                ex.getMessage(),
                String.format("An error occurred while processing your request with message: %s", ex.getMessage()),
                ex.getCause());

        return response;
    }
}
