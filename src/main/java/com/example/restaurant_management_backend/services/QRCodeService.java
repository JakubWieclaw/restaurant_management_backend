package com.example.restaurant_management_backend.services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Service
public class QRCodeService {

    private static final Logger logger = LoggerFactory.getLogger(QRCodeService.class);

    public byte[] generateQRCodeImage(String barcodeText) {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        BitMatrix bitMatrix;
        try {
            bitMatrix = qrCodeWriter.encode(barcodeText, BarcodeFormat.QR_CODE, 200, 200, hints);
        } catch (WriterException e) {
            logger.error("Error while generating QR code", e);
            throw new IllegalStateException(e);
        }
        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(bitMatrix);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        try {
            ImageIO.write(bufferedImage, "png", pngOutputStream);
        } catch (IOException e) {
            logger.error("Error while writing QR code to output stream", e);
            throw new IllegalStateException(e);
        }
        return pngOutputStream.toByteArray();
    }
}
