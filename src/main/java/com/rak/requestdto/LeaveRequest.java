package com.rak.requestdto;

import java.time.LocalDate;

import com.rak.enums.LeaveType;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveRequest 
{
	@NotNull(message = "Leave type must not be null")
    private LeaveType leaveType;

    @NotNull(message = "Start date must not be null")
    @FutureOrPresent(message = "Start date must be today or in the future")
    private LocalDate startDate;

    @NotNull(message = "End date must not be null")
    @FutureOrPresent(message = "End date must be today or in the future")
    private LocalDate lastDate;

    @NotBlank(message = "Reason must not be blank")
    @Size(min = 10, max = 250, message = "Reason must be between 10 and 250 characters")
    private String reason;
}
