package com.tracker.talent_screening.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "application_audit", schema = "talent-screening")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationAudit {

    @Id
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "applicant_id")
    private Applicant applicant;

    private String action;
    private String details;

    @Column(name = "timestamp")
    private LocalDateTime timestamp;



}
