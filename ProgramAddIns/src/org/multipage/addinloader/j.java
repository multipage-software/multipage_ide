/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2018-11-28
 *
 */
package org.multipage.addinloader;

import java.io.PrintStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Arrays;

import org.multipage.util.Safe;

/**
 * Log to standard output.
 * @author vakol
 *
 */
public class j {
	
	/**
	 * Display time stamp switch
	 */
	private static boolean logTimeSpan = true;
	
	/**
	 * Last time stamp.
	 */
	private static long lastTimeStampMs = System.currentTimeMillis();
	
	/**
	 * Enable logging time delta.
	 */
	private static boolean logTimeDelta = false;
	
	/**
	 * Lock object for log
	 */
	private static final Object synclog = new Object();
	
	/**
	 * Log formatted message on stdout or stderr or on "test"
	 * displayDelta - if is true, the delta time between time stamps is displayed
	 * @param parameter - can be omitted or "out" or "err" or of type LogParameter (with type and indentation)
	 * @param strings
	 */
	synchronized public static void log(Object parameter, Object ... strings) {
		try {
			
			synchronized (synclog) {
				
				String type = "";
				String indentation = "";
				
				long currentTimeMs = System.currentTimeMillis();
				long timeDeltaMs = currentTimeMs - lastTimeStampMs;
				
				Timestamp timeStamp = new Timestamp(lastTimeStampMs);
				String timeStampText = logTimeSpan ? new SimpleDateFormat("kk:mm:ss.SSS").format(timeStamp) + ": " : "";
				lastTimeStampMs = currentTimeMs;
				
				if (logTimeDelta) {
					System.err.format("delta %dms ", timeDeltaMs);
				}
				
				if (parameter instanceof LogParameter) {
					LogParameter logparam = (LogParameter) parameter;
					indentation = logparam.getIndentation();
					type = logparam.getType();
				}
				else if (parameter instanceof String) {
					type = (String) parameter;
				}
				
				if (!type.isEmpty()) {
					
					PrintStream os = "out".equals(type) || "test".equals(type) ? System.out : ("err".equals(type) ? System.err : null);
					if (strings.length > 0) {
						if (os != null) {
							Object [] parameters = Arrays.copyOfRange(strings, 1, strings.length);
							os.format(indentation + timeStampText + strings[0].toString() + '\n', parameters);
						}
						else {
							System.err.format(indentation + timeStampText + type + '\n', strings);
						}
						
					}
					else {
						if (os != null) {
							os.format(indentation + timeStampText + type);
						}
						else {
							System.err.format(indentation + timeStampText + type + '\n');
						}
					}
				}
				else {
					System.err.format(indentation + parameter.toString() + '\n', strings);
				}
			}
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Log time stamp next time the message is displayed.
	 */
	public static void enableTimeSpan(boolean enable) {
		
		logTimeSpan = enable;
	}
	
	/**
	 * Log time delta next time the message is displayed.
	 */
	public static void enableTimeDelta(boolean enable) {
		
		logTimeDelta = enable;
	}

	/**
	 * Log message.
	 * @param stringResource - identifier of a message
	 * @param strings
	 */
	public static void logMessage(String stringResource, Object ...strings) {
		try {
			
			// Try to load string resource.
			String message = AddInsUtility.getString(stringResource);
			log(message, strings);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
	
	/**
	 * Print stack trace
	 * @param caption
	 */
	public static void printStackTrace(String caption) {
		
		try {
			throw new Exception(caption);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Get function on stack.
	 * @param stackLevel
	 * @return
	 */
	public static String stack(int stackLevel) {
		
		try {
			// Omit current and the caller level.
			stackLevel += 2;
			
			// Get stack info.
			StackTraceElement [] stack = Thread.currentThread().getStackTrace();
			
			// Try to get stack element at given level.
			if (stackLevel > 0 && stackLevel <= stack.length) {
				StackTraceElement stackElement = stack[stackLevel];
				
				// Dump stack level.
				String stackElementDump = stackElement.toString();
				return stackElementDump;
			}
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return "unknown";
	}
}
