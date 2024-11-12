package com.rak.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rak.entity.EmpLeaveBal;

public interface EmpLeaveBalRepository extends JpaRepository<EmpLeaveBal, String>
{
	Optional<EmpLeaveBal> findByEmpId(String empId);
}
