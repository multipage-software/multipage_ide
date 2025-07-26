/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator;

import java.io.File;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.maclan.MiddleResult;
import org.maclan.MiddleUtility;
import org.maclan.help.Intellisense;
import org.maclan.help.ProgramHelp;
import org.maclan.server.AreaServer;
import org.maclan.server.DebugViewer;
import org.maclan.server.ProgramHttpServer;
import org.multipage.basic.ProgramBasic;
import org.multipage.gui.GeneralGui;
import org.multipage.gui.StateSerializer;
import org.multipage.gui.TextPopupMenu;
import org.multipage.sync.ProgramSync;
import org.multipage.sync.SyncMain;
import org.multipage.translator.ProgramDictionary;
import org.multipage.util.Lock;
import org.multipage.util.Obj;
import org.multipage.util.RepeatedTask;
import org.multipage.util.Safe;
import org.multipage.util.SimpleMethodRef;
import org.multipage.util.j;


/**
 * Main entry point for Generator application.
 * @author vakol
 *
 */
public class GeneratorMain {
	
	/**
	 * Default language and country.
	 */
	public static final String defaultLanguage = "en";
	public static final String defaultCountry = "US";
	
	/**
	 * GUI watchdog delay and idle timeout in milliseconds.
	 */
	private static final boolean GUI_WATCHDOG_ENABLED = false;
	private static final long GUI_WATCHDOG_DELAY_MS = 3000;
	private static final long GUI_WATCHDOG_MS = 1000;
	
	/**
	 * Serialized data location.
	 */
	private static final String serilizedDataLocation = "org.multipage.generator.settings";
	
	/**
	 * Application state serializer.
	 */
	private static StateSerializer serializer;
	
	/**
	 * Get state serializer
	 * @return
	 */
	public static StateSerializer getSerializer() {
		
		return serializer;
	}
	
