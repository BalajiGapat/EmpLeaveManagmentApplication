package com.rak.service;

import java.io.IOException;
import java.sql.Blob;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.rak.entity.EmpLeaveBal;
import com.rak.entity.EmpLeaves;
import com.rak.entity.Employee;
import com.rak.entity.Role;
import com.rak.enums.LeaveStatus;
import com.rak.enums.LeaveType;
import com.rak.exception.EmailIdAlreadyExistException;
import com.rak.exception.IncorrectPasswordException;
import com.rak.exception.InsufficientLeaveBalanceException;
import com.rak.exception.ResourceNotFoundException;
import com.rak.mailutils.MailUtils;
import com.rak.repository.EmpLeaveBalRepository;
import com.rak.repository.EmpLeaveRepository;
import com.rak.repository.EmployeeRepository;
import com.rak.repository.RoleRepository;
import com.rak.requestdto.LeaveRequest;
import com.rak.requestdto.ProfileUpdateRequest;
import com.rak.requestdto.RegistrationRequest;
import com.rak.responsedto.EmployeeResponse;
import com.rak.responsedto.RegistrationResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.val;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile(value = {"local", "dev", "prod"})
public class EmployeeServiceImpl implements EmployeeService
{
	private final EmployeeRepository employeeRepository;
	private final EmpLeaveBalRepository empLeaveBalRepository;
	private final RoleRepository roleRepository;
	private final EmpLeaveRepository empLeaveRepository;
	private final PasswordEncoder passwordEncoder;
	private final MailUtils mailUtils;
	
	
	// private final JavaMailSender javaMailSender;
	
	@Value("${spring.mail.username}")
	private String fromEmailId;
	
	// Inject the manager and HR email IDs from application.properties
    @Value("${manager.email}")
    private String managerEmailId;

    @Value("${hr.email}")
    private String hrEmailId; 
	
	@Override
	public RegistrationResponse registerEmployee(RegistrationRequest registrationRequest) throws EmailIdAlreadyExistException
	{
		 log.info("Starting employee registration process for email: {}", registrationRequest.getPersonalMailId());

		// validate email
		Optional<Employee> optionalEmp=employeeRepository.findByPersonalMailId(registrationRequest.getPersonalMailId());
		if(optionalEmp.isPresent())
		{
			log.error("Email ID: {} already exists in the system...!!!", registrationRequest.getPersonalMailId());
			throw new EmailIdAlreadyExistException("email id already exist...!!!");
		}
		
		// Get or create a role for the employee
		Role role=roleRepository.findByRole("ROLE_EMP").orElseGet( ()->{
			Role rolee=Role
						.builder()
						.role("ROLE_EMP")
						.emps(new HashSet<>())
						.build();
			log.info("New role with name: {} is created...!!!", rolee);
			return roleRepository.save(rolee);
		} );
		
		// create Employee entity
		Employee emp=Employee
					.builder()
					.empId(generateEmpId())
					.firstName(registrationRequest.getFirstName())
					.lastName(registrationRequest.getLastName())
					.personalMailId(registrationRequest.getPersonalMailId())
					.companyMailId(generateCompanyMailId(registrationRequest.getFirstName(), registrationRequest.getLastName()))
					.password(passwordEncoder.encode(generateTempPassword(registrationRequest.getFirstName())))
					.leaveList(new ArrayList<>())
					.isProfileCompleted(false)
					.build();
		
		log.info("Employee entity created: {}", emp);
		
		role.assignRoleToEmp(emp);
		
		// Create EmpLeaveBal entity and set the employee
	    EmpLeaveBal empLeaveBal = EmpLeaveBal.builder()
								            .employee(emp)  // Set the employee relationship
								            .build();

	    // Associate the leave balance with the employee
	    emp.setEmpLeaveBal(empLeaveBal);
		
	    // Save employee 
	    employeeRepository.save(emp);
		
	    // Send email with credentials
	    mailUtils.sendEmailToEmployee(fromEmailId, emp.getPersonalMailId(), emp.getEmpId(), emp.getCompanyMailId(), generateTempPassword(registrationRequest.getFirstName()));
	    
	    
	    
		// build response object
		RegistrationResponse response=RegistrationResponse
														.builder()
														.message("registration successfull...!!!")
														.empId(emp.getEmpId())
														.companyMailId(emp.getCompanyMailId())
														.build();
		
		return response;
	}
	
