package com.example.cometdrive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.KeyEvent;

public class DummyBluetoothButtonReceiver extends BroadcastReceiver 
{
	SharedPreferences pref;
	
	public DummyBluetoothButtonReceiver()
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
	            	
	            }
	            else if(KeyEvent.KEYCODE_MEDIA_NEXT == event.getKeyCode())
	            {
	            	          	
	            }
	            else if(KeyEvent.KEYCODE_MEDIA_PREVIOUS == event.getKeyCode())
	            {
	            	
	            }
            }
        }
	}
}