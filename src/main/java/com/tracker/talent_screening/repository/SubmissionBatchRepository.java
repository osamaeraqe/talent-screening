package com.tracker.talent_screening.repository;

import com.tracker.talent_screening.model.SubmissionBatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SubmissionBatchRepository extends JpaRepository<SubmissionBatch, Long> {
    /**
 * Finds a SubmissionBatch with the given batch number.
 *
 * @param batchNumber the batch number to search for
 * @return an Optional containing the matching SubmissionBatch, or empty if none is found
 */
Optional<SubmissionBatch> findByBatchNumber(String batchNumber);

    /**
 * Retrieves a SubmissionBatch by its process instance ID.
 *
 * @param processInstanceId the process instance identifier associated with the batch
 * @return an Optional containing the matching SubmissionBatch if present, otherwise Optional.empty()
 */
Optional<SubmissionBatch> findByProcessInstanceId(String processInstanceId);
}