package com.tdea.banktransfer.service;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.tdea.banktransfer.dto.TransactionResponse;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Locale;
import java.util.Map;

/**
 * Generates QR codes with the basic data of a transaction.
 */
@Service
public class QrCodeService {

    private static final int SIZE = 300;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final Locale COLOMBIA = Locale.of("es", "CO");

    /**
     * Builds the plain text encoded inside the QR code.
     *
     * @param tx the transaction
     * @return multi-line text with reference, accounts, amount, date and status
     */
    public String buildContent(TransactionResponse tx) {
        String amount = NumberFormat.getCurrencyInstance(COLOMBIA).format(tx.amount());
        return """
                BANCO TdeA - Comprobante de transferencia
                Referencia: %s
                Cuenta origen: %s
                Cuenta destino: %s
                Monto: %s
                Fecha: %s
                Estado: %s""".formatted(tx.reference(), tx.sourceAccount(), tx.targetAccount(),
                amount, tx.createdAt().format(DATE_FORMAT), tx.status());
    }

    /**
     * Renders the QR code of a transaction as PNG bytes.
     *
     * @param tx the transaction
     * @return PNG image bytes
     */
    public byte[] generatePng(TransactionResponse tx) {
        try {
            BitMatrix matrix = new QRCodeWriter().encode(buildContent(tx), BarcodeFormat.QR_CODE, SIZE, SIZE,
                    Map.of(EncodeHintType.CHARACTER_SET, "UTF-8",
                            EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M,
                            EncodeHintType.MARGIN, 1));
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "PNG", out);
            return out.toByteArray();
        } catch (WriterException e) {
            throw new IllegalStateException("No fue posible generar el código QR", e);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /**
     * Renders the QR code of a transaction as a PNG data URI.
     *
     * @param tx the transaction
     * @return data URI ready to be used in an HTML img tag
     */
    public String generateDataUri(TransactionResponse tx) {
        return "data:image/png;base64," + Base64.getEncoder().encodeToString(generatePng(tx));
    }
}
