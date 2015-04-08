package com.example.audiorecplayfinalaug14;


/* This Program is used to Record and Play back Some audio 
 * The Recorded Audio is written to a file, and same file is used during Play back
 * */

import java.io.DataInputStream;
import java.util.Arrays;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaCodecInfo.CodecCapabilities;
import android.media.MediaRecorder.AudioEncoder;
import android.media.MediaRecorder.AudioSource;
import android.net.rtp.AudioCodec;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.os.Build;

public class Main extends ActionBarActivity {

	private static int[] mSampleRates = new int[] { 8000, 11025, 22050, 44100 };

	private String TAG = "AUDIO_RECORD_PLAYBACK_SAURABH";
	private static final int RECORDER_BPP = 16;
	private static final String AUDIO_RECORDER_FILE_EXT_WAV = ".wav";
	private static final String AUDIO_RECORDER_FOLDER = "AudioRecorder";
	private static final String AUDIO_RECORDER_TEMP_FILE = "record_temp.raw";
	private static final int RECORDER_SAMPLERATE = 8000;
	private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
	private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

	private AudioRecord recorder = null;
	private AudioCodec codec = AudioCodec.getCodec(100, "AMR/8000", "mode-set=1");
	
	private int bufferSize = 0;

	/* Threads for Recording/Playing */
	private Thread recordingThread = null;
	//private Thread playingThread = null;

	/* Flags for Recording/Playing */
	private boolean isRecording = false;
	//private boolean isPlaying = false;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "\n\n\n\n======================= Starting Application now =======================");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		setButtonHandlers();

		enableButton(R.id.btnStartRec,true);
		enableButton(R.id.btnStopRec,false);

		enableButton(R.id.btnStartPlay,false);
		enableButton(R.id.btnStopPlay,false);

		bufferSize = AudioRecord.getMinBufferSize(8000,
				AudioFormat.CHANNEL_IN_DEFAULT,
				AudioFormat.ENCODING_PCM_16BIT);		/* Return 640 */

		/** Overriding bufferSize value of 640 with 320**/
		bufferSize = 320;
		Log.d(TAG, "AudioRecord==> Size of 'BufferSize' :" +bufferSize);

		/**AudioCodec arr[] = codec.getCodecs();
		Log.d(TAG, "Supported Codecs :" +Arrays.toString(arr) +"\n");
		**/

