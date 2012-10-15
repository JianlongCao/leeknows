package com.leeknows;

import com.leemodels.bookfunc;
import com.leemodels.bookinfo;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class leesearch extends Activity {
    /** Called when the activity is first created. */
	public EditText E_input;
	public Button btn_classroom;
	public RadioGroup radiogroup;
	public RadioButton radio0,radio1,radio2;
	public TextView abouttext ;
	public String search_type="title";
	protected final static int TEXTUPDATE = 0x102;
	protected final static int TEXTDISMISS = 0x103;
	private static final int MYLIBSETUP =Menu.FIRST;
	private static final int MYLIBEXIT =Menu.FIRST+1;
	public bookfunc b_func;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("南京理工大学图书查询系统");
        setContentView(R.layout.main);
        initialize();
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    	boolean openclass = prefs.getBoolean("OpenClassroom", false);
    	if(openclass) btn_classroom.setVisibility(View.VISIBLE);
    	
        radiogroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {  
            
            @Override  
            public void onCheckedChanged(RadioGroup group, int checkedId) {  
                // TODO Auto-generated method stub  
                if(checkedId == radio0.getId())
                	search_type = "title";
                else if(checkedId == radio1.getId())
                	search_type = "author";
                else if(checkedId == radio2.getId())
                	search_type = "keyword";
				}  
        }); 
	}
    public void initialize()
    {
    	btn_classroom=(Button)findViewById(R.id.BClassroom);
    	radiogroup =(RadioGroup)findViewById(R.id.radioGroup1);
    	radio0 = (RadioButton)findViewById(R.id.radio0);
    	radio1 = (RadioButton)findViewById(R.id.radio1);
    	radio2 = (RadioButton)findViewById(R.id.radio2); 
    	abouttext = (TextView)findViewById(R.id.abouttext);
    	b_func = new bookfunc();
    }
    public void onclick_confirm(View v)
    {
		// TODO Auto-generated method stub
    	if(isNetworkVailable()){
         E_input = (EditText)findViewById(R.id.E_input);
         String input = E_input.getText().toString().replace(' ', '+');
         if(!input.equals("")){
	         bookinfo b_info=new bookinfo(search_type,input); //目前V0.1支持图书名搜索
	         Bundle inputdata = new Bundle();
	         inputdata.putSerializable("S_book", b_info);
	         //Intent intent = new Intent(leesearch.this,leeresult.class);
	         Intent intent = new Intent(leesearch.this,leeresult.class);
	         intent.putExtras(inputdata);
	         startActivity(intent);
         }
    	}
    	else{
			  b_func.toastinfo(getBaseContext(), "网络出了故障？");
 			  
    	}
    }
    
    public void onclick_store(View v)
    {
		// TODO Auto-generated method stub
    	Intent intent = new Intent(leesearch.this,leestore.class);
    	startActivity(intent);
    }
    
    public boolean isNetworkVailable() {
    	  ConnectivityManager cManager = (ConnectivityManager)leesearch.this.getSystemService(Context.CONNECTIVITY_SERVICE);
    	  NetworkInfo info = cManager.getActiveNetworkInfo();
    	  if (info != null && info.isAvailable()) {
    	   return true;
    	  } else {
    	   return false;
    	  }
    	}
    public void onclick_aboutauthor(View v)
    {
    	Message msg = new Message();
		if(abouttext.getVisibility()==View.INVISIBLE)
			msg.what = leelocation.TEXTUPDATE;
		else
			msg.what = leelocation.TEXTDISMISS;
		mHandler.sendMessage(msg);
    }
    public void onclick_mylib(View v)
    {
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        
        String username = prefs.getString("Libuser", "");
        String password = prefs.getString("Libpass", "");
        Boolean notify_service = prefs.getBoolean("Libnotify", true);
        if (username.length() == 0 ||password.length() == 0) {
        	Intent intent  = new Intent(leesearch.this,leesetting.class);
        	startActivity(intent);
        }
        else
        {
        	Intent intent  = new Intent(leesearch.this,leemylib.class);
        	startActivity(intent);
        }
    	
    }
    
    public void onclick_classroom(View v){
    	Intent intent  = new Intent(leesearch.this,leeclassroom.class);
    	startActivity(intent);
    }
    private Handler mHandler= new Handler(){

        @Override
        public void handleMessage(Message msg) {
        	switch (msg.what) {   
            case leesearch.TEXTUPDATE:
            	 updateStatus(leesearch.TEXTUPDATE);
            	 break;
            case leesearch.TEXTDISMISS:
            	updateStatus(leesearch.TEXTDISMISS);
            	break;
        	}
        	super.handleMessage(msg);
        }};
    public void updateStatus(final int id) {
            // Be a good citizen.  Make sure UI changes fire on the UI thread.
            this.runOnUiThread(new Runnable() {
                public void run() {
                	switch (id) {
					case leesearch.TEXTUPDATE:
						abouttext.setVisibility(View.VISIBLE);
						break;
					case leesearch.TEXTDISMISS:
						abouttext.setVisibility(View.INVISIBLE);
						break;
					default:
						break;
					}             		
                     
                }
            });
        }
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MYLIBSETUP, 0, "设置");	
        menu.add(0,MYLIBEXIT,0,"退出");
        
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MYLIBSETUP	:
            	Intent intent = new Intent(leesearch.this,leesetting.class);
            	startActivity(intent);
            	
                break;
            case MYLIBEXIT	:
            	android.os.Process.killProcess(android.os.Process.myPid());
            	System.exit(0);
            	
                break;
        }
        return true;
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            new AlertDialog.Builder(this)
                    // .setIcon(R.drawable.services)
                    .setTitle("退出")
                    .setMessage("确定关闭理知道？")
                    .setNegativeButton("取消",
                            new DialogInterface.OnClickListener() {

								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									
								}
//                                @Override
//                                public void onClick(DialogInterface dialog,
//                                        int which) {
//                                }
                            })
                    .setPositiveButton("确定",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                        int whichButton) {
                                    finish();
                                }
                            }).show();
            return true;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        System.exit(0);
    }
}