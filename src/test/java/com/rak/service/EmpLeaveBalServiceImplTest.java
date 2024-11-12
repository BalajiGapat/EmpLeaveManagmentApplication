package com.rak.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.rak.entity.EmpLeaveBal;
import com.rak.entity.Employee;
import com.rak.enums.LeaveType;
import com.rak.repository.EmpLeaveBalRepository;
import com.rak.repository.EmployeeRepository;

public class EmpLeaveBalServiceImplTest  
{
    @Mock
    private EmpLeaveBalRepository empLeaveBalRepository;

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmpLeaveBalServiceImpl empLeaveBalService;

    private Employee employee;
    
    private EmpLeaveBal empLeaveBal;

    @BeforeEach
    public void setUp() 
    {
        MockitoAnnotations.openMocks(this); //  is used to initialize the mock objects in your test class annotated with @Mock annotations

        // Setting up mock employee and leave balance data
        employee=new Employee();
        employee.setEmpId("test");
        employee.setCompanyMailId("test@gmail.com");
        
        empLeaveBal=new EmpLeaveBal();
        empLeaveBal.setEmpId("test");
        empLeaveBal.setSickLeaveBal(10);
        empLeaveBal.setCasualLeaveBal(10);
        empLeaveBal.setOtherLeaveBal(10);
        empLeaveBal.setTotalLeaveBal(50);
    } 

    // Test for getEmpLeaveBalance()
    @Test
    public void testGetEmpLeaveBalance_Success() 
    {
    	when(employeeRepository.findByCompanyMailId(anyString())).thenReturn(Optional.of(employee));
    	when(empLeaveBalRepository.findByEmpId(anyString())).thenReturn(Optional.of(empLeaveBal));
    	
    	EmpLeaveBal result=empLeaveBalService.getEmpLeaveBalance("test@gmail.com");
    	
    	assertEquals(empLeaveBal.getEmpId(), result.getEmpId());
    	verify(employeeRepository, times(1)).findByCompanyMailId(anyString());
    	verify(empLeaveBalRepository, times(1)).findByEmpId("test");
    
    }

    @Test 
    public void testGetEmpLeaveBalance_EmployeeNotFound() 
    {
    	when(employeeRepository.findByCompanyMailId(anyString())).thenReturn(Optional.empty());
    	assertThrows(UsernameNotFoundException.class, ()->empLeaveBalService.getEmpLeaveBalance("test@gmail.com"));
    	
    	verify(employeeRepository, times(1)).findByCompanyMailId("test@gmail.com");
    	verify(empLeaveBalRepository, times(0)).findByEmpId(anyString());
    }

    
    
    // Test for resetEmployeeLeaves()
    @Test 
    public void testResetEmployeeLeaves()
    { 
    	List<EmpLeaveBal> empLeaveBalList=List.of(empLeaveBal);
    	when(empLeaveBalRepository.findAll()).thenReturn(empLeaveBalList);
    	when(empLeaveBalRepository.save(any(EmpLeaveBal.class))).thenReturn(empLeaveBal);
    	
    	empLeaveBalService.resetEmployeeLeaves();
    	
    	verify(empLeaveBalRepository, times(1)).findAll();
    	verify(empLeaveBalRepository, times(1)).save(any(EmpLeaveBal.class));
    	
    	// Verify the rest logic
    	assertEquals(LeaveType.SICK_LEAVE.getLeaveDays(), empLeaveBal.getSickLeaveBal());
    	assertEquals(LeaveType.CASUAL_LEAVE.getLeaveDays(), empLeaveBal.getCasualLeaveBal());
        assertEquals(LeaveType.OTHER.getLeaveDays(), empLeaveBal.getOtherLeaveBal());
        assertEquals(LeaveType.SICK_LEAVE.getLeaveDays() + LeaveType.CASUAL_LEAVE.getLeaveDays() + LeaveType.OTHER.getLeaveDays(), empLeaveBal.getTotalLeaveBal());
    
    }
    
    
}