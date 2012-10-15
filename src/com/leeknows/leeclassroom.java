package com.leeknows;

import com.httpclient.httphandler;

import android.app.Activity;
import android.app.Service;
import android.os.Bundle;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

public class leeclassroom extends Activity{

	private String url = "http://njust.aliapp.com/classroom.php";
	private String Params = "";
	private String Params_old ="123";
	private String beginIndex = "<hr />";
	private String endIndex = "<table border=1>";
	private TextView text_class;
	private Spinner spin_week;
	private Spinner spin_classno;
	private String[] classno = {"0","9","11","15","17","19"};
	private String classcontent="";
	private boolean searchflag=false;
	private boolean serviceon=false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.classroom);
		//setProgressBarIndeterminateVisibility(false);
		serviceon=true;
		new Thread(new MyThread()).start();
		text_class = (TextView)findViewById(R.id.classcontent);
		spin_week = (Spinner)findViewById(R.id.spin_week);
		spin_classno = (Spinner)findViewById(R.id.spin_classno);
		text_class.setMovementMethod(ScrollingMovementMethod.getInstance());
		
	}
	
	public void onclick(View v){
		switch(v.getId())
		{
		case R.id.btn_changetime:
//			?wday=1&&hour=9
			Params = "wday="+(int)(spin_week.getSelectedItemId()+1)+
			"&&hour="+classno[(int)spin_classno.getSelectedItemId()]+"&&min=01";
			classcontent="";
			searchflag = true;
			break;
		}
	}
	 //线程更新数据
    class MyThread implements Runnable{  
  	  
        @Override  
        public void run() {  
        	while(serviceon){
        		if(Params!=Params_old)
        		{
        			runOnUiThread(new Runnable() {
        				public void run() {
        			setProgressBarIndeterminateVisibility(true);
        				}});
        			if(((searchflag==true)&&(int)spin_classno.getSelectedItemId()==0))
        			{
        				for(int i=1;i<6;i++)
        				Updateclassroom("wday="+(int)(spin_week.getSelectedItemId()+1)+
        						"&&hour="+classno[i]+"&&min=01");
        			}
        			else
        				Updateclassroom(Params);
        			Params_old = Params;
            		searchflag=false;
            		runOnUiThread(new Runnable() {
        				public void run() {
        			setProgressBarIndeterminateVisibility(false);
        				}});        			
            		}
        		
        	}
        	
       			
           } 
        }
    public void Updateclassroom(String params){
			String html = httphandler.httpget(url, params);
	  			 //parse html
			if(html.indexOf("temporarily unavailable")!=-1)
			html="网站暂时无法访问";
			else{
				html = html.substring(html.indexOf(beginIndex)+6);
				html = html.substring(html.indexOf(beginIndex)+6);
				html = html.substring(0, html.indexOf(endIndex));
				html=html.replaceAll("<hr","'\n");
				html=html.replaceAll("<br"," ");
				html=html.replaceAll("/>","");
			}
			Log.d("leeclassroom", html);
			if((int)spin_classno.getSelectedItemId()==0) //all
				classcontent+=html;
			runOnUiThread(new Runnable() {
			public void run() {
				final String temp = classcontent;
				text_class.setText(temp);
			}
			
		});
    }

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		serviceon=false;
		
	}
          
	
}