/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */
package org.multipage.generator;

import java.util.LinkedList;

import javax.swing.JTextPane;

import org.multipage.gui.Utility;
import org.multipage.util.Safe;

/**
 * Class for log items.
 * @author vakol
 */
class XdebugLogMessage extends LoggingDialog.LoggedMessage {
	
	/**
	 * List of log messages.
	 */
	private static LinkedList<XdebugLogMessage> listLoggedMessages = new LinkedList<XdebugLogMessage>();
	
	/**
	 * Filter string.
	 */
	private static String filterString = "*";
	
	/**
	 * Filter flags.
	 */
	private static boolean caseSensitive = false;
	private static boolean wholeWords = false;
	private static boolean exactMatch = false;
	
	/**
	 * Constructor.
	 * @param message
	 */
	public XdebugLogMessage(String message) {
		super(message);
	}
	
	/**
	 * Add new log message.
	 * @param message
	 */
	public static void addLogMessage(String message) {
		try {
			
			XdebugLogMessage logMessage = new XdebugLogMessage(message);
			listLoggedMessages.addLast(logMessage);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Display HTML log.
	 * @param textPane
	 */
	public static void displayHtmlLog(JTextPane textPane) {
		try {
			
			String logContent = "";
			
			for (XdebugLogMessage logMessage : listLoggedMessages) {
				
				String messageText = logMessage.getText();
				
				// Filter messages.
				if (!filter(messageText)) {
					continue;
				}
				
				// Append message text.
				logContent += messageText + "<br/>";
			}
			
			// Wrap content with HTML tags.
			logContent = String.format("<html>%s</html>", logContent);
			textPane.setText(logContent);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Returns false if the message is filtered or true if the message passes.
	 * @param messageText
	 * @return
	 */
	private static boolean filter(String messageText) {
		
		try {
			boolean matches = Utility.matches(messageText, filterString, caseSensitive, wholeWords, exactMatch);
			return matches;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return false;
	}

	/**
	 * Set filter and display filtered log messages.
	 * @param filterString
	 * @param caseSensitive
	 * @param wholeWords
	 * @param exactMatch
	 */
	public static void setFulltextFilter(String filterString, boolean caseSensitive, boolean wholeWords, boolean exactMatch) {
		try {
			
			XdebugLogMessage.filterString = !filterString.isEmpty() ?  filterString : "*";
			XdebugLogMessage.caseSensitive = caseSensitive;
			XdebugLogMessage.wholeWords = wholeWords;
			XdebugLogMessage.exactMatch = exactMatch;
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}