package com.rak.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.rak.entity.Employee;
import com.rak.entity.Role;
import com.rak.exception.RoleAlreadyPresentException;
import com.rak.exception.RoleNotFoundException;
import com.rak.repository.EmployeeRepository;
import com.rak.repository.RoleRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleServiceImpl implements RoleService
{
	private final EmployeeRepository employeeRepository;
	private final RoleRepository roleRepository;
	
	@Override
	public String assignRoleToUser(String empId, String role) 
	{
		Employee emp=employeeRepository.findById(empId).orElseThrow(()->new UsernameNotFoundException("username not found for given empId...!!!"));
		
		Role newRole=roleRepository.findByRole(role).orElseGet(()->{
			Role roles=new Role(); 
			roles.setRole(role);
			roles.setEmps(new HashSet<>());
			return roleRepository.save(roles);
		}); 
		 
		newRole.assignRoleToEmp(emp);  
		employeeRepository.save(emp);
		
		return "role: "+newRole.getRole()+" is assign to emp: "+emp.getCompanyMailId();
	}
	

	@Transactional
	public String removeRoleFromUser(String empId, String rolee) throws RoleNotFoundException
	{
		Employee emp=employeeRepository.findById(empId).orElseThrow(()->new UsernameNotFoundException("username not found for given empId...!!!"));
		Role role=roleRepository.findByRole(rolee).orElseThrow(() -> new RoleNotFoundException("Role not found...!!!"));
		
		Set<Role> roleSet=emp.getRoles();
		
		if(roleSet.contains(role))
		{
			boolean flag=roleSet.remove(role);
			// System.out.println("flg: "+flag);
			role.removeRoleFromEmp(emp);
			emp.setRoles(roleSet);
		} 
		
		employeeRepository.save(emp);
		
		return "role: "+role.getRole()+" is remove from emp: "+emp.getCompanyMailId();
	}
	
	public String deleteRole(String rolee) throws RoleNotFoundException
	{
		Role role=roleRepository.findByRole(rolee).orElseThrow(() -> new RoleNotFoundException("Role not found....!!!"));
		role.removeAllEmpsFromRole();
		roleRepository.deleteById(role.getRoleId());
		
		return "Role: "+role+" is depeted permonontly...!!!";
	}
	
	public String createRole(String role) throws RoleAlreadyPresentException
	{
		Optional<Role> optionalRole=roleRepository.findByRole(role);
		if(optionalRole.isPresent())
			throw new RoleAlreadyPresentException("role already present...!!!");
		
		Role newRole=new Role(); 
		newRole.setRole(role);
		newRole.setEmps(new HashSet<>()); 
		roleRepository.save(newRole);
		
		return "new role with Role: "+newRole+" is created...!!!";
	}
 
}
