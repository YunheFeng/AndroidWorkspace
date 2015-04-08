package com.example.recplaywithsocket;

import com.example.androidrecplayusingbufferwithsocket.R;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
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
	
	private static String TAG = "AUDIO_RECORD_############ ";
	static {
		System.loadLibrary("socket_test");
	}

	public static native byte[] getArrayElements();
	public static native void sendArrayElements(byte var[]);
	public static native void ConnectClient();
	
	private boolean isRunning = true;

	private Thread m_thread;

	private AudioRecord recorder = null;
	private AudioTrack track = null;

	int bufferSize = 320;
	byte buffer[] = new byte[bufferSize];

		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_buffer_with_socket_main);
		
		ConnectClient();
		
		enableButton(R.id.StartButton,true);
		enableButton(R.id.StopButton,false);

		/* Assign Button Click Handlers */
		((Button)findViewById(R.id.StartButton)).setOnClickListener(btnClick);
		((Button)findViewById(R.id.StopButton)).setOnClickListener(btnClick);

		/*if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
					.add(R.id.container, new PlaceholderFragment()).commit();
		}*/
		Log.d(TAG, "\n\n\n\n\n\n============================= Starting Application.. =====================================");
	}

	private View.OnClickListener btnClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.StartButton:
			{
				Log.d(TAG, "======== Start Button Pressed ==========");
				isRunning = true;
				Log.d(TAG, "========== isRunning = true =============");
				do_loopback(isRunning);
				enableButton(R.id.StartButton,false);
				enableButton(R.id.StopButton,true);
				break;
			}
			case R.id.StopButton:
			{
				Log.d(TAG, "======== Stop Button Pressed ==========");
				isRunning = false;
				Log.d(TAG, "========== isRunning = false =============");
				do_loopback(isRunning);
				enableButton(R.id.StopButton,false);
				enableButton(R.id.StartButton,true);
				break;
			}
			}
		}
	}; 


	public void run_loop (boolean isRunning)
	{
		/** ============================ If Stop Button is pressed ============================ **/
		if (isRunning == false) {
			Log.d(TAG, "===== Entering run Loop with isRunning == false ===== ");
			
			if (AudioRecord.STATE_INITIALIZED == recorder.getState()){
				recorder.stop();
				recorder.release();
			}
			if (AudioTrack.STATE_INITIALIZED == track.getState()){
				track.stop();
				track.release();
			}
			return;
		}
		/** ==================================================================================== **/
		
		int readbytes, writebytes;
		Log.d(TAG, "===== Entering run Loop ===== ");

		recorder = findAudioRecord(recorder);
		if (recorder == null) {
			Log.e(TAG, "======== findAudioRecord : Returned Error! =========== ");
			return;
		}

		track = findAudioTrack(track);
		if (track == null) {
			Log.e(TAG, "======== findAudioTrack : Returned Error! ========== ");
			return;
		}

		if ((AudioRecord.STATE_INITIALIZED == recorder.getState()) && (AudioTrack.STATE_INITIALIZED == track.getState()))
		{
			recorder.startRecording();
			Log.d(TAG, "========= Recorder Started... =========");
			track.play();
			Log.d(TAG, "========= Track Started... =========");
		} else {
			Log.d(TAG, "========= Initilazation failed for AudioRecord or AudioTrack =========");
			return;
		}


		while ((isRunning == true)) 
		{
			readbytes = 0;												/* Reset read bytes for next Iteration */
			writebytes = 0;												/* Reset write bytes for next Iteration */

			/* Recording and Playing in chunks of 320 bytes */
			bufferSize = 320;
			
			readbytes = recorder.read(buffer, 0, bufferSize);
			if(-1 == checkAudioRecordforError(readbytes)) 				/* Error Checking Code for AudioRecord */
			{
				Log.d(TAG, "========= Read Error =========");
				return;
			}
			
			/* Newly added code for Encoding and Decoding */
			sendArrayElements(buffer);
			buffer = getArrayElements();
			/* ========================================== */

			writebytes = track.write(buffer, 0, bufferSize);
			if (-1 == checkAudioTrackforError(writebytes)) 				/* Error Checking Code for AudioRecord */
			{
				Log.d(TAG, "========= Write Error =========");
				return;
			}
		}
		Log.i(TAG, "loopback exit");
		return;
	}


	public int checkAudioRecordforError(int readbytes)
	{
		if (readbytes ==  AudioRecord.ERROR_INVALID_OPERATION || readbytes ==  AudioRecord.ERROR_BAD_VALUE) 
		{
			if(readbytes == AudioRecord.ERROR_INVALID_OPERATION)
				Log.d(TAG, "========= read Error : ERROR_INVALID_OPERATION ===========");
			else if (readbytes == AudioRecord.ERROR_BAD_VALUE)
				Log.d(TAG, "========= read Error : ERROR_BAD_VALUE ===========");
			else if (readbytes == AudioRecord.ERROR)
				Log.d(TAG, "========= read Error : ERROR Unknown ===========");
			return -1;
		}
		return readbytes;
	}
	public int checkAudioTrackforError(int writebytes)
	{
		if (writebytes ==  AudioTrack.ERROR_INVALID_OPERATION || writebytes ==  AudioTrack.ERROR_BAD_VALUE)
		{
			if(writebytes == AudioTrack.ERROR_INVALID_OPERATION)
				Log.d(TAG, "========= read Error : ERROR_INVALID_OPERATION ===========");
			else if (writebytes == AudioTrack.ERROR_BAD_VALUE)
				Log.d(TAG, "========= read Error : ERROR_BAD_VALUE ===========");
			else if (writebytes == AudioTrack.ERROR)
				Log.d(TAG, "========= read Error : ERROR Unknown ===========");
			return -1;
		}
		return writebytes;
	}


	public AudioTrack findAudioTrack (AudioTrack track)
	{
		Log.d(TAG, "=============================== Initialising Playing API ===============================");
		int m_bufferSize;
		m_bufferSize = AudioTrack.getMinBufferSize(8000, 
				AudioFormat.CHANNEL_OUT_MONO, 
				AudioFormat.ENCODING_PCM_16BIT);		/* Return 640 */

		/** Overriding bufferSize value of 640 with 320**/
		Log.d(TAG, "========= BEFORE AudioTrack ==> bufferSize : "+m_bufferSize+"=========");
		Log.d(TAG, "========= AudioTrack ==> bufferSize : "+m_bufferSize+"=========");

		if (m_bufferSize != AudioTrack.ERROR_BAD_VALUE) 
		{
			track = new AudioTrack(AudioManager.STREAM_MUSIC, 8000, 
					AudioFormat.CHANNEL_OUT_MONO, 
					AudioFormat.ENCODING_PCM_16BIT, m_bufferSize, 
					AudioTrack.MODE_STREAM);

			int type = track.getStreamType();
			Log.d(TAG, "========= AudioTrack ==> getStreamType : "+type+"====");

			if (track.getState() == AudioTrack.STATE_UNINITIALIZED) {
				Log.e(TAG, "=============================== AudioTrack UnInitilaised =============================== ");
				return null;
			}
		}
		return track;
	}


	public AudioRecord findAudioRecord (AudioRecord recorder)
	{
		int m_bufferSize;
		Log.d(TAG, "=============================== Initialising Record API ===============================");
		m_bufferSize = AudioRecord.getMinBufferSize(8000,
				AudioFormat.CHANNEL_IN_MONO,
				AudioFormat.ENCODING_PCM_16BIT);

		/** Overriding bufferSize value of 640 with 320**/
		Log.d(TAG, "========= BEFORE AudioRecord ==> bufferSize : "+m_bufferSize+"=========");
		Log.d(TAG, "========= AudioRecord ==> bufferSize : "+m_bufferSize+"=========");

		if (m_bufferSize != AudioRecord.ERROR_BAD_VALUE) 
		{
			recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, 8000, 
					AudioFormat.CHANNEL_IN_MONO,
					AudioFormat.ENCODING_PCM_16BIT, m_bufferSize);

			if (recorder.getState() == AudioRecord.STATE_UNINITIALIZED) {
				Log.e(TAG, "=============================== AudioRecord UnInitilaised =============================== ");
				return null;
			}
		}
		Log.d(TAG, "=============================== Initialising Record Completed ===============================");
		return recorder;
	}

	private void do_loopback(final boolean flag) {
		Log.d(TAG, "========== within do_loopback ============");
		m_thread = new Thread(new Runnable() {
			public void run() {
				run_loop(flag);
			}
		});
		m_thread.start();
	}
	
	/* Function to Enable/Disable Buttons */
	private void enableButton(int id,boolean isEnable){
		((Button)findViewById(id)).setEnabled(isEnable);
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.buffer_with_socket_main, menu);
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
	public static class PlaceholderFragment extends Fragment {
		public PlaceholderFragment() {
		}
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater
					.inflate(R.layout.fragment_buffer_with_socket_main,
							container, false);
			return rootView;
		}
	}
}