		/*if (savedInstanceState == null) {
			getSupportFragmentManager().beginTransaction()
			.add(R.id.container, new PlaceholderFragment()).commit();
		}*/
	}

	private void startRecording()
	{
		recorder = findAudioRecord();
		int i = recorder.getState();
		if(i==1)
			recorder.startRecording();

		isRecording = true;
		recordingThread = new Thread(new Runnable() {
			@Override
			public void run() {
				writeAudioDataToFile();
			}
		},"AudioRecorder Thread");
		recordingThread.start();
	}

	private void stopRecording(){
		if(null != recorder){
			isRecording = false;

			int i = recorder.getState();
			if(i==1)
				recorder.stop();
			recorder.release();
			Log.d(TAG, "===== Recording Audio Completed ===== ");

			recorder = null;
			recordingThread = null;
		}

		//copyWaveFile(getTempFilename(), getFilename());
		//deleteTempFile();
	}

	public void StartPlaying()
	{
		int minBufferSize = AudioTrack.getMinBufferSize(8000, 
				AudioFormat.CHANNEL_OUT_MONO, 
				AudioFormat.ENCODING_PCM_16BIT);		/* Return 640 */
		
		/** Overriding bufferSize value of 640 with 320**/
		minBufferSize = 320;
		
		Log.d(TAG, "===== StartPlaying ==> Value of  minBufferSize : ["+minBufferSize+"] ===== ");
		int bufferSize = minBufferSize;
		AudioTrack at = new AudioTrack(AudioManager.STREAM_MUSIC, 8000, AudioFormat.CHANNEL_OUT_MONO, 
				//AudioFormat.ENCODING_PCM_16BIT, minBufferSize, AudioTrack.MODE_STREAM);
				AudioFormat.ENCODING_PCM_16BIT, bufferSize, AudioTrack.MODE_STREAM);

		int i = 0;
		byte[] temp = new byte[bufferSize];
		try {
			//FileInputStream fin = new FileInputStream("/sdcard/AudioRecorder/audiofile.wav");
			//Log.d(TAG, "===== opening file : /sdcard/AudioRecorder/audiofile.wav ===== ");
			
			FileInputStream fin = new FileInputStream(raw_filename);
			Log.d(TAG, "===== Opening file : "+raw_filename+" ===== ");

			DataInputStream dis = new DataInputStream(fin); 
			
			SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS");
			Calendar cal = Calendar.getInstance();
			String strDate = s.format(cal.getTime());

			at.play();
			while((i = dis.read(temp, 0, bufferSize)) > -1)
			{
				cal = Calendar.getInstance();
				strDate = s.format(cal.getTime());
				//Log.e(TAG, "================== Buffer before Playing ===================="+Arrays.toString(temp));
				at.write(temp, 0, i);
				//Log.e(TAG, "================== Buffer after Playing ===================="+Arrays.toString(temp));
				//Log.d(TAG, "===== Playing Audio ===== Time :"+strDate);
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

	private void writeAudioDataToFile()
	{
		byte data[] = new byte[bufferSize];
		String filename = getTempFilename();
		FileOutputStream os = null;

		try {
			os = new FileOutputStream(filename);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int read = 0;

		SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SS");
		Calendar cal = Calendar.getInstance();
		String strDate = s.format(cal.getTime());

		if(null != os){
			while(isRecording){

				/* Printing Time Stamp */
				//Log.e(TAG, "================== Buffer Before Reading ===================="+Arrays.toString(data));
				read = recorder.read(data, 0, bufferSize);
				//Log.e(TAG, "================== Buffer After Reading ===================="+Arrays.toString(data));
				
				cal = Calendar.getInstance();
				strDate = s.format(cal.getTime());
				//Log.d(TAG, "===== Recording Audio ===== Time :"+strDate);

				if(AudioRecord.ERROR_INVALID_OPERATION != read){
					try {
						os.write(data);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}

			try {
				os.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private String getFilename(){
		String filepath = Environment.getExternalStorageDirectory().getPath();
		File file = new File(filepath,AUDIO_RECORDER_FOLDER);

		if(!file.exists()){
			file.mkdirs();
		}
		return (file.getAbsolutePath() + "/" + "audiofile" + AUDIO_RECORDER_FILE_EXT_WAV);
	}

	private String raw_filename = null;

	private String getTempFilename(){
		String filepath = Environment.getExternalStorageDirectory().getPath();
		File file = new File(filepath,AUDIO_RECORDER_FOLDER);

		if(!file.exists()){
			file.mkdirs();
		}

		File tempFile = new File(filepath,AUDIO_RECORDER_TEMP_FILE);

		//if(tempFile.exists())
		//	tempFile.delete();

		raw_filename = file.getAbsolutePath() + "/" + AUDIO_RECORDER_TEMP_FILE;
		Log.d(TAG, "================= Temp File Name : "+file.getAbsolutePath() + "/" + AUDIO_RECORDER_TEMP_FILE+"=================");
		return (file.getAbsolutePath() + "/" + AUDIO_RECORDER_TEMP_FILE);
	}

	private void copyWaveFile(String inFilename,String outFilename){
		FileInputStream in = null;
		FileOutputStream out = null;
		long totalAudioLen = 0;
		long totalDataLen = totalAudioLen + 36;
		long longSampleRate = RECORDER_SAMPLERATE;
		int channels = 1;
		long byteRate = (RECORDER_BPP/8) * RECORDER_SAMPLERATE * channels;

		byte[] data = new byte[bufferSize];

		try {
			in = new FileInputStream(inFilename);
			out = new FileOutputStream(outFilename);
			totalAudioLen = in.getChannel().size();
			totalDataLen = totalAudioLen + 36;

			Log.d(TAG, "File size: " + totalDataLen);

			WriteWaveFileHeader(out, totalAudioLen, totalDataLen,
					longSampleRate, channels, byteRate);

			while(in.read(data) != -1){
				out.write(data);
			}
			in.close();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void WriteWaveFileHeader(
			FileOutputStream out, long totalAudioLen,
			long totalDataLen, long longSampleRate, int channels,
			long byteRate) throws IOException {

		byte[] header = new byte[44];

		header[0] = 'R';  // RIFF/WAVE header
		header[1] = 'I';
		header[2] = 'F';
		header[3] = 'F';
		header[4] = (byte) (totalDataLen & 0xff);
		header[5] = (byte) ((totalDataLen >> 8) & 0xff);
		header[6] = (byte) ((totalDataLen >> 16) & 0xff);
		header[7] = (byte) ((totalDataLen >> 24) & 0xff);
		header[8] = 'W';
		header[9] = 'A';
		header[10] = 'V';
		header[11] = 'E';
		header[12] = 'f';  // 'fmt ' chunk
		header[13] = 'm';
		header[14] = 't';
		header[15] = ' ';
		header[16] = 16;  // 4 bytes: size of 'fmt ' chunk
		header[17] = 0;
		header[18] = 0;
		header[19] = 0;
		header[20] = 1;  // format = 1
		header[21] = 0;
		header[22] = (byte) channels;
		header[23] = 0;
		header[24] = (byte) (longSampleRate & 0xff);
		header[25] = (byte) ((longSampleRate >> 8) & 0xff);
		header[26] = (byte) ((longSampleRate >> 16) & 0xff);
		header[27] = (byte) ((longSampleRate >> 24) & 0xff);
		header[28] = (byte) (byteRate & 0xff);
		header[29] = (byte) ((byteRate >> 8) & 0xff);
		header[30] = (byte) ((byteRate >> 16) & 0xff);
		header[31] = (byte) ((byteRate >> 24) & 0xff);
		header[32] = (byte) (2 * 16 / 8);  // block align
		header[33] = 0;
		header[34] = RECORDER_BPP;  // bits per sample
		header[35] = 0;
		header[36] = 'd';
		header[37] = 'a';
		header[38] = 't';
		header[39] = 'a';
		header[40] = (byte) (totalAudioLen & 0xff);
		header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
		header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
		header[43] = (byte) ((totalAudioLen >> 24) & 0xff);

		out.write(header, 0, 44);
	}

	/* Method will attempt to find correct combination of Audio Configuration, by attempting 
	 * different combinations of Sampling rate, channel configuration and encoding_types */
	public AudioRecord findAudioRecord()
	{
		for (int rate : mSampleRates) {
			for (short audioFormat : new short[] { AudioFormat.ENCODING_PCM_8BIT, AudioFormat.ENCODING_PCM_16BIT }) {
				for (short channelConfig : new short[] { AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO }) {
					try {
						Log.d(TAG, "Attempting rate " + rate + "Hz, bits: " + audioFormat + ", channel: "
								+ channelConfig);
						int bufferSize = AudioRecord.getMinBufferSize(rate, channelConfig, audioFormat);

						if (bufferSize != AudioRecord.ERROR_BAD_VALUE) {
							// check if we can instantiate and have a success
							AudioRecord recorder = new AudioRecord(AudioSource.DEFAULT, rate, channelConfig, audioFormat, bufferSize);

							if (recorder.getState() == AudioRecord.STATE_INITIALIZED) {
								Log.d(TAG, "================== Final Values: rate " + rate + "Hz, audioFormat: " + audioFormat + ", channel: "
										+ channelConfig+" ==================");
								return recorder;
							}
						}
					} catch (Exception e) {
						Log.e(TAG, rate + "Exception, keep trying.",e);
					}
				}
			}
		}
		return null;
	}

	private void deleteTempFile() {
		File file = new File(getTempFilename());
		file.delete();
	}

	private View.OnClickListener btnClick = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.btnStartRec:{
				Log.d(TAG, "Start Recording");
				enableButton(R.id.btnStartRec,false);
				enableButton(R.id.btnStopRec,true);
				startRecording();
				enableButton(R.id.btnStopPlay,false);
				break;
			}
			case R.id.btnStopRec:{
				Log.d(TAG, "Stop Recording");
				enableButton(R.id.btnStartRec,true);
				enableButton(R.id.btnStopRec,false);
				stopRecording();
				enableButton(R.id.btnStartPlay,true);
				break;
			}
			case R.id.btnStartPlay:{
				Log.d(TAG, "Play Recording");
				enableButton(R.id.btnStartRec,false);
				enableButton(R.id.btnStopRec,false);
				StartPlaying();
				enableButton(R.id.btnStartPlay,false);
				enableButton(R.id.btnStopPlay,true);
				break;
			}

			case R.id.btnStopPlay:{
				Log.d(TAG, "Stop Playing");
				//StopPlaying();
				enableButton(R.id.btnStartPlay,true);
				enableButton(R.id.btnStopPlay,false);
				enableButton(R.id.btnStartRec,true);
				break;
			}
			}
		}
	}; 

	/* Assign OnClickListener to Buttons */
	private void setButtonHandlers() {
		((Button)findViewById(R.id.btnStartRec)).setOnClickListener(btnClick);
		((Button)findViewById(R.id.btnStopRec)).setOnClickListener(btnClick);
		((Button)findViewById(R.id.btnStartPlay)).setOnClickListener(btnClick);
		((Button)findViewById(R.id.btnStopPlay)).setOnClickListener(btnClick);
	}

	/* Function to Enable/Disable Buttons */
	private void enableButton(int id,boolean isEnable){
		((Button)findViewById(id)).setEnabled(isEnable);
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
