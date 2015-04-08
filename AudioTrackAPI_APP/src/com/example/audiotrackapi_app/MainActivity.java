package com.example.audiotrackapi_app;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

public class MainActivity extends ActionBarActivity {

	String file_path = "/tmp/Krypton.wav"; 
	String TAG = "GSIP_AUDIO"; 
	String new_file_path = "/sdcard/myfile.wav";

	private static AudioRecord recorder = null;
	private static boolean isRecording = false;
	private static Thread recordingThread = null;

	private static final int RECORDER_SAMPLERATE = 8000;
	private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_STEREO;
	private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

	int BUFFERELEMENTS2REC=1024;
	int BYTES_PER_ELEMENTS=2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		playWav();	/* Plays a audio track form the buffer */
		
		if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
			.add(R.id.container, new PlaceholderFragment()).commit();
		}
	}

	private void startRecording() 
	{
		recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
				RECORDER_SAMPLERATE, RECORDER_CHANNELS,
				RECORDER_AUDIO_ENCODING, BUFFERELEMENTS2REC * BYTES_PER_ELEMENTS);

		recorder.startRecording();
		isRecording = true;
		recordingThread = new Thread(new Runnable() {

			public void run() {
				writeAudioDataToFile();
			}
		}, "AudioRecorder Thread");
		recordingThread.start();
	}

	private void writeAudioDataToFile() {
		// Write the output audio in byte
		String filePath = new_file_path;

		short sData[] = new short[BUFFERELEMENTS2REC];

		FileOutputStream os = null;
		try {
			os = new FileOutputStream(filePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		while (isRecording) {
			// gets the voice output from microphone to byte format
			recorder.read(sData, 0, BUFFERELEMENTS2REC);
			System.out.println("Short wirting to file" + sData.toString());
			try {
				// writes the data to file from buffer stores the voice buffer
				byte bData[] = short2byte(sData);

				os.write(bData, 0, BUFFERELEMENTS2REC * BYTES_PER_ELEMENTS);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		try {
			os.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private byte[] short2byte(short[] sData) {
		int shortArrsize = sData.length;
		byte[] bytes = new byte[shortArrsize * 2];

		for (int i = 0; i < shortArrsize; i++) {
			bytes[i * 2] = (byte) (sData[i] & 0x00FF);
			bytes[(i * 2) + 1] = (byte) (sData[i] >> 8);
			sData[i] = 0;
		}
		return bytes;
	}

	@SuppressWarnings("unused")
	private void PlayAudioFileViaAudioTrack(String filePath) throws IOException
	{
		// We keep temporarily filePath globally as we have only two sample sounds now..
		if (filePath==null)
			return;

		int intSize = android.media.AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_OUT_STEREO,
				AudioFormat.ENCODING_PCM_16BIT); 

		AudioTrack at = new AudioTrack(AudioManager.STREAM_MUSIC, 44100, AudioFormat.CHANNEL_OUT_STEREO,
				AudioFormat.ENCODING_PCM_16BIT, intSize, AudioTrack.MODE_STREAM); 

		if (at == null){
			Log.d("TCAudio", "audio track is not initialised ");
			return;
		}

		int count = 512 * 1024; // 512 kb
		//Reading the file..

		File file = new File(filePath);
		byte[] byteData = new byte[(int)count];

		FileInputStream in = null;
		try {
			in = new FileInputStream( file );
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		int bytesread = 0, ret = 0;
		int size = (int) file.length();

		at.play();
		while (bytesread < size) 
		{ 
			ret = in.read(byteData, 0, count); 
			if (ret != -1) { // Write the byte array to the track 
				at.write(byteData, 0, ret); 
				bytesread += ret; 
			} else 
				break; 
		} 
		in.close(); 
		at.stop(); 
		at.release(); 
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

	public void playWav(){
	    int minBufferSize = AudioTrack.getMinBufferSize(44100, AudioFormat.CHANNEL_OUT_STEREO, AudioFormat.ENCODING_PCM_16BIT);
	    int bufferSize = 1024;
	    AudioTrack at = new AudioTrack(AudioManager.STREAM_VOICE_CALL, 44100, AudioFormat.CHANNEL_OUT_STEREO, 
	    		AudioFormat.ENCODING_PCM_16BIT, minBufferSize, AudioTrack.MODE_STREAM);

	    int i = 0;
	    byte[] temp = new byte[bufferSize];
	    try {
	        FileInputStream fin = new FileInputStream("/sdcard/song1.wav");
	        Log.d(TAG, "===== opening file : /sdcard/song1.wav ===== ");
	        DataInputStream dis = new DataInputStream(fin);

	        at.play();
	        while((i = dis.read(temp, 0, bufferSize)) > -1){
	            at.write(temp, 0, i);
	            Log.d(TAG, "===== Playing Audio ===== ");
	        }
	        Log.d(TAG, "===== Playing Audio Completed ===== ");
	        at.stop();
	        at.release();
	        dis.close();
	        fin.close();

	    } catch (FileNotFoundException e) {
	        // TODO
	        e.printStackTrace();
	    } catch (IOException e) {
	        // TODO
	        e.printStackTrace();
	    }       
	}
}