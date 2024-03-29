/*
 * Copyright 2010-2018 (C) vakol
 * 
 * Created on : 08-08-2018
 *
 */

package org.maclan.server;

import java.awt.Component;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import org.multipage.gui.CallbackNoArg;
import org.multipage.gui.Utility;

/**
 * 
 * @author user
 *
 */
public abstract class DebugListener {
	
	/**
	 * List of current sessions. The list is synchronize and can be edited with concurrent threads.
	 */
	protected List<DebugListenerSession> sessions = Collections.synchronizedList(new LinkedList<DebugListenerSession>());
	
	/**
	 * Listener that determines if code should be debugged
	 */
	protected static CallbackNoArg enableListener = null;
	
    /**
     * Invoked when a new Xdebug session should be accepted.
     */
    public Consumer<XdebugListenerSession> acceptSessionLambda = null;
	
    /**
     * Invoked when input packet has been received by the debug server.
     */
    public BiConsumer<DebugListenerSession, XdebugResponse> inputPacketLambda = null;
    
	
	/**
	 * Sets listener that determines if debugger is enabled
	 * @param listener
	 */
	public static void setDebugPhpListener(CallbackNoArg listener) {
		
		enableListener = listener;
	}
	
	/**
	 * Set open debug viewer listeners.
	 */
	public abstract void setDebugViewerListeners(DebugViewerCallback callback);
	
	/**
	 * Activates debugger. The method creates a main thread that communicates with client.
	 */
	protected abstract void activate() throws Exception;
	
	/**
	 * Start debugging
	 */
	public abstract boolean startDebugging();
	
	/**
	 * Get current debug listener sessions.
	 */
	public List<DebugListenerSession> getSessions() {
		
		return sessions;
	}
	
	/**
	 * Find session with session ID.
	 * @param sessionId
	 * @throws Exception 
	 */
	public DebugListenerSession getSession(long sessionId)
			throws Exception {
		
		DebugListenerSession foundSession = null;
		
		for (DebugListenerSession session : sessions) {
			if (session.sessionId == sessionId) {
				
				if (foundSession != null) {
					Utility.throwException("org.maclan.server.messageDuplicitSessionIds", sessionId);
				}
				foundSession = session;
			}
		}
		return foundSession;
	}
	
	/**
	 * Get current debug viewer component.
	 * @param debugViewerComponent
	 */
	public abstract void setViewerComponent(Component debugViewerComponent);
	
	/**
	 * Stop debugging
	 */
	public abstract void stopDebugging();
	
	/**
	 * Close debugger
	 */
	public abstract void close();
}
