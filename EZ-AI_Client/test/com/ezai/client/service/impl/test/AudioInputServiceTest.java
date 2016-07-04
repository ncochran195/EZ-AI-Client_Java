package com.ezai.client.service.impl.test;

import com.ezai.client.service.IAudioInputService;
import com.ezai.client.service.impl.AudioInputService;

public class AudioInputServiceTest {
	public static void main(String args[]) {
		IAudioInputService audioInputService = AudioInputService.getInstance();
		try {
			audioInputService.recordAndWriteAudioFile("test/resource/in.wav", 20, 1.2, 1.0, 5);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
