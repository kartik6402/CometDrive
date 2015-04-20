package com.example.cometdrive;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

//import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
//import com.amazonaws.auth.policy.Condition;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.*;
//import com.amazonaws.services.dynamodbv2.model.AttributeValue;
//import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.*;
//import com.amazonaws.services.dynamodbv2.model.*;

public class DriverInputScreen extends Activity implements android.view.View.OnClickListener 
{
	//Variables
	Spinner spnRoute;
	EditText etCabCapacity;
	Button btnContinue;
	List<String> dropdownRouteList;
	SharedPreferences Pref;
	Editor editor;
	
	//Database Values
	CognitoCachingCredentialsProvider credentialsProvider;
	AmazonDynamoDBClient ddbClient;
	DynamoDBMapper mapper;
	DriverDatabaseController dbcontroller;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		//Code to Fix the Thread Issue
		//Strict Thread policy
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.driver_input_screen);
		Initialize();
		LoadRouteInfo();
	}
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		android.os.Process.killProcess(android.os.Process.myPid());
	}
	
	public void LoadRouteInfo()
	{	
		dropdownRouteList = new ArrayList<String>();
		dropdownRouteList = dbcontroller.LoadRouteInfo();
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, dropdownRouteList);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnRoute.setAdapter(dataAdapter);
	}

	@Override
	protected void onResume() 
	{
		// TODO Auto-generated method stub
		super.onResume();
		if(Pref.getString("Close", "FALSE").equals("TRUE"))
		{
			editor.putString("Close", "FALSE");
			editor.commit();
			this.finish();
		}
	}
	@Override
	public void onClick(View v) 
	{
		switch (v.getId()) 
		{
			case R.id.btnContinue:
				String[] routeInformation = spnRoute.getSelectedItem().toString().split("-");
				String CabCapacity = etCabCapacity.getText().toString().trim();
				
				if(!CabCapacity.equals(""))
				{
					editor = Pref.edit();
					editor.putString("RouteID",routeInformation[0]);
				    editor.putString("RouteName",routeInformation[1]);
				    editor.putInt("TotalRiders",0);
				    editor.putInt("CurrentRiders",0);
				    editor.putString("RouteName",routeInformation[1]);
				    editor.putInt("VehicleCapacity",Integer.parseInt(CabCapacity));
				    editor.commit();
				    RetrieveAndUpdateVehicleID(routeInformation[0],Integer.parseInt(CabCapacity),0,0);
				    
				    Toast.makeText(this, routeInformation[0]+" "+ etCabCapacity.getText(), Toast.LENGTH_SHORT).show();
				    Intent DriverUserInterfaceControllerIntent = new Intent(this,DriverUserInterfaceController.class);
				    startActivity(DriverUserInterfaceControllerIntent);
				}
				else
					Toast.makeText(this, "Please enter the rider capacity of the cab", Toast.LENGTH_SHORT).show();
			break;

		default:
			break;
		}
		
	}
	
	public void RetrieveAndUpdateVehicleID(String routeid,int vehiclecapacity, int currentriders,int totalriders)
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
		editor.putInt("VehicleID", VehicleID);
		editor.commit();
		
		DriverDatabaseController db = new DriverDatabaseController(this);  
        db.UpdateLiveVehicleInformation(routeid, VehicleID, 0.0, 0.0, vehiclecapacity, currentriders, totalriders);
    }
	public void Initialize()
	{
		//Initialize Variables
		Pref = getSharedPreferences("COMET", 0);
		etCabCapacity= (EditText) findViewById(R.id.etCabCapacity);
		spnRoute = (Spinner) findViewById(R.id.spnSelectRoute);
	    btnContinue =(Button)findViewById(R.id.btnContinue);
	    btnContinue.setOnClickListener(this);
	    
	    dbcontroller = new DriverDatabaseController(this);
		//Initialize Database Connection
		credentialsProvider = new CognitoCachingCredentialsProvider(
			    this, // Context
			    "us-east-1:12837181-a44f-4651-a188-0204c5a59553", // Identity Pool ID
			    Regions.US_EAST_1 // Region
			);		
		ddbClient = new AmazonDynamoDBClient(credentialsProvider);
		mapper = new DynamoDBMapper(ddbClient);
	}
}
