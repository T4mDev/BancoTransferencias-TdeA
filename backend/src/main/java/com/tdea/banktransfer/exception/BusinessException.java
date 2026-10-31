package com.tdea.banktransfer.exception;

import org.springframework.http.HttpStatus;

/**
 * Base type for every business rule violation of the bank system.
 * Each subtype defines the HTTP status returned to the client.
 */
public abstract sealed class BusinessException extends RuntimeException
        permits AccountNotFoundException, DuplicateAccountException,
                InsufficientFundsException, InvalidTransferException {

    private final HttpStatus status;

    /**
     * Creates a business exception.
     *
     * @param message human readable message
     * @param status  HTTP status associated with the error
     */
    protected BusinessException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    /**
     * Returns the HTTP status associated with this error.
     *
     * @return the HTTP status
     */
    public HttpStatus getStatus() {
        return status;
    }
}
