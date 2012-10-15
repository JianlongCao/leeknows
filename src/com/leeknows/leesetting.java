package com.leeknows;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

/**
 * Handles SIP authentication settings for the Walkie Talkie app.
 */
public class leesetting extends PreferenceActivity {

    @Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
    	Log.d("settings","destroy");
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        
        String username = prefs.getString("Libuser", "");
        String password = prefs.getString("Libpass", "");
        Boolean notify_service = prefs.getBoolean("Libnotify", false);
        Intent service_intent = new Intent(this,leenotify.class);

        if (username.length() != 0 &&password.length() != 0) {
        	if(notify_service)
        	{
        		  Log.d("Lee", "create service");
            	  service_intent.putExtra("Run", false);
        		  startService(service_intent);
        	}
              
        	else
        	{
        		 Log.d("Lee", "servicestop");
        		stopService(service_intent);
        	}
        }
        else stopService(service_intent);
    	
		super.onDestroy();
	}

	@Override
    public void onCreate(Bundle savedInstanceState) {
        // Note that none of the preferences are actually defined here.
        // They're all in the XML file res/xml/preferences.xml.
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }
}
