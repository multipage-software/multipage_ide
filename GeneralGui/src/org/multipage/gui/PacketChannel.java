/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2024-04-04
 *
 */
package org.multipage.gui;

import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousCloseException;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

import org.multipage.util.Obj;
import org.multipage.util.RepeatedTask;
import org.multipage.util.Safe;

/**
 * Packet channel that uses sockets.
 * @author vakol
 */
public class PacketChannel {
	
	/**
     * Exit flag.
     */
	private static boolean exit = false;
	
	/**
	 * Socket address object.
	 */
	private InetSocketAddress socketAddress = null;
	
	/**
	 * Input socket object.
	 */
	private AsynchronousServerSocketChannel serverSocketChannel = null;
	
	/**
	 * Client socket channel.
	 */
	private AsynchronousSocketChannel clientSocketChannel = null;
	
	/**
	 * Connected server socket address.
	 */
	private InetSocketAddress serverSocketAddress = null;
	
	/**
	 * Constructor.
	 */
	public PacketChannel() {
		
	}
	
	/**
	 * Constructor.
	 * @param clientSocketChannel
	 */
	public PacketChannel(AsynchronousSocketChannel clientSocketChannel) {
		
		this.clientSocketChannel = clientSocketChannel;
	}

	/**
	 * Open server socket.
	 * @param hostname
	 * @param port
	 * @throws Exception 
	 */
	public void listen(String hostname, int port)
			throws Exception {
		
		// Open listening socket.
        serverSocketChannel = AsynchronousServerSocketChannel.open();
        socketAddress = new InetSocketAddress(hostname, port);
        serverSocketChannel.bind(socketAddress);
        
        Obj<Boolean> acceptNewConnection = new Obj<>(true);
        
        int objectId = hashCode();
        String objectIdText = String.valueOf(objectId);
        
        // Set timeouts.
        long startDelayMs = 0L;
        long idleTimeMs = 100L;
        long timeoutMs = -1L;
        
        // Create thread that accepts incoming connections.
        RepeatedTask.loopNonBlocking("AcceptXdebugConnections" + objectIdText, startDelayMs, idleTimeMs, timeoutMs, (running, exception) -> {
        	
        	// Set event that accepts incoming connections from input socket.
        	if (acceptNewConnection.ref) {
        		acceptNewConnection.ref = false;
        		
	            serverSocketChannel.accept(this, new CompletionHandler<AsynchronousSocketChannel, PacketChannel>() {
	            
	            	// This event is invoked when the socket connection is completed.
	            	@Override
	    			public void completed(AsynchronousSocketChannel client, PacketChannel packetChannel) {
	            		
	            		acceptNewConnection.ref = true;
	            		
	            		try {
	            			// Remember connected socket channel.
	    	        		clientSocketChannel = client;
	    	
	    	    			// Invoke callback function.
	    	    			PacketSession packetSession = onStartSession(client);
	    	    			if (packetSession == null) {
	    	    				return;
	    	    			}
	    	
	    	        		// Read incoming packets until the connection is closed or interrupted.
	    					packetSession.startReadingPackets(client);
	            		}
	            		catch (Exception e) {
	            			onException(e);
	            		}
	            	}
	            	
	    			// If the connection failed...
	                public void failed(Throwable e, PacketChannel packetChannel) {
	                	// Ignore close exception.
	                	if (e instanceof AsynchronousCloseException) {
                            return;
	                	}
	                	onException(e);
	                }
	            });
        	}
        	
	        return !exit;
        });
	}
	
	/**
	 * Set the exit flag.
	 * @param exit
	 */
	public static void setExitFlag() {
			
        PacketChannel.exit = true;
	}
	
	/**
	 * Callback function called after accepting new connection from client to server and creating new session. 
	 * @param client
	 * @return
	 */
	protected PacketSession onStartSession(AsynchronousSocketChannel client)
			throws Exception {
		
		// You can override this method.
		return null;
	}
	
	/**
	 * Connect client socket.
	 * @param hostname
	 * @param port
	 * @throws Exception 
	 */
	public void connect(String hostname, int port)
			throws Exception {
		
		try {
			// Remember server socket address.
			serverSocketAddress = new InetSocketAddress(hostname, port);
			clientSocketChannel = AsynchronousSocketChannel.open();
			
			// Create non-blocking socket channel and connect it.
	        clientSocketChannel.connect(serverSocketAddress, this, new CompletionHandler<Void, PacketChannel>() {
	        	
	        	// On successful connection.
				@Override
				public void completed(Void result, PacketChannel packetChannel) {
					
					try {
						// Callback function.
		    			PacketSession packetSession = onConnected(packetChannel);
						
		        		// Read incoming packets until the connection is closed or interrupted.
						packetSession.startReadingPackets(clientSocketChannel);
					}
		    		catch (Exception e) {
		    			onException(e);
		    		}
				}
				
				// On failed connection.
				@Override
				public void failed(Throwable e, PacketChannel packetChannel) {
					onException(e);
				}
	        });
		}
		catch (Exception e) {
			onThrownException(e);
		}
	}
	
	/**
	 * Close server channel.
	 */
	public void closeServer() {
		
		try {
			if (serverSocketChannel == null) {
				return;
			}
			if (!serverSocketChannel.isOpen()) {
				return;
			}
			serverSocketChannel.close();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}

	/**
	 * Callback function called after connection to server is established.
	 * @param packetChannel
	 * @return
	 */
	protected PacketSession onConnected(PacketChannel packetChannel) 
			throws Exception {

		// Override this method. 
		return null;
	}
	
	/**
	 * Get socket address.
	 * @return
	 */
	public InetSocketAddress getSocketAddress() {
		
		return socketAddress;
	}
	
	/**
	 * Get server socket channel.
	 * @return
	 */
	public AsynchronousServerSocketChannel getServerSocket() {
		
		return serverSocketChannel;
	}
	
	/**
	 * Get client socket channel.
	 * @return
	 */
	public AsynchronousSocketChannel getClientSocketChannel() {
		
		return clientSocketChannel;
	}
	
	/**
	 * Fired on packet exception.
	 * @param e
	 */
	protected void onThrownException(Throwable e)
			throws Exception {
		
		// Override this method.
		onException(e);
		Exception exception = new Exception(e);
		throw exception;
	}
	
	/**
	 * Fired on packet exception.
	 * @param e
	 */
	protected void onException(Throwable e) {
		
		// Override this method.
		e.printStackTrace();
	}
}
