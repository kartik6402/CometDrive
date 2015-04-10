package com.example.cometdrive;

import android.app.Activity;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.WindowManager;

public class DisplayBlank extends Activity implements LocationListener
{

	LocationManager lm;
	DriverDatabaseController dbcontroller;
	SharedPreferences pref;
	double vehicleLatitude;
	double vehicleLongitude;

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.blank);
		Initialize();
	}

	public void Initialize()
	{
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	    lm = (LocationManager)getSystemService(LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0,this);
        this.onLocationChanged(null);
        pref= getSharedPreferences("COMET", 0);
        //new MyTask().execute();
	}
	
	@Override
	public void onLocationChanged(Location location) 
	{
		// TODO Auto-generated method stub
		if(location != null)
		{
			float cSpeed_mps = location.getSpeed();
			float cSpeed_kmph = (float) (cSpeed_mps* 3600/(1000)); //1609.344 for miles

			if(cSpeed_kmph==0)
			{
				lm.removeUpdates(this);
				this.finish();
			}
			else
			{
				//TextView txt = (TextView)findViewById(R.id.txtloc);
				//txt.setText(cSpeed_kmph+" Kmph");
				//txt.setText("");
			}
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) { }

	@Override
	public void onProviderEnabled(String provider) {	}

	@Override
	public void onProviderDisabled(String provider) {	}
		
}
