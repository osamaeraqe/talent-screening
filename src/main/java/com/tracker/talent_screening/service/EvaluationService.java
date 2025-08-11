package com.tracker.talent_screening.service;


import com.tracker.talent_screening.helper.PDFExtractor;
import com.tracker.talent_screening.model.Applicant;
import com.tracker.talent_screening.model.ApplicantDocument;
import com.tracker.talent_screening.repository.ApplicantDocumentRepository;
import com.tracker.talent_screening.repository.ApplicantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service
public class EvaluationService {

    private PDFExtractor pdfExtractor;

    @Autowired
    private ApplicantRepository applicantRepository;

    @Autowired
    private ApplicantDocumentRepository applicantDocumentRepository;

    /**
     * Extracts and returns text from the applicant's PDF associated with the given process instance id.
     *
     * Looks up the Applicant by processInstanceId, retrieves that applicant's document, constructs a File
     * from the document's file path, and returns the text extracted from the PDF.
     *
     * @param processId the process instance id used to locate the Applicant
     * @return the text extracted from the applicant's PDF file
     */
    public String prepForAutoEvaluation(String processId){

        Applicant applicant = applicantRepository.findByprocessInstanceId(processId).get();

        ApplicantDocument applicantDocument = applicantDocumentRepository.findByApplicantId(applicant.getId()).get();
        File file = new File(applicantDocument.getFilePath());
        return pdfExtractor.extractTextFromPDF(file);
    }


}
