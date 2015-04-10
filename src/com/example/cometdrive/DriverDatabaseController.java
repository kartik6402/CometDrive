package com.example.cometdrive;

import android.app.Activity;
import android.content.Context;

//import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.*;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;
//import com.amazonaws.services.dynamodbv2.model.*;

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
	
	public void UpdateLiveVehicleInformation(String RouteID,int vehicleID,double Latitude, double Longitude,int TotalCapacity,int CurrentRiders,int TotalRiders)
	{
		DBLiveVehicleInformationClass Route = new DBLiveVehicleInformationClass();
        Route.setRouteID(RouteID);
        Route.setVehicleID(vehicleID);	
        Route.setVehicleLat(Latitude);
        Route.setVehicleLong(Longitude);
        Route.setVehicleTotalCapacity(TotalCapacity);
        Route.setCurrentRiders(CurrentRiders);
        Route.setTotalRiders(TotalRiders);
        mapper.save(Route);
    }
	
	public void UpdateStatisticsInformation(String RouteID,String CabID,String Capacity,Context mcontext)
	{
		
	}
}

