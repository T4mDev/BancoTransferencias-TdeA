package com.tdea.banktransfer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Record of a money transfer between two accounts (transaction history).
 * Account numbers are stored as plain values so the history is kept even for
 * rejected transfers that point to non-existent accounts.
 */
@Entity
@Table(name = "transactions")
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 36)
    private String reference;

    @Column(name = "source_account", nullable = false, length = 20)
    private String sourceAccount;

    @Column(name = "target_account", nullable = false, length = 20)
    private String targetAccount;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    @Column(length = 200)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TransactionStatus status;

    @Column(name = "failure_reason", length = 255)
    private String failureReason;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * Creates an empty transaction (required by JPA).
     */
    public Transaction() {
    }

    /**
     * Creates a transaction with its main data.
     *
     * @param sourceAccount source account number
     * @param targetAccount target account number
     * @param amount        transferred amount
     * @param description   optional description
     * @param status        final status
     */
    public Transaction(String sourceAccount, String targetAccount, BigDecimal amount,
                       String description, TransactionStatus status) {
        this.sourceAccount = sourceAccount;
        this.targetAccount = targetAccount;
        this.amount = amount;
        this.description = description;
        this.status = status;
    }

    @PrePersist
    void onCreate() {
        if (reference == null) {
            reference = UUID.randomUUID().toString();
        }
        createdAt = LocalDateTime.now();
    }

    /** @return the transaction id */
    public Long getId() {
        return id;
    }

    /** @return the unique public reference */
    public String getReference() {
        return reference;
    }

    /** @param reference the unique public reference */
    public void setReference(String reference) {
        this.reference = reference;
    }

    /** @return the source account number */
    public String getSourceAccount() {
        return sourceAccount;
    }

    /** @param sourceAccount the source account number */
    public void setSourceAccount(String sourceAccount) {
        this.sourceAccount = sourceAccount;
    }

    /** @return the target account number */
    public String getTargetAccount() {
        return targetAccount;
    }

    /** @param targetAccount the target account number */
    public void setTargetAccount(String targetAccount) {
        this.targetAccount = targetAccount;
    }

    /** @return the transferred amount */
    public BigDecimal getAmount() {
        return amount;
    }

    /** @param amount the transferred amount */
    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    /** @return the description */
    public String getDescription() {
        return description;
    }

    /** @param description the description */
    public void setDescription(String description) {
        this.description = description;
    }

    /** @return the final status */
    public TransactionStatus getStatus() {
        return status;
    }

    /** @param status the final status */
    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    /** @return the reason the transfer was rejected, if any */
    public String getFailureReason() {
        return failureReason;
    }

    /** @param failureReason the reason the transfer was rejected */
    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    /** @return the creation timestamp */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
