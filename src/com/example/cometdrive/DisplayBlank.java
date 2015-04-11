package com.example.cometdrive;

import java.sql.Timestamp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.Toast;

public class DisplayBlank extends Activity implements LocationListener
{

	LocationManager lm;
	DriverDatabaseController dbcontroller;
	SharedPreferences pref;
	double vehicleLatitude;
	double vehicleLongitude;
	int updatecounter =0;
	UpdateDatabaseTask updatertask;
	Editor editor;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.blank);
		Initialize();
		UpdateStatistics();
		editor.putInt("RidersAtStop", 0);
		editor.commit();
	}
	
	@Override
	protected void onResume() 
	{
		// TODO Auto-generated method stub
		super.onResume();
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0,this);
	}
	
	@Override
	protected void onPause() 
	{
		// TODO Auto-generated method stub
		super.onPause();
		updatertask.cancel(true);
	}
	public void Initialize()
	{
		dbcontroller = new DriverDatabaseController(this);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	    lm = (LocationManager)getSystemService(LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0,this);
        this.onLocationChanged(null);
        pref= getSharedPreferences("COMET", 0);
        editor = pref.edit();
        updatertask = new UpdateDatabaseTask();
        updatertask.execute();
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
		}
	}

	@Override
	public void onLocationChanged(Location location) 
	{
		
		if(location != null)
		{
			float cSpeed_mps = location.getSpeed();
			float cSpeed_kmph = (float) (cSpeed_mps* 3600/(1000)); //1609.344 for miles
			vehicleLatitude = location.getLatitude();
			vehicleLongitude =location.getLongitude();
			
			
			Toast.makeText(this, String.valueOf(vehicleLatitude), Toast.LENGTH_SHORT).show();
			
			if(cSpeed_kmph==0)
			{
				lm.removeUpdates(this);
				this.finish();
			}
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) { }

	@Override
	public void onProviderEnabled(String provider) {	}

	@Override
	public void onProviderDisabled(String provider) {	}
	
	//Asynchronous Task to Update values based on the button Click event.
	class UpdateDatabaseTask extends AsyncTask<Void, Integer, Void>
    {
    	@Override
		protected void onPreExecute() 
		{ 	}
    	
		@Override
		protected Void doInBackground(Void... params) 
		{
			while(true)
			{
				updatecounter++;
				SharedPreferences PREF = getSharedPreferences("COMET", 0);
				int TodayTotal = PREF.getInt("TotalRiders", 0);
				int Current = PREF.getInt("CurrentRiders", 0);
				publishProgress(TodayTotal,Current);
				
				try
				{
					if(updatecounter == 100)
					{						
						dbcontroller.UpdateLiveVehicleInformation(pref.getString("RouteID", "0"),pref.getInt("VehicleID",0),vehicleLatitude, vehicleLongitude,pref.getInt("VehicleCapacity",0),	pref.getInt("CurrentRiders",0),	pref.getInt("TotalRiders",0));
						updatecounter=0;
					}
					Thread.sleep(100);				
				}
				catch(InterruptedException e)
				{ 
					e.printStackTrace();
				}
			}
			//return null;
		}
		
		@Override
		protected void onProgressUpdate(Integer... values) 
		{
			// TODO Auto-generated method stub
			//txtToday.setText(values[0].toString());	
			//txtCurrent.setText(values[1].toString());
			
		}
		@Override
		protected void onPostExecute(Void result) 
		{
			// TODO Auto-generated method stub
			
		}
	}
		
}
