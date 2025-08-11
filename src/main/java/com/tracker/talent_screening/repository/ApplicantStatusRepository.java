package com.tracker.talent_screening.repository;

import com.tracker.talent_screening.model.Applicant;
import com.tracker.talent_screening.model.ApplicantStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ApplicantStatusRepository extends JpaRepository<ApplicantStatus, Integer> {

    /**
 * Finds an ApplicantStatus by its name.
 *
 * @param name the status name to search for
 * @return an Optional containing the matching ApplicantStatus, or Optional.empty() if none found
 */
Optional<ApplicantStatus> findByName(String name);


}
