package com.ezai.client.handler.test;

import com.ezai.client.handler.ConfigurationHandler;

public class ConfigurationHandlerTest {
	
	public static void main(String[] args) {
		try {
			ConfigurationHandler configurationHandler = ConfigurationHandler.getInstance();
			System.out.println(configurationHandler.getValue("ipAddress"));
			configurationHandler.setValue("ipAddress", "test2");
			System.out.println(configurationHandler.getValue("ipAddress"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
