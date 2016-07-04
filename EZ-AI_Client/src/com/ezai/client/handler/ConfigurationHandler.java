package com.ezai.client.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.ezai.client.service.IFileIOService;
import com.ezai.client.service.impl.FileIOService;

public class ConfigurationHandler {
	private Map<String, String> configuration = null;
	private static String configFileLocation = "resource/conf.csv";
	
	public static ConfigurationHandler instance;
	private ConfigurationHandler() {};
	public static ConfigurationHandler getInstance() throws Exception{
		if (instance == null) {
			instance = new ConfigurationHandler();
			try {
				instance.init(configFileLocation);
			} catch (Exception e) {
				throw e;
			}
		}
		return instance;
	}
	
	public void init(String configFileLocation) throws Exception {
		this.configuration = getDefaultConfiguration();
		IFileIOService fileIOService = FileIOService.getInstance();
		try {
			//	Override with saved config
			Map<String, String> savedConfig = fileIOService.getCSVContent(configFileLocation);
			this.configuration.putAll(savedConfig);
		} catch (IOException e) {
			throw new Exception("could not load config from " + configFileLocation);
		}
	}
	
	public String getValue(String attribute) {
		return configuration.get(attribute);
	}
	
	public void setValue(String attribute, String value) throws Exception {
		//	Update local
		configuration.put(attribute, value);
		//	Update saved copy
		IFileIOService fileIOService = FileIOService.getInstance();
		try {
			fileIOService.writeSCVAttribute(configFileLocation, attribute, value);
		} catch (IOException e) {
			throw new Exception("could not write to config file " + configFileLocation);
		}
	}
	
	private Map<String, String> getDefaultConfiguration() {
		Map<String, String> defaultConfiguration = new HashMap<String, String>();
		
		defaultConfiguration.put("ipAddress", "");
		defaultConfiguration.put("ezaiParseAudioURL", "http://$IP_ADDRESS:8080/EZ-AI_Server/REST/parseAudio.json?deviceId=$DEVICE_ID&isStaged=$IS_STAGED");
		defaultConfiguration.put("ezaiGetAudioURL", "http://$IP_ADDRESS:8080/EZ-AI_Server/REST/getAudio.out?text=$TEXT");
		defaultConfiguration.put("ezaiParseImageURL", "http://$IP_ADDRESS:8080/EZ-AI_Server/REST/processImage.json?deviceId=$DEVICE_ID");
		defaultConfiguration.put("cutoffStartMultiplier", "1.2");
		defaultConfiguration.put("cutoffEndMultiplier", "1");
		defaultConfiguration.put("minimumMicVolume", "20");
		defaultConfiguration.put("framePadding", "5");
		defaultConfiguration.put("ezaiGetDeviceIdURL", "http://$IP_ADDRESS:8080/EZ-AI_Server/REST/getDeviceId.json");
		defaultConfiguration.put("ezaiHealthCheckURL", "http://$IP_ADDRESS:8080/EZ-AI_Server/REST/healthCheck.json");
		defaultConfiguration.put("ezaiParseTextURL", "http://$IP_ADDRESS:8080/EZ-AI_Server/REST/textOnlyReply.json?query=$QUERY&deviceId=$DEVICE_ID&isStaged=$IS_STAGED");
		defaultConfiguration.put("activationKeyword", "hey rafik");
		defaultConfiguration.put("cancelKeyword", "cancel");
		defaultConfiguration.put("audioInputFilename", "resource/in.wav");
		defaultConfiguration.put("audioOutputFilename", "resource/out.wav");
		defaultConfiguration.put("imageInFilename", "resource/in.jpg");
		
		return defaultConfiguration;
	}
}
