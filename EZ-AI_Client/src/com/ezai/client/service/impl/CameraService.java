package com.ezai.client.service.impl;

import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;

import com.ezai.client.gui.TestGui;
import com.ezai.client.service.ICameraService;

public class CameraService implements ICameraService {

	//	The OpenCV camera instance
	VideoCapture camera = null;
	
	//	Singleton instance
	public static ICameraService instance = null;
	
	/**
	 * Gets the instance of the CameraService
	 * @return the instance of the cameraService
	 * @throws Exception gets thrown if OpenCV cannot open the camera.
	 */
	public static ICameraService getInstance() throws Exception {
		if (CameraService.instance == null) {
			try {
				CameraService.instance = new CameraService();
			} catch (Exception e) {
				throw e;
			}
		}
		return CameraService.instance;
	}

	//	Init the camera instance
	//	If the camera cannot be opened, throw an exception
	private CameraService() throws Exception {
		//	Load OpenCV platform specific files
		try {
			System.load("/ez-ai_client/libopencv_java310.so");
		} catch (java.lang.UnsatisfiedLinkError e) {
			try {
				System.load("c:\\ez-ai_client\\opencv_java310.dll");
			} catch (java.lang.UnsatisfiedLinkError e2) {
				System.out.println("libopencv_java310.dll(WINDOWS) OR libopencv_java310.so(UNIX) CANNOT BE FOUND AT /ez-ai_client/opencv_java310");
				TestGui.getInstance().addText("libopencv_java310.dll(WINDOWS) OR libopencv_java310.so(UNIX) CANNOT BE FOUND AT /ez-ai_client/opencv_java310");
				e2.printStackTrace();
			}
		}

		//	Open the camera
		camera = new VideoCapture(0);
		if (!camera.isOpened()) {
			throw new Exception("The camera cannot be opened.");
		}		
	}

	@Override
	public synchronized void takeAndSavePicture(String filename) {
		try {
			Mat frame = new Mat();
			camera.read(frame);
			Imgcodecs.imwrite(filename, frame);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
