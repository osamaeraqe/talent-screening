package com.tracker.talent_screening.repository;

import com.tracker.talent_screening.model.Applicant;
import com.tracker.talent_screening.model.ApplicantDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ApplicantDocumentRepository  extends JpaRepository<ApplicantDocument, Integer> {

    /**
 * Finds an ApplicantDocument associated with the given applicant ID.
 *
 * @param applicantId the primary key of the applicant to match
 * @return an Optional containing the matching ApplicantDocument, or Optional.empty() if none found
 * @implNote If multiple documents exist for the same applicantId, the persistence provider may throw a runtime exception. 
 */
Optional<ApplicantDocument> findByApplicantId(Long applicantId);


}
