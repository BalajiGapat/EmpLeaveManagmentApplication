package com.rak.controller;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rak.requestdto.LoginRequest;
import com.rak.responsedto.JwtResponse;
import com.rak.security.jwt.JwtUtils;
import com.rak.security.user.SecurityUser;
import com.rak.service.EmployeeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController 
{
	private final AuthenticationManager authenticationManager;
	private final JwtUtils jwtUtils;
	
	private final EmployeeService employeeService;
	
	@PostMapping("/login") // http://localhost:8080/auth/login
	public ResponseEntity<JwtResponse> UserLogin(@RequestBody @Valid LoginRequest loginRequest)
	{
		Authentication authentication=authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getCompanyMailId(), loginRequest.getPassword()));
		
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwtToken=jwtUtils.generateJwtTokenForUser(authentication);
		
		SecurityUser user=(SecurityUser) authentication.getPrincipal();
		Set<String> roles=user
							.getAuthorities()
							.stream()
							.map(authority->authority.getAuthority())
							.collect(Collectors.toSet());
		
		JwtResponse response=JwtResponse
									.builder()
									.empId(user.getEmpId())
									.companymailId(user.getUsername())
									.roles(roles)
									.tokenType("Bearer")
									.token(jwtToken)
									.build();
									
		return ResponseEntity.ok(response); 
	}
	
	 
	@PostMapping("/forgot-password") // http://localhost:8080/auth/forgot-password
    public ResponseEntity<String> forgotPassword(@RequestParam(required = false) String empId, @RequestParam(required = false) String companyMailId)
    {
		System.out.println("in forgot password...!!!");
        // Validate input: at least one parameter (empId or companyMailId) must not be null
        if (empId == null && companyMailId == null) 
            return new ResponseEntity<>("Either Employee ID or Company Email ID must be provided.", HttpStatus.BAD_REQUEST);

        String response = employeeService.forgotPassword(empId, companyMailId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
	
}
