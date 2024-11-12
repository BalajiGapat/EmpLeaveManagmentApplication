package com.rak.security.user;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.rak.entity.Employee;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

// Step: 2.1
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class SecurityUser implements UserDetails
{
	
	private Employee employee;
	
	public SecurityUser(Employee employee)
	{
		this.employee=employee;
	}
	

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() 
	{
		
//		Set<Role> roles=employee.getRoles();
//		Set<GrantedAuthority> authorititySet=new HashSet<>();
//		for(Role role : roles)
//		{
//			GrantedAuthority authority=new SimpleGrantedAuthority(role.toString());
//			authoritityList.add(authority);
//		}
		
		//OR
		Set<GrantedAuthority> authorititySet;
		authorititySet=employee
							.getRoles()
							.stream()
							.map(authority->new SimpleGrantedAuthority(authority.toString()))
							.collect(Collectors.toSet());
		
		return authorititySet;
	}

	@Override
	public String getPassword() 
	{
		return employee.getPassword();
	}

	@Override
	public String getUsername() 
	{
		return employee.getCompanyMailId();
	}
	
	public String getEmpId()
	{
		return employee.getEmpId();
	}

}
