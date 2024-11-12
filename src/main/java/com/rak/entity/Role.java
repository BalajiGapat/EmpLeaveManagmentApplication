package com.rak.entity;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name="roles")
@Builder
@Getter
@Setter
@AllArgsConstructor
public class Role 
{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long roleId;
	
	private String role;
	
	public Role()
	{
		
	}
	
	public Role(String role)
	{
		this.role=role;
	}
	
	@ManyToMany(mappedBy="roles", fetch=FetchType.EAGER)
	@Builder.Default
	private Set<Employee> emps=new HashSet<>();
	
	public String toString()
	{
		return this.role;
	}
	
	// ================= Helper Methods ====================
	
	public void assignRoleToEmp(Employee employee)
	{
		this.getEmps().add(employee); // assign employee to role
		employee.getRoles().add(this); // assign role to employee
	}
	
	
	public void removeRoleFromEmp(Employee employee)
	{
		this.getEmps().remove(employee); // remove employee from role 
		employee.getRoles().remove(this); // remove role from employee
	}
	
	public void removeAllEmpsFromRole()
	{
		Set<Employee> empSet=this.getEmps();
		
		Iterator<Employee> itr=empSet.iterator();
		while(itr.hasNext())
		{
			Employee employee=itr.next();
			employee.getRoles().remove(this);
		}
		
	}
}
