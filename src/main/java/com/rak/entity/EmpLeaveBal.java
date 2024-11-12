package com.rak.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rak.enums.LeaveType;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@Entity
@Table(name="emp_leave_bal")
@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "employee")
public class EmpLeaveBal
{	
	@Id
    private String empId; // Now the primary key is the same as the Employee's empId
	
	@Builder.Default
	private int sickLeaveBal=LeaveType.SICK_LEAVE.getLeaveDays();
	
	@Builder.Default
	private int casualLeaveBal=LeaveType.CASUAL_LEAVE.getLeaveDays();
	
	@Builder.Default
	private int otherLeaveBal=LeaveType.OTHER.getLeaveDays();
	
	@Builder.Default
	private int totalLeaveBal=LeaveType.SICK_LEAVE.getLeaveDays()+LeaveType.CASUAL_LEAVE.getLeaveDays()+LeaveType.OTHER.getLeaveDays();

	@OneToOne
    @MapsId // This makes empId the same as the primary key of Employee
    @JoinColumn(name = "emp_id") // Foreign key to Employee table
	@JsonIgnore
	private Employee employee;
	
	
}
