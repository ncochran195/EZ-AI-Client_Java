package com.ezai.client.service.impl.test;

import java.io.IOException;
import java.util.Map;

import com.ezai.client.service.IFileIOService;
import com.ezai.client.service.impl.FileIOService;

public class FileIOServiceTest {
	public static void main(String[] args) {
		IFileIOService fileIOService = FileIOService.getInstance();
		try {
			//	Test 1
			Map<String, String> conf = fileIOService.getCSVContent("test/resource/conf.csv");
			for (String key : conf.keySet()) {
				System.out.println(key + " : " + conf.get(key));
			}
			System.out.println();

			//	Test 2
			fileIOService.writeSCVAttribute("test/resource/conf.csv", "ipAddress", "Test");
			
			//	Test 3
			conf = fileIOService.getCSVContent("test/resource/conf.csv");
			for (String key : conf.keySet()) {
				System.out.println(key + " : " + conf.get(key));
			}
			System.out.println();
			
			//	Test 4
			fileIOService.writeSCVAttribute("test/resource/conf.csv", "testAttr", "testVal");

			//	Test 5
			conf = fileIOService.getCSVContent("test/resource/conf.csv");
			for (String key : conf.keySet()) {
				System.out.println(key + " : " + conf.get(key));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
