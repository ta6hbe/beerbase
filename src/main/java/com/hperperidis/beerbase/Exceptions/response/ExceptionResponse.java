package com.hperperidis.beerbase.Exceptions.response;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

/**
 * POJO used to return JSON formatted messages to API consumers, when runtime Exceptions have been thrown.
 * Used by the Controller Advice class to return JSON errors to the clients.
 *
 * @author C. Perperidis(tahbe@hotmail.com)
 */
@Getter
@Setter
public class ExceptionResponse {
    private int statusCode;
    private long timestamp;
    private String message;
    private String description;
    private Throwable cause;

    public ExceptionResponse(int statusCode, long timestamp, String message, String description, Throwable cause) {
        this.statusCode = statusCode;
        this.timestamp = timestamp;
        this.message = message;
        this.description = description;
        this.cause = cause;
    }




}
