package com.rak.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.rak.entity.EmpLeaveBal;
import com.rak.entity.EmpLeaves;
import com.rak.entity.Employee;
import com.rak.enums.LeaveStatus;
import com.rak.enums.LeaveType;
import com.rak.exception.InsufficientLeaveBalanceException;
import com.rak.exception.InvalidLeaveIdException;
import com.rak.exception.NoLeaveFoundException;
import com.rak.mailutils.MailUtils;
import com.rak.repository.EmpLeaveBalRepository;
import com.rak.repository.EmpLeaveRepository;
import com.rak.repository.EmployeeRepository;
import com.rak.responsedto.EmpLeaveResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeLeaveServiceImpl implements EmployeeLeaveService
{
	private final EmpLeaveRepository empLeaveRepository;
	private final EmpLeaveBalRepository empLeaveBalRepository;
	private final EmployeeRepository employeeRepository;
	private final MailUtils mailUtils;
	
	@Value("${spring.mail.username}")
	private String fromEmailId;
	
	// Inject the manager and HR email IDs from application.properties
    @Value("${manager.email}")
    private String managerEmailId;

    @Value("${hr.email}")
    private String hrEmailId;

	public String managerLeaveApproval(Long leaveId) throws InvalidLeaveIdException, InsufficientLeaveBalanceException
	{
		EmpLeaves empLeaveObj=empLeaveRepository.findByleaveId(leaveId).orElseThrow( ()-> new InvalidLeaveIdException("provided leaveid is invalid...!!!") );
		Employee emp=empLeaveObj.getEmp();
		
		LocalDate startDate=empLeaveObj.getStartDate();
		LocalDate lastDate=empLeaveObj.getLastDate();
		LeaveType leaveType=empLeaveObj.getLeaveType();
		int leaveDays=(int) ChronoUnit.DAYS.between(startDate, lastDate);
		 
		if(lastDate.isBefore(startDate))
			throw new IllegalArgumentException("lastDate must be after startDate...!!!");
		
		if(!isLeaveAvailable(emp.getEmpId(), leaveType, leaveDays))
			throw new InsufficientLeaveBalanceException("insufficent leave balance...!!!");
		 
		empLeaveObj.setManagerAction(LeaveStatus.APPROVED);
		emp.getLeaveList().add(empLeaveObj);
		employeeRepository.save(emp);
		System.out.println("leaveObject: "+empLeaveObj);
		 
		// Send email with credentials
		mailUtils.sendLeaveNotificationToHr(fromEmailId, emp.getFirstName()+" "+emp.getLastName(), leaveType, hrEmailId, startDate, lastDate, empLeaveObj.getReason());
			    
		return "leave approved by manager...!!!";
	}
	
	public String managerLeaveReject(Long leaveId) throws InvalidLeaveIdException
	{
		EmpLeaves empLeaveObj=empLeaveRepository.findByleaveId(leaveId).orElseThrow( ()-> new InvalidLeaveIdException("provided leaveid is invalid...!!!") );
		Employee emp=empLeaveObj.getEmp();
		
		empLeaveObj.setManagerAction(LeaveStatus.REJECTED);
		empLeaveObj.setHrAction(LeaveStatus.REJECTED);
		empLeaveObj.setLeaveStatus(LeaveStatus.REJECTED);
		emp.getLeaveList().add(empLeaveObj);
		employeeRepository.save(emp);
		
		return "leave rejected by manager...!!!";
	}
	
	
	
	
	@Override
	public String hrLeaveApproval(Long leaveId) throws InvalidLeaveIdException, InsufficientLeaveBalanceException 
	{
		EmpLeaves empLeaveObj=empLeaveRepository.findByleaveId(leaveId).orElseThrow( ()-> new InvalidLeaveIdException("provided leaveid is invalid...!!!") );
		Employee emp=empLeaveObj.getEmp();
		EmpLeaveBal empLeaveBal=empLeaveBalRepository.findByEmpId(emp.getEmpId()).get();
		
		LocalDate startDate=empLeaveObj.getStartDate();
		LocalDate lastDate=empLeaveObj.getLastDate();
		LeaveType leaveType=empLeaveObj.getLeaveType();
		int leaveDays=(int) ChronoUnit.DAYS.between(startDate, lastDate);
		
		if(lastDate.isBefore(startDate))
			throw new IllegalArgumentException("lastDate must be after startDate...!!!");
		
		if(!isLeaveAvailable(emp.getEmpId(), leaveType, leaveDays))
			throw new InsufficientLeaveBalanceException("insufficent leave balance...!!!");
		
		if(empLeaveObj.getManagerAction()==LeaveStatus.APPROVED)
		{
			deductLeavesFromEmpLeaveBal(leaveDays, leaveType, empLeaveBal);
		
			empLeaveBalRepository.save(empLeaveBal);
		
			empLeaveObj.setHrAction(LeaveStatus.APPROVED);
			empLeaveObj.setLeaveStatus(LeaveStatus.APPROVED);
			emp.getLeaveList().add(empLeaveObj);
			employeeRepository.save(emp);
	
			return "leave approved by HR Level successfully...!!!";
		}
		else
			return "wait for manager approval...!!!";
	}

	@Override
	public String hrLeaveReject(Long leaveId) throws InvalidLeaveIdException 
	{
		EmpLeaves leaveObj= empLeaveRepository.findByleaveId(leaveId).orElseThrow( ()-> new InvalidLeaveIdException("incorect leaveId...!!!") );
    	leaveObj.setLeaveStatus(LeaveStatus.REJECTED);
    	empLeaveRepository.save(leaveObj);
		return "HR  rejected leave successfully...!!!";
	}
	
	@Override
	public List<EmpLeaves> getAllAppliedLeaves(String companyMailId) throws NoLeaveFoundException
	{
		Employee emp=employeeRepository.findByCompanyMailId(companyMailId).orElseThrow(()->new UsernameNotFoundException("username not found...!!!"));
		List<EmpLeaves> leaveList=empLeaveRepository.findByEmpEmpId(emp.getEmpId());
		if (leaveList.isEmpty()) 
        {
            log.error("No leaves found for employee: {}", companyMailId);
            throw new NoLeaveFoundException("No leaves found");
        }
		return leaveList;
		
	}
	
	
	public List<EmpLeaveResponse> getAllLeaves()
	{
		List<EmpLeaves> empLeaveList=empLeaveRepository.findAll();
		
		List<EmpLeaveResponse> list=new ArrayList<>();
		
		for(EmpLeaves empLeave : empLeaveList)
		{
			EmpLeaves empLeaves=empLeaveRepository.findById(empLeave.getLeaveId()).get();
			Employee emp=empLeaves.getEmp();
			
			EmpLeaveResponse obj=new EmpLeaveResponse();
			
			obj.setLeaveId(empLeave.getLeaveId());
			obj.setLeaveType(empLeave.getLeaveType());
			obj.setReason(empLeave.getReason());
			obj.setDepartment("development");
			obj.setStartDate(empLeave.getStartDate());
			obj.setEndDate(empLeave.getLastDate());
			obj.setManagerAction(empLeave.getManagerAction()); 
			obj.setHrAction(empLeave.getHrAction());
			obj.setLeaveStatus(empLeave.getLeaveStatus());
			obj.setEmpFullName(emp.getFirstName()+" "+emp.getLastName());
			
			list.add(obj);
		}
		
		return list;
	}
	
	
	
	
	
	
	// =========================== Utility Method ============================
	
	public boolean isLeaveAvailable(String empId, LeaveType leaveType, int leaveDays)
    {
    	EmpLeaveBal empLeaveBal=empLeaveBalRepository.findById(empId).get();
    	
    	if(leaveType==LeaveType.SICK_LEAVE)
    	{ 
    		if(empLeaveBal.getSickLeaveBal()>=leaveDays)
    			return true;
    	}
    	else if(leaveType==LeaveType.CASUAL_LEAVE)
    	{
    		if(empLeaveBal.getCasualLeaveBal()>=leaveDays)
    			return true;
    	}
    	else if(leaveType==LeaveType.OTHER)
    	{
    		if(empLeaveBal.getOtherLeaveBal()>=leaveDays)
    			return true;
    	}
    	
    	return false;
    }
	
	public void deductLeavesFromEmpLeaveBal(int leaveDays, LeaveType leaveType, EmpLeaveBal empLeaveBal)
	{
		if(leaveType==LeaveType.SICK_LEAVE)
			empLeaveBal.setSickLeaveBal(empLeaveBal.getSickLeaveBal()-leaveDays);
		else if(leaveType==LeaveType.CASUAL_LEAVE)
			empLeaveBal.setCasualLeaveBal(empLeaveBal.getCasualLeaveBal()-leaveDays);
		else
			empLeaveBal.setOtherLeaveBal(empLeaveBal.getOtherLeaveBal()-leaveDays);
		
		empLeaveBal.setTotalLeaveBal(empLeaveBal.getTotalLeaveBal()-leaveDays);
	}
	
	
	
	
	

}
