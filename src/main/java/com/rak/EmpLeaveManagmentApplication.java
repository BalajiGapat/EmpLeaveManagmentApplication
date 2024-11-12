package com.rak;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.rak.requestdto.RegistrationRequest;
import com.rak.service.EmployeeService;

import lombok.RequiredArgsConstructor;
import springfox.documentation.oas.annotations.EnableOpenApi;

@SpringBootApplication
@RequiredArgsConstructor
@EnableScheduling
// @EnableOpenApi
public class EmpLeaveManagmentApplication // implements CommandLineRunner
{
	private final EmployeeService employeeService;
	
	public static void main(String[] args) 
	{
		SpringApplication.run(EmpLeaveManagmentApplication.class, args);
	}

//	@Override
//	public void run(String... args) throws Exception 
//	{
//		RegistrationRequest registrationRequest1=new RegistrationRequest("balaji", "gapat", "gapatbalaji2901@gmail.com");
//		employeeService.registerEmployee(registrationRequest1);
//		
//		RegistrationRequest registrationRequest2=new RegistrationRequest("sachin", "bharate", "gapatbalaji1896@gmail.com");
//		employeeService.registerEmployee(registrationRequest2);
//	}
	
}
