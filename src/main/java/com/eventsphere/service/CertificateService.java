package com.eventsphere.service;

import com.eventsphere.dto.CertificateDTO;
import java.util.List;
import java.util.Optional;

public interface CertificateService {
    CertificateDTO generateCertificate(Long registrationId, String certificateType);
    Optional<CertificateDTO> getCertificateById(Long id);
    Optional<CertificateDTO> getCertificateByCertificateNumber(String certificateNumber);
    List<CertificateDTO> getCertificatesByRegistration(Long registrationId);
    List<CertificateDTO> getCertificatesByEvent(Long eventId, String certificateType);
    void generateCertificatesForEvent(Long eventId, String certificateType);
    boolean verifyCertificate(String certificateNumber);
    byte[] downloadCertificatePDF(Long certificateId);
}
