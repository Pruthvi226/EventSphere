package com.eventsphere.service.impl;

import com.eventsphere.entity.Certificate;
import com.eventsphere.entity.EventRegistration;
import com.eventsphere.repository.CertificateRepository;
import com.eventsphere.repository.EventRegistrationRepository;
import com.eventsphere.service.PDFCertificateService;
import com.eventsphere.exception.ResourceNotFoundException;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
public class PDFCertificateServiceImpl implements PDFCertificateService {
    
    @Autowired
    private CertificateRepository certificateRepository;
    
    @Autowired
    private EventRegistrationRepository registrationRepository;
    
    @Override
    public byte[] generateCertificatePDF(Long registrationId, String certificateType) {
        EventRegistration registration = registrationRepository.findById(registrationId)
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found"));
        
        String studentName = registration.getStudent().getFullName();
        String eventName = registration.getEvent().getTitle();
        String issueDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd MMMM yyyy"));
        
        if ("WINNER".equalsIgnoreCase(certificateType)) {
            return generateWinnerCertificate(studentName, eventName, issueDate);
        } else {
            return generateParticipationCertificate(studentName, eventName, issueDate);
        }
    }
    
    @Override
    public byte[] generateParticipationCertificate(String studentName, String eventName, String issueDate) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, baos);
            document.open();
            
            // Add border
            document.add(new Paragraph("\n"));
            
            // Title
            Font titleFont = new Font(Font.HELVETICA, 28, Font.BOLD);
            Paragraph title = new Paragraph("CERTIFICATE OF PARTICIPATION", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            
            document.add(new Paragraph("\n"));
            
            // Content
            Font contentFont = new Font(Font.HELVETICA, 14);
            Paragraph content = new Paragraph("This is to certify that", contentFont);
            content.setAlignment(Element.ALIGN_CENTER);
            document.add(content);
            
            document.add(new Paragraph("\n"));
            
            Font nameFont = new Font(Font.HELVETICA, 20, Font.BOLD);
            Paragraph name = new Paragraph(studentName, nameFont);
            name.setAlignment(Element.ALIGN_CENTER);
            document.add(name);
            
            document.add(new Paragraph("\n"));
            
            Paragraph participated = new Paragraph("has successfully participated in", contentFont);
            participated.setAlignment(Element.ALIGN_CENTER);
            document.add(participated);
            
            document.add(new Paragraph("\n"));
            
            Font eventFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Paragraph event = new Paragraph(eventName, eventFont);
            event.setAlignment(Element.ALIGN_CENTER);
            document.add(event);
            
            document.add(new Paragraph("\n\n"));
            
            Paragraph dateP = new Paragraph("Date: " + issueDate, contentFont);
            dateP.setAlignment(Element.ALIGN_CENTER);
            document.add(dateP);
            
            document.close();
            return baos.toByteArray();
        } catch (DocumentException e) {
            throw new RuntimeException("Failed to generate PDF certificate", e);
        }
    }
    
    @Override
    public byte[] generateWinnerCertificate(String studentName, String eventName, String issueDate) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, baos);
            document.open();
            
            document.add(new Paragraph("\n"));
            
            // Title
            Font titleFont = new Font(Font.HELVETICA, 28, Font.BOLD);
            Paragraph title = new Paragraph("CERTIFICATE OF ACHIEVEMENT", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            
            document.add(new Paragraph("\n"));
            
            // Content
            Font contentFont = new Font(Font.HELVETICA, 14);
            Paragraph content = new Paragraph("This is to certify that", contentFont);
            content.setAlignment(Element.ALIGN_CENTER);
            document.add(content);
            
            document.add(new Paragraph("\n"));
            
            Font nameFont = new Font(Font.HELVETICA, 20, Font.BOLD);
            Paragraph name = new Paragraph(studentName, nameFont);
            name.setAlignment(Element.ALIGN_CENTER);
            document.add(name);
            
            document.add(new Paragraph("\n"));
            
            Paragraph won = new Paragraph("has won first place in", contentFont);
            won.setAlignment(Element.ALIGN_CENTER);
            document.add(won);
            
            document.add(new Paragraph("\n"));
            
            Font eventFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Paragraph event = new Paragraph(eventName, eventFont);
            event.setAlignment(Element.ALIGN_CENTER);
            document.add(event);
            
            document.add(new Paragraph("\n\n"));
            
            Paragraph dateP = new Paragraph("Date: " + issueDate, contentFont);
            dateP.setAlignment(Element.ALIGN_CENTER);
            document.add(dateP);
            
            document.close();
            return baos.toByteArray();
        } catch (DocumentException e) {
            throw new RuntimeException("Failed to generate PDF certificate", e);
        }
    }
    
    @Override
    public Optional<byte[]> getCertificatePDF(Long certificateId) {
        Optional<Certificate> certificate = certificateRepository.findById(certificateId);
        if (certificate.isPresent() && certificate.get().getFilePath() != null) {
            // In a real implementation, read from file system
            return Optional.empty();
        }
        return Optional.empty();
    }
}
