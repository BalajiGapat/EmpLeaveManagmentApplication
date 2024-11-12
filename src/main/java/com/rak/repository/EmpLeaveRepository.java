package com.rak.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rak.entity.EmpLeaves;

public interface EmpLeaveRepository extends JpaRepository<EmpLeaves, Long>
{
	Optional<EmpLeaves> findByleaveId(Long leaveId);
	List<EmpLeaves> findByEmpEmpId(String empEmpId);
}
