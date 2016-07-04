package com.ezai.client.service;

public interface IAudioOutputService {
	/**
	 * This method reads the audio data at filename and plays the audio
	 * @param filename the filename of the audio file to play
	 */
	public void readAndPlayAudioData(String filename);
	
	/**
	 * This method stops whatever audio is playing
	 */
	public void stopAudio();
}
