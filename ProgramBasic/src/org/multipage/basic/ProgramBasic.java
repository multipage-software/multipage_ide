/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.basic;

import java.awt.Component;
import java.awt.Dialog.ModalityType;
import java.awt.Window;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.Properties;

import org.maclan.Area;
import org.maclan.Middle;
import org.maclan.MiddleResult;
import org.maclan.MiddleUtility;
import org.maclan.Slot;
import org.maclan.help.ProgramHelp;
import org.maclan.server.JettyHttpServer;
import org.maclan.server.ProgramHttpServer;
import org.multipage.gui.FindReplaceDialog;
import org.multipage.gui.HelpDialog;
import org.multipage.gui.SerializeStateAdapter;
import org.multipage.gui.StateInputStream;
import org.multipage.gui.StateOutputStream;
import org.multipage.gui.StateSerializer;
import org.multipage.gui.Utility;
import org.multipage.util.Obj;
import org.multipage.util.Resources;
import org.multipage.util.Safe;

/**
 * Basic application functions.
 * @author vakol
 *
 */
public class ProgramBasic {
	
	/**
	 * Resource location.
	 */
	private static final String resourcesLocation = "org.multipage.basic.properties.messages";

	/**
	 * Use login flag.
	 */
	private static boolean useLogin = true;

	/**
	 * Middle layer.
	 */
	private static Middle middle;

	/**
	 * Login dialog.
	 */
	private static LoginDialog loginDialog;
	
