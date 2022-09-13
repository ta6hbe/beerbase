package com.hperperidis.beerbase.Exceptions;

import lombok.Getter;

@Getter
public class RemoteServiceException extends RuntimeException {

    private final String remotePath;

    public RemoteServiceException(String message, String remoteServicePath) {
        super(message);
        this.remotePath = remoteServicePath;
    }

    public RemoteServiceException(String message, String remoteServicePath, Throwable cause) {
        super(message, cause);
        this.remotePath = remoteServicePath;
    }
}
