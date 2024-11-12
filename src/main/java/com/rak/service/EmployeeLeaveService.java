package com.rak.service;

import java.util.List;

import com.rak.entity.EmpLeaves;
import com.rak.exception.InsufficientLeaveBalanceException;
import com.rak.exception.InvalidLeaveIdException;
import com.rak.exception.NoLeaveFoundException;
import com.rak.responsedto.EmpLeaveResponse;

public interface EmployeeLeaveService 
{
	public String hrLeaveApproval(Long leaveId) throws InvalidLeaveIdException, InsufficientLeaveBalanceException;
	public String hrLeaveReject(Long leaveId) throws InvalidLeaveIdException;
	public List<EmpLeaves> getAllAppliedLeaves(String companyMailId) throws NoLeaveFoundException;
	public List<EmpLeaveResponse> getAllLeaves();
	
	public String managerLeaveApproval(Long leaveId) throws InvalidLeaveIdException, InsufficientLeaveBalanceException;
	public String managerLeaveReject(Long leaveId) throws InvalidLeaveIdException;
	
	
}