	/**
	 * HTTP server.
	 */
	private static ProgramHttpServer httpServer = new JettyHttpServer();
	
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
	 * Initialize program basic layer.
	 * @param serializer 
	 * @param dynamicMiddle 
	 * @wbp.parser.entryPoint
	 */
	public static boolean initialize(String language, String country,
			StateSerializer serializer, Middle dynamicMiddle) {
		
		try {
			// Remember the serializer
			ProgramBasic.serializer = serializer;
			
			// Set local identifiers.
			Resources.setLanguageAndCountry(language, country);
			
			// Create new middle instance.
			if (dynamicMiddle == null) {
				middle = MiddleUtility.newMiddleInstance();
			}
			else {
				middle = dynamicMiddle;
			}
			
			// Load resources file.
			if (!Resources.loadResource(resourcesLocation)) {
				return false;
			}
	
			// Add state serializer.
			if (serializer != null) {
				serializer.add(new SerializeStateAdapter() {
					
					// On read state.
					@Override
					protected void onReadState(StateInputStream inputStream)
							throws IOException, ClassNotFoundException {
						// Serialize program dictionary.
						seriliazeData(inputStream);
					}
					// On write state.
					@Override
					protected void onWriteState(StateOutputStream outputStream)
							throws IOException {
						// Serialize program dictionary.
						serializeData(outputStream);
					}
					// On set default state.
					@Override
					protected void onSetDefaultState() {
						// Set default data.
						setDefaultData();
					}
				});
			}
	
			return true;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}
	
	/**
	 * Set default data.
	 */
	protected static void setDefaultData() {
		try {
			
			// Set default language.
			ProgramBasic.getMiddle().setCurrentLanguageId(0L);
			
			// Default data.
			LoginDialog.setDefaultData();
			FindReplaceDialog.setDefaultData();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
			
	}
	/**
	 * Serialize module data.
	 * @param inputStream
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static void seriliazeData(StateInputStream inputStream)
		throws IOException, ClassNotFoundException {

		// Read current language.
		ProgramBasic.getMiddle().setCurrentLanguageId(inputStream.readLong());
		
		// Load data.
		LoginDialog.serializeData(inputStream);
		FindReplaceDialog.serializeData(inputStream);
	}

	/**
	 * Serialize module data.
	 * @param outputStream
	 * @throws IOException
	 */
	public static void serializeData(StateOutputStream outputStream)
		throws IOException {

		// Write current language.
		outputStream.writeLong(ProgramBasic.getMiddle().getCurrentLanguageId());
		
		// Save data
		LoginDialog.serializeData(outputStream);
		FindReplaceDialog.serializeData(outputStream);
	}

	/**
	 * @return the middle.middle
	 */
	public static Middle getMiddle() {
		return middle;
	}
	
	/**
	 * 
	 * @return
	 * @throws Exception 
	 */
	public static Middle loginMiddle() throws Exception {
		
		MiddleResult result = middle.login(getLoginProperties());
		result.throwPossibleException();
		
		return middle;
	}
	
	/**
	 * Update database access.
	 * @param databaseAccess 
	 */
	public static void updateDatabaseAccess(String databaseAccess) {
		try {
			
			// Attach existing or create new database.
			Obj<Boolean> isNewDatabase = new Obj<Boolean>();
			Properties loginProperties = getLoginProperties();
			MiddleResult result = middle.attachOrCreateNewBasicArea(loginProperties, foundDatabaseNames -> {
						
						try {
							return SelectDatabaseDialog.showDialog(null, foundDatabaseNames);
						}
						catch (Throwable e) {
							Safe.exception(e);
						}
						return "";
					},
					isNewDatabase);
			
			if (result.isOK()) {
				
				// Import help.
				if (isNewDatabase.ref) {
					
					System.err.println("Importing Maclan reference into Basic Area.");
					
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
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * 
	 */
	public static void logoutMiddle() {
		try {
			
			middle.logout(MiddleResult.OK);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Get login properties. Delegate to login dialog.
	 */
	public static Properties getLoginProperties() {
		
		if (loginDialog == null) {
			return null;
		}
		
		try {
			return loginDialog.getLoginProperties();
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Get available databases.
	 */
	public static MiddleResult loadAvailableDatabases(Properties loginProperties, LinkedList<String> databaseNames) {
		
		try {
			// Load database names from DBMS
			String server = loginProperties.getProperty("server");
			int port = Integer.parseInt(loginProperties.getProperty("port"));
			boolean ssl = Boolean.parseBoolean(loginProperties.getProperty("ssl"));
			String username = loginProperties.getProperty("username");
			String password = loginProperties.getProperty("password");
			
			MiddleResult result = ProgramBasic.getMiddle().getDatabaseNames(server, port, ssl, username, password, databaseNames);
			return result;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Show login dialog.
	 * @return
	 */
	public static MiddleResult loginDialog(Window parentWindow, String title) {
		
		try {
			if (!useLogin) {
				return MiddleResult.OK;
			}
	
			if (loginDialog == null ) {
				
				if (title == null) {
					title = Resources.getString("org.multipage.basic.textLoginDialog");
				}
				
				loginDialog = new LoginDialog(parentWindow,
					title,
					ModalityType.APPLICATION_MODAL);
			}
			
			// Show login dialog.
			loginDialog.setVisible(true);
			loginDialog.dispose();
			return loginDialog.result;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}
	
	/**
	 * Show login dialog with a possibility to create new database.
	 * @param confirmDatabaseLambda
	 * @return
	 */
	public static MiddleResult loginDialog() {
		
		try {
			MiddleResult result = loginDialog(null, "org.multipage.generator.textLoginDialog");
			return result;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return null;
	}

	/**
	 * Set attempts.
	 * @param n
	 */
	public static void setAttempts(int n) {
		try {
			
			if (loginDialog != null) {
				loginDialog.setAttempts(n);
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Set "use login" flag.
	 * @param useLogin
	 */
	public static void setUseLogin(boolean useLogin) {
		
		ProgramBasic.useLogin = useLogin;
	}
	
	/**
	 * Get is login flag.
	 * @return
	 */
	public static boolean isUsedLogin() {
		
		return useLogin;
	}
	
	/**
	 * Start HTTP server.
	 * @param noLogin 
	 */
	public static ProgramHttpServer startHttpServer(final int portNumber, boolean noLogin) {
		
		// Create new HTTP server.
		try {
			httpServer.create(noLogin ? new Properties() : getLoginProperties(), portNumber);
		}
		catch (Exception e) {
			
			Utility.show2(String.format(
					Resources.getString("org.multipage.basic.messageCannotStartWebServer"), portNumber, e.getMessage()));
		}
		return httpServer;
	}
	
	/**
	 * Set HTTP server login.
	 */
	public static void setHttpServerLogin() {
		
		if (!useLogin) {
			return;
		}
		try {
			
			Properties login = getLoginProperties();
			httpServer.setLogin(login);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Close HTTP server.
	 */
	public static void stopHttpServer() {
		try {
			
			httpServer.stop();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Get HTTP server.
	 * @return 
	 */
	public static ProgramHttpServer getHttpServer() {
		
		return httpServer;
	}
	
	/**
	 * 
	 * @param parent
	 * @param slotId
	 * @wbp.parser.entryPoint
	 */
	public static boolean showSlotHelp(Component parent, long slotId) {
		
		try {
			Obj<String> description = new Obj<String>("");
			
			// Load description.
			Middle middle = ProgramBasic.getMiddle();
			Properties login = ProgramBasic.getLoginProperties();
			
			MiddleResult result = middle.loadSlotDescription(login, slotId, description);
			if (result.isNotOK()) {
				
				result.show(parent);
				return false;
			}
			
			if (description.ref == null || description.ref.isEmpty()) {
				Utility.show(parent, "org.multipage.basic.messageNoDescriptionForSlot");
				return false;
			}
			
			HelpDialog.showDialog(parent, Resources.getString("org.multipage.basic.textSlotHelpDialog"), description.ref);
			return true;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}
	
	/**
	 * Insert an area to a super area.
	 * @param superarea
	 * @param area
	 * @throws Exception 
	 */
	public static void insert(Area superarea, Area area)
			throws Exception {
		
		MiddleResult result = middle.insertArea(superarea, area, true, null, null);
		result.throwPossibleException();
	}
	
	/**
	 * Insert slots into the area.
	 * @param area
	 * @param slots
	 */
	public static void insert(Area area, LinkedList<Slot> slots)
			throws Exception {
		
		MiddleResult result = middle.insertAreaSlots(area, slots);
		result.throwPossibleException();
	}
	
	/**
	 * Load slot text value.
	 * @param slotId
	 * @return
	 */
	public static String getSlotText(long slotId)
			throws Exception {
		
		Obj<String> textValue = new Obj<String>();
		
		MiddleResult result = MiddleResult.OK;
		try {
			Middle middle = loginMiddle();
			result = middle.loadSlotTextValue(slotId, true, textValue);
		}
		catch (Exception e) {
			result = MiddleResult.exceptionToResult(e);
		}
		finally {
			result.throwPossibleException();
		}
		
		return textValue.ref;
	}
	
	/**
	 * Get all slots in the model.
	 * @return
	 */
	public static LinkedList<Slot> getSlots() 
			throws Exception {
		
		LinkedList<Slot> allSlots = new LinkedList<Slot>();
		
		// Load all slots.
		MiddleResult result = middle.loadSlots(allSlots);
		result.throwPossibleException();
		
		// Return result.
		return allSlots;
	}
}
