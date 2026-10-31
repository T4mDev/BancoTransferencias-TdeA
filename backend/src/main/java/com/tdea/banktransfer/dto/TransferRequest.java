package com.tdea.banktransfer.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Data needed to transfer money between two accounts.
 *
 * @param sourceAccount account number that sends the money
 * @param targetAccount account number that receives the money
 * @param amount        amount to transfer (greater than zero)
 * @param description   optional description
 */
public record TransferRequest(
        @NotBlank(message = "la cuenta origen es obligatoria") String sourceAccount,
        @NotBlank(message = "la cuenta destino es obligatoria") String targetAccount,
        @NotNull(message = "el monto es obligatorio")
        @DecimalMin(value = "0.01", message = "el monto debe ser mayor que cero")
        @Digits(integer = 17, fraction = 2, message = "máximo 2 decimales") BigDecimal amount,
        @Size(max = 200, message = "máximo 200 caracteres") String description) {
}
