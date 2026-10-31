package com.tdea.banktransfer.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when trying to register an account number that already exists.
 */
public final class DuplicateAccountException extends BusinessException {

    /**
     * Creates the exception for the given account number.
     *
     * @param accountNumber duplicated account number
     */
    public DuplicateAccountException(String accountNumber) {
        super("Ya existe una cuenta con el número %s.".formatted(accountNumber), HttpStatus.CONFLICT);
    }
}
