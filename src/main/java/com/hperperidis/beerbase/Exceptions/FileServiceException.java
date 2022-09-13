package com.hperperidis.beerbase.Exceptions;

import java.io.IOException;

public class FileServiceException extends RuntimeException {

    public FileServiceException(String message) {
        super(message);
    }
    public FileServiceException(String message, Throwable exception) {
        super(message, exception);
    }

    public FileServiceException(Throwable exception) {
        super(exception);
    }
}
