/*
 * Copyright 2013 Carnegie Mellon University.
 * Portions Copyright 2004 Sun Microsystems, Inc.
 * Portions Copyright 2004 Mitsubishi Electric Research Laboratories.
 * All Rights Reserved.  Use is subject to license terms.
 *
 * See the file "license.terms" for information on usage and
 * redistribution of this file, and for a DISCLAIMER OF ALL
 * WARRANTIES.
 */

package edu.cmu.sphinx.api.ezai.client;

import java.io.IOException;

import edu.cmu.sphinx.api.AbstractSpeechRecognizer;
import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.ezai.client.Microphone;
import edu.cmu.sphinx.frontend.util.StreamDataSource;


/**
 * High-level class for live speech recognition.
 */
public class LiveSpeechRecognizer extends AbstractSpeechRecognizer {

    private final Microphone microphone;

    /**
     * Constructs new live recognition object.
     *
     * @param configuration common configuration
     * @throws IOException if model IO went wrong
     */
    public LiveSpeechRecognizer(Configuration configuration) throws IOException
    {
        super(configuration);
        microphone = SpeechSourceProvider.getMicrophone();
        context.getInstance(StreamDataSource.class)
            .setInputStream(microphone.getStream());
    }

    /**
     * Starts recognition process.
     *
     * @param clear clear cached microphone data
     * @see         LiveSpeechRecognizer#stopRecognition()
     */
    public void startRecognition(boolean clear) {
        recognizer.allocate();
        microphone.startRecording();
    }

    /**
     * Stops recognition process.
     *
     * Recognition process is paused until the next call to startRecognition.
     *
     * @see LiveSpeechRecognizer#startRecognition(boolean)
     */
    public void stopRecognition() {
        microphone.stopRecording();
        recognizer.deallocate();
    }
    
    public void resumeRecognition(boolean clear) {
        //recognizer.allocate();
        microphone.startRecording();
    }

    public void pauseRecognition() {
        microphone.stopRecording();
        //recognizer.deallocate();
    }

}
