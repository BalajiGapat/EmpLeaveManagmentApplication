package com.rak.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rak.exception.EmailIdAlreadyExistException;
import com.rak.exception.InsufficientLeaveBalanceException;
import com.rak.exception.InvalidLeaveIdException;
import com.rak.exception.ResourceNotFoundException;
import com.rak.exception.RoleAlreadyPresentException;
import com.rak.exception.RoleNotFoundException;
import com.rak.requestdto.RegistrationRequest;
import com.rak.responsedto.EmpLeaveResponse;
import com.rak.responsedto.EmployeeResponse;
import com.rak.responsedto.RegistrationResponse;
import com.rak.service.EmployeeLeaveService;
import com.rak.service.EmployeeService;
import com.rak.service.RoleService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController 
{
	private final EmployeeService employeeService;
	private final EmployeeLeaveService employeeLeaveService;
	private final RoleService roleService;
	
	@PostMapping("/emp-registration") // http://localhost:8080/admin/emp-registration
	@PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
	public ResponseEntity<RegistrationResponse> registerUser(@RequestBody @Valid RegistrationRequest registrationRequest) throws EmailIdAlreadyExistException
	{
		RegistrationResponse response = employeeService.registerEmployee(registrationRequest);
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/approve-leave") // http://localhost:8080/admin/approve-leave?leaveId=1
	@PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
	public ResponseEntity<String> approveEmpLeave(@RequestParam Long leaveId) throws InvalidLeaveIdException, InsufficientLeaveBalanceException 
	{
		String response = employeeLeaveService.hrLeaveApproval(leaveId);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@PostMapping("/reject-leave") // http://localhost:8080/admin/reject-leave?leaveId=1
	@PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
	public ResponseEntity<String> rejectEmpLeave(@RequestParam @NotBlank(message = "leaveId cannot be empty") Long leaveId) throws InvalidLeaveIdException
	{
		String response = employeeLeaveService.hrLeaveReject(leaveId);
		return new ResponseEntity<String>(response, HttpStatus.OK);
	}
	
	@GetMapping("/all-leaves") // http://localhost:8080/admin/all-leaves
	@PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
	public ResponseEntity<List<EmpLeaveResponse>> getAllAppliedLeaves()
	{
		List<EmpLeaveResponse> response=employeeLeaveService.getAllLeaves();
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
    
	@GetMapping("/emps") // http://localhost:8080/admin/emps
	@PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
	public ResponseEntity<List<EmployeeResponse>> getAllEmployee()
	{
		List<EmployeeResponse> employeeList = employeeService.gellAllEmps();
		return new ResponseEntity<List<EmployeeResponse>>(employeeList, HttpStatus.OK);
	}
    
	// Tested
	@DeleteMapping("/emps/empId/{empId}") // http://localhost:8080/admin/emps/empId/12345
	@PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
	public ResponseEntity<String> removeEmp(@PathVariable @NotBlank(message = "empId cannot be empty") String empId) throws ResourceNotFoundException 
	{
		String response=employeeService.removeEmp(empId);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	
	@PostMapping("/assign-emp-role") // http://localhost:8080/admin/assign-emp-role
	@PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
	public ResponseEntity<String> addRoleToUser(@RequestParam String empId, @RequestParam String role)
	{
		String response=roleService.assignRoleToUser(empId, role);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}
	
	@PostMapping("remove-emp-role") // http://localhost:8080/admin/remove-emp-role
	@PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
	public ResponseEntity<String> removeRoleFromUser(@RequestParam String empId, @RequestParam String role) throws RoleNotFoundException
	{
		String respose=roleService.removeRoleFromUser(empId, role);
		return new ResponseEntity<String>(respose, HttpStatus.OK);
	}
	 
	// tested
	@DeleteMapping("/delete-role") // http://localhost:8080/admin/delete-role
	@PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
	public ResponseEntity<String> removeRole(@RequestParam String role) throws RoleNotFoundException
	{
		String response=roleService.deleteRole(role);
		return new ResponseEntity<String>(response, HttpStatus.OK);
	}
	
	@PostMapping("/create-role") // http://localhost:8080/admin/create-role
	@PreAuthorize("hasRole('ADMIN') or hasRole('HR')")
	public ResponseEntity<String> createRole(@RequestParam @NotBlank String role) throws RoleAlreadyPresentException
	{
		String response=roleService.createRole(role);
		return ResponseEntity.status(HttpStatus.CREATED).body(response); 
	}
	
}
