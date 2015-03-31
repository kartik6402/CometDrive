package com.example.cometdrive;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;

@DynamoDBTable(tableName = "RouteInfo")
public class DBDataClass {
    private String routeid;
    private String cabid;
    private String name;
    private String cab_lat;
    private String cab_long;
    private String cab_curr_capacity;
    private String cab_total_capacity;
    private String total_riders;
    
    
    //
    @DynamoDBAttribute (attributeName = "Name")
    public String getName() {return name;}
	public void setName(String name) { this.name = name;}

	//
	@DynamoDBHashKey (attributeName = "RouteID")
    public String getRouteID() {return routeid;}
    public void setRouteid(String routeid) {this.routeid = routeid;}
	
    //
	@DynamoDBRangeKey (attributeName = "CabID")
    public String getCabID() {return cabid; }
	public void setCabid(String cabid) {this.cabid = cabid;}

	
 }