package com.rak.responsedto;

import java.util.Set;

import com.rak.entity.Role;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class EmployeeResponse 
{
	private String empId;
	private String firstName;
    private String middleName;
    private String lastName;
    private String personalMailId; 
    private String companyMailId;
    private Long mobile;
    private Set<String> role;
}
