package com.rak.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rak.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee, String>
{
	public Optional<Employee> findByCompanyMailId(String companyMailId);
	public Optional<Employee> findByPersonalMailId(String personalMailId);
	
	// Method to find by either mailId or empId
    public Optional<Employee> findByEmpIdOrCompanyMailId(String empId, String companyMailId);
}
