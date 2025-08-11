package com.tracker.talent_screening.repository;

import com.tracker.talent_screening.model.Applicant;
import com.tracker.talent_screening.model.ApplicantDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApplicantDocumentRepository  extends JpaRepository<ApplicantDocument, Integer> {

    Optional<ApplicantDocument> findFirstByApplicant_IdOrderByUploadedAtDesc(Long applicantId);

}
