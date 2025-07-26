/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2022-10-25
 *
 */
package org.multipage.gui;

import java.util.LinkedList;

/**
 * Callback methods for log.
 * @author vakol
 *
 */
public class LoggingCallback {
	
	/**
	 * Log message.
	 * @param message
	 */
	public static void log(Message message) {
		
		// Override this method.
	}
	
	/**
	 * Log message.
	 * @param message
	 * @param eventHandle
	 * @param executionTime
	 */
	public static void log(Message message, EventHandle eventHandle, long executionTime) {
		
		// Override this method.
	}
	
	/**
	 * Log queue snapshot.
	 * @param queueSnapshot
	 * @param now
	 */
	public static void addMessageQueueSnapshot(LinkedList<Message> queueSnapshot, long now) {
		
		// Override this method.
	}
	
	/**
	 * Break logging.
	 * @param signal
	 */
	public static void breakPoint(Signal signal) {
		
		// Override this method.
	}
}
