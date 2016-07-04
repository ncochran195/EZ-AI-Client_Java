package com.ezai.client.handler;

import com.ezai.client.gui.TestGui;
import com.ezai.client.model.AIResponse;
import com.ezai.client.service.impl.AudioInputService;
import com.ezai.client.service.impl.AudioOutputService;
import com.ezai.client.service.impl.WebService;

public class AudioHandler {
	private boolean isStaged = false;
	private boolean handlingAudio;
	
	private static AudioHandler instance;

	public static AudioHandler getInstance() {
		if (instance == null) {
			instance = new AudioHandler();
		}
		return instance;
	}

	private AudioHandler() { }

	// This method checks if we are handling audio. If not, we record, send the
	// audio, get the audio, play the audio until we are no longer staged.
	public void handleAudio() {
		if (!handlingAudio) {
			handlingAudio = true;
			new Thread(() -> {
				System.out.println("STARTING HANDLE AUDIO THREAD");
				TestGui.getInstance().addText("STARTING HANDLE AUDIO THREAD");
				//	while we are staged (already handling a request) and
				//	and we are handling audio (we can stop handling audio if we cancel audio)
				boolean firstTime = true;
				while ((isStaged||firstTime) && handlingAudio) {
					firstTime = false;
					try {
						// Then start recording the audio
						KeywordInputHandler.getInstance().stopRecognition();
						try {
							AudioInputService.getInstance().recordAndWriteAudioFile(ConfigurationHandler.getInstance().getValue("audioInputFilename"), Integer.parseInt(ConfigurationHandler.getInstance().getValue("minimumMicVolume")), Double.parseDouble(ConfigurationHandler.getInstance().getValue("cutoffStartMultiplier")), Double.parseDouble(ConfigurationHandler.getInstance().getValue("cutoffEndMultiplier")), Integer.parseInt(ConfigurationHandler.getInstance().getValue("framePadding")));
						} catch(Exception e) {
							//	We need to catch the 'volume too low' exception explicitly here or else the keyword input handler never gets started.
							//	Just start keyword input handler and iterage the loop again.
							System.out.println("VOLUME TOO LOW");
							TestGui.getInstance().addText("VOLUME TOO LOW");
							KeywordInputHandler.getInstance().startRecognition();
							continue;
						}
						
						//	Get the device ID
						String deviceId = WebService.getInstance().getDeviceID(ConfigurationHandler.getInstance().getValue("ezaiGetDeviceIdURL").replace("$IP_ADDRESS", ConfigurationHandler.getInstance().getValue("ipAddress")));
						
						// After we have the audio recorded, send the audio to the ezai server
						String sendAudioUrl = ConfigurationHandler.getInstance().getValue("ezaiParseAudioURL").replace("$IP_ADDRESS", ConfigurationHandler.getInstance().getValue("ipAddress"));
						AIResponse response = WebService.getInstance().sendAudio(sendAudioUrl, ConfigurationHandler.getInstance().getValue("audioInputFilename"), deviceId, isStaged);
						
						// Parse the ezai server response
						instance.isStaged = response.getIsStaged();
						String reply = response.getResponse();
						
						//	We do not want to listen to 'cancel' if we are a staged command.
						if (!instance.isStaged) {
							KeywordInputHandler.getInstance().startRecognition();
						}
						
						// Get the audio
						WebService.getInstance().getAudio(ConfigurationHandler.getInstance().getValue("ezaiGetAudioURL").replace("$IP_ADDRESS", ConfigurationHandler.getInstance().getValue("ipAddress")), reply, ConfigurationHandler.getInstance().getValue("audioOutputFilename"));
						
						//	We need to make sure that we are still handling audio, because a user can 'cancel' between the last iteration of the main loop and this point.
						if (handlingAudio) {
							//	Play the audio
							AudioOutputService.getInstance().readAndPlayAudioData(ConfigurationHandler.getInstance().getValue("audioOutputFilename"));
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				handlingAudio = false;
				System.out.println("CLOSING HANDLE AUDIO THREAD");	
				TestGui.getInstance().addText("CLOSING HANDLE AUDIO THREAD");

			}).start();
		}
		else {
			try {
				KeywordInputHandler.getInstance().startRecognition();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	// This method checks if we are handling audio. If not, we get the audio,
	// play the audio and call handleAudio asynchronously.
	public void handleAudioWithSpeech(String speech, boolean isStaged) {
		if (!handlingAudio) {
			handlingAudio = true;
			try {
				instance.isStaged = isStaged;
				String audioOutFileName = ConfigurationHandler.getInstance().getValue("audioOutputFilename");
				String ipAddress = ConfigurationHandler.getInstance().getValue("ipAddress");
				String getAudioUrl = ConfigurationHandler.getInstance().getValue("ezaiGetAudioURL").replace("$IP_ADDRESS", ipAddress);
				
				// Get the audio
				WebService.getInstance().getAudio(getAudioUrl, speech, audioOutFileName);
			
				//	Play the audio
				AudioOutputService.getInstance().readAndPlayAudioData(audioOutFileName);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			if (instance.isStaged) {
				// start handleAudio
				handlingAudio = false;
				handleAudio();
			}
			handlingAudio = false;
		}
	}
	
	public void cancelAudio() {
		handlingAudio = false;
	}
	
	public boolean isHandlingAudio() {
		return handlingAudio;
	}
	
	public void setIsStaged(boolean isStaged) {
		instance.isStaged = isStaged;
	}
	
	public boolean getIsStaged() {
		return instance.isStaged;
	}
}