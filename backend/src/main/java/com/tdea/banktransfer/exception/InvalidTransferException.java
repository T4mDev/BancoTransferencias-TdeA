package com.tdea.banktransfer.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when the transfer data breaks a business rule (same account, invalid amount...).
 */
public final class InvalidTransferException extends BusinessException {

    /**
     * Creates the exception with a custom message.
     *
     * @param message description of the violated rule
     */
    public InvalidTransferException(String message) {
        super(message, HttpStatus.BAD_REQUEST);
    }
}
