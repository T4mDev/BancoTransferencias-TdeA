package com.tdea.banktransfer.repository;

import com.tdea.banktransfer.entity.Account;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Data access for {@link Account}.
 */
public interface AccountRepository extends JpaRepository<Account, Long> {

    /**
     * Finds an account by its number.
     *
     * @param accountNumber account number
     * @return the account, if it exists
     */
    Optional<Account> findByAccountNumber(String accountNumber);

    /**
     * Finds an account by its number and locks the row for update, so two
     * concurrent transfers cannot read a stale balance.
     *
     * @param accountNumber account number
     * @return the locked account, if it exists
     */
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Account a where a.accountNumber = :accountNumber")
    Optional<Account> findByAccountNumberForUpdate(@Param("accountNumber") String accountNumber);

    /**
     * Checks whether an account number is already registered.
     *
     * @param accountNumber account number
     * @return {@code true} when it exists
     */
    boolean existsByAccountNumber(String accountNumber);
}
