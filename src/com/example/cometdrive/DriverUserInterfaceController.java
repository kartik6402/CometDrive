package com.example.cometdrive;

import android.support.v7.app.ActionBarActivity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


public class DriverUserInterfaceController extends ActionBarActivity implements LocationListener,android.view.View.OnClickListener
{
	LocationManager lm;
	TextView txtacc;
	AudioManager am;
	ComponentName cmp;
	
	TextView tvRouteName;
	TextView tvTotalRiders;
	TextView tvCurrentRiders;
	TextView tvTotalCapacity;
	Button btnIncrement;
	Button btnDecrement;
	Button btnFull;
	Switch swShuttleService;
	SharedPreferences pref;
	Editor editor;
	double vehicleLatitude;
	double vehicleLongitude;
	DriverDatabaseController dbcontroller;
	int updatecounter=0;
	Location location;
	UpdateDatabaseTask asyncTask;
	
	
	@Override
    protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);               
        setContentView(R.layout.driver_user_interface);
        Initialize();  
	}
		
	@Override
	protected void onResume() 
	{
		super.onResume();
		Initialize();     
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onPause() 
	{
		super.onPause();
		asyncTask.cancel(true);
		lm.removeUpdates(this);
		am.unregisterMediaButtonEventReceiver(cmp);
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) 
    {
        int id = item.getItemId();
        if (id == R.id.action_settings) 
        {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
	public void onClick(View v) 
    {
    	int today;
		int current;
		int capacity;
		int ridersAtStop;
		
    	switch (v.getId()) 
    	{
			case R.id.btnIncrement:
				today = pref.getInt("TotalRiders", 0);
	    		current = pref.getInt("CurrentRiders", 0);
	    		capacity = pref.getInt("VehicleCapacity",8);
	    		ridersAtStop = pref.getInt("RidersAtStop",0);
	    		ridersAtStop++;
	        	
	    		if(current<capacity)
	    		{   
	        	    current++;
	    		}
	    		today++;
	        	editor.putInt("TotalRiders", today);
	    		editor.putInt("CurrentRiders", current);
	    		editor.putInt("RidersAtStop", ridersAtStop);
	    		editor.commit();
	    		break;
			case R.id.btnDecrement:
				current = pref.getInt("CurrentRiders", 0);
	        	if(current>0)
	        	    current--;
	        	editor.putInt("CurrentRiders", current);
	    		editor.commit();
				break;
			case R.id.btnFull:
				today = pref.getInt("TotalRiders", 0);
	        	current = pref.getInt("CurrentRiders", 0);
	        	capacity = pref.getInt("VehicleCapacity",8);
	        	today = today + (capacity-current);
	        	current = capacity;
	        	ridersAtStop = capacity;
	        	editor.putInt("TotalRiders", today);
	    		editor.putInt("CurrentRiders", current);
	    		editor.putInt("RidersAtStop", ridersAtStop);
	    		editor.commit();
				break;

		default:
			break;
		}
		
	}
    
	@SuppressWarnings("deprecation")
	public void Initialize()
	{

	    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		//Variables
		tvRouteName  	= (TextView) findViewById(R.id.tvRouteName);
		tvTotalRiders  	= (TextView) findViewById(R.id.tvTotalRider);
		tvCurrentRiders = (TextView)findViewById(R.id.tvCurrentRiders);
		tvTotalCapacity = (TextView)findViewById(R.id.tvTotalCapacity);
		btnIncrement	=(Button)findViewById(R.id.btnIncrement);
		btnDecrement	=(Button)findViewById(R.id.btnDecrement);
		btnFull 		=(Button)findViewById(R.id.btnFull);
		swShuttleService=(Switch)findViewById(R.id.swShuttleService);
		pref			= getSharedPreferences("COMET", 0);
		editor 			= pref.edit();
		dbcontroller = new DriverDatabaseController(this);
		swShuttleService.setChecked(true);
		
		tvRouteName.setText(pref.getString("RouteName", "Route Information not Loaded"));
		tvTotalCapacity.setText(String.valueOf(pref.getInt("VehicleCapacity", 0)));
		
		//Initialize GPS Variables
		lm = (LocationManager)getSystemService(LOCATION_SERVICE);
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0,0,this);
        location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    	
        this.onLocationChanged(null);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        //Initialize Media Control variables
        am = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
        cmp = new ComponentName(getPackageName(),BluetoothButtonReceiver.class.getName());
        am.registerMediaButtonEventReceiver(cmp);
        
        //Initialize on click listener for buttons
        btnIncrement.setOnClickListener(this);
        btnDecrement.setOnClickListener(this);
        btnFull.setOnClickListener(this);
        editor.putString("Close", "FALSE");
        editor.commit();
        asyncTask = new UpdateDatabaseTask();
        asyncTask.execute();
	}
	
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		//super.onBackPressed();
		
	}
	
	@SuppressWarnings("deprecation")
	public void OnSwitchClick(View view)
	{
		boolean on = ((Switch) view).isChecked();
	    
	    if (!on) 
	    {
	    	editor.putString("Close", "TRUE");
	    	editor.commit();
	    	asyncTask.cancel(true);
	    	lm.removeUpdates(this);	
	    	am.unregisterMediaButtonEventReceiver(cmp);
	    	dbcontroller.DeleteLiveVehicleInformation(pref.getString("RouteID", "0"), pref.getInt("VehicleID", 0));
	    	this.finish();
	    
	    	Toast.makeText(this, "You are Logged Out, Thank you for Using CometRide",  Toast.LENGTH_SHORT).show();
	    }
	}
	
    //GPS Related Functions
    
	@Override
	public void onLocationChanged(Location location) 
	{
		if(location == null)
		{
			//txtacc.setText("-.- Kmph");
			//Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_OFF_TIMEOUT, 1);			
		}
		else
		{
			float cSpeed_mps = location.getSpeed();
			vehicleLatitude = location.getLatitude();
			vehicleLongitude = location.getLongitude();
			editor.putString("Latitude", String.valueOf(vehicleLatitude));
			editor.putString("Longitude", String.valueOf(vehicleLongitude));
			editor.commit();
			
			//TextView tv = (TextView) findViewById(R.id.tvCurrentRidersLabel);
			//TextView tv1 = (TextView) findViewById(R.id.tvTotalCapacityLabel);
			//tv.setText(String.valueOf(vehicleLatitude));
			//tv1.setText(String.valueOf(vehicleLongitude));
			
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
	public void onProviderEnabled(String provider) {}

	@Override
	public void onProviderDisabled(String provider) {}
	
	//Asynchronous Task to Update values based on the button Click event.
	class UpdateDatabaseTask extends AsyncTask<Void, Integer, Void>
    {
    	private TextView txtToday;
    	private TextView txtCurrent;
    	
		@Override
		protected void onPreExecute() 
		{
			// TODO Auto-generated method stub
			txtToday = (TextView)findViewById(R.id.tvTotalRider);
			txtCurrent = (TextView)findViewById(R.id.tvCurrentRiders);
		}
    	
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub
			
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
			txtToday.setText(values[0].toString());	
			txtCurrent.setText(values[1].toString());
			
		}
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			
		}
	}
	

}