	/**
	 * Main entry point.
	 * @param applicationNaming 
	 * @param args
	 * @param pathToMiddleObjects
	 * @param useLogin
	 */
	public static void main(String applicationNaming, String[] args, String pathToMiddleObjects, final boolean useLogin) {
		try {
			
			// Set default application naming. It is used for example for application user directory
			MiddleUtility.setApplicationNaming(applicationNaming);
			
		    // Set System L&F.
	        try {
				UIManager.setLookAndFeel(
				    UIManager.getSystemLookAndFeelClassName());
			}
	        catch (Exception e) {
			}
	        
			// Get local identifiers.
			String language;
			String country;
			
			if (args.length == 0) {
				
				language = defaultLanguage;
				country = defaultCountry;
			}
			else if (args.length == 2) {
				
				language = args[0];
				country = args[1];
			}
			else {
				// If there are wrong parameters.
				JOptionPane.showMessageDialog(
						null,
						"Error application arguments.\nUse:\n\tjava ProgramBuilder language COUNTRY",
						"Swing GUI Builder",
						JOptionPane.ERROR_MESSAGE);
				System.exit(1);
				return;
			}
			
			// Set locale.
			Locale.setDefault(new Locale(language, country));
			
			// Set Sync main application title
			SyncMain.setMainApplicationTitleCallback(() -> { return ProgramGenerator.getApplicationTitle(); });
			
			// Load application properties.
			MiddleUtility.loadApplicationProperties();
			
			// Load servers settings.
			MiddleUtility.loadServersProperties();
			
			// Set path to middle objects.
			MiddleUtility.setPathToMiddle(pathToMiddleObjects);
			
			// Create application state serializer.
			String userDirectory = MiddleUtility.getUserDirectory();
			String serializedFile = null;
			
			if (!userDirectory.isEmpty()) {
				serializedFile = userDirectory + File.separatorChar + serilizedDataLocation;
			}
			else {
				serializedFile = serilizedDataLocation;
			}
			
			serializer = new StateSerializer(serializedFile);
			
			// Initialize general GUI level.
			if (!GeneralGui.initialize(language, country, serializer)) {
				System.exit(3);
				return;
			}
			
			// Initialize program basic level.
			if (!ProgramBasic.initialize(language, country, serializer, null)) {
				System.exit(4);
				return;
			}
			
			// Initialize program dictionary level.
			if (!ProgramDictionary.initialize(language, country, serializer)) {
				System.exit(5);
				return;
			}
					
			// Initialize program sync layer.
			if (!ProgramSync.initialize(language, country, serializer)) {
				System.exit(6);
				return;
			}
			
			// Initialize program help layer.
			if (!ProgramHelp.initialize(language, country, serializer)) {
				System.exit(7);
				return;
			}
			
			// Initialize program generator layer.
			if (!ProgramGenerator.initialize(language, country, serializer)) {
				System.exit(8);
				return;
			}
			
			// Initialize intellisense.
			Intellisense.initialize();
			
			// Set middle layer model reference.
			ProgramBasic.getMiddle().setModel(ProgramGenerator.getAreasModel());
	
			// Set use login flag.
			ProgramBasic.setUseLogin(useLogin);
	
			// Initialize area server.
			AreaServer.initialize();
			
	        Safe.invokeLater(() -> {
				// User interface.
				userInterface();
	        });
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
		
	/**
	 * User interface.
	 * @throws Exception 
	 */
	protected static void userInterface() {
		try {
			
			// Load serialized data.
			serializer.startLoadingSerializedStates();
			
			// Enable/disable CSS editors in text popup trayMenu.
			TextPopupMenu.enableCss = true;
			
			// Database login.
			MiddleResult loginResult = MiddleResult.OK;
			boolean useLogin = ProgramBasic.isUsedLogin();
			if (useLogin) {
				loginResult = ProgramBasic.loginDialog();
			}
			
			if (loginResult.isOK()) {
				
				// Create basic area if it doesn't exist.
				Obj<Boolean> newBasicArea = new Obj<Boolean>();
				Properties loginProperties = ProgramBasic.getLoginProperties();
				
				MiddleResult result = ProgramBasic.getMiddle().attachOrCreateNewBasicArea(loginProperties, null, newBasicArea);
				if (result.isOK()) {
					
					
					// Import help.
					if (newBasicArea.ref) {
						
						j.log("Importing Maclan reference into Basic Area.");
						
						InputStream maclanHelpXml = ProgramHelp.openMaclanReferenceXml();
						InputStream maclanHelpDat = ProgramHelp.openMaclanReferenceDat();
						
						try {
							ProgramBasic.loginMiddle();
							result = ProgramBasic.getMiddle().importTemplate(maclanHelpXml, maclanHelpDat);
						}
						catch (Exception e) {
						}
						finally {
							ProgramBasic.logoutMiddle();
						}
					}
				}
				if (result.isNotOK()) {
					result.show(null);
				}
				
				// Start HTTP server.
				ProgramHttpServer httpServer = ProgramBasic.startHttpServer(Settings.getHttpPortNumber(), !ProgramBasic.isUsedLogin());
				
				// Attach Area Server debugger to the debug viewer.
				DebugViewer debugViewer = DebugViewer.getInstance();
				if (debugViewer != null) {
					debugViewer.attachDebuggerListener(httpServer.getDebuggerListener());
				}
	
				// Initialize main frame class. Create and show main frame.
				GeneratorMainFrame mainFrame = new GeneratorMainFrame();
				
				// Initialize Open and Save dialog path
				MiddleUtility.initOpenSavePath();
				
				// On close main frame.
				mainFrame.setCloseListener(new SimpleMethodRef() {
					// On close window.
					@Override
					public void run() {
						try {
							
							closeApplication();
						}
						catch(Throwable expt) {
							Safe.exception(expt);
						};
					}
				});
				
				// Show main frame
				mainFrame.setVisible(true);
				
				// Start GUI watchdog.
				Safe.invokeLater(() -> {
					GeneratorMain.startGuiWatchdog();
				});
			}
			else {
				// Close application.
				closeApplication();
				// Exit the application.
				System.exit(0);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Starts the GUI main thread watch dog.
	 * @throws Exception 
	 */
	private static void startGuiWatchdog() {

		// Check development flag.
		if (!GUI_WATCHDOG_ENABLED) {
			return;
		}
		
		try {
			final long timeoutMs = -1L;
			
			RepeatedTask.loopNonBlocking("GUI-WathdogThread", GUI_WATCHDOG_MS, GUI_WATCHDOG_DELAY_MS, timeoutMs, (exit, exception) -> {
				
	            // Check if main window is active.
				JFrame mainFrame = GeneratorMainFrame.getFrame();
				if (mainFrame == null) {
					return exit;
				}
				
				Obj<Boolean> isActive = new Obj<Boolean>(false);
				Lock lock = new Lock();
				Safe.invokeLater(() -> {
					isActive.ref = true;
					Lock.notify(lock);
				});
				boolean lockTimeout = Lock.waitFor(lock, GUI_WATCHDOG_MS);
				if (lockTimeout) {
					isActive.ref = false;
				}
				
				if (!isActive.ref) {
					
					// Save application state.
					MiddleUtility.saveServerProperties();
					// Save serialized data.
					serializer.startSavingSerializedStates();
					
					// Kill all threads and exit the application.
					GeneratorMain.InterruptAllThreads();
					
					System.exit(-1);
				}
				return exit;
			});
		}
		catch (Exception e) {
			Safe.exception(e);
		}
	}
	
	/**
	 * Stop GUI watchdog.
	 */
	public static void stopGuiWatchDog() {
		try {
			
			RepeatedTask.stopTask("GUI-WathdogThread");
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Interrupt all currently running threads.
	 */
	public static void InterruptAllThreads() {
		try {
			
			Thread currentThread = Thread.currentThread();
	        long currentThreadId = currentThread.getId();
	
	        Set<Thread> allThreads = Thread.getAllStackTraces().keySet();
	        System.out.format("\nInterrupting %d threads:\n", allThreads.size());
	        
	        Obj<Integer> index = new Obj<Integer>(1);
	        
	        allThreads.stream().forEach(thread -> {
	        	try {
					
					long threadId = thread.getId();
		        	if (threadId == currentThreadId) {
		        		return;
		        	}
		        	
		            System.out.format("%d. Thread [%x] %s interrupted.\n", index.ref++, threadId, thread.getName());
		        	try {
		        		thread.interrupt();
		        	}
		        	catch (Exception e) {
		        		Safe.exception(e);
		        	}
				}
				catch(Throwable expt) {
					Safe.exception(expt);
				};
	        });
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Close application.
	 */
	private static void closeApplication() {
		try {
			
			// Stop Area Server.
			AreaServer.stop();
			// Stop HTTP server.
			ProgramBasic.stopHttpServer();
			// Stop Sync.
			SyncMain.stop();
			// Save server properties.
			MiddleUtility.saveServerProperties();
			// Save serialized data.
			serializer.startSavingSerializedStates();
			
			// Stop all repeted tasks.
			boolean success = RepeatedTask.stopAllTasks();
			if (!success) {
				// Interrupt all running threads.
				InterruptAllThreads();
			}
			// Stop SWT thread that displayed embedded browsers.
			SwtBrowserCanvas.stopSwtThread();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
