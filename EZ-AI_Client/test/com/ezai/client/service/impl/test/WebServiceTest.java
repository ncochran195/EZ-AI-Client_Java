package com.ezai.client.service.impl.test;

import com.ezai.client.handler.ConfigurationHandler;
import com.ezai.client.model.AIResponse;
import com.ezai.client.service.IWebService;
import com.ezai.client.service.impl.WebService;

public class WebServiceTest {
	
	public static void main(String[] args) {
		try {
			//	Get config and web service instances
			ConfigurationHandler configurationHandler = ConfigurationHandler.getInstance();
			IWebService webService = WebService.getInstance();
			
			//	Try to get the ip from saved config first
			String ezaiServerIp = configurationHandler.getValue("ipAddress");
			
			//	If it is not in config
			if (ezaiServerIp == null || ezaiServerIp.equals("")) {
				//	Use web service to get ezai ip address 
				//	Test 1
				ezaiServerIp = webService.getServerIP(configurationHandler.getValue("ezaiHealthCheckURL"));
			}
			System.out.println("Got server ip: " + ezaiServerIp);
			
			//	Test 2
			String deviceId = webService.getDeviceID(configurationHandler.getValue("ezaiGetDeviceIdURL").replace("$IP_ADDRESS", ezaiServerIp));
			System.out.println("Got device id: " + deviceId);
			System.out.println();
			
			//	Test 3
			AIResponse aiResponse = webService.sendText(configurationHandler.getValue("ezaiParseTextURL").replace("$IP_ADDRESS", ezaiServerIp), "Hello", deviceId, false);
			System.out.println(aiResponse);
			System.out.println();
			
			//	Test 4
			aiResponse = webService.sendAudio(configurationHandler.getValue("ezaiParseAudioURL").replace("$IP_ADDRESS", ezaiServerIp), "test/resource/in.wav", deviceId, false);
			System.out.println(aiResponse);
			System.out.println();
			
			//	Test 5
			webService.getAudio(configurationHandler.getValue("ezaiGetAudioURL").replace("$IP_ADDRESS", ezaiServerIp), "Hello there, friend", "test/resource/out2.wav");
			
			//	Test 6
			webService.sendImage(configurationHandler.getValue("ezaiParseImageURL").replace("$IP_ADDRESS", ezaiServerIp), "test/resource/test.jpg", deviceId);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
