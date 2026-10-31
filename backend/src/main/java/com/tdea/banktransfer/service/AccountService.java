package com.tdea.banktransfer.service;

import com.tdea.banktransfer.dto.AccountRequest;
import com.tdea.banktransfer.dto.AccountResponse;
import com.tdea.banktransfer.entity.Account;
import com.tdea.banktransfer.exception.AccountNotFoundException;
import com.tdea.banktransfer.exception.DuplicateAccountException;
import com.tdea.banktransfer.repository.AccountRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Business logic for bank accounts.
 */
@Service
public class AccountService {

    private final AccountRepository accountRepository;

    /**
     * Creates the service.
     *
     * @param accountRepository account repository
     */
    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Lists every account ordered by id.
     *
     * @return all accounts
     */
    @Transactional(readOnly = true)
    public List<AccountResponse> findAll() {
        return accountRepository.findAll().stream()
                .sorted((a, b) -> a.getId().compareTo(b.getId()))
                .map(AccountResponse::from)
                .toList();
    }

    /**
     * Finds an account by its number.
     *
     * @param accountNumber account number
     * @return the account
     * @throws AccountNotFoundException when it does not exist
     */
    @Transactional(readOnly = true)
    public AccountResponse findByNumber(String accountNumber) {
        return accountRepository.findByAccountNumber(accountNumber)
                .map(AccountResponse::from)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
    }

    /**
     * Opens a new account.
     *
     * @param request account data
     * @return the created account
     * @throws DuplicateAccountException when the number is already registered
     */
    @Transactional
    public AccountResponse create(AccountRequest request) {
        if (accountRepository.existsByAccountNumber(request.accountNumber())) {
            throw new DuplicateAccountException(request.accountNumber());
        }
        Account account = new Account(request.accountNumber(), request.ownerName().trim(),
                request.initialBalance());
        return AccountResponse.from(accountRepository.save(account));
    }
}
