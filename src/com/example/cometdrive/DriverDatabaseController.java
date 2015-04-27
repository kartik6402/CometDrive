package com.example.cometdrive;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import android.app.Activity;
import android.content.Context;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.*;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;


public class DriverDatabaseController extends Activity
{
	CognitoCachingCredentialsProvider credentialsProvider;
	AmazonDynamoDBClient ddbClient;
	DynamoDBMapper mapper;
		
	public DriverDatabaseController(Context mcontext)
	{
		credentialsProvider = new CognitoCachingCredentialsProvider(
		    mcontext, // Context
		    "us-east-1:12837181-a44f-4651-a188-0204c5a59553", // Identity Pool ID
		    Regions.US_EAST_1 // Region
		);		
		
		ddbClient = new AmazonDynamoDBClient(credentialsProvider);
		mapper = new DynamoDBMapper(ddbClient);
	}
	
	public void UpdateLiveVehicleInformation(String RouteID,int vehicleID,double prevLat,double prevLong,double Latitude, double Longitude,int TotalCapacity,int CurrentRiders,int TotalRiders)
	{
		DBLiveVehicleInformationClass Route = new DBLiveVehicleInformationClass();
        Route.setRouteID(RouteID);
        Route.setVehicleID(vehicleID);	
        Route.setPrevLat(prevLat);
        Route.setPrevLong(prevLong);
        Route.setVehicleLat(Latitude);
        Route.setVehicleLong(Longitude);
        Route.setVehicleTotalCapacity(TotalCapacity);
        Route.setCurrentRiders(CurrentRiders);        
        Route.setTotalRiders(TotalRiders);
        mapper.save(Route);
    }
	
	public void UpdateStatisticsInformation(String RouteID,String TimeStamp,int vehicleID,double Latitude,double Longitude,int RidersAtStop,int CurrentRiders,int TotalRiders,int Capacity)
	{
		DBStatisticInformationClass statistic = new DBStatisticInformationClass();
		statistic.setRouteID(RouteID);
		statistic.settimestamp(TimeStamp);
		statistic.setVehicleid(vehicleID);
		statistic.setLatitude(Latitude);
		statistic.setLongitude(Longitude);
		statistic.setRidersatstop(RidersAtStop);
		statistic.setCurrentriders(CurrentRiders);
		statistic.setTotalriders(TotalRiders);
		statistic.setCapacity(Capacity);
		mapper.save(statistic);
	}
	
	public void DeleteLiveVehicleInformation(String RouteID,int VehicleID)
	{
		DBLiveVehicleInformationClass Route = new DBLiveVehicleInformationClass();
		Route.setRouteID(RouteID);
		Route.setVehicleID(VehicleID);
		mapper.delete(Route);
	}
	
	public List<String> LoadRouteInfo()
	{
		List<String> dbrouteInfo = new ArrayList<String>();
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
		PaginatedScanList<DBRouteInformationClass> result = mapper.scan(DBRouteInformationClass.class, scanExpression);
		for (DBRouteInformationClass routeInformation : result) 
		{
			if(!dbrouteInfo.contains(routeInformation.getRouteid()))
			{
				dbrouteInfo.add(routeInformation.getRouteid());
			}
		}
		Collections.sort(dbrouteInfo);
		return dbrouteInfo;
	}
	
	public int RetrieveAndUpdateVehicleID(String routeid,int vehiclecapacity, int currentriders,int totalriders)
	{
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
		PaginatedScanList<DBLiveVehicleInformationClass> result = mapper.scan(DBLiveVehicleInformationClass.class, scanExpression);
		int VehicleID=0;
		
		for (DBLiveVehicleInformationClass routeInformation : result) 
		{
			if(routeInformation.getRouteID().equals(routeid))
			{
				if(routeInformation.getVehicleID()>VehicleID)
				{
					VehicleID = routeInformation.getVehicleID();
				}
			}
		}
		VehicleID++;
		//UpdateLiveVehicleInformation(routeid, VehicleID, 0.0, 0.0, vehiclecapacity, currentriders, totalriders);
        return VehicleID;
    }

	@SuppressWarnings("unchecked")
	public String FindDriver(String DriverID,java.sql.Timestamp currentTime) 
	{
		DBScheduleInformationClass driver= new DBScheduleInformationClass();
		String returnVal = "";
		boolean validIDFlg= false;
		driver.setDriverid(DriverID);
		@SuppressWarnings("rawtypes")
		DynamoDBQueryExpression queryExpression = new DynamoDBQueryExpression()
        	.withHashKeyValues(driver)
        	.withConsistentRead(false);

		PaginatedQueryList<DBScheduleInformationClass> result = mapper.query(DBScheduleInformationClass.class, queryExpression);
		
		for (DBScheduleInformationClass dbScheduleInformationClass : result) 
		{
			java.sql.Timestamp startTimeStamp = java.sql.Timestamp.valueOf(dbScheduleInformationClass.getStarttime());
			java.sql.Timestamp endTimeStamp = java.sql.Timestamp.valueOf(dbScheduleInformationClass.getEndtime());
			validIDFlg =true;
			startTimeStamp = new java.sql.Timestamp(startTimeStamp.getTime()-(16*60*1000));
			if(currentTime.after(startTimeStamp) && currentTime .before(endTimeStamp))
			{
				return dbScheduleInformationClass.getRouteid();
			}
		}
		if(!validIDFlg)
			returnVal="No ID";
		
		return returnVal;
	}

	public void UpdateShiftInformation(String routeID, String driverID,String startTime, String endTime, int totalRiders) 
	{
		DBShiftInformationClass Shift = new DBShiftInformationClass();
		Shift.setRouteid(routeID);
		Shift.setStarttime(startTime);	
		Shift.setEndtime(endTime);
		Shift.setDriverid(driverID);
		Shift.setTotalriders(totalRiders);
        mapper.save(Shift);
	}
}

