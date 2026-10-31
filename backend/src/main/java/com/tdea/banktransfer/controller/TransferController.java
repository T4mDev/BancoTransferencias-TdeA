package com.tdea.banktransfer.controller;

import com.tdea.banktransfer.dto.TransactionResponse;
import com.tdea.banktransfer.dto.TransferRequest;
import com.tdea.banktransfer.dto.TransferResponse;
import com.tdea.banktransfer.service.QrCodeService;
import com.tdea.banktransfer.service.TransferService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * REST endpoints for transfers, transaction history and QR codes.
 */
@RestController
@RequestMapping("/api")
@Tag(name = "Transferencias", description = "Traslados entre cuentas e histórico de transacciones")
public class TransferController {

    private final TransferService transferService;
    private final QrCodeService qrCodeService;

    /**
     * Creates the controller.
     *
     * @param transferService transfer service
     * @param qrCodeService   QR code generator
     */
    public TransferController(TransferService transferService, QrCodeService qrCodeService) {
        this.transferService = transferService;
        this.qrCodeService = qrCodeService;
    }

    /**
     * Transfers money between two accounts.
     *
     * @param request transfer data
     * @return the transaction and its QR code (201); 400, 404 or 422 when a rule fails
     */
    @PostMapping("/transferencias")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Realizar una transferencia",
            description = "Devuelve la transacción y un código QR (data URI PNG) con sus datos básicos")
    public TransferResponse transfer(@Valid @RequestBody TransferRequest request) {
        return transferService.transfer(request);
    }

    /**
     * Returns the transaction history.
     *
     * @param cuenta optional account number to filter by
     * @return transactions from newest to oldest (200)
     */
    @GetMapping("/transacciones")
    @Operation(summary = "Histórico de transacciones", description = "Filtro opcional por número de cuenta")
    public List<TransactionResponse> history(@RequestParam(required = false) String cuenta) {
        return transferService.findHistory(cuenta);
    }

    /**
     * Returns the QR code image of a transaction.
     *
     * @param reference transaction reference
     * @return PNG image (200) or 400 when the reference does not exist
     */
    @GetMapping(value = "/transacciones/{reference}/qr", produces = MediaType.IMAGE_PNG_VALUE)
    @Operation(summary = "Código QR de una transacción (PNG)")
    public byte[] qr(@PathVariable String reference) {
        return qrCodeService.generatePng(transferService.findByReference(reference));
    }
}
