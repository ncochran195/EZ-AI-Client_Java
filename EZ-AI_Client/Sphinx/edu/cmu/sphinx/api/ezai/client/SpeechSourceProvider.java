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

import edu.cmu.sphinx.api.ezai.client.Microphone;

public class SpeechSourceProvider {
	private static Microphone mic = null;
    public static Microphone getMicrophone() {
    	if (mic == null)
    		mic = new Microphone(16000, 16, true, false);
        return mic;
    }
}
