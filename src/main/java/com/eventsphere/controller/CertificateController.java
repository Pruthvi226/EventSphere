package com.eventsphere.controller;

import com.eventsphere.dto.CertificateDTO;
import com.eventsphere.service.CertificateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/certificates")
public class CertificateController {
    @Autowired
    private CertificateService certificateService;

    @GetMapping("/{id}/download")
    public ResponseEntity<byte[]> downloadCertificate(@PathVariable Long id) {
        CertificateDTO certificate = certificateService.getCertificateById(id)
                .orElseThrow(() -> new RuntimeException("Certificate not found"));
        byte[] pdf = certificateService.downloadCertificatePDF(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment()
                .filename(certificate.getCertificateNumber() + ".pdf")
                .build());

        return ResponseEntity.ok()
                .headers(headers)
                .body(pdf);
    }
}
