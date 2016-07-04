package com.ezai.client.service;

public interface ICameraService {
	
	/**
	 * This method takes an image from the camera and writes the data to filename.
	 * @param filename the name of the file that the camera's image data gets written to
	 */
	public void takeAndSavePicture(String filename);
}
