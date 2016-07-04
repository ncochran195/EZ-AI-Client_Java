package com.ezai.client.service;

public interface IAudioInputService {
	
	/**
	 * This method starts recording audio when the volume is considered 'loud' and stops when the volume is considered 'quiet' and saves the audio data to filename.
	 * In this case, being loud means that the volume has exceeded the background noise volume times startMultiplier
	 * and being quiet means the volume has been below this volume for more than paddingFrames frames of audio.
	 * @param filename the name of the file to be saved
	 * @param startMultiplier the multiplier used to determine if the current volume is loud
	 * @param paddingFrames the number of audio frames to pad the beginning and end of the audio by
	 * @throws Exception if the volume is too low
	 */
	public void recordAndWriteAudioFile(String filename, int minimumVolume, double startMultiplier, double endMultiplier, int paddingFrames) throws Exception;
}
