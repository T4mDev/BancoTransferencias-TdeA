package com.tdea.banktransfer.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Bank account that can send and receive money.
 */
@Entity
@Table(name = "accounts")
public class Account {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "account_number", nullable = false, unique = true, length = 20)
    private String accountNumber;

    @Column(name = "owner_name", nullable = false, length = 120)
    private String ownerName;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal balance;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Version
    private Long version;

    /**
     * Creates an empty account (required by JPA).
     */
    public Account() {
    }

    /**
     * Creates an account with its basic data.
     *
     * @param accountNumber unique account number
     * @param ownerName     name of the account holder
     * @param balance       initial balance
     */
    public Account(String accountNumber, String ownerName, BigDecimal balance) {
        this.accountNumber = accountNumber;
        this.ownerName = ownerName;
        this.balance = balance;
    }

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
    }

    /**
     * Subtracts the given amount from the balance.
     *
     * @param amount amount to withdraw
     */
    public void debit(BigDecimal amount) {
        balance = balance.subtract(amount);
    }

    /**
     * Adds the given amount to the balance.
     *
     * @param amount amount to deposit
     */
    public void credit(BigDecimal amount) {
        balance = balance.add(amount);
    }

    /** @return the account id */
    public Long getId() {
        return id;
    }

    /** @param id the account id */
    public void setId(Long id) {
        this.id = id;
    }

    /** @return the account number */
    public String getAccountNumber() {
        return accountNumber;
    }

    /** @param accountNumber the account number */
    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    /** @return the owner name */
    public String getOwnerName() {
        return ownerName;
    }

    /** @param ownerName the owner name */
    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    /** @return the current balance */
    public BigDecimal getBalance() {
        return balance;
    }

    /** @param balance the current balance */
    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    /** @return the creation timestamp */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /** @return the optimistic lock version */
    public Long getVersion() {
        return version;
    }
}
