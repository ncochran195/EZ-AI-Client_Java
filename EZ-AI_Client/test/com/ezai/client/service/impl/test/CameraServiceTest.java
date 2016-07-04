package com.ezai.client.service.impl.test;

import com.ezai.client.service.ICameraService;
import com.ezai.client.service.impl.CameraService;

public class CameraServiceTest {
	public static void main(String[] args) {
		try {
			ICameraService cameraService = CameraService.getInstance();
			cameraService.takeAndSavePicture("test/resource/test.jpg");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
