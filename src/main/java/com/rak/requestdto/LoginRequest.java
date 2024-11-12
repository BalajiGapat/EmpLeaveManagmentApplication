package com.rak.requestdto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class LoginRequest
{
	@NotBlank(message = "Company email ID must not be blank")
    @Email(message = "Please provide a valid email address")
    private String companyMailId;

    @NotBlank(message = "Password must not be blank")
    @Size(min = 8, message = "Password must be at least 8 characters long")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$", message = "Password must contain at least one digit, one special character, and no whitespace")
    private String password;
    
    // Explanation of the Regular Expression in @Pattern:
    	// 1) ^(?=.*[0-9]): Ensures at least one numeric digit.
    	// 2) (?=.*[@#$%^&+=!]): Ensures at least one special character from the list (@#$%^&+=!). You can adjust this list based on your required special characters.
    	// 3) (?=\\S+$): Ensures no whitespace characters (spaces, tabs, etc.).
    	// 4) .{8,}$: Ensures the password has a minimum length of 8 characters.
}
