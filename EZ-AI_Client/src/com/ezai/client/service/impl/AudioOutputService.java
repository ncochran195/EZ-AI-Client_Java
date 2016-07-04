package com.ezai.client.service.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import com.ezai.client.service.IAudioOutputService;

public class AudioOutputService implements IAudioOutputService {
	SourceDataLine line = null;

	private static IAudioOutputService instance;
	private AudioOutputService() {};
	public static IAudioOutputService getInstance() {
		if (instance == null) {
			instance = new AudioOutputService();
		}
		return instance;
	}
	
	@Override
	public void readAndPlayAudioData(String filename) {
		File file = new File(filename);
		AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 16000f, 16, 1, 2, 16000f, false);
		int frameSizeInBytes = format.getFrameSize();
		final int bufSize = 16384;

		FileInputStream fileInputStream = null;
		byte[] bFile = new byte[(int) file.length()];
		try {
			fileInputStream = new FileInputStream(file);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			fileInputStream.read(bFile);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			fileInputStream.close();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		ByteArrayInputStream bais = new ByteArrayInputStream(bFile);
		AudioInputStream audioInputStream = new AudioInputStream(bais, format, bFile.length / frameSizeInBytes);

		line = null;

		// reset to the beginning of the stream
		try {
			audioInputStream.reset();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// get an AudioInputStream of the desired format for playback
		AudioInputStream playbackInputStream = AudioSystem.getAudioInputStream(format, audioInputStream);
		// define the required attributes for our line,
		// and make sure a compatible line is supported.
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
		// get and open the source data line for playback.
		try {
			line = (SourceDataLine) AudioSystem.getLine(info);
			line.open(format, bufSize);
		} catch (LineUnavailableException ex) {
			ex.printStackTrace();
		}
		// play back the captured audio data
		int bufferLengthInFrames = line.getBufferSize() / 8;
		int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
		byte[] data = new byte[bufferLengthInBytes];
		int numBytesRead = 0;

		// start the source data line
		line.start();

		while (true) {
			try {
				if ((numBytesRead = playbackInputStream.read(data)) == -1) {
					break;
				}
				int numBytesRemaining = numBytesRead;
				while (numBytesRemaining > 0) {
					numBytesRemaining -= line.write(data, 0, numBytesRemaining);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		line.drain();
		line.stop();
		line.close();
		line = null;

	}
	@Override
	public void stopAudio() {
		// TODO Auto-generated method stub
		if (line != null && line.isActive())
			line.stop();
	}

}
