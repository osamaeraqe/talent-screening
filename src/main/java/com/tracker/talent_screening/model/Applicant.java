package com.tracker.talent_screening.model;


import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "applicant", schema = "talent-screening")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Applicant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String phonenumber;

    @ManyToOne
    @JoinColumn(name = "applyedjob", nullable = false)
    private Job applyedJob;

    @ManyToOne
    @JoinColumn(name = "statusid", nullable = false)
    private ApplicantStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
