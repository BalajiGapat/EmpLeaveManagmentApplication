package com.rak.entity;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.rak.enums.LeaveStatus;
import com.rak.enums.LeaveType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="emp_leave")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class EmpLeaves 
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long leaveId;
	
	@Enumerated(EnumType.STRING)
	private LeaveType leaveType;
	
	private LocalDate startDate;
	
	private LocalDate lastDate;

	private String reason;
	
	@Enumerated(EnumType.STRING)
	@Builder.Default
	private LeaveStatus leaveStatus=LeaveStatus.PENDING;

	// HR level action
	@Enumerated(EnumType.STRING)
	@Builder.Default
	private LeaveStatus managerAction=LeaveStatus.PENDING;
	
	
	// HR level action
	@Enumerated(EnumType.STRING)
	@Builder.Default
	private LeaveStatus hrAction=LeaveStatus.PENDING;
	
	@ManyToOne(/* cascade = CascadeType.ALL */)
	@JsonIgnore
	@JoinColumn(name="emp_id") // FK=emp_id =. query method name findByEmpEmpId(String empEmpId)
	private Employee emp;
}
