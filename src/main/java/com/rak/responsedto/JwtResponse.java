package com.rak.responsedto;

import java.util.Set;

import com.rak.entity.Role;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JwtResponse 
{
	private String empId;
	private String companymailId;
	private String token;
	@Builder.Default
	private String tokenType="Bearer";
	private Set<String> roles;
}
