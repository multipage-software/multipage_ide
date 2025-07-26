/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.util;

/**
 * Utility functions.
 * @author vakol
 *
 */
public class CommonUtil {
	
	/**
	 * Start time.
	 */
	private static long startTime;

	/**
	 * Start measure the time.
	 */
	public static void startMeasureTime() {
		try {
			
			startTime = System.currentTimeMillis();
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}

	/**
	 * Stop measure the time.
	 */
	public static long stopMeasureTime() {
		
		try {
			long stopTime = System.currentTimeMillis();
			long deltaT = stopTime - startTime;
			
			System.out.println("delta t = " + deltaT + " ms");
			return deltaT;
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
		return 0;
	}
}
