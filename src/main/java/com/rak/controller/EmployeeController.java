package com.rak.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.sql.rowset.serial.SerialException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.rak.entity.EmpLeaveBal;
import com.rak.entity.EmpLeaves;
import com.rak.exception.InsufficientLeaveBalanceException;
import com.rak.exception.NoLeaveFoundException;
import com.rak.requestdto.ChangePasswordRequest;
import com.rak.requestdto.LeaveRequest;
import com.rak.requestdto.ProfileUpdateRequest;
import com.rak.service.EmpLeaveBalService;
import com.rak.service.EmployeeLeaveService;
import com.rak.service.EmployeeService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/emps")
@RequiredArgsConstructor
@Slf4j
public class EmployeeController 
{
	private final EmployeeService employeeService;
	private final EmployeeLeaveService employeeLeaveService;
	private final EmpLeaveBalService empLeaveBalService;
	
	
	@GetMapping("/home") // // http://localhost:8080/emps/home
	public String homePage()
	{ 
		return "this is home page...!!!";
	}
	
	// Update profile with logging and validation
    @PostMapping("/update-profile") // http://localhost:8080/emps/update-profile
    @PreAuthorize(" hasRole('ADMIN') or hasRole('EMP') ")
    public ResponseEntity<String> updateProfile(@RequestParam @NotBlank(message = "First name is required") String firstName, 
    		@RequestParam @NotBlank(message = "Middle name is required") String middleName, 
    		@RequestParam @NotBlank(message = "Last name is required") String lastName, 
    		@RequestParam @NotNull(message = "Mobile number is required") 
    			@Digits(integer = 10, fraction = 0, message = "Mobile number must be numeric and exactly 10 digits")  Long mobile, 
    		@RequestParam(required = false) MultipartFile photo) throws SerialException, IOException, SQLException 
    {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String companyMailId = authentication.getName();
        
        ProfileUpdateRequest profileUpdateRequest = new ProfileUpdateRequest(firstName, middleName, lastName, mobile);
        employeeService.updateProfile(companyMailId, profileUpdateRequest, photo);

        return ResponseEntity.status(HttpStatus.OK).body("Profile updated successfully!");
    }

    // Apply for leave with logging and validation
    @PostMapping("/leaves") // http://localhost:8080/emps/leaves
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMP')")
    public ResponseEntity<String> applyForLeave(@RequestBody @Valid LeaveRequest leaveRequest) throws InsufficientLeaveBalanceException 
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String companyMailId = authentication.getName();

        // Validate leave request dates
        if (leaveRequest.getStartDate().isAfter(leaveRequest.getLastDate())) 
        {
            log.error("Invalid leave dates: Start date {} is after End date {}", leaveRequest.getStartDate(), leaveRequest.getLastDate());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid leave dates: End date cannot be before start date.");
        }

        employeeService.applyForLeave(companyMailId, leaveRequest);

        log.info("Leave applied successfully for employee: {}, waiting for approval", companyMailId);
        return ResponseEntity.status(HttpStatus.CREATED).body("Leave applied successfully, waiting for approval!");
    }

    
    // Change password with logging and validation
    @PostMapping("/change-password") // http://localhost:8080/emps/change-password
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMP')")
    public ResponseEntity<String> changePassword(@RequestBody @Valid ChangePasswordRequest changePasswordRequest) 
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String companyMailId = authentication.getName();
        
        // Calling service to change the password
        String response = employeeService.changePassword(companyMailId, changePasswordRequest.getOldPassword(), changePasswordRequest.getNewPassword());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Get leave balance with logging and validation
    @GetMapping("/leave-balance") // http://localhost:8080/emps/leave-balance
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMP')")
    public ResponseEntity<EmpLeaveBal> getEmpLeaveBalance() 
    {
    	 Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
         String companyMailId = authentication.getName();
    	
        EmpLeaveBal leaveBal = empLeaveBalService.getEmpLeaveBalance(companyMailId);

        if (leaveBal == null) 
        {
            log.error("No leave balance found for employee: {}", companyMailId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        log.info("Leave balance retrieved for employee: {}", companyMailId);
        return ResponseEntity.ok(leaveBal);
    }

    // Get all applied leaves with logging and validation
    @GetMapping("/leaves") // http://localhost:8080/emps/leaves
    @PreAuthorize("hasRole('ADMIN') or hasRole('EMP')")
    public ResponseEntity<List<EmpLeaves>> getAllAppliedLeaves() throws NoLeaveFoundException 
    {
    	Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String companyMailId = authentication.getName();
    	
        List<EmpLeaves> leaves = employeeLeaveService.getAllAppliedLeaves(companyMailId);
        return ResponseEntity.ok(leaves);
    }
    
}
