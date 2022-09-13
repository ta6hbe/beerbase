package com.hperperidis.beerbase.Exceptions;

public class BeerServiceException extends RuntimeException {

    public BeerServiceException(String message) {
        super(message);
    }

    public BeerServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public BeerServiceException(Throwable cause) {
        super(cause);
    }

}
