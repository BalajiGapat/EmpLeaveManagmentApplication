package com.rak.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.rak.entity.Employee;
import com.rak.entity.Role;
import com.rak.exception.RoleAlreadyPresentException;
import com.rak.exception.RoleNotFoundException;
import com.rak.repository.EmployeeRepository;
import com.rak.repository.RoleRepository;

class RoleServiceImplTest 
{
    @InjectMocks
    private RoleServiceImpl roleService;

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private RoleRepository roleRepository;

    private Employee emp;
    private Role empRole;
    
    
    @BeforeEach
    void setUp()  
    {
    	String empId="RAK-123";
    	String role="ROLE_ADMIN";
    	
    	empRole=new Role();
    	empRole.setRole(role);
    	empRole.setEmps(new HashSet<>());
    	
    	emp=new Employee();
    	emp.setEmpId(empId);
    	emp.setRoles(new HashSet<>());
    	emp.setCompanyMailId("rak@gmail.com");
    	
    	Set<Role> roleSet=new HashSet<>();
    	roleSet.add(empRole);
    	
    	Set<Employee> empSet=new HashSet<>();
    	empSet.add(emp);
    	
    	empRole.setEmps(empSet);
    	emp.setRoles(roleSet);
    	
        MockitoAnnotations.openMocks(this); // Initialize mocks
    }

    @Test
    void testAssignRoleToUser_Success() 
    {
    	when(employeeRepository.findById(emp.getEmpId())).thenReturn(Optional.of(emp));
    	when(roleRepository.findByRole(empRole.getRole())).thenReturn(Optional.of(empRole));
    	
    	String result=roleService.assignRoleToUser(emp.getEmpId(), empRole.getRole());
    	assertEquals("role: "+empRole+" is assign to emp: "+emp.getCompanyMailId(), result);
    	verify(employeeRepository, times(1)).save(emp); // ensure save method is called
    	
    }
    
    @Test
    public void testAssignRoleToUser_FindExistingRole()
    {
    	// when
    	when(employeeRepository.findById(emp.getEmpId())).thenReturn(Optional.of(emp));
    	when(roleRepository.findByRole(empRole.getRole())).thenReturn(Optional.of(empRole));
    	
    	String result=roleService.assignRoleToUser(emp.getEmpId(), empRole.getRole());
    	
    	assertEquals("role: "+empRole+" is assign to emp: "+emp.getCompanyMailId(), result);
    	 
    	assertNotNull(result);
    	verify(employeeRepository, times(1)).findById(emp.getEmpId());
    	verify(roleRepository, times(1)).findByRole(empRole.getRole());

    	verify(roleRepository, never()).save(any(Role.class)); 
    }
    
    @Test
    public void testAssignRoleToUser_WhenRoleNotFound()
    {
    	// when
    	when(employeeRepository.findById(emp.getEmpId())).thenReturn(Optional.of(emp));
    	when(roleRepository.findByRole(empRole.getRole())).thenReturn(Optional.empty());
    	
    	when(roleRepository.save(any(Role.class))).thenAnswer(invocation->invocation.getArgument(0));
    	
    	String result=roleService.assignRoleToUser(emp.getEmpId(), empRole.getRole());
    	
    	assertEquals("role: "+empRole+" is assign to emp: "+emp.getCompanyMailId(), result);
    	verify(roleRepository, times(1)).save(any(Role.class));
    	
    }
    
    @Test
    void testAssignRoleToUser_WhenEmployeeNotFound() 
    {
    	when(employeeRepository.findById(emp.getEmpId())).thenReturn(Optional.empty());
    	
    	// acts and assert
    	assertThrows(UsernameNotFoundException.class, ()->roleService.assignRoleToUser(emp.getEmpId(), empRole.getRole()));	
    }
    
    @Test
    public void testRemoveRoleFromUser_Success() throws RoleNotFoundException
    {

    	when(employeeRepository.findById(emp.getEmpId())).thenReturn(Optional.of(emp));
    	when(roleRepository.findByRole(empRole.getRole())).thenReturn(Optional.of(empRole));
    	
    	String result=roleService.removeRoleFromUser(emp.getEmpId(), empRole.getRole());
    	
    	assertEquals("role: "+empRole+" is remove from emp: "+emp.getCompanyMailId(), result);
    	verify(employeeRepository, times(1)).findById(emp.getEmpId());
    	verify(roleRepository, times(1)).findByRole(empRole.getRole());
    }
    
    @Test
    public void testRemoveRoleFromUser_WhenRoleNotFound()
    {
    	when(employeeRepository.findById(emp.getEmpId())).thenReturn(Optional.of(emp));
    	when(roleRepository.findByRole(empRole.getRole())).thenReturn(Optional.empty());
    	
    	assertThrows(RoleNotFoundException.class, ()->roleService.removeRoleFromUser(emp.getEmpId(), empRole.getRole()));
    	verify(employeeRepository, times(0)).save(emp);
    }
    
    @Test
    public void testDeleteRole_WhenRoleFound() throws RoleNotFoundException
    {
    	when(roleRepository.findByRole(empRole.getRole())).thenReturn(Optional.of(empRole));
    	doNothing().when(roleRepository).deleteById(empRole.getRoleId());
    	 
    	String result=roleService.deleteRole(empRole.getRole());
    	
    	assertNotNull(result);
    	assertEquals("Role: "+empRole.getRole()+" is depeted permonontly...!!!", result);
    	verify(roleRepository, times(1)).deleteById(empRole.getRoleId());
    }
    
    @Test
    public void testDeleteRole_WhenRoleNotFound()
    {
    	when(roleRepository.findByRole(empRole.getRole())).thenReturn(Optional.empty());
    	assertThrows(RoleNotFoundException.class, ()->roleService.deleteRole(empRole.getRole()));
    }
    
    @Test
    public void testCreateRole_WhenRoleIsNotPresent() throws RoleAlreadyPresentException
    {
    	when(roleRepository.findByRole(empRole.getRole())).thenReturn(Optional.empty());
    	String result=roleService.createRole(empRole.getRole());
    	assertEquals("new role with Role: "+empRole.getRole()+" is created...!!!", result);
    	
    	verify(roleRepository, times(1)).save(any(Role.class));
    }
    
    @Test
    public void testCreateRole_WhenRoleIsPresent()
    {
    	when(roleRepository.findByRole(empRole.getRole())).thenReturn(Optional.of(empRole));
    	
    	assertThrows(RoleAlreadyPresentException.class, ()->roleService.createRole(empRole.getRole()));
    	verify(roleRepository, times(1)).findByRole(anyString());
    }
    
   
}