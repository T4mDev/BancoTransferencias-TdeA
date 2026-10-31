package com.tdea.banktransfer.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Data required to open a new account.
 *
 * @param accountNumber  unique number, 6 to 20 digits
 * @param ownerName      account holder name
 * @param initialBalance opening balance (zero or greater)
 */
public record AccountRequest(
        @NotBlank(message = "el número de cuenta es obligatorio") @Pattern(regexp = "\\d{6,20}", message = "debe tener entre 6 y 20 dígitos")
        String accountNumber,
        @NotBlank(message = "el titular es obligatorio") @Size(max = 120, message = "máximo 120 caracteres")
        String ownerName,
        @NotNull(message = "el saldo inicial es obligatorio")
        @DecimalMin(value = "0.00", message = "el saldo no puede ser negativo")
        @Digits(integer = 17, fraction = 2, message = "máximo 2 decimales")
        BigDecimal initialBalance) {
}
