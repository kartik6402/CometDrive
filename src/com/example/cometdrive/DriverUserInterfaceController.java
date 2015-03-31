package com.example.cometdrive;

import android.support.v7.app.ActionBarActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;


public class DriverUserInterfaceController extends ActionBarActivity implements LocationListener
{
	LocationManager lm;
	TextView txtacc;
	AudioManager am;
	ComponentName cmp;
	
	@SuppressWarnings("deprecation")
	@Override
    protected void onCreate(Bundle savedInstanceState) 
	{
		//Code to Fix the Thread Issue
		//Strict Thread policy
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		super.onCreate(savedInstanceState);               
        setContentView(R.layout.driver_user_interface);
        
        lm = (LocationManager)getSystemService(LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0,this);
        this.onLocationChanged(null);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        am = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
        cmp = new ComponentName(getPackageName(),BluetoothButtonReceiver.class.getName());
        am.registerMediaButtonEventReceiver(cmp);
         
        DBMapper db = new DBMapper();  
        db.LoadData(this);
        new MyTask().execute();   
	}
		
	@Override
	protected void onResume() {
		super.onResume();
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		lm = (LocationManager)getSystemService(LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0,this);        
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) 
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

	@Override
	public void onLocationChanged(Location location) {
		if(location == null)
		{
			//txtacc.setText("-.- Kmph");
			//Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 1);			
		}
		else
		{
			float cSpeed_mps = location.getSpeed();
			float cSpeed_mph = (float) (cSpeed_mps* 3600/(1000)); //1609.344 for miles
			
			if(cSpeed_mph==0.0)
			{
				//txtacc.setText(cSpeed_mph+" Kmph\n Latitude "+location.getLatitude()+"\nLongitude "+location.getLongitude());				
			}
			else
			{
				lm.removeUpdates(this);				
				Intent in = new Intent(this, DisplayBlank.class);
		        startActivity(in);
			}			
		}
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}
	
	class MyTask extends AsyncTask<Void, Integer, Void>
    {
    	private TextView txtToday;
    	private TextView txtCurrent;
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			txtToday = (TextView)findViewById(R.id.txtToday);
			txtCurrent = (TextView)findViewById(R.id.txtCurrent);
		}
    	
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			
			while(true)
			{
				SharedPreferences PREF = getSharedPreferences("COMET", 0);
				int TodayTotal = PREF.getInt("Today", 0);
				int Current = PREF.getInt("Current", 0);
				publishProgress(TodayTotal,Current);
				try{
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
		protected void onProgressUpdate(Integer... values) {
			// TODO Auto-generated method stub
			txtToday.setText(values[0].toString());	
			txtCurrent.setText(values[1].toString());
		}
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			
		}
	}
   
}


