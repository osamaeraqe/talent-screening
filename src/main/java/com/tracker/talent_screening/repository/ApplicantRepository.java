package com.tracker.talent_screening.repository;

import com.tracker.talent_screening.model.Applicant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApplicantRepository extends JpaRepository<Applicant, Long> {

    // You can add custom queries here if needed
    Optional<Applicant> findByEmail(String email);

    // Example: Find applicants by job id
    List<Applicant> findByApplyedJobId(Long jobId);

    // Example: Find applicants by status
    List<Applicant> findByStatusId(Long statusId);

    // Find applicant By ProcessInstance Id
    Optional<Applicant> findByProcessInstanceId(String processInstanceId);
}