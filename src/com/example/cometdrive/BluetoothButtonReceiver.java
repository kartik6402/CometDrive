package com.example.cometdrive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.view.KeyEvent;

public class BluetoothButtonReceiver extends BroadcastReceiver 
{
	SharedPreferences pref;
	
	public BluetoothButtonReceiver()
	{
		super();
	}
		
	@Override
	public void onReceive(Context context, Intent intent) 
	{
		if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) 
		{
            KeyEvent event = (KeyEvent)intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            if(event.getAction() == KeyEvent.ACTION_DOWN)
            {
	            if (KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE == event.getKeyCode()) 
	            {
	            	UpdateCapacity("Full", context);    
	            }
	            else if(KeyEvent.KEYCODE_MEDIA_NEXT == event.getKeyCode())
	            {
	            	UpdateCapacity("Increment", context);          	
	            }
	            else if(KeyEvent.KEYCODE_MEDIA_PREVIOUS == event.getKeyCode())
	            {
	            	UpdateCapacity("Decrement", context);
	            }
            }
        }
	}
	
	public void UpdateCapacity(String Mode,Context mcontext)
	{
		pref= mcontext.getSharedPreferences("COMET", 0);
		
		if(Mode.equalsIgnoreCase("Full"))
		{
			int today = pref.getInt("TotalRiders", 0);
        	int current = pref.getInt("CurrentRiders", 0);
        	int capacity = pref.getInt("VehicleCapacity",8);
        	
        	today = today + (capacity-current);
        	current = capacity;
        	            	
        	Editor edit = pref.edit();
    		edit.putInt("TotalRiders", today);
    		edit.putInt("CurrentRiders", current);
    		edit.commit();
			
		}
		else if(Mode.equalsIgnoreCase("Increment"))
		{
			int today = pref.getInt("TotalRiders", 0);
    		int current = pref.getInt("CurrentRiders", 0);
    		int capacity = pref.getInt("VehicleCapacity",8);
    		
    		if(current<capacity)
    		{   
        	    current++;
    		}
    		today++;
        	Editor edit = pref.edit();
    		edit.putInt("TotalRiders", today);
    		edit.putInt("CurrentRiders", current);
    		edit.commit();
		}
		else if(Mode.equalsIgnoreCase("Decrement"))
		{
			int current = pref.getInt("CurrentRiders", 0);
        	if(current>0)
        	    current--;
        	
        	Editor edit = pref.edit();
    		edit.putInt("CurrentRiders", current);
    		edit.commit();
		}			
	}
}