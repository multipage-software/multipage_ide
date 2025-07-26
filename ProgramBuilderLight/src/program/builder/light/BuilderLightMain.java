/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package program.builder.light;

import org.multipage.util.Safe;

import program.builder.BuilderMain;

/**
 * Main entry point for the Builder application with built-in Apache Derby database.
 * @author vakol
 *
 */
public class BuilderLightMain {

	/**
	 * Main entry point.
	 * @param args
	 */
	public static void main(String[] args) {
		
		try {
			// Use Apache Derby middle layer.
			BuilderMain.main("Multipage Builder Standalone", args, "org.multipage.derby", false, null);
		}
		catch (Throwable e) {
			Safe.exception(e);
		}
	}
}
