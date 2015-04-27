package com.example.cometdrive;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

@DynamoDBTable(tableName = "ScheduleInformation")
public class DBScheduleInformationClass 
{
	private String driverid;
	private String starttime;
	private String endtime;
	private String routeid;
	
	@DynamoDBHashKey (attributeName = "DriverID")
	public String getDriverid() {return driverid;}
	public void setDriverid(String driverid) {this.driverid = driverid;}
	
	@DynamoDBRangeKey (attributeName = "StartTime" )
	public String getStarttime() {return starttime;}
	public void setStarttime(String starttime) {this.starttime = starttime;}
	
	@DynamoDBAttribute (attributeName = "EndTime" )
	public String getEndtime() {return endtime;}
	public void setEndtime(String endtime) {this.endtime = endtime;}
	
	@DynamoDBAttribute (attributeName = "RouteID" )
	public String getRouteid() {return routeid;	}
	public void setRouteid(String routeid) {this.routeid = routeid;}
	
}
