package com.tracker.talent_screening.model;


import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "submission_batch", schema = "talent-screening")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SubmissionBatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long batchId;

    @Column(unique = true, nullable = false)
    private String batchNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @OneToMany(mappedBy = "submissionBatch", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Applicant> applicants = new ArrayList<>();

    @Column(name = "process_instance_id", unique = true)
    private String processInstanceId;
}
