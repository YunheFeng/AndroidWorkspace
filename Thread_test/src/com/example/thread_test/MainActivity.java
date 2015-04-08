package com.example.thread_test;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.os.Build;

public class MainActivity extends ActionBarActivity {

	private static final String TAG = "THREAD_TEST";
	private Thread m_thread;
	public static int event = 0;
	public static int flag = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_main);

		((Button)findViewById(R.id.Button1)).setOnClickListener(btnClick);
		((Button)findViewById(R.id.Button2)).setOnClickListener(btnClick);
		((Button)findViewById(R.id.Button3)).setOnClickListener(btnClick);

		/*if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}*/
		Log.d(TAG, "======== Starting Application... ==========");
		run_thread();
	}

	private View.OnClickListener btnClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.Button1:
			{
				Log.d(TAG, "======== Event1 Button Pressed ==========");
				event = 1;
				flag = 1;
				break;
			}
			case R.id.Button2:
			{
				Log.d(TAG, "======== Event2 Button Pressed ==========");
				event = 2;
				flag = 2;
				break;
			}
			case R.id.Button3:
			{
				Log.d(TAG, "======== Event3 Button Pressed ==========");
				event = 4;
				flag = 3;
				break;
			}
			}
		}
	}; 


	public static synchronized void do_loop()
	{
		while (true) {
			Log.d(TAG, "====== Event:" +event+ "==== Flag:" +flag+ "====");
			if (event == 1)
			{
				while(flag == 1) {
					Log.d(TAG, "====== Event :1 ==== While runnning ====");
				}
			} else if(event == 2) 
			{
				Log.d(TAG, "====== Event :2 =======");
			}
			else {
				Log.d(TAG, "====== sleeping... =======");
				try {
					Thread.sleep(500);                 //1000 milliseconds is one second.
				} catch(InterruptedException ex) {
					Thread.currentThread().interrupt();
				}	
			}
		}
	}

	private void run_thread() {
		Log.d(TAG, "========== within do_loopback ============");
		m_thread = new Thread(new Runnable() {
			public void run() {
				Log.d(TAG, "======Thread ############ Event:"+event+"==== Flag:"+flag+ "====");
				do_loop();
			}
		});
		m_thread.start();
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_main, container,
					false);
			return rootView;
		}
	}

}
