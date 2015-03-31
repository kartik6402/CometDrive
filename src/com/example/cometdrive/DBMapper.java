package com.example.cometdrive;

import android.app.Activity;
import android.content.Context;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.*;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;
import com.amazonaws.services.dynamodbv2.model.*;

public class DBMapper extends Activity
{
	CognitoCachingCredentialsProvider credentialsProvider;
	AmazonDynamoDBClient ddbClient;
	DynamoDBMapper mapper;
		
	public void LoadData(Context mcontext)
	{
		credentialsProvider = new CognitoCachingCredentialsProvider(
		    mcontext, // Context
		    "us-east-1:12837181-a44f-4651-a188-0204c5a59553", // Identity Pool ID
		    Regions.US_EAST_1 // Region
		);		
		
		ddbClient = new AmazonDynamoDBClient(credentialsProvider);
		mapper = new DynamoDBMapper(ddbClient);
		
	}
	public void UpdateLocation(String RouteID,String CabID,String Latitude, String Longitude,Context mContext)
	{
		DBDataClass Route = new DBDataClass();
        Route.setRouteid("R1");
        Route.setCabid("C1");	
        Route.setCab_lat("3.4N");
        Route.setCab_long("45.6S");
        Route.setCab_total_capacity("8");
        Route.setCab_curr_capacity("6");
        Route.setTotal_riders("123");
        mapper.save(Route);
    }
	
	public void UpdateCapacity(String RouteID,String CabID,String Capacity,Context mcontext)
	{
		
	}
}

