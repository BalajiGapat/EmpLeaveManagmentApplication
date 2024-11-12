package com.rak.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rak.exception.InsufficientLeaveBalanceException;
import com.rak.exception.InvalidLeaveIdException;
import com.rak.service.EmployeeLeaveService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/manager")
@AllArgsConstructor
public class ManagerController 
{
	private final EmployeeLeaveService  employeeLeaveService;
	
	@PreAuthorize(" hasRole('ADMIN') or hasRole('MANAGER') ")
	@GetMapping("/welcome") // http://localhost:8080/manager/welcome
	public String welcome()
	{
		return "this is welcome page...!!!";
	}
	
	
	@PostMapping("/approve-leave") // http://localhost:8080/manager/approve-leave?leaveId=1
	@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
	public ResponseEntity<String> managerApproveLeave(@RequestParam  Long leaveId) throws InvalidLeaveIdException, InsufficientLeaveBalanceException
	{
		String response=employeeLeaveService.managerLeaveApproval(leaveId);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
	}
	

	@PostMapping("/reject-leave") // http://localhost:8080/manager/approve-leave?leaveId=1
	@PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
	public ResponseEntity<String> managerRejectLeave(@RequestParam Long leaveId) throws InvalidLeaveIdException, InsufficientLeaveBalanceException
	{
		String response=employeeLeaveService.managerLeaveReject(leaveId);
		return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
	}
}
