package com.example.cometdrive;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

@DynamoDBTable(tableName = "ShiftInformation")
public class DBShiftInformationClass 
{

	private String routeid;
	private String driverid;
	private String starttime;
	private String endtime;
	private int totalriders;
	
	@DynamoDBHashKey (attributeName = "RouteID")
	public String getRouteid() {return routeid;	}
	public void setRouteid(String routeid) {this.routeid = routeid;}
	
	@DynamoDBAttribute (attributeName = "DriverID" )
	public String getDriverid() {	return driverid;	}
	public void setDriverid(String driverid) {	this.driverid = driverid;}
	
	@DynamoDBRangeKey (attributeName = "ShiftStartTime")
	public String getStarttime() {	return starttime;	}
	public void setStarttime(String starttime) {this.starttime = starttime;}
	
	@DynamoDBAttribute (attributeName = "ShiftEndTime" )
	public String getEndtime() {return endtime;}
	public void setEndtime(String endtime) {this.endtime = endtime;}
	
	@DynamoDBAttribute (attributeName = "TotalRiders" )
	public int getTotalriders() {return totalriders;}
	public void setTotalriders(int totalriders) {this.totalriders = totalriders;}
	
}
