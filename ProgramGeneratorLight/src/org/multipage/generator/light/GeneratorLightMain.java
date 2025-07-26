/*
 * Copyright 2010-2025 (C) vakol
 * 
 * Created on : 2017-04-26
 *
 */

package org.multipage.generator.light;

import org.multipage.generator.GeneratorMain;
import org.multipage.util.Safe;


/**
 * Main entry point for the Generator Light application.
 * @author vakol
 *
 */
public class GeneratorLightMain {
	
	/**
	 * Main entry point.
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			
			// Use Derby middle layer with extensions.
			GeneratorMain.main("Multipage Generator Standalone", args, "org.multipage.derby", false);
		}
		catch(Throwable expt) {
			Safe.exception(expt);
		};
	}
}
