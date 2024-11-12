package com.rak.mailutils;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.rak.enums.LeaveType;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class MailUtils 
{
	private final JavaMailSender javaMailSender;
	
	@Value("${spring.mail.username}")
	private String fromEmailId;
	
	// Inject the manager and HR email IDs from application.properties
    @Value("${manager.email}")
    private String managerEmailId;

    @Value("${hr.email}")
    private String hrEmailId;
	
	
	public void sendEmailToEmployee(String fromMailId, String toMailId, String empId, String companyMailId, String tempPassword) 
	{
		String body = "Dear Employee,\n\n"
                + "Welcome to RAK Softech Private Limited!\n\n"
                + "We are excited to have you onboard. Below are your employee credentials:\n\n"
                + "Employee ID: " + empId + "\n"
                + "Company Email ID: " + companyMailId + "\n"
                + "Temporary Password: " + tempPassword + "\n\n"
                + "Please make sure to log in and change your temporary password at your earliest convenience.\n\n"
                + "You can log in to the system using the following link: " + "https://raksoftech.com/\n\n"
                + "If you have any questions or require assistance, feel free to contact us.\n\n"
                + "Best Regards,\n"
                + "RAK Softech Pvt Ltd\n"
                + "HR Team";

	    
	    String subject = "Welcome to RAK SoftTech Solution - Employee Credentials";
	    
	    SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
	    simpleMailMessage.setFrom(fromMailId);
	    simpleMailMessage.setTo(toMailId);
	    simpleMailMessage.setSubject(subject);
	    simpleMailMessage.setText(body);
	    
	    javaMailSender.send(simpleMailMessage);
	    log.info("login credenticals is sended to employee through mail id: {} ", toMailId);
	    System.out.println("Email sent successfully...!!!");
	}
	
	
	public void sendLeaveNotificationToManager(String fromMailId, String employeeFullName, LeaveType leaveType, String managerEmail, LocalDate startDate, LocalDate lastDate, String reason) 
	{
        // Construct email message
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromMailId); // ("noreply@company.com"); // Set your sender's email
        message.setTo(managerEmail);  
        message.setSubject("Leave Application Submitted by " + employeeFullName);
        
        String mailContent = "Dear Manager,\n\n" +
                "This is to inform you that " + employeeFullName + " has applied for leave. Below are the details of the leave request:\n\n" +
                "Leave Type: " + leaveType + "\n" +
                "Leave Duration: " + startDate + " to " + lastDate + "\n" +
                "Reason for Leave: " + reason + "\n\n" +
                "Please review the application and take necessary action in the system.\n\n" +
                "Best Regards,\n" +
                "Employee Leave Management System\n" +
                "RAK Softech Pvt Ltd";
        
        message.setText(mailContent);
        
        // Send email
        javaMailSender.send(message);
        			
     System.out.println("notification mail sent successfully to Manager and HR...!!!");
    
	}
	
	public void sendLeaveNotificationToHr(String fromMailId, String employeeFullName, LeaveType leaveType, String hrEmail, LocalDate startDate, LocalDate lastDate, String reason) 
	{
        // Construct email message
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromMailId); // ("noreply@company.com"); // Set your sender's email
        message.setTo(hrEmail);  // Send to both Manager and HR
        message.setSubject("Leave Application Submitted by " + employeeFullName);
        
        String mailContent = "Dear HR,\n\n" +
                "This is to inform you that the leave application for " + employeeFullName + " has been reviewed by the manager. Below are the details of the request:\n\n" +
                "Leave Type: " + leaveType + "\n" +
                "Leave Period: " + startDate + " to " + lastDate + "\n" +
                "Reason for Leave: " + reason + "\n\n" +
                "Please proceed with the necessary steps for final processing in the system.\n\n" +
                "Best Regards,\n" +
                "Employee Leave Management System\n" +
                "RAK Softech Pvt Ltd";
        
        message.setText(mailContent);
        
        // Send email
        javaMailSender.send(message);
        System.out.println("notification mail sent successfully to Manager and HR...!!!");
    }
	
	
	public void sendTempPasswordToEmployee(String toMailId, String empId, String tempPassword) 
	{
        // Create the email body
        String body = "Dear Employee,\n\n"
                + "A temporary password has been generated for your account:\n\n"
                + "Employee ID: " + empId + "\n"
                + "Temporary Password: " + tempPassword + "\n\n"
                + "Please log in and change your password immediately for security purposes.\n\n"
                + "You can log in to the system using the following link: https://raksoftech.com/\n\n"
                + "If you have any questions or face any issues, feel free to contact support.\n\n"
                + "Best Regards,\n"
                + "RAK Softech Pvt Ltd\n"
                + "HR Team";

        String subject = "Your Temporary Password for EmpId: "+empId;

        // Create the email message
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom(fromEmailId);
        simpleMailMessage.setTo(toMailId);
        simpleMailMessage.setSubject(subject);
        simpleMailMessage.setText(body);

        // Send the email
        javaMailSender.send(simpleMailMessage);

        // Log success
        log.info("Temporary password sent to employee with email: {}", toMailId);
        System.out.println("Temporary password email sent successfully to: " + toMailId);
    }
	
	
	
}
