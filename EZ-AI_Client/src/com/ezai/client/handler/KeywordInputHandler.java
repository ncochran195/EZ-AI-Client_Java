package com.ezai.client.handler;

import java.io.IOException;

import com.ezai.client.gui.TestGui;
import com.ezai.client.service.impl.AudioOutputService;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.ezai.client.LiveSpeechRecognizer;

public class KeywordInputHandler {
	private static final String ACOUSTIC_MODEL = "resource:/edu/cmu/sphinx/models/en-us/en-us";
	private static final String DICTIONARY_PATH = "resource:/edu/cmu/sphinx/models/en-us/cmudict-en-us.dict";
	private static final String GRAMMAR_PATH = "resource:/edu/cmu/sphinx/demo/dialog/";

	// Recognizer instance
	LiveSpeechRecognizer jsgfRecognizer = null;

	// Singleton instance and method
	private static KeywordInputHandler instance;

	public static KeywordInputHandler getInstance() throws IOException {
		if (instance == null)
			instance = new KeywordInputHandler();
		return instance;
	}

	private KeywordInputHandler() throws IOException {
		// Setup the config
		Configuration configuration = new Configuration();
		configuration.setAcousticModelPath(ACOUSTIC_MODEL);
		configuration.setDictionaryPath(DICTIONARY_PATH);
		configuration.setGrammarPath(GRAMMAR_PATH);
		configuration.setUseGrammar(true);
		configuration.setGrammarName("dialog");
		// Setup the recognizer
		jsgfRecognizer = new LiveSpeechRecognizer(configuration);
	}
	Thread keywordThread = null;
	public void start() {
		keywordThread = new Thread(() -> {
			TestGui.getInstance().addText("STARTING KEYWORD INPUT THREAD");
			System.out.println("STARTING KEYWORD INPUT THREAD");
			jsgfRecognizer.startRecognition(true);
			while (true) {
				String utterance = jsgfRecognizer.getResult().getHypothesis();
				if (utterance != null) {
					System.out.println(utterance);
					TestGui.getInstance().addText(utterance);
					// If the text equals the keyword...
					try {
						if (utterance.equals(ConfigurationHandler.getInstance().getValue("activationKeyword"))) {
							// Handle Audio (we do not wait. We want to be able
							// to listen to cancel)
							AudioHandler.getInstance().handleAudio();
						} else if (utterance.equals(ConfigurationHandler.getInstance().getValue("cancelKeyword"))) {
							// Stop the audio
							if (!AudioHandler.getInstance().getIsStaged()) {
								AudioHandler.getInstance().cancelAudio();
								AudioOutputService.getInstance().stopAudio();
							}
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		keywordThread.start();
	}

	@SuppressWarnings("deprecation")
	public void stopRecognition() {
	//	jsgfRecognizer.stopRecognition();
		if (keywordThread != null) {
			keywordThread.stop();
			instance = null;
		}
	}

	public void startRecognition() {
	//	jsgfRecognizer.startRecognition(true);
		try {
			instance = new KeywordInputHandler();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		instance.start();
	}

}
