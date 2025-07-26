/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2023-05-03
 *
 */
package org.maclan.server;

import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

import org.multipage.gui.CallbackNoArg;
import org.multipage.gui.PacketChannel;
import org.multipage.gui.PacketSession;
import org.multipage.util.Resources;

/**
 * Xdebug listener has a built-in socket server that accepts requests from debugger clients.
 * @author vakol
 *
 */
public class XdebugListener {
	
	/**
	 * Default Xdebug port. It is set to port number 9004 because JVM Xdebug already uses port number 9000.
	 */
	public static final int DEFAULT_XDEBUG_PORT = 9004;
	
	/**
	 * Singleton object.
	 */
    private static XdebugListener instance = null;

    /**
     * Invoked when a new Xdebug viewer has to be opened.
     */
    private Consumer<XdebugListenerSession> openDebugViewerLambda = null;
    
    /**
     * Invoked when debug viewer is closed.
     */
    private Runnable closeDebugViewerLambda = null;
	
	/**
	 * Packet channel that accepts incomming packets.
	 */
	private PacketChannel packetChannel = null;

	/**
	 * List of current sessions. The list is synchronized and can be edited with concurrent threads.
	 */
	protected List<XdebugListenerSession> sessions = Collections.synchronizedList(new LinkedList<>());
	
    /**
     * Get singleton object.
     * @return
     */
    public static synchronized XdebugListener getInstance() {
        if (instance == null) {
            instance = new XdebugListener();
        }
        return instance;
    }
    
	/**
	 * Get current debug listener sessions.
	 */
	public List<XdebugListenerSession> getSessions() {
		
		return sessions;
	}
	
	/**
	 * Ensure that sessions are opened. Remove the closed sessions.
	 */
	public void ensureLiveSessions() {
		
		synchronized (sessions) {
			
			LinkedList<XdebugListenerSession> closedSessions = new LinkedList<>();
			
			// Find closed sessions.
			for (XdebugListenerSession session : sessions) {
				
				boolean isOpen = session.isOpen();
				if (!isOpen) {
					closedSessions.addLast(session);
				}
			}
	
			// Remove the closed sessions.
			sessions.removeAll(closedSessions);			
		}
	}
	
	/**
	 * Opens listener port.
	 * @param port
	 */
	public void openListenerPort(int port)
			throws Exception {
		
		packetChannel = new PacketChannel() {
			@Override
			protected PacketSession onStartSession(AsynchronousSocketChannel client) {
				
				PacketSession packetSession = XdebugListener.this.onOpenDebugViewer(client);
				return packetSession;
			}
		};
		
		// Open packet channel.
		packetChannel.listen("localhost", port);
	}
	
	/**
	 * On accepting incomming connection.
	 * @param socketClient
	 * @param packetReader
	 */
	protected PacketSession onOpenDebugViewer(AsynchronousSocketChannel socketClient) {
		
		// Create and remember new session object.
		try {
			
			synchronized (sessions) {
				// Create Xdebug session.
				AsynchronousServerSocketChannel socketServer = packetChannel.getServerSocket();
				XdebugListenerSession xdebugSession = XdebugListenerSession.newSession(socketServer, socketClient, this);
				sessions.add(xdebugSession);
				
				// Call the "accept session" callback.
				onOpenDebugViewer(xdebugSession);
				
				return xdebugSession.getPacketSession();
			}
		}
		catch (Exception e) {
			onException(e);
		}
		return null;
	}
	
	/**
	 * Set lambda function that opens debug viewer. 
	 * @param openLambda
	 */
	public void setOpenDebugViewerLambda(Consumer<XdebugListenerSession> openLambda) {
		
		openDebugViewerLambda = openLambda;
	}

	/**
	 * On open Xdebug viewer.
	 * @param server
	 * @param client
	 * @param attachment
	 */
	protected void onOpenDebugViewer(XdebugListenerSession listenerSession) {
		
		if (openDebugViewerLambda != null) {
			openDebugViewerLambda.accept(listenerSession);
		}
	}
	
	/**
	 * Set lambda function that closes debug viewer. 
	 * @param openLambda
	 */
	public void setCloseDebugViewerLambda(Runnable closeLambda) {
		
		closeDebugViewerLambda = closeLambda;
	}	
	
	/**
	 * On close debugger.
	 */
	public void onCloseDebugger() {
		
		if (closeDebugViewerLambda != null) {
			closeDebugViewerLambda.run();
		}
	}
	
	/**
	 * Fired on exception.
	 * @param messageId
	 * @param parameters
	 * @throws Exception
	 */
	protected void onThrownException(String messageId, Object ... parameters)
			throws Exception {
		
		String messageFormat = Resources.getString(messageId); 
		String message = String.format(messageFormat, parameters);
		Exception e = new Exception(message);
		onThrownException(e);
	}
	
	/**
	 * Fired on exception.
	 * @param e
	 * @throws Exception
	 */
	protected void onThrownException(Exception e)
			throws Exception {
		
		// Override this method.
		onException(e);
		throw e;
	}
	
	/**
	 * Fired on exception.
	 * @param e
	 */
	protected void onException(Exception e) {
		
		// Override this method.
		e.printStackTrace();
	}
	
	/**
	 * Set PHP debug listener.
	 * @param callbackNoArg
	 */
	public static void setDebugPhpListener(CallbackNoArg callbackNoArg) {
		// TODO: <---MAKE
		
	}
}
