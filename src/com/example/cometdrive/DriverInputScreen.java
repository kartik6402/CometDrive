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
import android.widget.TextView;
import android.widget.Toast;


public class DriverInputScreen extends Activity implements android.view.View.OnClickListener 
{
	//Variables
	Spinner spnRoute;
	EditText etCabCapacity;
	EditText etDriverID;
	TextView tvAssignedRoute;
	TextView tvCabCapacity;
	Button btnContinue;
	Button btnCancel;
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
		
	    //LoadRouteInfo();
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
		etDriverID =(EditText)findViewById(R.id.etDriverId);
		tvAssignedRoute = (TextView)findViewById(R.id.tvAssignedRoute);
		tvCabCapacity =(TextView)findViewById(R.id.tvCabCapacityLabel);
		etCabCapacity= (EditText)findViewById(R.id.etCabCapacity);
		btnContinue =(Button)findViewById(R.id.btnContinue);
	    btnContinue.setText("Login >>");
	    btnCancel =(Button)findViewById(R.id.btnCancel);
	    btnCancel.setOnClickListener(this);
	    
		btnContinue.setOnClickListener(this);
		
		tvAssignedRoute.setVisibility(View.GONE);
		tvCabCapacity.setVisibility(View.GONE);
		etCabCapacity.setVisibility(View.GONE);
		
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
				String driverID = etDriverID.getText().toString();
				if(btnContinue.getText().equals("Report On Duty >>"))
				{
					CabCapacity = etCabCapacity.getText().toString().trim();
					if(!CabCapacity.equals(""))
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
					{
						Toast.makeText(this, "Please enter a Valid Cab Capacity", Toast.LENGTH_SHORT).show();
					}
				}
				else if(btnContinue.getText().equals("Login >>"))
				{
					if(!driverID.equals(""))
					{
						java.util.Date date= new java.util.Date();
						Timestamp timeStamp = new Timestamp(date.getTime());
						routeInformation = dbController.FindDriver(driverID,timeStamp);
						if(!routeInformation.equals(""))
						{

							tvAssignedRoute.setVisibility(View.VISIBLE);
							tvCabCapacity.setVisibility(View.VISIBLE);
							etCabCapacity.setVisibility(View.VISIBLE);
							etDriverID.setEnabled(false);
							btnContinue.setText("Report On Duty >>");
							tvAssignedRoute.setText("Assigned Route :"+routeInformation);
							Toast.makeText(this, "Login Successful !!", Toast.LENGTH_SHORT).show();
						}
						else
						{
							Toast.makeText(this, "Please enter a Valid Driver ID", Toast.LENGTH_SHORT).show();
						}
					}
					else
						Toast.makeText(this, "Pleas enter a valid Driver ID ", Toast.LENGTH_SHORT).show();
				}
			break;
			case R.id.btnCancel:
				this.finish();
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
	        Dialog.setMessage("Have a Safe Drive !!");
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
