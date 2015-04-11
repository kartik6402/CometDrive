package com.example.cometdrive;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;

@DynamoDBTable(tableName = "StatisticInformation")
public class DBStatisticInformationClass 
{
	private String routeid;
    private String timestamp;
    private int vehicleid;
    private double latitude;
    private double longitude;
    private int ridersatstop;
    private int currentriders;
    private int totalriders;
    private int vehiclecapacity;
    
	@DynamoDBHashKey (attributeName = "RouteID")
    public String getRouteID() {return routeid;}
    public void setRouteID(String routeid) {this.routeid = routeid;}
    
    @DynamoDBRangeKey (attributeName = "TimeStamp")
    public String gettimestamp() {return timestamp; }
	public void settimestamp(String timestamp) {this.timestamp = timestamp;}
    
	@DynamoDBAttribute (attributeName = "VehicleID" )
    public int getVehicleid() {return vehicleid; }
	public void setVehicleid(int vehicleid) {this.vehicleid = vehicleid;	}
	
	@DynamoDBAttribute (attributeName = "Latitude" )
	public double getLatitude() {return latitude;}
	public void setLatitude(double latitude) {this.latitude = latitude;	}
	
	@DynamoDBAttribute (attributeName = "Longitude" )
	public double getLongitude() {return longitude;	}
	public void setLongitude(double longitude) {this.longitude = longitude;	}
	
	@DynamoDBAttribute (attributeName = "RidersAtStop" )
	public int getRidersatstop() {	return ridersatstop; }
	public void setRidersatstop(int ridersatstop) {	this.ridersatstop = ridersatstop;}
	
	@DynamoDBAttribute (attributeName = "CurrentRiders" )
	public int getCurrentriders() {	return currentriders; }
	public void setCurrentriders(int currentriders) {this.currentriders = currentriders; }
	
	@DynamoDBAttribute (attributeName = "TotalRiders" )
	public int getTotalriders() {return totalriders;}
	public void setTotalriders(int totalriders) {this.totalriders = totalriders;}
	
	@DynamoDBAttribute (attributeName = "VehicleCapacity" )
	public int getCapacity() {return vehiclecapacity;}
	public void setCapacity(int vehiclecapacity) {	this.vehiclecapacity = vehiclecapacity;	}
	
 }