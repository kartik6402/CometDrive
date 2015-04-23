package com.example.cometdrive;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;

@DynamoDBTable(tableName = "DriverInfo")
public class DBDriverInfoClass 
{
	private String driverid;
	private String email;
	private String name;
	private Long phone;
	
	@DynamoDBHashKey (attributeName = "DriverID")
	public String getDriverid() {	return driverid; }
	public void setDriverid(String driverid) {	this.driverid = driverid;	}

	@DynamoDBAttribute (attributeName = "Email" )
	public String getEmail() {	return email;	}
	public void setEmail(String email) {	this.email = email; }
	
	@DynamoDBAttribute (attributeName = "Name" )
	public String getName() {	return name;	}
	public void setName(String name) {		this.name = name;	}
	
	@DynamoDBAttribute (attributeName = "Phone" )
	public Long getPhone() {		return phone;	}
	public void setPhone(Long phone) {		this.phone = phone;	}
	
}
