package com.tracker.talent_screening.service;


import com.tracker.talent_screening.helper.PDFExtractor;
import com.tracker.talent_screening.model.Applicant;
import com.tracker.talent_screening.model.ApplicantDocument;
import com.tracker.talent_screening.repository.ApplicantDocumentRepository;
import com.tracker.talent_screening.repository.ApplicantRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;

@Service
@RequiredArgsConstructor
public class EvaluationService {

    private final PDFExtractor pdfExtractor;


    private final ApplicantRepository applicantRepository;


    private final  ApplicantDocumentRepository applicantDocumentRepository;

    @Transactional(readOnly = true)
    public String prepForAutoEvaluation(String processId){

        var applicant = applicantRepository
                               .findByProcessInstanceId(processId)
                                .orElseThrow(() ->
                                        new IllegalArgumentException("No applicant found for processId=" + processId));
        var applicantDocument = applicantDocumentRepository
                                .findFirstByApplicant_IdOrderByUploadedAtDesc(applicant.getId())
                                .orElseThrow(() ->
                                        new IllegalArgumentException("No document found for applicantId=" + applicant.getId()));
        
                        var file = new File(applicantDocument.getFilePath());
                if (!file.exists() || !file.isFile()) {
                        throw new IllegalStateException("File not found at path: "+  file.getAbsolutePath());
                    }
                return pdfExtractor.extractTextFromPDF(file);
    }


}
