/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.util;

import org.multipage.util.Safe;

/**
 * Class for text output.
 * @author vakol
 *
 */
public class TextOutputCapturer {
	
	/**
	 * Fields.
	 */
    private StringBuilder stringBuilder;
    
    /**
     * Constructor.
     */
	public TextOutputCapturer() {
		
		stringBuilder = new StringBuilder();
    }
	
	/**
	 * Stop.
	 * @return
	 */
    public String stop() {
        
    	try {
	    	String capturedValue = stringBuilder.toString();
	    	stringBuilder.setLength(0);
	        return capturedValue;
	    }
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "";
    }
    
    /**
     * Print text.
     * @param text
     */
	public void print(String text) {
		try {
			
			stringBuilder.append(text);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Print text line.
	 * @param text
	 */
	public void println(String text) {
		try {
			
			stringBuilder.append(text);
			stringBuilder.append('\n');
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
