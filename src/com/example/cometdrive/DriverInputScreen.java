package com.example.cometdrive;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;


public class DriverInputScreen extends Activity implements android.view.View.OnClickListener 
{
	//Variables
	Spinner spnRoute;
	EditText etCabCapacity;
	EditText etDriverID;
	Button btnContinue;
	List<String> dropdownRouteList;
	SharedPreferences Pref;
	Editor editor;
	DriverDatabaseController dbController;
	String routeInformation;
	String CabCapacity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.driver_input_screen);
		
	}
	
	@Override
	protected void onResume(){	
		super.onResume();
		Initialize();
		LoadRouteInfo();
		if(Pref.getString("Close", "FALSE").equals("TRUE"))
		{
			editor.putString("Close", "FALSE");
			editor.commit();
			this.finish();
		}
	}
	
	@Override
	protected void onRestart() {
		// TODO Auto-generated method stub
		super.onRestart();
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		android.os.Process.killProcess(android.os.Process.myPid());
	}
	
	public void Initialize()
	{
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		
		//Initialize Variables
		Pref = getSharedPreferences("COMET", 0);
		editor = Pref.edit();
		etCabCapacity= (EditText) findViewById(R.id.etCabCapacity);
		etDriverID =(EditText)findViewById(R.id.etDriverId);
		spnRoute = (Spinner) findViewById(R.id.spnSelectRoute);
	    btnContinue =(Button)findViewById(R.id.btnContinue);
	    btnContinue.setOnClickListener(this);
	    dbController = new DriverDatabaseController(this);
	}
	
	public void LoadRouteInfo()
	{	
		dropdownRouteList = new ArrayList<String>();
		dropdownRouteList = dbController.LoadRouteInfo();
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item, dropdownRouteList);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spnRoute.setAdapter(dataAdapter);
	}

	
	@Override
	public void onClick(View v) 
	{
		switch (v.getId()) 
		{
			case R.id.btnContinue:
				routeInformation = spnRoute.getSelectedItem().toString();
				CabCapacity = etCabCapacity.getText().toString().trim();
				String driverID = etDriverID.getText().toString();
				if(!CabCapacity.equals("") && (!driverID.equals("")))
				{
					if(dbController.FindDriver(driverID))
					{
						java.util.Date date= new java.util.Date();
						String timeStamp = new Timestamp(date.getTime())+"";
						
						editor = Pref.edit();
						editor.putString("DriverID", driverID);
						editor.putString("StartTime", timeStamp);
						editor.putString("RouteID",routeInformation);
					    editor.putInt("TotalRiders",0);
					    editor.putInt("CurrentRiders",0);
					    editor.putInt("VehicleCapacity",Integer.parseInt(CabCapacity));
					    editor.commit();
					    btnContinue.setEnabled(false);
					    new LoadingTask().execute();
					}
					else
						Toast.makeText(this, "Please enter a Valid Driver ID", Toast.LENGTH_SHORT).show();
				}
				else
					Toast.makeText(this, "Please enter DriverID and Capacity to Proceed ", Toast.LENGTH_SHORT).show();
			break;

		default:
			break;
		}
		
	}
	
	/** Inner class for implementing progress bar before fetching data **/
	private class LoadingTask extends AsyncTask<Void, Void, Integer> 
	{
	    private ProgressDialog Dialog = new ProgressDialog(DriverInputScreen.this);

	    @Override
	    protected void onPreExecute()
	    {
	        Dialog.setMessage("Loading CometDrive...");
	        Dialog.show();
	    }

	    @Override
	    protected Integer doInBackground(Void... params) 
	    {
	    	int vehicleID = dbController.RetrieveAndUpdateVehicleID(routeInformation,Integer.parseInt(CabCapacity),0,0);
			editor.putInt("VehicleID", vehicleID);
			editor.commit();
			return 0;
	    }

	    @Override
	    protected void onPostExecute(Integer result)
	    {
	    	if(result==0)
	        {
	        	Toast.makeText(DriverInputScreen.this, routeInformation+" "+ etCabCapacity.getText(), Toast.LENGTH_SHORT).show();
			    Intent DriverUserInterfaceControllerIntent = new Intent(DriverInputScreen.this,DriverUserInterfaceController.class);
			    startActivity(DriverUserInterfaceControllerIntent);
		    }
	        Dialog.dismiss();
	    }
	}
	
	
}
