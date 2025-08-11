package com.tracker.talent_screening.repository;

import com.tracker.talent_screening.model.EmployeeRoles;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRoleRepository extends JpaRepository<EmployeeRoles, Long> {
    // Custom queries to find employees by role+department can be added here
}
