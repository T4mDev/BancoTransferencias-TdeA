package com.tdea.banktransfer.entity;

/**
 * Final status of a transfer transaction.
 */
public enum TransactionStatus {
    /** The money was moved successfully. */
    COMPLETED,
    /** The transfer was rejected by a business rule. */
    REJECTED
}
