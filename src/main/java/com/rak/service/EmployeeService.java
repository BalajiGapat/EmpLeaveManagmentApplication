package com.rak.service;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

import javax.sql.rowset.serial.SerialException;

import org.springframework.web.multipart.MultipartFile;

import com.rak.entity.Employee;
import com.rak.exception.EmailIdAlreadyExistException;
import com.rak.exception.InsufficientLeaveBalanceException;
import com.rak.exception.ResourceNotFoundException;
import com.rak.requestdto.LeaveRequest;
import com.rak.requestdto.ProfileUpdateRequest;
import com.rak.requestdto.RegistrationRequest;
import com.rak.responsedto.EmployeeResponse;
import com.rak.responsedto.RegistrationResponse;

public interface EmployeeService 
{
	public RegistrationResponse registerEmployee(RegistrationRequest registrationDto) throws EmailIdAlreadyExistException;
	public String updateProfile(String companyMailId, ProfileUpdateRequest profileUpdateRequest, MultipartFile photo) throws IOException, SerialException, SQLException;
	public void applyForLeave(String companyMailId, LeaveRequest leaveRequest) throws InsufficientLeaveBalanceException;
	public List<EmployeeResponse> gellAllEmps();
	public String removeEmp(String empId) throws ResourceNotFoundException;
	public String changePassword(String companyMailId, String oldPassword, String newPassword);
	public String forgotPassword(String empId, String companyMailId);
}
