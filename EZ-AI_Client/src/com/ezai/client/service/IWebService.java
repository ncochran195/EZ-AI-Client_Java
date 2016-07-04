package com.ezai.client.service;

import com.ezai.client.model.AIResponse;

public interface IWebService {
	/**
	 * This method gets the EZ-AI Server IP.
	 * This can be done in any way, but will involve sending multiple healthCheck
	 * calls and iterating the class D ip values
	 * @param ezaiHealthCheckUrl the URL used to ping each of the IP addresses
	 * @return the IP address of the EZ-AI Server
	 * @throws exception if the client device's ip address cannot be determined or the server's ip address cannot be determined
	 */
	public String getServerIP(String ezaiHealthCheckUrl) throws Exception;
	
	/**
	 * This method returns the device ID from the EZ-AI Server
	 * The device ID is needed for other web service calls
	 * @param ezaiGetDeviceIdUrl the URL to get the device ID from
	 * @return the device ID
	 * @throws exception if the request cannot be completed
	 */
	public String getDeviceID(String ezaiGetDeviceIdUrl) throws Exception;
	
	/**
	 * This method returns the response of the AI given the input text
	 * @param text the speech text to be sent to the EZ-AI Server
	 * @param deviceId the device ID of this client (given by the ezai server)
	 * @param isStaged if the current interaction is staged (given by the ezai server, false by default)
	 * @param ezaiSendTextUrl the URL to send the text to
	 * @return the AI server's response to this text
	 * @throws exception if the request cannot be completed
	 */
	public AIResponse sendText(String ezaiSendTextUrl, String text, String deviceId, boolean isStaged) throws Exception;
	
	/**
	 * This method returns the response of the AI given the audio file found at filename
	 * @param filename the filename of the audio file to be sent to the server
	 * @param ezaiSendAudioUrl the URL to send the audio to
	 * @param deviceId the device ID of this client (given by the ezai server)
	 * @param isStaged if the current interaction is staged (given by the ezai server, false by default)
	 * @return the AI server's response to this audio file
	 * @throws exception if the request cannot be completed
	 */
	public AIResponse sendAudio(String ezaiSendAudioUrl, String filename, String deviceId, boolean isStaged) throws Exception;
	
	/**
	 * This method returns the response of the AI given the image found at filename
	 * @param filename the filename of the image file to be sent to the server
	 * @param ezaiSendImageUrl the URL to send the image to
	 * @param deviceId the device ID of this client (given by the ezai server)
	 * @return the AI server's response to this image
	 * @throws exception if the request cannot be completed
	 */
	public AIResponse sendImage(String ezaiSendImageUrl, String deviceId, String filename) throws Exception;
	
	/**
	 * This method sends the text provided to the AI server and saves the audio response to filename
	 * @param text the text to be sent to the AI server to have converted to audio
	 * @param ezaiGetAudioUrl the URL to get the audio data from
	 * @param filename the name of the file to have the AI's audio response saved to
	 * @throws exception if the request cannot be completed
	 */
	public void getAudio(String ezaiGetAudioUrl, String text, String filename) throws Exception;
}