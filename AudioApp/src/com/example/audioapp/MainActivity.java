package com.example.audioapp;

import java.io.IOException;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {

	private static MediaRecorder mediaRecorder;
	private static MediaPlayer mediaPlayer;
	
	private static TextView filepath;
	private static String audioFilePath;
	private static Button stopButton;
	private static Button playButton;
	private static Button recordButton;

	private boolean isRecording = false;

	protected boolean hasMicrophone() {
		PackageManager pmanager = this.getPackageManager();
		return pmanager.hasSystemFeature(PackageManager.FEATURE_MICROPHONE);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		recordButton = (Button) findViewById(R.id.recordButton);
		playButton = (Button) findViewById(R.id.playButton);
		stopButton = (Button) findViewById(R.id.stopButton);
		filepath = (TextView) findViewById(R.id.filepath);

		audioFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() 
				+ "/myaudio.3gp";	
		filepath.setText("File saved at :  " + audioFilePath);

		if (!hasMicrophone())
		{
			stopButton.setEnabled(false);
			playButton.setEnabled(false);
			recordButton.setEnabled(false);
		} else {
			playButton.setEnabled(false);
			stopButton.setEnabled(false);
		}
		
		playButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					playAudio(v);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		recordButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					recordAudio(v);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		stopButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				stopClicked(v);
			}
		});
	}

	public void recordAudio (View view) throws IOException
	{
		isRecording = true;
		stopButton.setEnabled(true);
		playButton.setEnabled(false);
		recordButton.setEnabled(false);

		try {
			mediaRecorder = new MediaRecorder();
			mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			mediaRecorder.setOutputFile(audioFilePath);
			mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			mediaRecorder.prepare();
		} catch (Exception e) {
			e.printStackTrace();
		}

		mediaRecorder.start();			
	}

	public void stopClicked (View view)
	{
		stopButton.setEnabled(false);
		playButton.setEnabled(true);

		if (isRecording)
		{	
			recordButton.setEnabled(false);
			mediaRecorder.stop();
			mediaRecorder.release();
			mediaRecorder = null;
			isRecording = false;
		} else {
			mediaPlayer.release();
			mediaPlayer = null;
			recordButton.setEnabled(true);
		}
	}

	public void playAudio (View view) throws IOException
	{
		playButton.setEnabled(false);
		recordButton.setEnabled(false);
		stopButton.setEnabled(true);

		mediaPlayer = new MediaPlayer();
		mediaPlayer.setDataSource(audioFilePath);
		mediaPlayer.prepare();
		mediaPlayer.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

}
