package com.tdea.banktransfer;

import com.tdea.banktransfer.dto.AccountRequest;
import com.tdea.banktransfer.dto.TransactionResponse;
import com.tdea.banktransfer.dto.TransferRequest;
import com.tdea.banktransfer.dto.TransferResponse;
import com.tdea.banktransfer.entity.TransactionStatus;
import com.tdea.banktransfer.exception.AccountNotFoundException;
import com.tdea.banktransfer.exception.InsufficientFundsException;
import com.tdea.banktransfer.exception.InvalidTransferException;
import com.tdea.banktransfer.repository.AccountRepository;
import com.tdea.banktransfer.repository.TransactionRepository;
import com.tdea.banktransfer.service.AccountService;
import com.tdea.banktransfer.service.TransferService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class TransferServiceTest {

    @Autowired
    private AccountService accountService;
    @Autowired
    private TransferService transferService;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private TransactionRepository transactionRepository;

    @BeforeEach
    void setUp() {
        transactionRepository.deleteAll();
        accountRepository.deleteAll();
        accountService.create(new AccountRequest("111111", "Ana", new BigDecimal("1000.00")));
        accountService.create(new AccountRequest("222222", "Beto", new BigDecimal("500.00")));
    }

    @Test
    void transferMovesMoneyAndReturnsQr() {
        TransferResponse result = transferService.transfer(
                new TransferRequest("111111", "222222", new BigDecimal("300.00"), "Pago"));

        assertEquals(0, new BigDecimal("700.00").compareTo(accountService.findByNumber("111111").balance()));
        assertEquals(0, new BigDecimal("800.00").compareTo(accountService.findByNumber("222222").balance()));
        assertEquals(TransactionStatus.COMPLETED, result.transaction().status());
        assertTrue(result.qrCode().startsWith("data:image/png;base64,"));
        assertTrue(result.qrContent().contains(result.transaction().reference()));
    }

    @Test
    void insufficientFundsIsRejectedAndRecordedInHistory() {
        assertThrows(InsufficientFundsException.class, () -> transferService.transfer(
                new TransferRequest("222222", "111111", new BigDecimal("900.00"), null)));

        assertEquals(0, new BigDecimal("500.00").compareTo(accountService.findByNumber("222222").balance()));
        List<TransactionResponse> history = transferService.findHistory("222222");
        assertEquals(1, history.size());
        assertEquals(TransactionStatus.REJECTED, history.getFirst().status());
    }

    @Test
    void sameAccountAndMissingAccountAreRejected() {
        assertThrows(InvalidTransferException.class, () -> transferService.transfer(
                new TransferRequest("111111", "111111", BigDecimal.TEN, null)));
        assertThrows(AccountNotFoundException.class, () -> transferService.transfer(
                new TransferRequest("111111", "999999", BigDecimal.TEN, null)));
        assertEquals(0, new BigDecimal("1000.00").compareTo(accountService.findByNumber("111111").balance()));
    }
}
