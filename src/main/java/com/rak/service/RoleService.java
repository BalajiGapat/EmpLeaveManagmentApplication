package com.rak.service;

import com.rak.exception.RoleAlreadyPresentException;
import com.rak.exception.RoleNotFoundException;

public interface RoleService 
{
	public String assignRoleToUser(String empId, String role);

	public String removeRoleFromUser(String empId, String role) throws RoleNotFoundException;
	
	public String deleteRole(String role) throws RoleNotFoundException;
	
	public String createRole(String role) throws RoleAlreadyPresentException;
}
