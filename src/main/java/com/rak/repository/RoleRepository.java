package com.rak.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rak.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long>
{
	public Optional<Role> findByRole(String role);
}
