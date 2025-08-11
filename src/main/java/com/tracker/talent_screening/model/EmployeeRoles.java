package com.tracker.talent_screening.model;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(
        name = "employee_roles",
        schema = "talent-screening",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_employee_role",
                columnNames = {"employee_id", "role"}
        )
)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRoles {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id ;

    @Column(nullable = false)
    String role;

    @Column
    String department;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;
}
