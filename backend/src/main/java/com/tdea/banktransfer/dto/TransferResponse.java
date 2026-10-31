package com.tdea.banktransfer.dto;

/**
 * Result of a successful transfer: the transaction plus its QR code.
 *
 * @param transaction the created transaction
 * @param qrCode      QR code image as a PNG data URI (data:image/png;base64,...)
 * @param qrContent   plain text encoded inside the QR code
 */
public record TransferResponse(TransactionResponse transaction, String qrCode, String qrContent) {
}
