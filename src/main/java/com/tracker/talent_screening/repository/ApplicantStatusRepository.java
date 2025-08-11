package com.tracker.talent_screening.repository;

import com.tracker.talent_screening.model.Applicant;
import com.tracker.talent_screening.model.ApplicantStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface ApplicantStatusRepository extends JpaRepository<ApplicantStatus, Integer> {

    Optional<ApplicantStatus> findByName(String name);


}
