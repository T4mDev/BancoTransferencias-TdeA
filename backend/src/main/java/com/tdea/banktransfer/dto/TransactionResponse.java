package com.tdea.banktransfer.dto;

import com.tdea.banktransfer.entity.Transaction;
import com.tdea.banktransfer.entity.TransactionStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Transaction data returned by the API (history entries).
 *
 * @param id            internal id
 * @param reference     unique public reference
 * @param sourceAccount source account number
 * @param targetAccount target account number
 * @param amount        transferred amount
 * @param description   description
 * @param status        final status
 * @param failureReason reason when rejected
 * @param createdAt     transaction date
 */
public record TransactionResponse(Long id, String reference, String sourceAccount, String targetAccount,
                                  BigDecimal amount, String description, TransactionStatus status,
                                  String failureReason, LocalDateTime createdAt) {

    /**
     * Maps an entity to its response representation.
     *
     * @param tx the entity
     * @return the response record
     */
    public static TransactionResponse from(Transaction tx) {
        return new TransactionResponse(tx.getId(), tx.getReference(), tx.getSourceAccount(),
                tx.getTargetAccount(), tx.getAmount(), tx.getDescription(), tx.getStatus(),
                tx.getFailureReason(), tx.getCreatedAt());
    }
}
