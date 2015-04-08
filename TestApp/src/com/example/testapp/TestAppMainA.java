package com.example.testapp;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.os.Build;

public class TestAppMainA extends ActionBarActivity 
{
	String TAG = "TEST_APP";

	ConnectivityManager cm =(ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	NetworkInfo nw = cm.getActiveNetworkInfo();
	TextView t1 = (TextView) findViewById(R.id.NWType);
	TextView t2 = (TextView) findViewById(R.id.Type);
	TextView t3 = (TextView) findViewById(R.id.ExtraInfo);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test_app_main);
		//findViewById(R.id.PrintButton).setOnClickListener(btnClick);
	}

	private View.OnClickListener btnClick = new View.OnClickListener() {
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.PrintButton:
				PutData();
			}
		}; 
	};

	void PutData()
	{
		Log.d(TAG, "Interface===");

		Log.d(TAG, "NetworkType: "+nw.toString() +"===");
		t1.setText(nw.toString());

		Log.d(TAG, "Type		: "+nw.getType() +"===");
		t2.setText(nw.getType());

		Log.d(TAG, "ExtraInfo	: "+nw.getExtraInfo() +"===");
		t3.setText(nw.getExtraInfo());

		Log.d(TAG, "Type		: "+nw.getSubtype() +"===");
		Log.d(TAG, "TypeName	: "+nw.getTypeName() +"===");
		Log.d(TAG, "Available?	: "+nw.isAvailable() +"===");
		Log.d(TAG, "Connected?	: "+nw.isConnected() +"===");
		Log.d(TAG, "Roaming?	: "+nw.isRoaming() +"===");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.test_app_main, menu);
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

	/*
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.fragment_test_app_main,
					container, false);
			return rootView;
		}
	}
	 */
}
