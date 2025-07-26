/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.maclan.server;

import java.io.File;
import java.util.Properties;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.util.thread.QueuedThreadPool;
import org.eclipse.jetty.util.thread.ThreadPool;
import org.eclipse.jetty.webapp.WebAppContext;
import org.maclan.MiddleResult;
import org.maclan.MiddleUtility;
import org.multipage.sync.SyncMain;

/**
 * Jetty HTTP server.
 * @author vakol
 *
 */
public class JettyHttpServer extends ProgramHttpServer {
	
	/**
	 * Temporary folder.
	 */
	static final String temporaryFolder = "JETTY-7E7E03C5-8226-49e6-8DBE-81247F20BA0F";
	
	/**
	 * Server.
	 */
	private Server server;

	/**
	 * Set login properties.
	 */
	@Override
	public void setLogin(Properties login) {
		
		// Delegate call.
		ProgramServlet.setLogin(login);
	}

	/**
	 * Create and start server.
	 */
	@Override
	public void create(Properties loginParam, int portNumber) throws Exception {
		
		// Set login parameters
		ProgramServlet.setLogin(loginParam);
		
		// Get web interface directory
		String webInterfaceDirectory = MiddleUtility.getWebInterfaceDirectory();
		File temporaryDirectory = new File(System.getProperty("java.io.tmpdir") + File.separatorChar + temporaryFolder);
		if (!temporaryDirectory.exists()) {
			temporaryDirectory.mkdirs();
		}
		
		// Write message that informs user about web interface directory on standard error output
		System.err.println("Web interface home directory: " + webInterfaceDirectory);
		
		// Create server
		server = new Server(portNumber);
		ThreadPool threadPool = server.getThreadPool();
		if(threadPool instanceof QueuedThreadPool) {
			
			QueuedThreadPool queuedThreadPool = (QueuedThreadPool) threadPool;
			queuedThreadPool.setMinThreads(100);
		}
		
		// Set context
		WebAppContext context = new WebAppContext();
		context.setContextPath("/");
		context.setExtractWAR(false);
		context.setDescriptor(getClass().getResource("/WEB-INF/web.xml").toString());
		context.setResourceBase(webInterfaceDirectory);
		context.setTempDirectory(temporaryDirectory);
		context.setPersistTempDirectory(true);
		context.setConfigurationDiscovered(false);
		context.setParentLoaderPriority(true);

		HandlerList handlerList = new HandlerList();
		handlerList.addHandler(context);
		server.setHandler(handlerList);
				
		// Listen for script engine connections on port and send them script.
		ProgramServlet.createScriptDataSocket(50000);
		
		// Start server
		server.start();
		
		// Start Sync.
		String user = "derbypass";
		String password = "";
		SyncMain.setAccessString("http://localhost:" + portNumber, user, password);
		SyncMain.startService(true);
		
		JavaScriptDebugger.setEnabled(true); // Enable debugger.
		
		// Start debug listener.
		MiddleResult result = startDebuggerListener();
		result.throwPossibleException();
		
		// Save a reference to debugger listener in context attribute.
		context.setAttribute("debugger", debuggerListener);
	}
	
	/**
	 * Stop server.
	 */
	@Override
	public void stop() {
		
		// Try to stop server.
		try {
			// Stops debugger
			MiddleResult result = stopDebuggerListener();
			result.throwPossibleException();
			
			// Stops HTTP server
			if (server != null) {
				server.stop();
			}
			
			// Unitialize
			SyncMain.unitialize();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
