package com.tdea.banktransfer.repository;

import com.tdea.banktransfer.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * Data access for {@link Transaction}.
 */
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Returns the full history, newest first.
     *
     * @return all transactions ordered by date descending
     */
    List<Transaction> findAllByOrderByCreatedAtDescIdDesc();

    /**
     * Returns the transactions where the account is source or target, newest first.
     *
     * @param sourceAccount account number as source
     * @param targetAccount account number as target
     * @return matching transactions
     */
    List<Transaction> findBySourceAccountOrTargetAccountOrderByCreatedAtDescIdDesc(
            String sourceAccount, String targetAccount);

    /**
     * Finds a transaction by its public reference.
     *
     * @param reference unique reference
     * @return the transaction, if it exists
     */
    Optional<Transaction> findByReference(String reference);
}
