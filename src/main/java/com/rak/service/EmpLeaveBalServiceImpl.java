package com.rak.service;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.rak.entity.EmpLeaveBal;
import com.rak.entity.Employee;
import com.rak.enums.LeaveType;
import com.rak.repository.EmpLeaveBalRepository;
import com.rak.repository.EmployeeRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmpLeaveBalServiceImpl implements EmpLeaveBalService
{
	private final EmpLeaveBalRepository empLeaveBalRepository;
	private final EmployeeRepository employeeRepository;
	
	@Override
	public EmpLeaveBal getEmpLeaveBalance(String companyMailId) 
	{
		Employee employee=employeeRepository.findByCompanyMailId(companyMailId).orElseThrow( ()->new UsernameNotFoundException("emp not found with given mailId...!!!") );
		
		return empLeaveBalRepository.findByEmpId(employee.getEmpId()).get();
	}
	
	
	// Reset total leaves for all employees on January 1st every year
	// (seconds, minutes, hours, day of the month, month, , day of the week) => * means any day of the week
	@Scheduled(cron = "0 0 0 1 1 *") 
	//@Scheduled(cron = "*/2 * * * * *") // for testing
	public void resetEmployeeLeaves() 
	{
		System.out.println("in scheduling emp leave balance....!!!!");
        List<EmpLeaveBal> empLeaveBalList=empLeaveBalRepository.findAll();
        
        empLeaveBalList = empLeaveBalList
				        			.stream()
				        			.map(emp->{
				        				emp.setSickLeaveBal(LeaveType.SICK_LEAVE.getLeaveDays());
				        				emp.setCasualLeaveBal(LeaveType.CASUAL_LEAVE.getLeaveDays());
				        				emp.setOtherLeaveBal(LeaveType.OTHER.getLeaveDays());
				        				emp.setTotalLeaveBal(LeaveType.SICK_LEAVE.getLeaveDays()+LeaveType.CASUAL_LEAVE.getLeaveDays()+LeaveType.OTHER.getLeaveDays());
				        				return emp; 
				        			})
				        			.toList();
        
        empLeaveBalList = empLeaveBalList
					        			.stream()
					        			.map(obj->empLeaveBalRepository.save(obj))
					        			.toList();
        
        empLeaveBalList.forEach(emp->System.out.println(emp));
        
	}

}
