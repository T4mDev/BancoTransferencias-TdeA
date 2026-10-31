package com.tdea.banktransfer.dto;

import com.tdea.banktransfer.entity.Account;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Account data returned by the API.
 *
 * @param id            internal id
 * @param accountNumber account number
 * @param ownerName     account holder name
 * @param balance       current balance
 * @param createdAt     creation date
 */
public record AccountResponse(Long id, String accountNumber, String ownerName,
                              BigDecimal balance, LocalDateTime createdAt) {

    /**
     * Maps an entity to its response representation.
     *
     * @param account the entity
     * @return the response record
     */
    public static AccountResponse from(Account account) {
        return new AccountResponse(account.getId(), account.getAccountNumber(),
                account.getOwnerName(), account.getBalance(), account.getCreatedAt());
    }
}
