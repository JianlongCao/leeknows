package com.leeknows;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AlarmReceiver extends BroadcastReceiver {
 
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		System.out.println("收到广播");
		Intent it=new Intent(context,leenotify.class);
		it.putExtra("Run", true);
		Log.d("Lee", "start service");
		context.startService(it);
	}
}