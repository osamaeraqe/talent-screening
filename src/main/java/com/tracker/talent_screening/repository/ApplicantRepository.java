package com.tracker.talent_screening.repository;

import com.tracker.talent_screening.model.Applicant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicantRepository extends JpaRepository<Applicant, Long> {

    /**
 * Find an applicant by their email address.
 *
 * @param email the email address to search for
 * @return an Optional containing the Applicant with the given email, or Optional.empty() if none found
 */
    Optional<Applicant> findByEmail(String email);

    /**
 * Finds all applicants who applied to the job with the given ID.
 *
 * @param jobId the identifier of the applied job
 * @return a list of matching Applicant entities; an empty list if no applicants are found
 */
    List<Applicant> findByApplyedJobId(Long jobId);

    /**
 * Finds all applicants that have the specified status ID.
 *
 * @param statusId the status identifier to match applicants against
 * @return a list of Applicants with the given status; empty if none match
 */
    List<Applicant> findByStatusId(Long statusId);

    /**
 * Finds an Applicant by its workflow/process instance identifier.
 *
 * @param processInstanceId the process instance identifier associated with the Applicant
 * @return an Optional containing the Applicant with the given processInstanceId, or Optional.empty() if none is found
 */
    Optional<Applicant> findByprocessInstanceId(String processInstanceId);
}