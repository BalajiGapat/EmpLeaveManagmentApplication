package com.rak.enums;

public enum LeaveType 
{
	SICK_LEAVE(10),
	CASUAL_LEAVE(10),
	OTHER(10);
	
	private int leaveDays;
	
	private LeaveType(int leaveDays)
	{
		this.leaveDays=leaveDays;
	}

	public int getLeaveDays() 
	{
		return this.leaveDays;
	}
	
}
