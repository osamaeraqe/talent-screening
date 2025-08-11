package com.tracker.talent_screening.repository;

import com.tracker.talent_screening.model.Applicant;
import com.tracker.talent_screening.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository  extends JpaRepository<Job, Integer> {


}
