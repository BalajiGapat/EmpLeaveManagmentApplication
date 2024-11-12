package com.rak.security.user;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.rak.entity.Employee;
import com.rak.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

// Step: 3.1
@Service
@RequiredArgsConstructor
@Slf4j
public class SecurityUserDetailsService implements UserDetailsService
{ 
	private final EmployeeRepository employeeRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
	{
		 log.info("Loading user by username: {}", username); // Log the username being searched
		 
		 Employee emp = employeeRepository.findByCompanyMailId(username)
								                .orElseThrow(() -> {
								                    log.error("User not found: {}", username); // Log a warning if the user is not found
								                    return new UsernameNotFoundException("Username not found: " + username);
								                });
		 
		 SecurityUser securityUser=new SecurityUser();
		securityUser.setEmployee(emp);
		log.info("User_details: {} ", securityUser);
		return securityUser;
	}

}
