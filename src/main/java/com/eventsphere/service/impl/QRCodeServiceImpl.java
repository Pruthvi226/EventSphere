package com.eventsphere.service.impl;

import com.eventsphere.service.QRCodeService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import org.springframework.stereotype.Service;
import java.awt.image.BufferedImage;
import java.util.Optional;

@Service
public class QRCodeServiceImpl implements QRCodeService {
    
    private static final int DEFAULT_WIDTH = 300;
    private static final int DEFAULT_HEIGHT = 300;
    
    @Override
    public BufferedImage generateQRCode(String data, int width, int height) {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, width, height);
            return MatrixToImageWriter.toBufferedImage(bitMatrix);
        } catch (WriterException e) {
            throw new RuntimeException("Failed to generate QR code", e);
        }
    }
    
    @Override
    public String generateRegistrationQRCode(Long registrationId) {
        return "REG-" + registrationId;
    }
    
    @Override
    public Optional<String> decodeQRCode(BufferedImage qrImage) {
        // Decoding QR codes is more complex and requires additional library
        // This is a placeholder implementation
        return Optional.empty();
    }
}
