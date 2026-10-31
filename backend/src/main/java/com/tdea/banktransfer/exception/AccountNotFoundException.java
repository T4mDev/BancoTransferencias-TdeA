package com.tdea.banktransfer.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when an account number does not exist.
 */
public final class AccountNotFoundException extends BusinessException {

    /**
     * Creates the exception for the given account number.
     *
     * @param accountNumber account number that was not found
     */
    public AccountNotFoundException(String accountNumber) {
        super("La cuenta %s no existe.".formatted(accountNumber), HttpStatus.NOT_FOUND);
    }
}
