package com.tracker.talent_screening.repository;

import com.tracker.talent_screening.model.SubmissionBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubmissionBatchRepository extends JpaRepository<SubmissionBatch, Long> {
    Optional<SubmissionBatch> findByBatchNumber(String batchNumber);

    Optional<SubmissionBatch> findByProcessInstanceId(String processInstanceId);
}