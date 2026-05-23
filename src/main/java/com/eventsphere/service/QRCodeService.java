package com.eventsphere.service;

import java.awt.image.BufferedImage;
import java.util.Optional;

public interface QRCodeService {
    BufferedImage generateQRCode(String data, int width, int height);
    String generateRegistrationQRCode(Long registrationId);
    Optional<String> decodeQRCode(BufferedImage qrImage);
}
