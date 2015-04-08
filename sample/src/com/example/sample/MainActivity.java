package com.example.sample;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {

	private Context mContext;
	private TextView mTextView;
	private NotificationManager notifyManager;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mTextView = (TextView) findViewById(R.id.counter);
        startCounter();
    }

    @Override
	public void onBackPressed() {
		
		//statusNotification("Ongoing call");
		 //moveTaskToBack(true);
		super.onBackPressed();
	}
    
    @Override
    protected void onPause() {
    	//statusNotification("Ongoing call");
    	super.onPause();
    }
	
	/*@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		 if ((keyCode == KeyEvent.KEYCODE_HOME)) {
			  Log.i("TAG", "************Inside home ************");
			  statusNotification("Ongoing call");
		      return true;
		    }
		
		return super.onKeyDown(keyCode, event);
	}*/
	
	@SuppressWarnings("deprecation")
	private void statusNotification(String event) {
		CharSequence text = event;
		notifyManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		Notification notification = new Notification(R.drawable.ic_launcher, text, System.currentTimeMillis());
		Intent intent = new Intent(this, MainActivity.class);		
		PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, intent, 0);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		notification.setLatestEventInfo(mContext, text, text, contentIntent);
		notifyManager.notify(1001, notification);
	}
	
	private void startCounter(){		
		new CountDownTimer(40000, 1000) {
            public void onTick(long millisUntilFinished) {
            	mTextView.setText("" + millisUntilFinished / 1000);
            }

            public void onFinish() {
            	mTextView.setText("done!");
            }
       }.start();
	}

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
