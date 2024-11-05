/*
 * Copyright 2010-2017 (C) vakol
 * 
 * Created on : 26-04-2017
 *
 */

package org.maclan.server;

import java.util.Properties;

import org.maclan.MiddleResult;

/**
 * @author
 *
 */
public abstract class ProgramHttpServer {
	
	/**
	 * Debugger listener.
	 */
	protected XdebugListener debuggerListener = null;
	
	/**
	 * Set login properties.
	 * @param loginProperties
	 */
	public abstract void setLogin(Properties loginProperties);

	/**
	 * Create server.
	 * @param login
	 * @param portNumber
	 * @throws Exception
	 */
	public abstract void create(Properties login, int portNumber) throws Exception;

	/**
	 * Stop server.
	 */
	public abstract void stop();
	
	/**
	 * Start debug listener.
	 */
	public MiddleResult startDebuggerListener() {
		
		try {
			debuggerListener = XdebugListener.getInstance();
			debuggerListener.openListenerPort(XdebugListener.DEFAULT_XDEBUG_PORT);
			return MiddleResult.OK;
		}
		catch (Exception e) {
			String message = e.getMessage();
			return MiddleResult.DEBUGGER_NOT_STARTED.format(message);
		}
	}
	
	/**
	 * Stop debugger listener.
	 */
	public MiddleResult stopDebuggerListener() {
		
		if (debuggerListener == null) {
			return MiddleResult.OK;
		}
		
		try {
			debuggerListener.onCloseDebugger();
			return MiddleResult.OK;
		}
		catch (Exception e) {
			String message = e.getMessage();
			return MiddleResult.DEBUGGER_NOT_STOPPED.format(message);
		}
	}

	/**
	 * Returns reference to debug listener.
	 * @return
	 */
	public XdebugListener getDebuggerListener() {
		
		return debuggerListener;
	}
}
