/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2020-03-26
 *
 */
package org.maclan.server;

import org.multipage.util.TextOutputCapturer;

/**
 * JavaScript block descriptor.
 * @author vakol
 *
 */
public class JavaScriptBlockDescriptor extends BlockDescriptor {
	
	/**
	 * Script output capturer.
	 */
	public TextOutputCapturer scriptOutputCapturer;

	/**
	 * JavaScript block descriptor.
	 */
	public JavaScriptBlockDescriptor() {
		
		scriptOutputCapturer = new TextOutputCapturer();
	}
}
