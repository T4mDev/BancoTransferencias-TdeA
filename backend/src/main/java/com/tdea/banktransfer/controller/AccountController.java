package com.tdea.banktransfer.controller;

import com.tdea.banktransfer.dto.AccountRequest;
import com.tdea.banktransfer.dto.AccountResponse;
import com.tdea.banktransfer.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST endpoints for bank accounts.
 */
@RestController
@RequestMapping("/api/cuentas")
@Tag(name = "Cuentas", description = "Gestión de cuentas bancarias")
public class AccountController {

    private final AccountService accountService;

    /**
     * Creates the controller.
     *
     * @param accountService account service
     */
    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    /**
     * Lists every account.
     *
     * @return all accounts (200)
     */
    @GetMapping
    @Operation(summary = "Listar todas las cuentas")
    public List<AccountResponse> findAll() {
        return accountService.findAll();
    }

    /**
     * Gets one account by its number.
     *
     * @param accountNumber account number
     * @return the account (200) or 404 when it does not exist
     */
    @GetMapping("/{accountNumber}")
    @Operation(summary = "Consultar una cuenta por su número")
    public AccountResponse findByNumber(@PathVariable String accountNumber) {
        return accountService.findByNumber(accountNumber);
    }

    /**
     * Opens a new account.
     *
     * @param request account data
     * @return the created account (201) or 409 when the number already exists
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Crear una cuenta")
    public AccountResponse create(@Valid @RequestBody AccountRequest request) {
        return accountService.create(request);
    }
}
