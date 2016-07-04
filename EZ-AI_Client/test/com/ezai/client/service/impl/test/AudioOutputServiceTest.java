package com.ezai.client.service.impl.test;

import com.ezai.client.service.IAudioOutputService;
import com.ezai.client.service.impl.AudioOutputService;

public class AudioOutputServiceTest {
	public static void main (String[] args) {
		IAudioOutputService audioOutputService = AudioOutputService.getInstance();
		audioOutputService.readAndPlayAudioData("test/resource/out.wav");
	}
}
