package com.example.audiocall;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;

public class MainActivity extends Activity {
	
	int BUFFERELEMENTS2REC=1024;
	int BYTES_PER_ELEMENTS=2;
	
	int sampling_rate=8000;
	int num_channel=2;
	int sample_size=2;
	int period_size;
	int audio_encoding_type=2;
	private static MediaPlayer mediaPlayer;

	private  final int RECORDER_CHANNELS = num_channel;
	private  final int RECORDER_AUDIO_ENCODING = audio_encoding_type;
	private static AudioRecord recorder = null;
	private static boolean isRecording = false;
	private static Thread recordingThread = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		

		
	}

	private void startRecording() 
	{
		recorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
				sampling_rate, num_channel,
				audio_encoding_type, BUFFERELEMENTS2REC
						* BYTES_PER_ELEMENTS);

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
		String filePath = "/sdcard/magan.3gp";

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

}
