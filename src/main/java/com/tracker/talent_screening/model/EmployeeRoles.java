package com.tracker.talent_screening.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "employee_roles", schema = "talent-screening")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRoles {

    @Id
    Long id ;

    @Column
    String role;

    @Column
    String department;
}
