package com.tdea.banktransfer.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when the source account does not have enough balance.
 */
public final class InsufficientFundsException extends BusinessException {

    /**
     * Creates the exception for the given account number.
     *
     * @param accountNumber account without enough funds
     */
    public InsufficientFundsException(String accountNumber) {
        super("Saldo insuficiente en la cuenta %s.".formatted(accountNumber), HttpStatus.UNPROCESSABLE_CONTENT);
    }
}
