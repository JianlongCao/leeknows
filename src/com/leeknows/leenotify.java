package com.leeknows;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import com.httpclient.httphandler;

import android.R.string;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.text.format.Time;
import android.util.Log;

public class leenotify extends Service{
	private AlarmManager mgr;
	private PendingIntent pi;
	private final int intervaltime = 86400000;//86400000
	private static boolean firstblood=false;
	@Override
	public void onStart(Intent intent, int startId) {
		if(firstblood)
		{
			Log.d("Lee","Just create");
			firstblood=false;
		}
		else
		{
			Bundle data = intent.getExtras();
			 Log.d("Lee", "service on start");
			if(data.getBoolean("Run", false))
			{
	  		  Log.d("Lee", "runbaby");
				runbaby();
			}
			super.onStart(intent, startId);
		}
	
	}

	@Override
	public void onCreate() {
		mgr=(AlarmManager)getSystemService(Context.ALARM_SERVICE);
		Intent i=new Intent(this, AlarmReceiver.class);
		pi=PendingIntent.getBroadcast(this, 0, i, 0);
		mgr.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+intervaltime,intervaltime, pi);
        nm=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        firstblood=true;
        Log.d("Lee","servicecreate");
		super.onCreate();
	}

	public ArrayList<HashMap<String, Object>> listItemtmp=null ;
	public static String uripost = "http://202.119.83.14:8080/uopac/reader/redr_verify.php";
	public static String sessionid;
	public SharedPreferences cookieshare;
	private final String Tag = "leenotify";
	private NotificationManager nm;
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		mgr.cancel(pi);
		super.onDestroy();
	}

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	private Boolean Login()
	{
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String username = prefs.getString("Libuser", "");
        String password = prefs.getString("Libpass", "");
        if(username == null || password == null)
        	return false;
		List <NameValuePair> params = new ArrayList <NameValuePair>(); 
        params.add(new BasicNameValuePair("number",username )); 
        params.add(new BasicNameValuePair("passwd", password));
        params.add(new BasicNameValuePair("select", "cert_no"));
        
        String html = httphandler.http_post_cookie(getApplicationContext(),params, uripost);
        
        if(html.equals(""))
        {
        	//登录失败处理 
        	return false;
        }
        else
        {
        	Document doc = Jsoup.parse(html);
        	Elements inps = doc.select("input[type=submit]");
        	if(inps.size()==0)
        	{
        		cookieshare = getApplicationContext().getSharedPreferences("cookieshare",0);
       		 	sessionid = cookieshare.getString("Sessionid", "");
        		 return true;
        	}
        	else 
        	{
        		return false;
        	}
        }
       
	}
	
	private void runbaby(){
		if(isNetworkVailable()){
			boolean judge =Login();
	    	if(judge) // 先登录
			{
	    		mylib_refresh();
			}
		}
	}
	 
		public void mylib_refresh()
		{
			
			String uriget = "http://202.119.83.14:8080/uopac/reader/book_lst.php";
			String html = httphandler.httpget_session(uriget, "", sessionid);
			listItemtmp = new ArrayList<HashMap<String,Object>>();
			String username=httphandler.http_parser_mylib(html,listItemtmp); 
			username="小"+username.substring(username.length()-1);
	    	String bookname="";
	    	String deadline="";
	    	String temp="";
	    	String notifycontent="";
	    	String []book_time;
	    	int delaybooks=0,remindbooks=0,ondaybooks=0;
	    	int delay_y=0,delay_m=0,delay_d=0,delay_all=0;
			Time time = new Time("GMT+8");    
	        time.setToNow();   
	    	for(int i=0;i<listItemtmp.size();)
			{
				HashMap<String, Object> map = new HashMap<String, Object>(); 
				map=listItemtmp.get(i);
				bookname = ((String) map.get("Itembigtitle")).substring(0, 4);
				temp = ((String) map.get("Itemsmalltitle"));
				deadline = temp.substring(temp.indexOf('~')+1,temp.indexOf(' '));
				
				book_time = deadline.split("[-]");
				delay_y = time.year-Integer.valueOf(book_time[0]);
				delay_m = time.month+1-Integer.valueOf(book_time[1]);
				delay_d = time.monthDay-Integer.valueOf(book_time[2]);
				delay_all = delay_y*365+delay_m*30+delay_d;
				if(delay_all>0)
				{
					delaybooks++;
				}
				else if(delay_all==0)
				{
					ondaybooks++;
				}
				else if(delay_all>-7)
				{
					remindbooks++;
				}
				i++;
			}
	    	if(ondaybooks!=0)
	    		notifycontent+=ondaybooks+"本书最后一天 ";
	    	if(delaybooks!=0)
	    		notifycontent+=delaybooks+"本书超期 ";
	    	if(remindbooks!=0)
	    		notifycontent+=remindbooks+"本书将到期 ";
	    	if(!notifycontent.equals(""))
	    	{
	    		notifycontent=username+"，你有"+notifycontent;
	    		showNotification(R.drawable.ic_launcher,"理知道提醒", notifycontent);
	    	}
	    	else
      		  Log.d("Lee", "no delay books");

		}
	 public void showNotification(int icon,String tickertext,String title){
	    	//设置一个唯一的ID，随便设置
	 
	    	//Notification管理器
	    	Notification notification=new Notification(icon,tickertext,System.currentTimeMillis());
	    	//后面的参数分别是显示在顶部通知栏的小图标，小图标旁的文字（短暂显示，自动消失）系统当前时间（不明白这个有什么用）
	    	notification.flags = Notification.FLAG_AUTO_CANCEL;
	    	Intent i = new Intent(leenotify.this, leemylib.class);
	    	i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);           
	    	//PendingIntent
	    	PendingIntent contentIntent = PendingIntent.getActivity(
	    	        leenotify.this, 
	    	        R.string.app_name, 
	    	        i, 
	    	        PendingIntent.FLAG_UPDATE_CURRENT);
	    	                 
	    	notification.setLatestEventInfo(
	    	        leenotify.this,
	    	        tickertext, 
	    	        title, 
	    	        contentIntent);
	    	nm.notify(R.string.app_name, notification);
	 
	    }
	 public boolean isNetworkVailable() {
   	  ConnectivityManager cManager = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
   	  NetworkInfo info = cManager.getActiveNetworkInfo();
   	  if (info != null && info.isAvailable()) {
   	   return true;
   	  } else {
   	   return false;
   	  }
   	}
	
	
}