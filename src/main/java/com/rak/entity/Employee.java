package com.rak.entity;

import java.sql.Blob;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name="emps")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
// @ToString(exclude = "leaveList")
@ToString(exclude = {"password", "empPhoto", "empLeaveBal", "leaveList"}) // Exclude sensitive fields
@Builder
@EqualsAndHashCode(exclude = "empLeaveBal") // Exclude empLeaveBal to avoid circular reference
public class Employee 
{
    @Id
    private String empId; // given by admin
    private String firstName;
    private String middleName;
    private String lastName;
   
    @Email
    @NotBlank
    private String personalMailId; 
    
    @Email
    private String companyMailId; // given by admin
    
    private Long mobile;


    @JsonIgnore
    private String password; // temporary password given by admin
    
    @Lob
    @Builder.Default
    @JsonIgnore
    private Blob empPhoto=null;
   
    @Builder.Default
    private boolean isProfileCompleted=false; // default false
    
    @OneToMany(mappedBy = "emp", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<EmpLeaves> leaveList = new ArrayList<>();
    
	@OneToOne(mappedBy = "employee", cascade = CascadeType.ALL)
	@JsonIgnore
    private EmpLeaveBal empLeaveBal;
	
	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinTable
	(
		name="emp_roles",
		joinColumns = @JoinColumn(name="emp_id", referencedColumnName = "empId"),
		inverseJoinColumns = @JoinColumn(name="role_id", referencedColumnName = "roleId")
	)
    @Builder.Default
    private Set<Role> roles=new HashSet<>();
	
	
	
	// Add a helper method to add a LeaveRequest
	public void addEmpLeave(EmpLeaves empLeaves)
	{
		leaveList.add(empLeaves);
		empLeaves.setEmp(this);
	}

}
    