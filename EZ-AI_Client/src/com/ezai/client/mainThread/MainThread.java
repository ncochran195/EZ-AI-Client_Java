package com.ezai.client.mainThread;

import com.ezai.client.gui.TestGui;
import com.ezai.client.handler.ConfigurationHandler;
import com.ezai.client.handler.ImageInputHandler;
import com.ezai.client.handler.KeywordInputHandler;
import com.ezai.client.service.impl.WebService;

public class MainThread {
	public static void main(String[] args) {
		try {
			//	Try to get the ip from config
			String ipAddress = ConfigurationHandler.getInstance().getValue("ipAddress");
			//	If the ip is not in config
			if (ipAddress == null || ipAddress.equals("")) {
				//	Scan for the ip
				ipAddress = WebService.getInstance().getServerIP(ConfigurationHandler.getInstance().getValue("ezaiHealthCheckURL"));
				//	and add to config
				ConfigurationHandler.getInstance().setValue("ipAddress", ipAddress);
			}

			//	Get the device ID.
			WebService.getInstance().getDeviceID(ConfigurationHandler.getInstance().getValue("ezaiGetDeviceIdURL").replace("$IP_ADDRESS", ipAddress));
			
			//	Start the keyword input handler
			KeywordInputHandler.getInstance().start();
			
			//	Start the image input handler
			ImageInputHandler.getInstance().start();
			
			//	Start the GUI
			TestGui.getInstance();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
