package com.tdea.banktransfer.config;

import com.tdea.banktransfer.entity.Account;
import com.tdea.banktransfer.repository.AccountRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

/**
 * Loads demo accounts the first time the application starts with an empty database.
 */
@Component
@ConditionalOnProperty(name = "app.seed-data", havingValue = "true")
public class DataSeeder implements CommandLineRunner {

    private final AccountRepository accountRepository;

    /**
     * Creates the seeder.
     *
     * @param accountRepository account repository
     */
    public DataSeeder(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Inserts demo accounts when there are none.
     *
     * @param args command line arguments
     */
    @Override
    public void run(String... args) {
        if (accountRepository.count() > 0) {
            return;
        }
        accountRepository.saveAll(List.of(
                new Account("1001001001", "Diego Ramírez", new BigDecimal("2500000.00")),
                new Account("1001001002", "Laura Gómez", new BigDecimal("1800000.00")),
                new Account("1001001003", "Pedro Martínez", new BigDecimal("950000.00")),
                new Account("1001001004", "Valentina Ríos", new BigDecimal("3200000.00"))));
    }
}
