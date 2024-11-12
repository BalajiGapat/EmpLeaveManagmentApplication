package com.rak.responsedto;

import java.time.LocalDate;

import com.rak.enums.LeaveStatus;
import com.rak.enums.LeaveType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmpLeaveResponse 
{
	private Long leaveId;
	private LeaveType leaveType;
	private LocalDate startDate;
	private LocalDate endDate;
	private LeaveStatus leaveStatus;
	private LeaveStatus managerAction;
	private LeaveStatus hrAction;
	private String reason;
	private String empFullName;
	private String department="Development";
}
