package com.rak.requestdto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationRequest 
{
	@NotNull(message = "First name cannot be null")
	@Size(min=2, message = "First name must be at least 2 characters long")
	private String firstName;
	
	@NotNull(message = "last name cannot be null")
	private String lastName;
	
	@Email(message = "Please provide a valid mail id...!!!")
	private String personalMailId;
}
