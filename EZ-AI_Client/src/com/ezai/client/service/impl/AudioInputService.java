package com.ezai.client.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.TargetDataLine;

import com.ezai.client.gui.TestGui;
import com.ezai.client.service.IAudioInputService;
import edu.cmu.sphinx.api.ezai.client.SpeechSourceProvider;

public class AudioInputService implements IAudioInputService {
	private TargetDataLine line;
	private boolean audioStarted;
	private boolean recording;
	
	//	Singleton instance/methods
	private static AudioInputService instance = null;
	private AudioInputService() {};
	public static IAudioInputService getInstance() {
		if (instance == null) {
			instance = new AudioInputService();
		}
		return instance;
	}
	
	//	Gets the ambient volume from the line based on numFrames frames
	private int getAmbientVolume(TargetDataLine line, int numFrames) {
		int[] volumeFrames = new int[numFrames];
		AudioFormat format = new AudioFormat(16000.0F, 16, 1, true, false);
		int frameSizeInBytes = format.getFrameSize();
		int bufferLengthInFrames = line.getBufferSize() / 8;
		int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
		byte[] data = new byte[bufferLengthInBytes];

		line.flush();
		for (int i = 0; i < numFrames; i++) {
			if ((line.read(data, 0, bufferLengthInBytes)) == -1) {
				break;
			}
			volumeFrames[i] = calculateRMSLevel(data);
		}

		int total = 0;
		for (int i = 0; i < volumeFrames.length; i++) {
			total += volumeFrames[i];
		}
		return total/numFrames;
	}

	@Override
	public void recordAndWriteAudioFile(String filename, int minimumVolume, double startMultiplier, double endMultiplier, int paddingFrames) throws Exception {
		if (!this.recording) {
			this.recording = true;
			this.audioStarted = false;

			// define the required attributes for our line,
			// and make sure a compatible line is supported.
			AudioFormat format = new AudioFormat(16000.0F, 16, 1, true, false);

			DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
			if (!AudioSystem.isLineSupported(info)) {
				return;
			}

			// get and open the target data line for capture.
			line = SpeechSourceProvider.getMicrophone().getLine();
			
			//	Variables for recording data
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			int frameSizeInBytes = format.getFrameSize();
			int bufferLengthInFrames = line.getBufferSize() / 8;
			int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
			ArrayList<byte[]> datas = new ArrayList<byte[]>();
			byte[] data = new byte[bufferLengthInBytes];
			ArrayList<Integer> bytesReads = new ArrayList<Integer>();
			int numBytesRead;

			line.flush();
			line.start();

			//	Calculate the ambient volume and the cutoff volume
			int ambientVolume = getAmbientVolume(line,
					paddingFrames);
			if (ambientVolume  < minimumVolume) {
				System.out.println("VOLUME TOO LOW");
				TestGui.getInstance().addText("VOLUME TOO LOW");
				throw new Exception("VOLUME TOO LOW");
			}
			double cutoffStartVolume = ambientVolume * startMultiplier;
			double cutoffEndVolume = ambientVolume * endMultiplier;
			System.out.println("CUTOFF START: " + cutoffStartVolume);
			TestGui.getInstance().addText("CUTOFF START: " + cutoffStartVolume);
			System.out.println("CUTOFF END: " + cutoffEndVolume);
			TestGui.getInstance().addText("CUTOFF END: " + cutoffEndVolume);

			//	Some of this data is appended to the front of the final audio data as padding
			ArrayList<byte[]> preDatas = new ArrayList<byte[]>();
			ArrayList<Integer> preBytesReads = new ArrayList<Integer>();
			
			//	List of all volumes (used to determine if we are quiet)
			ArrayList<Integer> volumes = new ArrayList<Integer>();
			line.flush();
			while (this.recording) {
				//	Read the data from the line
				if ((numBytesRead = line.read(data, 0, bufferLengthInBytes)) == -1) {
					break;
				}
				
				//	Calculate the current volume
				int volume = calculateRMSLevel(data);
				System.out.println("VOLUME: " + volume);
				TestGui.getInstance().addText("VOLUME: " + volume);

				//	Determine if we are 'loud'
				if (volume >= cutoffStartVolume) {
					this.audioStarted = true;
				}			
				if (this.audioStarted && preBytesReads.size() >= paddingFrames) {
					//	Record the audio data in the arrays
					volumes.add(volume);
					datas.add(data.clone());
					bytesReads.add(numBytesRead);
					
					//	If we have enough data to process
					if (volumes.size() >= paddingFrames) {
						//	Then determine if we have been quiet for all of the last $framePadding frames
						boolean loud = false;
						//	Iterate over the last $framePadding frames
						for (int i = 0; i < paddingFrames; i++) {
							//	If any of the last $framePadding frames are 'loud'
							if (volumes.get(volumes.size()-i-1) > cutoffEndVolume) {
								//	Then set the flag
								loud = true;
							}
						}
						//	If we have been quiet for the last $framePadding frames AND we have had at least one frame of 'loud' volume
						if (!loud) {
							//	Then we are done recording
							//	(This flag breaks us out of the while loop and
							//	allows us to move onto writing the data)
							this.recording = false;
						}
					}
				}
				else {
					preDatas.add(data.clone());
					preBytesReads.add(numBytesRead);
				}
			}
			
			//	Write the desired pre-recording padding (helps eliminate cutoff)
			//	(We will always have enough pre-padding frames because of the check above)
			for (int i = preDatas.size() - paddingFrames; i < preDatas.size(); i++) {
				byte[] b = preDatas.get(i);
				int n = preBytesReads.get(i);
				out.write(b, 0, n);
			}
			
			//	Write the data out
			for (int i = 0; i < datas.size(); i++) {
				byte[] b = datas.get(i);
				int n = bytesReads.get(i);
				out.write(b, 0, n);
			}
			
			// we reached the end of the stream.

			// stop and close the output stream
			try {
				out.flush();
				out.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			
			//	Write the file out
			byte audioBytes[] = out.toByteArray();
			ByteArrayInputStream bais = new ByteArrayInputStream(audioBytes);
			AudioInputStream audioInputStream = new AudioInputStream(bais, format, audioBytes.length / frameSizeInBytes);
			try {
				AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, new File(filename));
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				audioInputStream.reset();
			} catch (Exception ex) {
				ex.printStackTrace();
				return;
			}
		}
	}
	
	//	This method calculates the volume based on the Root-Mean-Square method
	private int calculateRMSLevel(byte[] audioData) {
		long lSum = 0;
		for (int i = 0; i < audioData.length; i++)
			lSum = lSum + audioData[i];

		double dAvg = lSum / audioData.length;
		double sumMeanSquare = 0d;

		for (int j = 0; j < audioData.length; j++)
			sumMeanSquare += Math.pow(audioData[j] - dAvg, 2d);

		double averageMeanSquare = sumMeanSquare / audioData.length;

		return (int) (Math.pow(averageMeanSquare, 0.5d) + 0.5);
	}
}
