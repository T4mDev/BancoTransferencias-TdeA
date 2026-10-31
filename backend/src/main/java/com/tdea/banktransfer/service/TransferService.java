package com.tdea.banktransfer.service;

import com.tdea.banktransfer.dto.TransactionResponse;
import com.tdea.banktransfer.dto.TransferRequest;
import com.tdea.banktransfer.dto.TransferResponse;
import com.tdea.banktransfer.entity.Account;
import com.tdea.banktransfer.entity.Transaction;
import com.tdea.banktransfer.entity.TransactionStatus;
import com.tdea.banktransfer.exception.AccountNotFoundException;
import com.tdea.banktransfer.exception.BusinessException;
import com.tdea.banktransfer.exception.InsufficientFundsException;
import com.tdea.banktransfer.exception.InvalidTransferException;
import com.tdea.banktransfer.repository.AccountRepository;
import com.tdea.banktransfer.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.math.BigDecimal;
import java.util.List;

/**
 * Business logic for money transfers and transaction history.
 *
 * <p>The money movement runs inside a database transaction with pessimistic row
 * locks. When a business rule fails, that transaction is rolled back and the
 * rejected attempt is saved in a separate transaction, so it still appears in
 * the history.</p>
 */
@Service
public class TransferService {

    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;
    private final QrCodeService qrCodeService;
    private final TransactionTemplate transactionTemplate;

    /**
     * Creates the service.
     *
     * @param accountRepository     account repository
     * @param transactionRepository transaction repository
     * @param qrCodeService         QR code generator
     * @param transactionManager    transaction manager used for programmatic transactions
     */
    public TransferService(AccountRepository accountRepository, TransactionRepository transactionRepository,
                           QrCodeService qrCodeService, PlatformTransactionManager transactionManager) {
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;
        this.qrCodeService = qrCodeService;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    /**
     * Transfers money from one account to another.
     *
     * @param request transfer data
     * @return the completed transaction and its QR code
     * @throws BusinessException when the accounts do not exist, are the same,
     *                           or the source has insufficient funds
     */
    public TransferResponse transfer(TransferRequest request) {
        TransactionResponse completed;
        try {
            completed = transactionTemplate.execute(status -> executeTransfer(request));
        } catch (BusinessException ex) {
            saveRejected(request, ex.getMessage());
            throw ex;
        }
        return new TransferResponse(completed, qrCodeService.generateDataUri(completed),
                qrCodeService.buildContent(completed));
    }

    /**
     * Returns the transaction history, optionally filtered by account number.
     *
     * @param accountNumber account number to filter by, or {@code null} for all
     * @return transactions ordered from newest to oldest
     */
    @Transactional(readOnly = true)
    public List<TransactionResponse> findHistory(String accountNumber) {
        List<Transaction> transactions = (accountNumber == null || accountNumber.isBlank())
                ? transactionRepository.findAllByOrderByCreatedAtDescIdDesc()
                : transactionRepository.findBySourceAccountOrTargetAccountOrderByCreatedAtDescIdDesc(
                        accountNumber, accountNumber);
        return transactions.stream().map(TransactionResponse::from).toList();
    }

    /**
     * Finds a transaction by its reference.
     *
     * @param reference unique reference
     * @return the transaction
     * @throws InvalidTransferException when the reference does not exist
     */
    @Transactional(readOnly = true)
    public TransactionResponse findByReference(String reference) {
        return transactionRepository.findByReference(reference)
                .map(TransactionResponse::from)
                .orElseThrow(() -> new InvalidTransferException("No existe la transacción " + reference));
    }

    private TransactionResponse executeTransfer(TransferRequest request) {
        String source = request.sourceAccount().trim();
        String target = request.targetAccount().trim();
        BigDecimal amount = request.amount();

        if (source.equals(target)) {
            throw new InvalidTransferException("La cuenta origen y destino no pueden ser la misma.");
        }
        if (amount.signum() <= 0) {
            throw new InvalidTransferException("El monto debe ser mayor que cero.");
        }

        // Lock rows always in the same order to avoid deadlocks between opposite transfers.
        boolean sourceFirst = source.compareTo(target) < 0;
        Account first = lock(sourceFirst ? source : target);
        Account second = lock(sourceFirst ? target : source);
        Account from = sourceFirst ? first : second;
        Account to = sourceFirst ? second : first;

        if (from.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException(source);
        }

        from.debit(amount);
        to.credit(amount);

        Transaction tx = new Transaction(source, target, amount, request.description(),
                TransactionStatus.COMPLETED);
        return TransactionResponse.from(transactionRepository.save(tx));
    }

    private Account lock(String accountNumber) {
        return accountRepository.findByAccountNumberForUpdate(accountNumber)
                .orElseThrow(() -> new AccountNotFoundException(accountNumber));
    }

    private void saveRejected(TransferRequest request, String reason) {
        transactionTemplate.executeWithoutResult(status -> {
            Transaction tx = new Transaction(request.sourceAccount().trim(), request.targetAccount().trim(),
                    request.amount(), request.description(), TransactionStatus.REJECTED);
            tx.setFailureReason(reason);
            transactionRepository.save(tx);
        });
    }
}
