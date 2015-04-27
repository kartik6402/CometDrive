package com.example.cometdrive;

import java.sql.Timestamp;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

public class DisplayBlank extends Activity implements LocationListener
{

	LocationManager lm;
	DriverDatabaseController dbcontroller;
	SharedPreferences pref;
	double vehicleLatitude;
	double vehicleLongitude;
	int updatecounter =0;
	Editor editor;
	AudioManager am;
	ComponentName cmp;
	int locationUpdateCounter =0;
	
	//################################Life Cycle Events ###################################//
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.blank);
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		Initialize();
		UpdateStatistics();
		editor.putInt("RidersAtStop", 0);
		editor.commit();
	}
	
	@Override
	protected void onRestart() {
		super.onRestart();
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onPause(){
		super.onPause();
		lm.removeUpdates(this);
		am.unregisterMediaButtonEventReceiver(cmp);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		lm.removeUpdates(this);
	}
	
	//########################################### Initialize ##################################//
	
@SuppressWarnings("deprecation")
	public void Initialize(){
		dbcontroller = new DriverDatabaseController(this);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		lm = (LocationManager)getSystemService(LOCATION_SERVICE);
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,0,this);
        this.onLocationChanged(null);
        
        am = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
        cmp = new ComponentName(getPackageName(),DummyBluetoothButtonReceiver.class.getName());
        am.registerMediaButtonEventReceiver(cmp);
        
        pref= getSharedPreferences("COMET", 0);
        editor = pref.edit();
     }
	
	public void UpdateStatistics()
	{
		String RouteID = pref.getString("RouteID", "0");
		java.util.Date date= new java.util.Date();
		String timeStamp = new Timestamp(date.getTime())+"";
		int VehicleID = pref.getInt("VehicleID", 0);
		int RidersAtStop = pref.getInt("RidersAtStop", 0);
		int CurrentRiders = pref.getInt("CurrentRiders", 0);
		int TotalRiders = pref.getInt("TotalRiders", 0);
		int Capacity = pref.getInt("VehicleCapacity", 0);
		double Lat = Double.parseDouble(pref.getString("Latitude", "0"));
		double Long = Double.parseDouble(pref.getString("Longitude", "0"));
		if(RidersAtStop>0)
		{
			dbcontroller.UpdateStatisticsInformation(RouteID,timeStamp,VehicleID,Lat,Long,RidersAtStop,CurrentRiders,TotalRiders,Capacity);
			Log.i("Comet","Statistics Information Table Updated");
		}
	}

	//################################## Location Listener Events #####################################//
	@Override
	public void onLocationChanged(Location location) 
	{	
		if(location != null)
		{
			float cSpeed_mps = location.getSpeed();
			//float cSpeed_kmph = (float) (cSpeed_mps* 3600/(1000)); //1609.344 for miles
			vehicleLatitude = location.getLatitude();
			vehicleLongitude =location.getLongitude();
			locationUpdateCounter++;
			
			if(cSpeed_mps<=0.000001)
			{
				lm.removeUpdates(this);
				this.finish();
			}
		}
		
		
		if(locationUpdateCounter == 5)
		{
			dbcontroller.UpdateLiveVehicleInformation(pref.getString("RouteID", "0"),pref.getInt("VehicleID",0),vehicleLatitude,vehicleLongitude,vehicleLatitude, vehicleLongitude,pref.getInt("VehicleCapacity",0),	pref.getInt("CurrentRiders",0),	pref.getInt("TotalRiders",0));
			Log.i("Comet","Live Vehicle Information Table Updated from Blank Screen");
			locationUpdateCounter=0;
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) { }

	@Override
	public void onProviderEnabled(String provider){		}

	@Override
	public void onProviderDisabled(String provider) {	}
		
}
