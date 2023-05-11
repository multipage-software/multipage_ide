/*
 * Copyright 2010-2023 (C) vakol
 * 
 * Created on : 08-05-2023
 *
 */
package org.maclan.server;

import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;

/**
 * Debug listener session object that stores session states.
 * @author vakol
 *
 */
public class DebugListenerSession {
	
	/**
	 * Maximum session ID.
	 */
	private static final int MAXIMUM_SESSION_ID = 1024;
	
	/**
	 * Current session ID.
	 */
	private static Integer currentSessionId = 0;
	
	/**
	 * Get session ID.
	 */
	protected static int generateNewSessionId() {
		
		synchronized (currentSessionId) {	
			if (currentSessionId < MAXIMUM_SESSION_ID) {
				currentSessionId++;
			}
			else {
				currentSessionId = 1;
			}
			return currentSessionId;
		}
	}
	
	/**
	 * Session ID.
	 */
	protected int sessionId = -1;

	/**
	 * Server socket reference.
	 */
	protected AsynchronousServerSocketChannel listenerSocket = null;
	
	/**
	 * Remote client socket reference.
	 */
	protected AsynchronousSocketChannel remoteProbeSocket = null;
	
	/**
	 * Connection attachment.
	 */
	protected Void connectionAttachment = null;
	
	/**
	 * Cosntructor.
	 * @param serverSocket
	 * @param remoteClientSocket
	 * @param attachment
	 */
	public DebugListenerSession(AsynchronousServerSocketChannel serverSocket, AsynchronousSocketChannel remoteClientSocket,
			Void attachment) {
		
		// Set members.
		this.sessionId = generateNewSessionId();
		this.listenerSocket = serverSocket;
		this.remoteProbeSocket = remoteClientSocket;
		this.connectionAttachment = attachment;
	}
	
	/**
	 * Get session ID.
	 * @return
	 */
	public int getSessionId() {
		
		if (sessionId < 1 && sessionId >= MAXIMUM_SESSION_ID) {
			sessionId = generateNewSessionId();
		}
		return sessionId; 
	}
	
	/**
	 * Get debug listener socket address.
	 */
	public AsynchronousServerSocketChannel getListenerSocket() {
		
		return listenerSocket;
	}
	
	/**
	 * Get connected remote debugger probe address of socket.
	 */
	public AsynchronousSocketChannel getRemoteProbeSocket() {
		
		return remoteProbeSocket;
	}
}
