package com.tracker.talent_screening.repository;

import com.tracker.talent_screening.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {
    // Find employees by name, roles, department, etc. if needed
}