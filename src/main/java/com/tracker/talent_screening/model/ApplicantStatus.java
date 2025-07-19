package com.tracker.talent_screening.model;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "applicant_status", schema = "talent-screening")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApplicantStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}