	@Override
	public String updateProfile(String companyMailId, @Valid ProfileUpdateRequest profileUpdateRequest, MultipartFile photo) throws IOException, SerialException, SQLException
	{
		Employee emp=employeeRepository.findByCompanyMailId(companyMailId).orElseThrow( ()->new UsernameNotFoundException("username not found...!!!") );
		
		emp.setFirstName(profileUpdateRequest.getFirstName());
		emp.setMiddleName(profileUpdateRequest.getMiddleName());
		emp.setLastName(profileUpdateRequest.getLastName());
		emp.setMobile(profileUpdateRequest.getMobile());
		emp.setProfileCompleted(true);
		
		if(!photo.isEmpty())
		{
			byte[] photoBytes=photo.getBytes();
			Blob photoBlob=new SerialBlob(photoBytes);
			emp.setEmpPhoto(photoBlob);
			log.info("employee photo is saved...!!!");
		}
		
		employeeRepository.save(emp);
		
		return "profile updated successfully...!!!";
	}
	
	
	@Override
	public void applyForLeave(String companyMailId, LeaveRequest leaveRequest) throws InsufficientLeaveBalanceException 
	{
		Employee employee=employeeRepository.findByCompanyMailId(companyMailId).orElseThrow( ()->new UsernameNotFoundException("emp not found with given mailId...!!!") );
		
		LeaveType leaveType=leaveRequest.getLeaveType();
		LocalDate startDate=leaveRequest.getStartDate();
		LocalDate lastDate=leaveRequest.getLastDate();
		int leaveDays=(int) ChronoUnit.DAYS.between(startDate, lastDate);
		
		if (lastDate.isBefore(startDate)) 
		{
		    log.error("End date {} cannot be before start date {}", lastDate, startDate);
		    throw new IllegalArgumentException("End date cannot be before start date...!!!");
		}

		if (!isLeaveAvailable(employee.getEmpId(), leaveType, leaveDays)) 
		{
		    log.error("Insufficient leave balance for Employee{}. Leave type: {}, Requested days: {}", companyMailId, leaveType, leaveDays);
		    throw new InsufficientLeaveBalanceException("Leave Balance is not sufficient...!!!");
		}

		// so far so good
		EmpLeaves empLeave1=EmpLeaves
				.builder()
				.leaveType(leaveType)
				.startDate(startDate)
				.lastDate(lastDate)
				.managerAction(LeaveStatus.PENDING)
				.hrAction(LeaveStatus.PENDING)
				.leaveStatus(LeaveStatus.PENDING)
				.reason(leaveRequest.getReason())
				.build();
		
		employee.addEmpLeave(empLeave1);
		employeeRepository.save(employee);

	    // Send email with credentials
		mailUtils.sendLeaveNotificationToManager(fromEmailId, employee.getFirstName()+" "+employee.getLastName(), leaveType, managerEmailId, startDate, lastDate, leaveRequest.getReason());

	}
	
	@Override
	public List<EmployeeResponse> gellAllEmps() 
	{
		List<Employee> list=employeeRepository.findAll();
		// list.forEach(emp->System.out.println(emp));
		return list.stream().map(emp->
				EmployeeResponse
				.builder()
				.empId(emp.getEmpId())
				.firstName(emp.getFirstName())
				.middleName(emp.getMiddleName())
				.lastName(emp.getLastName())
				.personalMailId(emp.getPersonalMailId())
				.companyMailId(emp.getCompanyMailId())
				.mobile(emp.getMobile())
				.role(emp.getRoles().stream().map(Role::getRole).collect(Collectors.toSet())).build()
				).toList();
	}
	
	// @Transactional
	@Override
	public String removeEmp(String empId) throws ResourceNotFoundException
	{
		Employee emp = employeeRepository.findById(empId).orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
		
		employeeRepository.delete(emp);
		
		return "employee with empId: "+emp.getEmpId()+" is deleted...!!!";
	}
	
	@Override
	public String changePassword(String companyMailId, String oldPassword, String newPassword) 
	{
	    // Log the action of changing password
	    log.info("Changing password for user with company email: {}", companyMailId);
	    
	    // Fetch the employee by email, and handle if not found
	    Employee emp = employeeRepository.findByCompanyMailId(companyMailId)
	            							.orElseThrow(() -> new UsernameNotFoundException("User not found for email: " + companyMailId));

	    // Log the comparison process without printing the actual passwords (for security reasons)
	    log.debug("Stored encoded password exists for the user.");

	    // Compare the raw old password with the encoded password stored in the database
	    if (passwordEncoder.matches(oldPassword, emp.getPassword()))
	    {
	        // Old password is correct, proceed to update with new password
	        emp.setPassword(passwordEncoder.encode(newPassword));
	        employeeRepository.save(emp);
	        
	        log.info("Password changed successfully for user: {}", companyMailId);
	        return "Password changed successfully!";
	    } 
	    else 
	    {
	        // Old password does not match, log an error
	        log.error("Old password is incorrect for user: {}", companyMailId);
	        throw new IncorrectPasswordException("Old password is incorrect!");
	    }
	}
	
	@Override
    public String forgotPassword(String empId, String companyMailId)
	{ 
		
		Employee emp=employeeRepository.findByEmpIdOrCompanyMailId(empId, companyMailId).orElseThrow( ()-> new UsernameNotFoundException("username not found...!!!") );
		
		String tempPassword=generateTempPassword(emp.getFirstName()); 
		emp.setPassword(passwordEncoder.encode(tempPassword));
		employeeRepository.save(emp);
		mailUtils.sendTempPasswordToEmployee(emp.getCompanyMailId(), emp.getEmpId(), tempPassword);
		
		return "temporary password in sended on mailId: "+emp.getCompanyMailId();
	}
	
	
	// ==================== Utility Method ===========================
	
	public static String generateEmpId()
	{
		long empId= (long) Math.floor(Math.random()*1000+1);
		return "RAK-"+empId;
	}
	
	public static String generateCompanyMailId(String firstName, String lastName) 
	{
	    if (firstName == null || firstName.isEmpty()) 
	        throw new IllegalArgumentException("First name cannot be null or empty");
	    
	    if (lastName == null || lastName.isEmpty()) 
	        throw new IllegalArgumentException("Last name cannot be null or empty");

	    return firstName + lastName.substring(0, 1) + "@raksofttech.com";
	}

	
	public static String generateTempPassword(String firstName)
	{
		return firstName.substring(0, 1).toUpperCase()+firstName.substring(1).toLowerCase()+"@123";
	}
	
	
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

}
