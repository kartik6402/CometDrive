package com.example.cometdrive;

import java.sql.Timestamp;

import android.support.v7.app.ActionBarActivity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;


public class DriverUserInterfaceController extends ActionBarActivity implements LocationListener,android.view.View.OnClickListener,OnCheckedChangeListener
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
	int locationUpdateCounter = 0;
	
	
	
	//################################Life Cycle Events ###################################//
	
	@Override
    protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);               
        setContentView(R.layout.driver_user_interface);
    	Log.i("Comet","Create");
    }
		
	@Override
	protected void onRestart(){
		super.onRestart();
		Log.i("Comet","Restart");
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		Log.i("Comet","Resume");
		Initialize();     
	}
	
	@SuppressWarnings("deprecation")
	@Override
	protected void onPause(){
		super.onPause();
		Log.i("Comet","Pause");
		//asyncTask.cancel(true);
		lm.removeUpdates(this);
		am.unregisterMediaButtonEventReceiver(cmp);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Log.i("Comet","Destroy");
	}
	
	
	//########################################### Initialize ##################################//
	
	@SuppressWarnings("deprecation")
	public void Initialize()
	{

	    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);

		//#######Initialize Variables
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
		swShuttleService.setOnCheckedChangeListener(this);
		
		tvRouteName.setText(pref.getString("RouteID", "Route Information not Loaded"));
		tvTotalCapacity.setText(String.valueOf(pref.getInt("VehicleCapacity", 0)));
		
		//###########Initialize GPS Variables to Update Every 5 Seconds##############//
		Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        //criteria.setPowerRequirement(Criteria.POWER_HIGH);
        lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
      	String provider = lm.getBestProvider(criteria, true);
      	lm.requestLocationUpdates(provider,100,0,this);
      	Location location = lm.getLastKnownLocation(provider);
      	
      	Toast.makeText(this,provider, Toast.LENGTH_SHORT).show();
		//lm = (LocationManager)getSystemService(LOCATION_SERVICE);
        //lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,1000,0,this);
        //location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    	if(location!=null)
    	{
    		this.onLocationChanged(location);
    	}
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
    
	
	@Override
	public void onBackPressed() {
		Toast.makeText(this,"Please Turn off the service to Exit", Toast.LENGTH_SHORT).show();
	}
	
	private Location prevLoc;
    //################################## Location Listener Events #####################################//
	@Override
	public void onLocationChanged(Location location) 
	{
		Log.i("Comet","Update");
		
		if(location != null)
		{
			float cSpeed_mps = location.getSpeed();
			vehicleLatitude = location.getLatitude();
			vehicleLongitude = location.getLongitude();
			editor.putString("Latitude", String.valueOf(vehicleLatitude));
			editor.putString("Longitude", String.valueOf(vehicleLongitude));
			editor.commit();
			locationUpdateCounter++;
			
			//float cSpeed_mph = (float) (cSpeed_mps* 3600/(1000)); //1609.344 for miles
			
			if(cSpeed_mps>=0.000001)
			{
				lm.removeUpdates(this);				
				Intent in = new Intent(this, DisplayBlank.class);
		        startActivity(in);
			}			
		}
		if(locationUpdateCounter==4)
		{
			if(prevLoc==null)
				prevLoc = location;
			Toast.makeText(this,"Loc", Toast.LENGTH_SHORT).show();
			dbcontroller.UpdateLiveVehicleInformation(pref.getString("RouteID", "0"),pref.getInt("VehicleID",0),prevLoc.getLatitude(),prevLoc.getLongitude(),vehicleLatitude, vehicleLongitude,pref.getInt("VehicleCapacity",0),	pref.getInt("CurrentRiders",0),	pref.getInt("TotalRiders",0));
			Log.i("Comet","Live Information Table Updated from MainScreen");
			locationUpdateCounter=0;
			prevLoc = location;
		}
		
	}
	
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {}
	
	@Override
	public void onProviderEnabled(String provider) {}

	@Override
	public void onProviderDisabled(String provider) {}
	
	
	//##########################Asynchronous Task to Update values based on the button Click event. #####################//
	class UpdateDatabaseTask extends AsyncTask<Void, Integer, Void>
    {
    	private TextView txtToday;
    	private TextView txtCurrent;
    	
		@Override
		protected void onPreExecute() 
		{
			txtToday = (TextView)findViewById(R.id.tvTotalRider);
			txtCurrent = (TextView)findViewById(R.id.tvCurrentRiders);
		}
    	
		@Override
		protected Void doInBackground(Void... params) 
		{
			while(true)	
			{
				SharedPreferences PREF = getSharedPreferences("COMET", 0);
				int TodayTotal = PREF.getInt("TotalRiders", 0);
				int Current = PREF.getInt("CurrentRiders", 0);
				publishProgress(TodayTotal,Current);
				
				try{
					Thread.sleep(100);				
				}
				catch(InterruptedException e){ 
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
	}

	/** Inner class for implementing progress bar before fetching data **/
	private class LoadingTask extends AsyncTask<Void, Void, Integer> 
	{
	    private ProgressDialog Dialog = new ProgressDialog(DriverUserInterfaceController.this);

	    @Override
	    protected void onPreExecute()
	    {
	        Dialog.setMessage("Closing CometDrive...");
	        Dialog.show();
	    }

	    @Override
	    protected Integer doInBackground(Void... params) 
	    {
	    	return 0;
	    }

	    @Override
	    protected void onPostExecute(Integer result)
	    {
	    	if(result==0)
	        {	
	    		
		    }
	        Dialog.dismiss();   
	    }
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) 
	{
		if (!isChecked) 
	    {
			dbcontroller.DeleteLiveVehicleInformation(pref.getString("RouteID", "0"), pref.getInt("VehicleID", 0));
	    	java.util.Date date= new java.util.Date();
			String endTime = new Timestamp(date.getTime())+"";
	        dbcontroller.UpdateShiftInformation(pref.getString("RouteID", "Route"),pref.getString("DriverID", "Driver"),pref.getString("StartTime", "01/01/2015 12:00:00"),endTime,pref.getInt("TotalRiders", 0));
	    	editor.putString("Close", "TRUE");
	    	editor.commit();
	    	asyncTask.cancel(true);
	    	lm.removeUpdates(DriverUserInterfaceController.this);	
	    	am.unregisterMediaButtonEventReceiver(cmp);
	    	//new LoadingTask().execute();
	    	Toast.makeText(DriverUserInterfaceController.this, "You are Logged Out, Thank you for Using CometDrive",  Toast.LENGTH_SHORT).show();
	    	new LoadingTask().execute();
	    	this.finish();
	    	
	    }
	}

	
}



