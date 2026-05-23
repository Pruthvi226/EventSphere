package com.eventsphere.service.impl;

import com.eventsphere.dto.CertificateDTO;
import com.eventsphere.entity.Certificate;
import com.eventsphere.entity.EventRegistration;
import com.eventsphere.entity.Attendance;
import com.eventsphere.repository.CertificateRepository;
import com.eventsphere.repository.EventRegistrationRepository;
import com.eventsphere.repository.AttendanceRepository;
import com.eventsphere.service.CertificateService;
import com.eventsphere.service.PDFCertificateService;
import com.eventsphere.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class CertificateServiceImpl implements CertificateService {
    
    @Autowired
    private CertificateRepository certificateRepository;
    
    @Autowired
    private EventRegistrationRepository registrationRepository;
    
    @Autowired
    private AttendanceRepository attendanceRepository;
    
    @Autowired
    private PDFCertificateService pdfCertificateService;
    
    @Override
    public CertificateDTO generateCertificate(Long registrationId, String certificateType) {
        EventRegistration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found"));
        
        // Check if certificate already exists
        List<Certificate> existing = certificateRepository.findByRegistration(registration);
        if (!existing.isEmpty()) {
            throw new IllegalArgumentException("Certificate already exists for this registration");
        }
        
        Certificate certificate = new Certificate();
        certificate.setCertificateNumber(generateCertificateNumber());
        certificate.setIssueDate(LocalDateTime.now());
        certificate.setCertificateType(Certificate.CertificateType.valueOf(certificateType.toUpperCase()));
        certificate.setRegistration(registration);
        
        Certificate saved = certificateRepository.save(certificate);
        return mapToDTO(saved);
    }
    
    @Override
    public Optional<CertificateDTO> getCertificateById(Long id) {
        return certificateRepository.findById(id).map(this::mapToDTO);
    }
    
    @Override
    public Optional<CertificateDTO> getCertificateByCertificateNumber(String certificateNumber) {
        return certificateRepository.findByCertificateNumber(certificateNumber).map(this::mapToDTO);
    }
    
    @Override
    public List<CertificateDTO> getCertificatesByRegistration(Long registrationId) {
        EventRegistration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found"));
        return certificateRepository.findByRegistration(registration).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<CertificateDTO> getCertificatesByEvent(Long eventId, String certificateType) {
        return certificateRepository.findByTypeAndEvent(
                Certificate.CertificateType.valueOf(certificateType.toUpperCase()), eventId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Override
    public void generateCertificatesForEvent(Long eventId, String certificateType) {
        List<EventRegistration> registrations = registrationRepository.findAll().stream()
                .filter(r -> r.getEvent().getId().equals(eventId))
                .collect(Collectors.toList());
        
        for (EventRegistration registration : registrations) {
            // Only for attended students
            Optional<Attendance> attendance = attendanceRepository.findByRegistration(registration);
            if (attendance.isPresent() && attendance.get().getAttended()) {
                try {
                    generateCertificate(registration.getId(), certificateType);
                } catch (IllegalArgumentException e) {
                    // Certificate already exists, skip
                }
            }
        }
    }
    
    @Override
    public boolean verifyCertificate(String certificateNumber) {
        return certificateRepository.findByCertificateNumber(certificateNumber).isPresent();
    }
    
    @Override
    public byte[] downloadCertificatePDF(Long certificateId) {
        Certificate certificate = certificateRepository.findById(certificateId)
                .orElseThrow(() -> new ResourceNotFoundException("Certificate not found"));
        return pdfCertificateService.generateCertificatePDF(certificate.getRegistration().getId(), 
                certificate.getCertificateType().toString());
    }
    
    private String generateCertificateNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String uuid = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        return "CERT-" + timestamp + "-" + uuid;
    }
    
    private CertificateDTO mapToDTO(Certificate certificate) {
        CertificateDTO dto = new CertificateDTO();
        dto.setId(certificate.getId());
        dto.setCertificateNumber(certificate.getCertificateNumber());
        dto.setIssueDate(certificate.getIssueDate());
        dto.setCertificateType(certificate.getCertificateType().toString());
        dto.setFilePath(certificate.getFilePath());
        dto.setRegistrationId(certificate.getRegistration().getId());
        dto.setStudentName(certificate.getRegistration().getStudent().getFullName());
        dto.setEventTitle(certificate.getRegistration().getEvent().getTitle());
        dto.setCreatedAt(certificate.getCreatedAt());
        return dto;
    }
}
