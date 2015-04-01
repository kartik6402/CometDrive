package com.example.cometdrive;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;

@DynamoDBTable(tableName = "RouteInformation")
public class DBDataClass 
{
    private String routeid;
    private String routeName;
	private String cabid;
    private String cab_lat;
    private String cab_long;
    private String cab_curr_capacity;
    private String cab_total_capacity;
    private String total_riders;
    
    @DynamoDBHashKey (attributeName = "RouteID")
    public String getRouteID() {return routeid;}
    public void setRouteid(String routeid) {this.routeid = routeid;}
    
    @DynamoDBAttribute (attributeName = "RouteName")
    public String getRouteName() {	return routeName; }
	public void setRouteName(String routeName) { this.routeName = routeName; }
	
    @DynamoDBAttribute (attributeName = "CabID")
    public String getCabID() {return cabid; }
	public void setCabid(String cabid) {this.cabid = cabid;}
	
	@DynamoDBAttribute (attributeName = "Cab_Lat")
	public String getCab_lat() {return cab_lat;	}
	public void setCab_lat(String cab_lat) {this.cab_lat = cab_lat;}
	
	@DynamoDBAttribute (attributeName = "Cab_Long")
	public String getCab_long() {return cab_long;}
	public void setCab_long(String cab_long) {this.cab_long = cab_long;}
	
	@DynamoDBAttribute (attributeName = "Cab_Curr_Capacity")
	public String getCab_curr_capacity() {return cab_curr_capacity;	}
	public void setCab_curr_capacity(String cab_curr_capacity) {this.cab_curr_capacity = cab_curr_capacity;}
	
	@DynamoDBAttribute (attributeName = "Cab_Total_Capacity")
	public String getCab_total_capacity() {	return cab_total_capacity;	}
	public void setCab_total_capacity(String cab_total_capacity) {this.cab_total_capacity = cab_total_capacity;	}
	
	@DynamoDBAttribute (attributeName = "Total_Riders")
	public String getTotal_riders() {return total_riders;}
	public void setTotal_riders(String total_riders) {	this.total_riders = total_riders;}
	
 }