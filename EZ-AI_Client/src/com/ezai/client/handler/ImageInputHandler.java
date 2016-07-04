package com.ezai.client.handler;

import com.ezai.client.gui.TestGui;
import com.ezai.client.model.AIResponse;
import com.ezai.client.service.impl.AudioOutputService;
import com.ezai.client.service.impl.CameraService;
import com.ezai.client.service.impl.WebService;

public class ImageInputHandler {
	private static ImageInputHandler instance;
	private ImageInputHandler() { }
	public static ImageInputHandler getInstance() {
		if (instance == null) {
			instance = new ImageInputHandler();
		}
		return instance;
	}
	
	public void start() {
		new Thread(() -> {
			try {
				while (true) {
					String imageFileName = ConfigurationHandler.getInstance().getValue("imageInFilename");
					String ipAddress = ConfigurationHandler.getInstance().getValue("ipAddress");
					//	Take a picture and save the image
					TestGui.getInstance().addText("TAKING PICTURE");
					System.out.println("TAKING PICTURE");
					CameraService.getInstance().takeAndSavePicture(imageFileName);
					
					//	Update GUI
					TestGui.getInstance().updateImage();
					TestGui.getInstance().forceUpdate();
					
					//	Send the image to EZ-AI server
					String deviceId = WebService.getInstance().getDeviceID(ConfigurationHandler.getInstance().getValue("ezaiGetDeviceIdURL").replace("$IP_ADDRESS", ipAddress));
					String ezaiSendImageUrl = ConfigurationHandler.getInstance().getValue("ezaiParseImageURL").replace("$IP_ADDRESS", ipAddress).replace("$DEVICE_ID", deviceId);
					AIResponse response = WebService.getInstance().sendImage(ezaiSendImageUrl, imageFileName, deviceId);
					
					//	Parse the response
					//	if the response has an audio part and we are not handling audio
					if (response.getStatus().equalsIgnoreCase("success") && !AudioHandler.getInstance().isHandlingAudio()) {
						String audioOutFileName = ConfigurationHandler.getInstance().getValue("audioOutputFilename");
						String getAudioUrl = ConfigurationHandler.getInstance().getValue("ezaiGetAudioURL").replace("$IP_ADDRESS", ipAddress);
						
						// Get the audio
						WebService.getInstance().getAudio(getAudioUrl, response.getResponse(), audioOutFileName);
					
						//	Play the audio
						AudioOutputService.getInstance().readAndPlayAudioData(audioOutFileName);
						
						//	If we are staged, then handleAudio
						if (response.getIsStaged()) {
							AudioHandler.getInstance().setIsStaged(true);
							AudioHandler.getInstance().handleAudio();
						}
					}
					//	Wait for 5 seconds
					Thread.sleep(5000);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}).start();
	}
}
