package com.example.audiorecplay_mediarecorder;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.os.Build;

public class MainActivity extends ActionBarActivity {

    private static final String TAG = "AUDIO_RECORD_PLAYBACK_MEDIA_RECORDER";
    
    /* File where Record/Play back operation is performed */
    private static String mFileName = null;
    
    /* Android API for MediaRecoreder/MediaPlayer - Creating Instance*/
    private MediaRecorder mRecorder = new MediaRecorder();
    private MediaPlayer mPlayer = new MediaPlayer();
    
    /* Flags for Recording or Playing */
    private boolean isRecording = false;
    private boolean isPlaying = false;
    
    /* Audio Configuration */
    private static final int sampling_rate = 8000;
    private static final int no_of_channel = 1;
    private static final int encoding_bit_rate = 2;

    private void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }
    }

	public void StartPlaying()
	{
		int minBufferSize = AudioTrack.getMinBufferSize(8000, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
		Log.d(TAG, "===== Value of  minBufferSize : ["+minBufferSize+"]===== ");
		int bufferSize = minBufferSize;
		AudioTrack at = new AudioTrack(AudioManager.STREAM_MUSIC, 8000, AudioFormat.CHANNEL_OUT_MONO, 
				AudioFormat.ENCODING_PCM_16BIT, minBufferSize, AudioTrack.MODE_STREAM);

		int i = 0;
		byte[] temp = new byte[bufferSize];
		try {
			FileInputStream fin = new FileInputStream(mFileName);
			Log.d(TAG, "===== opening file : "+mFileName+" ===== ");

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

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    private void startRecording() 
    {
    	Log.d(TAG, "Entered Method startRecording");
    	isRecording = true;
    	Log.d(TAG, "setOutputFile set to "+mFileName);
    
    	/* For FSM, refer to : http://developer.android.com/reference/android/media/MediaRecorder.html */
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
        
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        
        mRecorder.setAudioSamplingRate(sampling_rate);
        mRecorder.setAudioChannels(no_of_channel);
        mRecorder.setAudioEncodingBitRate(encoding_bit_rate);

        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }
        
        Log.d(TAG, "Recording...");
        mRecorder.start();	
    }

    private void stopRecording() 
    {
    	Log.d(TAG, "Entered Method stopRecording");
    	isRecording = false;
    	Log.d(TAG, "Stopping Recording");
    	mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        Log.d(TAG, "Leaving Method stopRecording");
    }

    public void audio_init() {
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/audiorecordtest.wav";
        Log.d(TAG, "=================== setOutputFile set to "+mFileName +" ===================");
        return;
    }

    /* Assign OnClickListener to Buttons */
	private void setButtonHandlers() {
		((Button)findViewById(R.id.btnStartRec)).setOnClickListener(btnClick);
		((Button)findViewById(R.id.btnStopRec)).setOnClickListener(btnClick);
		((Button)findViewById(R.id.btnStartPlay)).setOnClickListener(btnClick);
		((Button)findViewById(R.id.btnStopPlay)).setOnClickListener(btnClick);
	}

	/* Method to match Android API to their respective Buttons */
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
				//StartPlaying();
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
	
	/* Function to Enable/Disable Buttons */
	private void enableButton(int id,boolean isEnable){
		((Button)findViewById(id)).setEnabled(isEnable);
	}
    
    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Log.d(TAG, "=================== Starting Application ===================");
        setContentView(R.layout.activity_main);
        setButtonHandlers();

		enableButton(R.id.btnStartRec,true);
		enableButton(R.id.btnStopRec,false);
		enableButton(R.id.btnStartPlay,false);
		enableButton(R.id.btnStopPlay,false);
        
		audio_init();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }
}