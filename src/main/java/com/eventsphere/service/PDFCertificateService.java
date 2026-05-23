package com.eventsphere.service;

import java.util.Optional;

public interface PDFCertificateService {
    byte[] generateCertificatePDF(Long registrationId, String certificateType);
    byte[] generateParticipationCertificate(String studentName, String eventName, String issueDate);
    byte[] generateWinnerCertificate(String studentName, String eventName, String issueDate);
    Optional<byte[]> getCertificatePDF(Long certificateId);
}
