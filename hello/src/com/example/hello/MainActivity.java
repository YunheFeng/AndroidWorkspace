package com.example.hello;
import java.util.Arrays;

import com.example.hello.R;

import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

class sample{
	public int b = 0;
}

public class MainActivity extends ActionBarActivity 
{
	public static int a = -1;
	
	String tag = "SAURABH";

	/* JNI Prototypes */
	public static native int[] getArray();
	public static native char[] getCharArray();
	public static native byte[] getByteArray();
	public static native void sendByteArray(byte var[]);
	public static native void samplefunc(sample x);
	/* Load JNI Library */
	static {
		System.loadLibrary("hellojni");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
			.add(R.id.container, new PlaceholderFragment()).commit();
		}
		Log.d(tag, "=============Inside Fucntion onCreate()");
		init();
		sample obj = new sample();
		Log.d(tag, "============= Before : Value of Port b: " + obj.b);
		samplefunc(obj);
		Log.d(tag, "============= After  : Value of Port b: " + obj.b);
	}

	/* My JNI Implementation */
	public void init() 
	{
		Log.d(tag, "============= Inside Fucntion init()");
		
		int result_test[] = null ;
		result_test = getArray();
		Log.d(tag, "=============[JAVA] Received Array at Java: result_test[] :"+Arrays.toString(result_test));

		char result_test_char[] = null ;
		result_test_char = getCharArray();
		Log.d(tag, "=============[JAVA] Received Array at Java: result_test[] :"+Arrays.toString(result_test_char));
		
		int i = 0;
		byte byte_arr_data[] = null;
		byte_arr_data = getByteArray();
		for (i =0 ;i < byte_arr_data.length; i++)
		{
			Log.d(tag, "======= [JAVA] Received Byte Array at Java from JNI : arr_data[] :"+byte_arr_data[i] );
		}

		byte arr_to_send[] = new byte[10];
		for (i=0 ; i<10; i++) {
			arr_to_send[i] = (byte) i;
			Log.d(tag, "======= [JAVA] Sending Byte Array from Java to JNI : arr_to_send[] :"+arr_to_send[i] );
		}
		sendByteArray(arr_to_send);
		return;
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
